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
package org.hambomb.cache.context;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;
import org.hambomb.cache.HambombCache;
import org.hambomb.cache.cluster.event.CacheLoaderEventMulticaster;
import org.hambomb.cache.cluster.listener.CacheMasterListener;
import org.hambomb.cache.cluster.node.CacheLoaderMaster;
import org.hambomb.cache.cluster.node.CacheLoaderSlave;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: <a herf="mailto:jarodchao@126.com>jarod </a>
 * @date: 2019-03-01
 */
public class CacheLoaderContext {


    public boolean masterFlag = false;

    public CacheLoaderMaster master;

    public CacheLoaderSlave slave;

    public IZkDataListener cacheMasterListener;

    public CacheLoaderEventMulticaster multicaster;

    public ZkClient zkClient;

    public CacheServerStrategy cacheServerStrategy;

    private static final Logger LOG = LoggerFactory.getLogger(CacheLoaderContext.class);


    public static CacheLoaderContext createMasterContext(ZkClient zkClient) {

        LOG.info("==============================Build master context is starting ");
        CacheLoaderContext context = new CacheLoaderContext();

        context.masterFlag = true;
        context.master = new CacheLoaderMaster();
        context.zkClient = zkClient;
        return context;
    }

    public static CacheLoaderContext createSlaveContext(ZkClient zkClient) {

        LOG.info("==============================Build slave context is starting ");

        CacheLoaderContext context = new CacheLoaderContext();

        context.slave = new CacheLoaderSlave();
        CacheLoaderEventMulticaster multicaster = new CacheLoaderEventMulticaster();
        context.cacheMasterListener = new CacheMasterListener(multicaster);
        context.multicaster = multicaster;
        context.zkClient = zkClient;
        return context;
    }


}
