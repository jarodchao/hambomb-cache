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
package org.hambomb.cache.storage.value;

import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * @author: <a herf="mailto:jarodchao@126.com>jarod </a>
 * @date: 2019-02-26
 */
public class RedisValueStorageStrategy<T> implements ValueStorageStrategy<T> {


    private RedisSerializer<T> redisSerializer;

    public RedisValueStorageStrategy(RedisSerializer<T> redisSerializer) {
        this.redisSerializer = redisSerializer;
    }

    @Override
    public byte[] serialize(T t) {
        return redisSerializer.serialize(t);
    }

    @Override
    public T deserialize(byte[] bytes) {
        return redisSerializer.deserialize(bytes);
    }
}