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
import org.hambomb.cache.db.entity.CacheObjectMapper;
import org.hambomb.cache.db.entity.Cachekey;
import org.hambomb.cache.db.entity.EntityLoader;
import org.hambomb.cache.db.entity.MapperScanner;
import org.hambomb.cache.handler.CacheHandler;
import org.hambomb.cache.handler.LocalCacheHandler;
import org.hambomb.cache.handler.RedisTemplateCacheHandler;
import org.hambomb.cache.index.IndexRepository;
import org.hambomb.cache.storage.value.KryoSerializationRedisSerializer;
import org.hambomb.cache.storage.value.RedisValueStorageStrategy;
import org.reflections.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private Map<String, EntityLoader> entityLoaderMap;

    private MapperScanner scanner;

    private Set<Class<? extends CacheObjectMapper>> mappers;

    private static final Logger LOG = LoggerFactory.getLogger(HambombCache.class);

    private ClusterProcessor clusterProcessor;

    RedisTemplate<String, Object> redisTemplate;

    public HambombCacheProcessor(ApplicationContext applicationContext, HambombCacheConfiguration hambombCacheConfiguration, ClusterProcessor clusterProcessor) {
        this.applicationContext = applicationContext;
        this.hambombCacheConfiguration = hambombCacheConfiguration;
        this.clusterProcessor = clusterProcessor;
    }

    public void startup() {

        startLoader();

    }

    public EntityLoader getEntityLoader(String key) {

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

        if (hambombCacheConfiguration.scanPackageName != null && !"".equals(hambombCacheConfiguration.scanPackageName)) {

            scanner = new MapperScanner(hambombCacheConfiguration.scanPackageName);

            mappers = scanner.scanMapper();
        }

        List<EntityLoader> entityLoaders = buildLoaders(mappers);

        entityLoaderMap = new HashMap<>(entityLoaders.size());

        for (EntityLoader entityLoader : entityLoaders) {

            if (hambombCacheConfiguration.cacheServerStrategy.equals(CacheServerStrategy.CLUSTER)) {

                KryoSerializationRedisSerializer<Object> kryoSerializationRedisSerializer = new KryoSerializationRedisSerializer();

                RedisValueStorageStrategy<Object> redisValueStorageStrategy = new RedisValueStorageStrategy(kryoSerializationRedisSerializer);

                CacheHandler<Object> redisTemplateCacheHandler = new RedisTemplateCacheHandler(hambombCacheConfiguration.redisTemplate, redisValueStorageStrategy);

                entityLoader.cacheHandler = redisTemplateCacheHandler;
            }else {
                entityLoader.cacheHandler = new LocalCacheHandler();
            }

            entityLoader.loadData();

            entityLoaderMap.put(entityLoader.entityClassName, entityLoader);
        }

        if (hambombCacheConfiguration.cacheServerStrategy.equals(CacheServerStrategy.CLUSTER)) {

            clusterProcessor.finishDataLoadNode();
        }
    }

    private List<EntityLoader> buildLoaders(Set<Class<? extends CacheObjectMapper>> mappers) {

        return mappers.parallelStream().map((Class<? extends CacheObjectMapper> aClass) -> {

            CacheObjectMapper mapper = null;

            if (applicationContext != null) {
                mapper = applicationContext.getBean(aClass);
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

            EntityLoader entityLoader = new EntityLoader(mapper);

            Class entityClass = mapper.getSubEntityClass();

            /** 取@的值 */
            Cachekey cachekey = (Cachekey) CacheUtils.getAnnotation(selectAllCacheObject, Cachekey.class);

            if (cachekey == null) {
                LOG.error("The CacheKey is missing from the selectAllCacheObject method implementation");
            }

            String[] pk = cachekey.primaryKeys();
            String[] fk = cachekey.findKeys();

            IndexRepository indexRepository =
                    IndexRepository.create(mapper.getClass().getSimpleName(), pk, fk,
                            hambombCacheConfiguration.keyGeneratorStrategy,cachekey.peek() == 0 ? fk.length : cachekey.peek());

            indexRepository.keyPermutationCombinationStrategy = cachekey.strategy();

            indexRepository.validate();

            entityLoader.addIndexFactory(indexRepository);

            entityLoader.initializeLoader();

            for (String p : pk) {

                entityLoader.addPkGetter(CacheUtils.getGetterMethod(p, entityClass));
            }

            for (String f : fk) {

                entityLoader.addFkGetter(CacheUtils.getGetterMethod(f, entityClass));
            }

            return entityLoader;

        }).collect(Collectors.toList());

    }

}
