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

import org.hambomb.cache.cluster.HambombCacheConfigForMaster;
import org.hambomb.cache.context.CacheLoaderContext;
import org.hambomb.cache.db.entity.PersonService;
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
@ContextConfiguration(classes = {HambombCacheConfigForMaster.class})
public class TestHambombCacheMaster {

    @Autowired
    private CacheLoaderContext cacheLoaderContext;

    @Autowired
    private PersonService personService;

    @Test
    public void test_HambombCache_afterPropertiesSet() {

        Assert.assertTrue("masterFlag not true",cacheLoaderContext.masterFlag);
        Assert.assertTrue("slave not null",cacheLoaderContext.master != null);

    }

    @Test
    public void test_getPerson() {

        personService.getPerson("mike", 10, "男");
    }
}
