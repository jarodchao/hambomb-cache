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

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * @author: <a herf="mailto:jarodchao@126.com>jarod </a>
 * @date: 2019-03-05
 */
public class LocalCacheHandler implements CacheHandler<Object> {

    private Cache<String, Object> cache = CacheBuilder.newBuilder().build();

    @Override

    public void put(String key, Object value) {

        cache.put(key, value);
    }

    @Override
    public Object getRealKey(String key) {
        return cache.getIfPresent(key);
    }

    @Override
    public void update(String key, Object value) {
        cache.put(key, value);
    }

    @Override
    public void delete(String key) {
        cache.invalidate(key);
    }

    @Override
    public Object getIndexKey(String key) {


        String realKey = (String) cache.getIfPresent(key);

        return cache.getIfPresent(realKey);
    }
}
