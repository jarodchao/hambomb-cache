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
package org.hambomb.cache.cluster;

import org.hambomb.cache.HambombCache;
import org.hambomb.cache.handler.LocalCacheHandler;
import org.hambomb.cache.storage.RedisKeyGeneratorStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author: <a herf="mailto:jarodchao@126.com>jarod </a>
 * @date: 2019-03-04
 */
@Configuration
@EnableAspectJAutoProxy
@ComponentScan(basePackages = {"org.hambomb.cache"})
public class HambombCacheConfigForMaster {


    @Bean
    public HambombCache hambombCache() {

        org.hambomb.cache.Configuration configuration = new org.hambomb.cache.Configuration();

        configuration.addCacheServerStrategy(org.hambomb.cache.Configuration.CacheServerStrategy.CLUSTER)
                .addScanPackageName("org.hambomb.cache.db.entity")
                .addKeyGeneratorStrategy(new RedisKeyGeneratorStrategy())
                .addCacheHandler(new LocalCacheHandler())
                .addZKUrl("localhost:2181");

        HambombCache hambombCache = new HambombCache(configuration);

        return hambombCache;

    }
}
