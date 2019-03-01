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
package org.hambomb.cache.index;

import org.hambomb.cache.storage.KeyCombinedStrategy;
import com.google.common.collect.Lists;
import org.raistlic.common.permutation.Combination;
import org.raistlic.common.permutation.Permutation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: <a herf="mailto:jarodchao@126.com>jarod </a>
 * @date: 2019-02-26
 */
public class IndexFactory {

    String loaderName;

    public String[] primaryIndex;

    public String[] indexKeys;

    public String uniqueKey;

    public Map<Object, Object> lookup;

    public KeyCombinedStrategy keyCombinedStrategy;


    public static IndexFactory create(String loaderName, String[] primaryIndex, String[] indexKeys,
                                      KeyCombinedStrategy keyCombinedStrategy) {

        return new IndexFactory(loaderName, primaryIndex, indexKeys, keyCombinedStrategy);
    }


    public IndexFactory(String loaderName, String[] primaryIndex, String[] indexKeys, KeyCombinedStrategy keyCombinedStrategy) {
        this.loaderName = loaderName;
        this.primaryIndex = primaryIndex;
        this.indexKeys = indexKeys;
        this.keyCombinedStrategy = keyCombinedStrategy;

    }

    public String buildUniqueKey(String[] primaryIndexValues){
        uniqueKey = keyCombinedStrategy.toPrimaryKey(Lists.asList(loaderName, primaryIndexValues));
        System.out.println("uniqueKey:" + uniqueKey);
        return uniqueKey;
    }

    public void buildLookup(String[] findIndexValues) {

        int size = indexKeys.length;

        lookup = new HashMap<>(size);

        if (size == 1) {
            lookup.put(findIndexValues[0], findIndexValues[0]);
        }

        for (int i = 1; i <= size ; i++) {

            Permutation.of(Arrays.asList(findIndexValues), size).forEach(indexes -> {

                List<String> keys = Lists.newArrayList(loaderName);
                keys.addAll(indexes);

                String key = keyCombinedStrategy.toKey(keys);

                System.out.println("FindKeys:" + key);
                lookup.put(key, key);

            });
        }

    }
}
