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

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;
import org.hambomb.cache.cluster.ClusterProcessor;
import org.hambomb.cache.cluster.event.CacheLoadInterruptedEvent;
import org.hambomb.cache.cluster.event.CacheLoaderEventMulticaster;
import org.hambomb.cache.cluster.listener.CacheLoadInterruptedListener;
import org.hambomb.cache.cluster.listener.CacheMasterListener;
import org.hambomb.cache.cluster.node.CacheLoaderMaster;
import org.hambomb.cache.context.CacheLoaderContext;
import org.hambomb.cache.handler.CacheHandler;
import org.hambomb.cache.handler.LocalCacheHandler;
import org.hambomb.cache.storage.LocalKeyGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;

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

    ClusterProcessor clusterProcessor;

    CacheLoaderEventMulticaster multicaster;

    IZkDataListener zkDataListener;

    private static final Logger LOG = LoggerFactory.getLogger(HambombCache.class);


    public HambombCache(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        if (StringUtils.isEmpty(configuration.scanPackageName)) {
            LOG.error("Configuration's  scanPackageName is null.");
        }

        if (Configuration.CacheServerStrategy.CLUSTER == configuration.cacheServerStrategy) {
            if (StringUtils.isEmpty(configuration.zkUrl)) {
                LOG.error("Configuration's  zkUrl is null.");
            }

            if (configuration.cacheHandler == null) {
                LOG.error("Configuration's cacheHandler is null.");
            }
        }

        if (configuration.keyGeneratorStrategy == null) {
            LOG.error("Configuration's keyGeneratorStrategy is null.");
        }

        if (configuration.keyPermutationStrategy == null) {
            LOG.error("Configuration's keyPermutationStrategy is null.");
        }

        if (Configuration.CacheServerStrategy.DEVELOP == configuration.cacheServerStrategy) {
            LOG.info("HambombCache will start develop mode.");
        }

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;

        if (Configuration.CacheServerStrategy.CLUSTER.equals(configuration.cacheServerStrategy)) {
            afterClusterCacheLoad();

            CacheLoaderMaster masterFlag = hambombCacheProcessor.fightMaster();

            CacheLoaderContext cacheLoaderContext = createCacheLoaderContext(masterFlag == null ? false : true);

            if (masterFlag == null) {
                LOG.info("Application Server not was a Master Node,HambombCache is stopping.");

                cacheLoaderContext.slave = hambombCacheProcessor.createSlave();

                return;
            } else {
                cacheLoaderContext.master = masterFlag;
            }
        } else if (Configuration.CacheServerStrategy.DEVELOP.equals(configuration.cacheServerStrategy)) {

            afterDevelopCacheLoad();

        }

        createCacheHandler();

        hambombCacheProcessor.startup();
    }

    private void createCacheHandler() {

        CacheHandler cacheHandler;

        if (Configuration.CacheServerStrategy.DEVELOP.equals(configuration.cacheServerStrategy)) {
            cacheHandler = new LocalCacheHandler();

        } else {
            cacheHandler = configuration.cacheHandler;
        }

        hambombCacheProcessor.addCacheHandler(cacheHandler);

    }

    private void afterClusterCacheLoad() {

        zkClient = new ZkClient(configuration.zkUrl, 5000, 5000, new SerializableSerializer());

        multicaster = new CacheLoaderEventMulticaster();
        zkDataListener = new CacheMasterListener(multicaster);

        clusterProcessor = new ClusterProcessor(zkClient, zkDataListener);


        registerBeanObject(ClusterProcessor.class, clusterProcessor);

        hambombCacheProcessor = new HambombCacheProcessor(applicationContext, configuration, clusterProcessor);

        registerBeanObject(HambombCacheProcessor.class, hambombCacheProcessor);

    }

    private void afterDevelopCacheLoad() {

        configuration.keyGeneratorStrategy = new LocalKeyGenerator();


        hambombCacheProcessor = new HambombCacheProcessor(applicationContext, configuration, clusterProcessor);

        registerBeanObject(HambombCacheProcessor.class, hambombCacheProcessor);
    }

    private CacheLoaderContext createCacheLoaderContext(Boolean masterFlag) {

        CacheLoaderContext cacheLoaderContext;

        if (masterFlag) {
            cacheLoaderContext = CacheLoaderContext.createMasterContext(zkClient);
        } else {
            cacheLoaderContext = CacheLoaderContext.createSlaveContext(zkClient);

            CacheLoadInterruptedEvent event = new CacheLoadInterruptedEvent("");
            CacheLoadInterruptedListener listener = new CacheLoadInterruptedListener(zkClient, hambombCacheProcessor);
            cacheLoaderContext.multicaster = multicaster;
            cacheLoaderContext.multicaster.addListener(event, listener);

            registerBeanObject(CacheLoaderEventMulticaster.class, cacheLoaderContext.multicaster);
        }

        registerBeanObject(CacheLoaderContext.class, cacheLoaderContext);

        return cacheLoaderContext;

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
