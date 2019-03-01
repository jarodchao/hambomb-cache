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
import java.time.LocalDateTime;

/**
 * @author: <a herf="mailto:jarodchao@126.com>jarod </a>
 * @date: 2019-02-28
 */
public class CacheMasterLoaderData implements Serializable {

    private static final long serialVersionUID = 8834333684799664822L;

    public static final String path = "/masterdata";

    public static final Integer FINISH_FLAG = 1;
    public static final Integer UNFINISH_FLAG = 0;

    private Integer flag;

    private LocalDateTime startTime;

    private LocalDateTime finishTime;

    private LocalDateTime lastUpdateTime;


    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    public LocalDateTime getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(LocalDateTime finishTime) {
        this.finishTime = finishTime;
    }

    public LocalDateTime getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(LocalDateTime lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public CacheMasterLoaderData() {
        flag = UNFINISH_FLAG;
        startTime = LocalDateTime.now();
    }

    public void finishDataLoad() {
        this.finishTime = LocalDateTime.now();
        this.flag = FINISH_FLAG;
    }
}
