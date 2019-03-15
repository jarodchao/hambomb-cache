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
package org.hambomb.cache.examples.config;

import org.hambomb.cache.CacheServerStrategy;
import org.hambomb.cache.HambombCache;
import org.hambomb.cache.HambombCacheConfiguration;
import org.hambomb.cache.storage.key.RedisKeyGeneratorStrategy;
import org.hambomb.cache.storage.value.KryoSerializationRedisSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author: <a herf="mailto:jarodchao@126.com>jarod </a>
 * @date: 2019-03-12
 */
@Configuration
@ComponentScan(basePackages = {"org.hambomb.cache"})
public class RedisClusterConfig {

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {

        return new LettuceConnectionFactory(new RedisStandaloneConfiguration("localhost", 6379));
    }


    @Bean
    @Autowired
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setEnableDefaultSerializer(false);
        template.setValueSerializer(new KryoSerializationRedisSerializer());
        template.setKeySerializer(new KryoSerializationRedisSerializer<>());
//        template.setDefaultSerializer(new KryoSerializationRedisSerializer());
//        template.setHashValueSerializer(new KryoSerializationRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    @Autowired
    public HambombCacheConfiguration hambombCacheConfig(RedisTemplate<String, Object> redisTemplate) {
        HambombCacheConfiguration hambombCacheConfiguration = new HambombCacheConfiguration();
        hambombCacheConfiguration.addScanPackageName("org.hambomb.cache.examples.mapper");
        hambombCacheConfiguration.addZKUrl("localhost:2181");
        hambombCacheConfiguration.addKeyGeneratorStrategy(new RedisKeyGeneratorStrategy());
        hambombCacheConfiguration.addCacheServerStrategy(CacheServerStrategy.CLUSTER);
        hambombCacheConfiguration.redisTemplate = redisTemplate;

        return hambombCacheConfiguration;
    }

    @Bean
    @Autowired
    public HambombCache hambombCache(HambombCacheConfiguration hambombCacheConfiguration) {

        HambombCache hambombCache = new HambombCache(hambombCacheConfiguration);
        return hambombCache;
    }
}
