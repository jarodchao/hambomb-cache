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
import org.hambomb.cache.cluster.HambombCacheConfigForSlave;
import org.hambomb.cache.cluster.node.ClusterRoot;
import org.hambomb.cache.context.CacheLoaderContext;
import org.junit.After;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.raistlic.common.permutation.Combination;
import org.raistlic.common.permutation.Permutation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;

/**
 * @author: <a herf="mailto:jarodchao@126.com>jarod </a>
 * @date: 2019-02-27
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {HambombCacheConfigForSlave.class})
public class TestHambombCacheSlave {

    @Autowired
    private CacheLoaderContext cacheLoaderContext;

    @Autowired
    private ClusterProcessor clusterProcessor;

    @Test
    public void test_HambombCache_afterPropertiesSet() {

        Assert.assertTrue("masterFlag not false",!cacheLoaderContext.masterFlag);
        Assert.assertTrue("slave not null",cacheLoaderContext.slave != null);
        Assert.assertTrue("multicaster is null",cacheLoaderContext.multicaster != null);
        Assert.assertTrue("CacheMasterListener is null",clusterProcessor.getCacheMasterListener() != null);
    }

    @After
    public void tearDown() throws Exception {

        ZkClient zkClient = new ZkClient("localhost:2181", 5000, 5000, new SerializableSerializer());

        if (zkClient.exists(ClusterRoot.getMasterPath())) {
            zkClient.deleteRecursive(ClusterRoot.getMasterPath());
        }

    }
}
