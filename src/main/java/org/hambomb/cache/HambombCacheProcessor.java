/*
 * Copyright 2019 The  Project
 *
 * The   Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.hambomb.cache;

import org.hambomb.cache.cluster.ClusterProcessor;
import org.hambomb.cache.cluster.node.CacheLoaderMaster;
import org.hambomb.cache.cluster.node.CacheLoaderSlave;
import org.hambomb.cache.context.*;
import org.hambomb.cache.loader.CacheObjectLoader;
import org.hambomb.cache.loader.CacheObjectMapper;
import org.hambomb.cache.loader.Cachekey;
import org.hambomb.cache.loader.MapperScanner;
import org.hambomb.cache.handler.CacheHandler;
import org.hambomb.cache.handler.LocalCacheHandler;
import org.hambomb.cache.handler.RedisTemplateCacheHandler;
import org.hambomb.cache.loader.IndexRepository;
import org.hambomb.cache.storage.value.KryoSerializationRedisSerializer;
import org.hambomb.cache.storage.value.RedisValueStorageStrategy;
import org.reflections.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author: <a herf="mailto:jarodchao@126.com>jarod </a>
 * @date: 2019-03-01
 */
public class HambombCacheProcessor {

    ApplicationContext applicationContext;

    HambombCacheConfiguration hambombCacheConfiguration;

    CacheLoaderContext cacheLoaderContext;

    private Map<String, CacheObjectLoader> entityLoaderMap;

    private MapperScanner scanner;

    private Set<Class<? extends CacheObjectMapper>> mappers;

    private static final Logger LOG = LoggerFactory.getLogger(HambombCache.class);

    private ClusterProcessor clusterProcessor;

    public HambombCacheProcessor(ApplicationContext applicationContext, HambombCacheConfiguration hambombCacheConfiguration, ClusterProcessor clusterProcessor) {
        this.applicationContext = applicationContext;
        this.hambombCacheConfiguration = hambombCacheConfiguration;
        this.clusterProcessor = clusterProcessor;
    }

    public HambombCacheProcessor(ApplicationContext applicationContext, HambombCacheConfiguration hambombCacheConfiguration, CacheLoaderContext cacheLoaderContext, ClusterProcessor clusterProcessor) {
        this.applicationContext = applicationContext;
        this.hambombCacheConfiguration = hambombCacheConfiguration;
        this.cacheLoaderContext = cacheLoaderContext;
        this.clusterProcessor = clusterProcessor;
    }

    public void startup() {

        startLoader();

    }

    public void setCacheLoaderContext(CacheLoaderContext cacheLoaderContext) {
        this.cacheLoaderContext = cacheLoaderContext;
    }

    public CacheObjectLoader getEntityLoader(String key) {

        return entityLoaderMap.get(key);
    }

    public CacheLoaderMaster fightMaster() {

        CacheLoaderMaster masterFlag = null;

        if (CacheServerStrategy.CLUSTER.equals(hambombCacheConfiguration.cacheServerStrategy) ) {

            clusterProcessor.initNodes();

            masterFlag = clusterProcessor.selectMasterLoader(true);

            clusterProcessor.initDataLoadNode();

        }

        return masterFlag;

    }

    public void restart() {

        clusterProcessor.initNodes();

        CacheLoaderMaster masterFlag = clusterProcessor.selectMasterLoader(false);

        if (masterFlag == null) {
            LOG.info("Application Server not was a Master Node,HambombCache is stopping.");
            return;
        }

        startLoader();

    }

    public CacheLoaderSlave createSlave() {
        return clusterProcessor.createSlaveNode();
    }


    private void startLoader() {

        scanner = new MapperScanner(hambombCacheConfiguration.scanPackageName);

        mappers = scanner.scanMapper();

        LOG.info("HambombCache: {} need to be processed were scanned.", mappers.size());

        List<CacheObjectLoader> cacheObjectLoaders = buildLoaders(mappers);

        entityLoaderMap = new HashMap<>(cacheObjectLoaders.size());

        for (CacheObjectLoader cacheObjectLoader : cacheObjectLoaders) {

            if (hambombCacheConfiguration.cacheServerStrategy.equals(CacheServerStrategy.CLUSTER)) {

                KryoSerializationRedisSerializer<Object> kryoSerializationRedisSerializer = new KryoSerializationRedisSerializer();

                RedisValueStorageStrategy<Object> redisValueStorageStrategy = new RedisValueStorageStrategy(kryoSerializationRedisSerializer);

                CacheHandler<Object> redisTemplateCacheHandler = new RedisTemplateCacheHandler(hambombCacheConfiguration.redisTemplate, redisValueStorageStrategy);

                cacheObjectLoader.cacheHandler = redisTemplateCacheHandler;
            }else {
                cacheObjectLoader.cacheHandler = new LocalCacheHandler();
            }

            cacheObjectLoader.loadData(cacheLoaderContext);

            entityLoaderMap.put(cacheObjectLoader.cacheObjectClassName, cacheObjectLoader);
        }

        if (hambombCacheConfiguration.cacheServerStrategy.equals(CacheServerStrategy.CLUSTER)) {

            clusterProcessor.finishDataLoadNode();
        }
    }

    private List<CacheObjectLoader> buildLoaders(Set<Class<? extends CacheObjectMapper>> mappers) {

        return mappers.parallelStream().map((Class<? extends CacheObjectMapper> aClass) -> {

            CacheObjectMapper mapper = null;

            if (applicationContext != null) {
                try {

                    mapper = applicationContext.getBean(aClass);
                } catch (BeansException ex) {
                    LOG.error(ex.getMessage());
                    throw new ConfigurationException(String.format("Bean %s not found.",aClass.getSimpleName()));
                }
            } else {
                try {
                    mapper = aClass.newInstance();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            Method selectAllCacheObject =
                    ReflectionUtils.getMethods(aClass, ReflectionUtils.withName("selectAllCacheObject"))
                            .stream().findFirst().get();

            CacheObjectLoader cacheObjectLoader = new CacheObjectLoader(mapper);

            cacheObjectLoader.cacheObjectClazz = mapper.getSubCacheObjectClass();

            /** 取@的值 */
            Cachekey cachekey = (Cachekey) CacheUtils.getAnnotation(selectAllCacheObject, Cachekey.class);

            if (cachekey == null) {
                LOG.error("The CacheKey is missing from the selectAllCacheObject method implementation");
                throw new HanmbombRuntimeException("The CacheKey is missing from the selectAllCacheObject method implementation.");
            }

            String[] pk = cachekey.primaryKeys();
            String[] fk = cachekey.findKeys();

            IndexRepository indexRepository =
                    IndexRepository.create(mapper.getClass().getSimpleName(), pk, fk,
                            hambombCacheConfiguration.keyGeneratorStrategy,cachekey.peek() == 0 ? fk.length : cachekey.peek());

            indexRepository.keyPermutationCombinationStrategy = cachekey.strategy();

            indexRepository.validate();

            cacheObjectLoader.addIndexFactory(indexRepository);

            cacheObjectLoader.initializeLoader();

            cacheObjectLoader.pk = pk;
            cacheObjectLoader.fk = fk;
            cacheObjectLoader.pkGetter = cacheObjectLoader.buildGetters(pk, null);
            cacheObjectLoader.fkGetter = cacheObjectLoader.buildGetters(fk, null);

            return cacheObjectLoader;

        }).collect(Collectors.toList());

    }

}
