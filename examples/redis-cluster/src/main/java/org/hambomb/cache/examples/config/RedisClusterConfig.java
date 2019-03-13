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

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hambomb.cache.Configuration;
import org.hambomb.cache.HambombCache;
import org.hambomb.cache.handler.CacheHandler;
import org.hambomb.cache.handler.LocalCacheHandler;
import org.hambomb.cache.handler.RedisTemplateCacheHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author: <a herf="mailto:jarodchao@126.com>jarod </a>
 * @date: 2019-03-12
 */
@org.springframework.context.annotation.Configuration
@ComponentScan(basePackages = {"org.hambomb.cache"})
public class RedisClusterConfig {

//    @Bean
//    public RedisConnectionFactory redisConnectionFactory() {
//
//        return new LettuceConnectionFactory(new RedisStandaloneConfiguration("localhost", 6379));
//    }


    @Bean
    @Autowired
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(factory);
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        template.setValueSerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    @Autowired
    public Configuration hambombCacheConfig(RedisTemplate<String, String> redisTemplate) {
        Configuration configuration = new Configuration();
        configuration.addScanPackageName("org.hambomb.cache.examples.mapper");
        configuration.addCacheServerStrategy(Configuration.CacheServerStrategy.DEVELOP);

        CacheHandler cacheHandler = new RedisTemplateCacheHandler(redisTemplate, null);
        configuration.addCacheHandler(cacheHandler);

        return configuration;
    }

    @Bean
    @Autowired
    public HambombCache hambombCache(Configuration configuration) {

        HambombCache hambombCache = new HambombCache(configuration);
        return hambombCache;
    }
}
