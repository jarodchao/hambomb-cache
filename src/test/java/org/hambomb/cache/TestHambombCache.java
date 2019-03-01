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

import org.hambomb.cache.storage.RedisKeyCcombinedStrategy;
import org.junit.Before;
import org.junit.Test;
import org.raistlic.common.permutation.Permutation;

import java.util.Arrays;
import java.util.List;

/**
 * @author: <a herf="mailto:jarodchao@126.com>jarod </a>
 * @date: 2019-02-27
 */
public class TestHambombCache {

    Configuration configuration;

    @Before
    public void setUp() throws Exception {
        configuration = new Configuration();
        configuration.strategy = Configuration.CacheServerStrategy.CLUSTER;
        configuration.addScanPackageName("org.hambomb.cache.db.entity");
        configuration.addKeyCombinedStrategy(new RedisKeyCcombinedStrategy());
    }

    @Test
    public void test_Process() throws Exception {

        HambombCache processor = new HambombCache(configuration);
        processor.afterPropertiesSet();
    }

    @Test
    public void test_Combination() throws Exception {

        List<String> findIndexValues = Arrays.asList("1", "15", "Tom");

        for (int i = 1; i <= findIndexValues.size(); i++) {
            Permutation.of(findIndexValues, i).forEach(indexes -> {

                System.out.println("FindKeys:" + indexes);

            });
        }
    }
}
