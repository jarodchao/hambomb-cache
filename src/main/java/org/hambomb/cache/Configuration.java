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

import org.hambomb.cache.handler.CacheHandler;
import org.hambomb.cache.handler.LocalCacheHandler;
import org.hambomb.cache.storage.KeyGeneratorStrategy;
import org.hambomb.cache.storage.KeyPermutationStrategy;

/**
 * Cache加载配置类
 * @author: <a herf="mailto:jarodchao@126.com>jarod </a>
 * @date: 2019-02-26
 */
public class Configuration {

    public String scanPackageName;

    public String zkUrl;

    public CacheServerStrategy cacheServerStrategy = CacheServerStrategy.STANDALONE;

    public DataLoadStrategy dataLoadStrategy = DataLoadStrategy.FULL;

    public KeyGeneratorStrategy keyGeneratorStrategy;

    public KeyPermutationStrategy keyPermutationStrategy = KeyPermutationStrategy.NONPERMUTATION;

    public CacheHandler cacheHandler = new LocalCacheHandler();

    public Configuration addCacheServerStrategy(CacheServerStrategy cacheServerStrategy) {
        this.cacheServerStrategy = cacheServerStrategy;
        return this;
    }

    public Configuration addZKUrl(String zkUrl) {
        this.zkUrl = zkUrl;
        return this;
    }

    public Configuration addScanPackageName(String scanPackageName) {
        this.scanPackageName = scanPackageName;
        return this;
    }

    public Configuration addKeyGeneratorStrategy(KeyGeneratorStrategy keyGeneratorStrategy) {
        this.keyGeneratorStrategy = keyGeneratorStrategy;
        return this;
    }

    public Configuration addCacheHandler(CacheHandler cacheHandler) {
        this.cacheHandler = cacheHandler;
        return this;
    }

    public enum CacheServerStrategy {

        STANDALONE,CLUSTER,DEVELOP;
    }

    enum DataLoadStrategy {
        FULL,INCREMENT;
    }
}
