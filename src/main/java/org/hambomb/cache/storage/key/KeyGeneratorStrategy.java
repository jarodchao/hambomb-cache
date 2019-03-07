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

import java.util.*;

/**
 * NoSQL Key生存策略
 * @author: <a herf="matilto:jarodchao@126.com>jarod </a>
 * @date: 2019-02-26
 */
public interface KeyGeneratorStrategy {

    default String join(Iterable<String> iterable, String separator) {

        return iterable == null ? null : join(iterable.iterator(), separator);
    }

    default String join(Iterator<?> iterator, String separator) {
        if(iterator == null) {
            return null;
        } else if(!iterator.hasNext()) {
            return "";
        } else {
            Object first = iterator.next();
            if(!iterator.hasNext()) {
                return Objects.toString(first, "");
            } else {
                StringBuilder buf = new StringBuilder(256);
                if(first != null) {
                    buf.append(first);
                }

                while(iterator.hasNext()) {

                    Object obj = iterator.next();

                    if(separator != null && obj != null) {
                        buf.append(separator);
                    }

                    if(obj != null) {
                        buf.append(obj);
                    }
                }

                return buf.toString();
            }
        }
    }

    String toKey(List<String> keys);

    String toPrimaryKey(List<String> keys);

}
