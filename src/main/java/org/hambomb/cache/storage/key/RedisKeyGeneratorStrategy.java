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
package org.hambomb.cache.storage.key;

import java.util.List;

/**
 * @author: <a herf="mailto:jarodchao@126.com>jarod </a>
 * @date: 2019-02-26
 */
public class RedisKeyGeneratorStrategy implements KeyGeneratorStrategy {

    public static final String DEFAULT_SEPARATOR = ":";
    public static final String DEFAULT_P_SEPARATOR = "-";


    String separator;
    String pSeparator;

    public RedisKeyGeneratorStrategy() {
    }

    public RedisKeyGeneratorStrategy(String separator) {
        this.separator = separator;
    }

    @Override
    public String toKey(List<String> keys) {

        return join(keys, separator == null ? DEFAULT_SEPARATOR : separator);
    }

    @Override
    public String toPrimaryKey(List<String> keys) {
        return join(keys, pSeparator == null ? DEFAULT_SEPARATOR : pSeparator);
    }
}
