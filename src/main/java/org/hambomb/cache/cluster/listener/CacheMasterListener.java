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
package org.hambomb.cache.cluster.listener;

import org.I0Itec.zkclient.IZkDataListener;
import org.hambomb.cache.cluster.event.CacheLoadInterruptedEvent;
import org.hambomb.cache.cluster.event.CacheLoaderEventMulticaster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: <a herf="mailto:jarodchao@126.com>jarod </a>
 * @date: 2019-03-01
 */
public class CacheMasterListener implements IZkDataListener {

    private static final Logger LOG = LoggerFactory.getLogger(CacheMasterListener.class);

    CacheLoaderEventMulticaster multicaster;


    public CacheMasterListener(CacheLoaderEventMulticaster multicaster) {
        this.multicaster = multicaster;
    }

    @Override
    public void handleDataChange(String dataPath, Object data) throws Exception {

    }

    @Override
    public void handleDataDeleted(String dataPath) throws Exception {

        LOG.info("Master Node of Cache Loader was shutdown!");

        CacheLoadInterruptedEvent event = new CacheLoadInterruptedEvent("Master was shutdown.");


        multicaster.publishEvent(event);

    }
}
