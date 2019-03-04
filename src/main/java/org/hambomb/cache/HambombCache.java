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

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;
import org.hambomb.cache.cluster.ClusterProcessor;
import org.hambomb.cache.cluster.event.CacheLoadInterruptedEvent;
import org.hambomb.cache.cluster.listener.CacheLoadInterruptedListener;
import org.hambomb.cache.context.CacheLoaderContext;
import org.hambomb.cache.db.entity.CacheObjectMapper;
import org.hambomb.cache.db.entity.Cachekey;
import org.hambomb.cache.db.entity.EntityLoader;
import org.hambomb.cache.db.entity.MapperScanner;
import org.hambomb.cache.handler.CacheHandler;
import org.hambomb.cache.index.IndexFactory;
import org.hambomb.cache.storage.RedisKeyCcombinedStrategy;
import org.reflections.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.reflections.ReflectionUtils.*;

/**
 * @author: <a herf="mailto:jarodchao@126.com>jarod </a>
 * @date: 2019-02-26
 */
public class HambombCache implements ApplicationContextAware, InitializingBean, BeanFactoryPostProcessor {


    ApplicationContext applicationContext;

    Configuration configuration;

    HambombCacheProcessor hambombCacheProcessor;

    BeanFactory beanFactory;

    ZkClient zkClient;

    private static final Logger LOG = LoggerFactory.getLogger(HambombCache.class);


    public HambombCache(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        LOG.info("HambombCache afterPropertiesSet");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        LOG.info("HambombCache setApplicationContext");
        this.applicationContext = applicationContext;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        LOG.info("HambombCache postProcessBeanFactory");
        this.beanFactory = beanFactory;

        if (Configuration.CacheServerStrategy.CLUSTER == configuration.strategy) {
            afterClusterCacheLoad();

            Boolean masterFlag = hambombCacheProcessor.fightMaster();

            createCacheLoaderContext(masterFlag);

            if (!masterFlag) {
                LOG.info("Application Server not was a Master Node,HambombCache is stopping.");
                return;
            }
        }

        hambombCacheProcessor.startup();
    }

    private void afterClusterCacheLoad() {

        zkClient = new ZkClient(configuration.zkUrl, 5000, 5000, new SerializableSerializer());
        ClusterProcessor clusterProcessor = null;
        try {
            clusterProcessor = new ClusterProcessor(zkClient);
        } catch (IOException e) {
            e.printStackTrace();
        }


        registerBeanObject(ClusterProcessor.class, clusterProcessor);


        hambombCacheProcessor = new HambombCacheProcessor(applicationContext, configuration, clusterProcessor);

        registerBeanObject(HambombCacheProcessor.class, hambombCacheProcessor);

    }

    private void createCacheLoaderContext(Boolean masterFlag) {

        CacheLoaderContext cacheLoaderContext;

        if (masterFlag) {
            cacheLoaderContext = CacheLoaderContext.createMasterContext(zkClient);
        } else {
            cacheLoaderContext = CacheLoaderContext.createSlaveContext(zkClient);

            CacheLoadInterruptedEvent event = new CacheLoadInterruptedEvent("");
            CacheLoadInterruptedListener listener = new CacheLoadInterruptedListener(zkClient, hambombCacheProcessor);
            cacheLoaderContext.multicaster.addListener(event, listener);
        }

        registerBeanObject(CacheLoaderContext.class, cacheLoaderContext);

    }

    private void registerBeanObject(Class<?> clazz, Object object) {

        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) beanFactory;

        defaultListableBeanFactory.registerSingleton(toBeanName(clazz), object);

    }

    private String toBeanName(Class<?> clazz) {

        String beanName = clazz.getSimpleName();

        String f = beanName.substring(0, 1);
        String s = beanName.substring(1, beanName.length());

        return f.toLowerCase() + s;
    }
}
