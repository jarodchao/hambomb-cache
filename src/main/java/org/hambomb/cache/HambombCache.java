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
import org.hambomb.cache.storage.key.LocalKeyGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.util.StringUtils;

/**
 * @author: <a herf="mailto:jarodchao@126.com>jarod </a>
 * @date: 2019-02-26
 */
public class HambombCache implements ApplicationContextAware, InitializingBean, ApplicationListener<ContextRefreshedEvent> {


    ApplicationContext applicationContext;

    HambombCacheConfiguration hambombCacheConfiguration;

    HambombCacheProcessor hambombCacheProcessor;

    ConfigurableListableBeanFactory beanFactory;

    ZkClient zkClient;

    ClusterProcessor clusterProcessor;

    CacheLoaderEventMulticaster multicaster;

    IZkDataListener zkDataListener;

    private static final Logger LOG = LoggerFactory.getLogger(HambombCache.class);


    public HambombCache(HambombCacheConfiguration hambombCacheConfiguration) {
        this.hambombCacheConfiguration = hambombCacheConfiguration;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        if (StringUtils.isEmpty(hambombCacheConfiguration.scanPackageName)) {
            LOG.error("HambombCacheConfiguration's  scanPackageName is null.");
        }

        if (CacheServerStrategy.CLUSTER == hambombCacheConfiguration.cacheServerStrategy) {
            if (StringUtils.isEmpty(hambombCacheConfiguration.zkUrl)) {
                LOG.error("HambombCacheConfiguration's  zkUrl is null.");
            }

        }

        if (hambombCacheConfiguration.keyGeneratorStrategy == null) {
            LOG.error("HambombCacheConfiguration's keyGeneratorStrategy is null.");
        }

        if (CacheServerStrategy.DEVELOP == hambombCacheConfiguration.cacheServerStrategy) {
            LOG.info("HambombCache will start develop mode.");
        }

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        if (CacheServerStrategy.CLUSTER.equals(hambombCacheConfiguration.cacheServerStrategy)) {

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
        } else if (CacheServerStrategy.DEVELOP.equals(hambombCacheConfiguration.cacheServerStrategy)) {

            afterDevelopCacheLoad();

        }

        hambombCacheProcessor.startup();
    }

    private void afterClusterCacheLoad() {

        zkClient = new ZkClient(hambombCacheConfiguration.zkUrl, 5000, 5000, new SerializableSerializer());

        multicaster = new CacheLoaderEventMulticaster();
        zkDataListener = new CacheMasterListener(multicaster);

        clusterProcessor = new ClusterProcessor(zkClient, zkDataListener);


        registerBeanObject(ClusterProcessor.class, clusterProcessor);

        hambombCacheProcessor = new HambombCacheProcessor(applicationContext, hambombCacheConfiguration, clusterProcessor);

        registerBeanObject(HambombCacheProcessor.class, hambombCacheProcessor);

    }

    private void afterDevelopCacheLoad() {

        hambombCacheConfiguration.keyGeneratorStrategy = new LocalKeyGenerator();


        hambombCacheProcessor = new HambombCacheProcessor(applicationContext, hambombCacheConfiguration, clusterProcessor);

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

        if (beanFactory == null) {
            ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) applicationContext;

            beanFactory = configurableApplicationContext.getBeanFactory();

        }

        beanFactory.registerSingleton(toBeanName(clazz), object);

    }

    private String toBeanName(Class<?> clazz) {

        String beanName = clazz.getSimpleName();

        String f = beanName.substring(0, 1);
        String s = beanName.substring(1, beanName.length());

        return f.toLowerCase() + s;
    }
}
