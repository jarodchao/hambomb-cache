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

import org.hambomb.cache.storage.value.KryoSerializationRedisSerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author: <a herf="mailto:jarodchao@126.com>jarod </a>
 * @date: 2019-03-15
 */
@ConditionalOnProperty(prefix = "hambomb.cache", name = "cacheServerStrategy", havingValue = "cluster")
public class HambombCacheRedisConfig {

    @Bean(name = "hambombCacheRedisTemplate")
    public RedisTemplate<String, Object> hambombCacheRedisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setEnableDefaultSerializer(false);
        template.setValueSerializer(new KryoSerializationRedisSerializer());
        template.setKeySerializer(new KryoSerializationRedisSerializer<>());
        template.afterPropertiesSet();
        return template;
    }
}
