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
package org.hambomb.cache.db.entity;

import com.google.common.reflect.TypeToken;

import java.util.List;

/**
 * 加载CacheObject的Mapper接口
 * 对于需要加载到cache中的数据库表实体对象需要实现此接口
 * @author: <a herf="mailto:jarodchao@126.com>jarod </a>
 * @date: 2019-02-25
 */
public interface CacheObjectMapper<T> {

    /**
     * 查询所有需要Cache的对象
     * @return
     */
    List<T> selectAllCacheObject();

    default Class<T> getSubEntityClass() {
        TypeToken<T> typeToken = new TypeToken<T>(getClass()) { };

        return (Class<T>) typeToken.getRawType();
    }
}
