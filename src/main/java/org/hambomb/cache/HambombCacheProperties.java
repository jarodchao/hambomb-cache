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

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author: <a herf="mailto:jarodchao@126.com>jarod </a>
 * @date: 2019-03-14
 */
@ConfigurationProperties(prefix = "hambomb.cache")
public class HambombCacheProperties {

    private String scanPackageName;

    private String zkUrl;

    private CacheServerStrategy cacheServerStrategy = CacheServerStrategy.STANDALONE;

    private DataLoadStrategy dataLoadStrategy = DataLoadStrategy.FULL;

    public HambombCacheProperties() {
        System.out.println("HambombCacheProperties");
    }

    public CacheServerStrategy getCacheServerStrategy() {
        return cacheServerStrategy;
    }

    public void setCacheServerStrategy(CacheServerStrategy cacheServerStrategy) {
        this.cacheServerStrategy = cacheServerStrategy;
    }

    public DataLoadStrategy getDataLoadStrategy() {
        return dataLoadStrategy;
    }

    public void setDataLoadStrategy(DataLoadStrategy dataLoadStrategy) {
        this.dataLoadStrategy = dataLoadStrategy;
    }

    public String getScanPackageName() {
        return scanPackageName;
    }

    public void setScanPackageName(String scanPackageName) {
        this.scanPackageName = scanPackageName;
    }

    public String getZkUrl() {
        return zkUrl;
    }

    public void setZkUrl(String zkUrl) {
        this.zkUrl = zkUrl;
    }
}
