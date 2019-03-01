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
import org.hambomb.cache.storage.KeyCombinedStrategy;
import sun.security.krb5.Config;

/**
 * Cache加载配置类
 * @author: <a herf="mailto:jarodchao@126.com>jarod </a>
 * @date: 2019-02-26
 */
public class Configuration {

    String scanPackageName;

    String zkUrl;

    CacheServerStrategy strategy = CacheServerStrategy.CLUSTER;

    DataLoadStrategy dataLoadStrategy = DataLoadStrategy.FULL;

    KeyCombinedStrategy keyCombinedStrategy;

    CacheHandler handler;

    Configuration addZKUrl(String zkUrl) {
        this.zkUrl = zkUrl;
        return this;
    }

    Configuration addScanPackageName(String scanPackageName) {
        this.scanPackageName = scanPackageName;
        return this;
    }

    Configuration addKeyCombinedStrategy(KeyCombinedStrategy keyCombinedStrategy) {
        this.keyCombinedStrategy = keyCombinedStrategy;
        return this;
    }

    Configuration addHandler(CacheHandler cacheHandler) {
        this.handler = handler;
        return this;
    }

    enum CacheServerStrategy {

        STANDALONE,CLUSTER;
    }

    enum DataLoadStrategy {
        FULL,INCREMENT;
    }
}