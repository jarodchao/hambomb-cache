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
package org.hambomb.cache.autoconfigure;

import org.hambomb.cache.context.CacheServerStrategy;
import org.hambomb.cache.HambombCache;
import org.hambomb.cache.context.HambombCacheConfiguration;
import org.hambomb.cache.storage.key.RedisKeyGeneratorStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author: <a herf="mailto:jarodchao@126.com>jarod </a>
 * @date: 2019-03-14
 */
@ComponentScan(basePackages = {"org.hambomb.cache"})
@EnableConfigurationProperties(HambombCacheProperties.class)
public class HambombCacheAutoConfiguration {

    @Autowired
    private HambombCacheProperties hambombCacheProperties;


    @Bean
    @ConditionalOnBean(name = "hambombCacheRedisTemplate")
    public HambombCacheConfiguration hambombCacheConfiguration(RedisTemplate<String, Object> redisTemplate) {

        HambombCacheConfiguration hambombCacheConfiguration = new HambombCacheConfiguration();
        hambombCacheConfiguration.addScanPackageName(hambombCacheProperties.getScanPackageName());
        hambombCacheConfiguration.addCacheServerStrategy(hambombCacheProperties.getCacheServerStrategy());

        if (hambombCacheProperties.getCacheServerStrategy().equals(CacheServerStrategy.CLUSTER)) {
            hambombCacheConfiguration.addZKUrl(hambombCacheProperties.getZkUrl());
            hambombCacheConfiguration.addKeyGeneratorStrategy(new RedisKeyGeneratorStrategy());
            hambombCacheConfiguration.redisTemplate = redisTemplate;
        }

        return hambombCacheConfiguration;
    }

    @Bean
    @ConditionalOnMissingBean(name = "hambombCacheRedisTemplate")
    public HambombCacheConfiguration hambombCacheConfiguration() {

        HambombCacheConfiguration hambombCacheConfiguration = new HambombCacheConfiguration();
        hambombCacheConfiguration.addScanPackageName(hambombCacheProperties.getScanPackageName());
        hambombCacheConfiguration.addCacheServerStrategy(hambombCacheProperties.getCacheServerStrategy());

        return hambombCacheConfiguration;
    }


    @Bean
    @ConditionalOnBean(name = "hambombCacheConfiguration")
    public HambombCache hambombCache(HambombCacheConfiguration hambombCacheConfiguration) {

        HambombCache hambombCache = new HambombCache(hambombCacheConfiguration);
        return hambombCache;
    }

    @Bean
    @ConditionalOnMissingBean(name = "hambombCache")
    public HambombCache hambombCache() {

        HambombCacheConfiguration hambombCacheConfiguration = new HambombCacheConfiguration();
        hambombCacheConfiguration.addScanPackageName(hambombCacheProperties.getScanPackageName());
        hambombCacheConfiguration.addCacheServerStrategy(hambombCacheProperties.getCacheServerStrategy());

        if (hambombCacheProperties.getCacheServerStrategy().equals(CacheServerStrategy.CLUSTER)) {
            hambombCacheConfiguration.addZKUrl(hambombCacheProperties.getZkUrl());
            hambombCacheConfiguration.addKeyGeneratorStrategy(new RedisKeyGeneratorStrategy());
        }


        HambombCache hambombCache = new HambombCache(hambombCacheConfiguration);
        return hambombCache;
    }


}
