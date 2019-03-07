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
package org.hambomb.cache.cluster.node;

import java.io.Serializable;
import java.util.List;

/**
 * @author: <a herf="mailto:jarodchao@126.com>jarod </a>
 * @date: 2019-02-28
 */
public class ClusterRoot implements Serializable {


    private static final long serialVersionUID = -5417109546000664789L;

    private static final String path = "/HamCache";

    private CacheLoaderMaster master;

    private List<CacheLoaderSlave> slaves;

    private CacheData cacheData;

    public CacheLoaderMaster getMaster() {
        return master;
    }

    public void setMaster(CacheLoaderMaster master) {
        this.master = master;
    }

    public List<CacheLoaderSlave> getSlaves() {
        return slaves;
    }

    public void setSlaves(List<CacheLoaderSlave> slaves) {
        this.slaves = slaves;
    }

    public CacheData getCacheData() {
        return cacheData;
    }

    public void setCacheData(CacheData cacheData) {
        this.cacheData = cacheData;
    }

    public static String getRootPath() {

        return path;
    }

    public static String getMasterPath() {
        StringBuilder stringBuilder = new StringBuilder(path);

        return stringBuilder.append(CacheLoaderMaster.path).toString();

    }

    public static String getSlavesPath() {
        StringBuilder stringBuilder = new StringBuilder(path);

        return stringBuilder.append(CacheLoaderSlave.path).toString();
    }

    public static String getSlavePath() {
        StringBuilder stringBuilder = new StringBuilder(path);

        stringBuilder.append(CacheLoaderSlave.path);

        return stringBuilder.append(CacheLoaderSlave.subPath).toString();
    }

    public static String getDataPath() {
        StringBuilder stringBuilder = new StringBuilder(path);

        return stringBuilder.append(CacheData.path).toString();
    }

    public static String getMasterData() {

        StringBuilder stringBuilder = new StringBuilder(getDataPath());

        return stringBuilder.append(CacheMasterLoaderData.path).toString();
    }

    public static String getUpdateData() {

        StringBuilder stringBuilder = new StringBuilder(getDataPath());

        return stringBuilder.append(CacheUpdateData.path).toString();

    }
}
