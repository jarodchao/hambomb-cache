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
package org.hambomb.cache.handler;

import org.hambomb.cache.storage.value.RedisValueStorageStrategy;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Nullable;

/**
 * @author: <a herf="mailto:jarodchao@126.com>jarod </a>
 * @date: 2019-02-26
 */
public class RedisTemplateCacheHandler<T> implements CacheHandler<T> {

    private RedisTemplate<String, Object> redisTemplate;

    private RedisValueStorageStrategy valueStorageStrategy;

    public RedisTemplateCacheHandler(RedisTemplate<String, Object> redisTemplate, RedisValueStorageStrategy valueStorageStrategy) {
        this.redisTemplate = redisTemplate;
        this.valueStorageStrategy = valueStorageStrategy;
    }

    @Override
    public void put(String key, T value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public T getRealKey(String key) {

        return (T) redisTemplate.opsForValue().get(key);
    }

    @Override
    public void update(String key, T value) {
        put(key, value);
    }

    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }


    @Override
    public T getIndexKey(String key) {

        String realKey = (String) redisTemplate.opsForValue().get(key);

        return (T) redisTemplate.opsForValue().get(realKey);

    }
}
