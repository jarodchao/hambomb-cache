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

import org.hambomb.cache.cluster.HambombCacheConfigForSlave;
import org.hambomb.cache.cluster.event.CacheLoadInterruptedEvent;
import org.hambomb.cache.cluster.node.CacheMasterLoaderData;
import org.hambomb.cache.cluster.node.ClusterRoot;
import org.hambomb.cache.context.CacheLoaderContext;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author: <a herf="mailto:jarodchao@126.com>jarod </a>
 * @date: 2019-02-27
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {HambombCacheConfigForSlave.class})
public class TestHambombCacheMasterForInterrupt {

    @Autowired
    private CacheLoaderContext cacheLoaderContext;

    @Test
    public void test_HambombCache_afterPropertiesSet() {

        CacheMasterLoaderData data = cacheLoaderContext.zkClient.readData(ClusterRoot.getMasterData());
        data.setFlag(CacheMasterLoaderData.UNFINISH_FLAG);
        cacheLoaderContext.zkClient.writeData(ClusterRoot.getMasterData(), data);
        cacheLoaderContext.zkClient.delete(ClusterRoot.getMasterPath());
        cacheLoaderContext.multicaster.publishEvent(new CacheLoadInterruptedEvent("test"));
        data = cacheLoaderContext.zkClient.readData(ClusterRoot.getMasterData());

        Assert.assertTrue("未完成加载",data.getFlag() != CacheMasterLoaderData.FINISH_FLAG);


    }
}
