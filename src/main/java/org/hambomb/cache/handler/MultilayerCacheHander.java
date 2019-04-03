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

import org.hambomb.cache.context.CacheLoaderContext;
import org.springframework.util.StringUtils;

/**
 * @author: <a herf="mailto:jarodchao@126.com>jarod </a>
 * @date: 2019-04-02
 */
public class MultilayerCacheHander<T> implements CacheHandler<T> {

    private CacheHandler firstCacheHandler;

    private CacheHandler secondCacheHandler;

    private CacheLoaderContext cacheLoaderContext;

    public MultilayerCacheHander(CacheHandler firstCacheHandler, CacheHandler secondCacheHandler) {
        this.firstCacheHandler = firstCacheHandler;
        this.secondCacheHandler = secondCacheHandler;
    }

    public MultilayerCacheHander(CacheHandler firstCacheHandler, CacheHandler secondCacheHandler, CacheLoaderContext cacheLoaderContext) {
        this.firstCacheHandler = firstCacheHandler;
        this.secondCacheHandler = secondCacheHandler;
        this.cacheLoaderContext = cacheLoaderContext;
    }

    @Override
    public void put(String key, T value) {

        firstCacheHandler.put(key, value);

        secondCacheHandler.put(key, value);
    }

    @Override
    public T getByRealKey(String key) {

        T t = (T) firstCacheHandler.getByRealKey(key);

        if (t == null) {
            t = (T)secondCacheHandler.getByRealKey(key);

            if (t == null) {

                firstCacheHandler.put(key, t);
            }

        }

        return t;
    }

    @Override
    public T getByIndexKey(String key) {

        T t = (T) firstCacheHandler.getByIndexKey(key);

        if (t == null) {
            t = (T) secondCacheHandler.getByIndexKey(key);

            if (t != null) {
                String realKey = secondCacheHandler.getRealKeyByIndexKey(key);

                firstCacheHandler.put(key, realKey);
                firstCacheHandler.put(realKey, t);
            }
        }

        return t;
    }

    @Override
    public void update(String key, T value) {
        put(key, value);
    }

    @Override
    public void delete(String key) {
        firstCacheHandler.delete(key);
        secondCacheHandler.delete(key);
    }

    @Override
    public void load(String key, T value) {

        firstCacheHandler.put(key, value);

        if (cacheLoaderContext.masterFlag) {
            secondCacheHandler.put(key, value);
        }
    }


    @Override
    public String getRealKeyByIndexKey(String key) {

        String realKey = firstCacheHandler.getRealKeyByIndexKey(key);
        return !StringUtils.isEmpty(realKey) ? realKey : secondCacheHandler.getRealKeyByIndexKey(key);
    }
}
