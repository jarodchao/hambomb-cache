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

import org.hambomb.cache.CacheUtils;
import org.hambomb.cache.ConfigurationException;
import org.hambomb.cache.storage.key.KeyGeneratorStrategy;
import com.google.common.collect.Lists;
import org.hambomb.cache.storage.key.KeyPermutationCombinationStrategy;
import org.raistlic.common.permutation.Combination;
import org.raistlic.common.permutation.Permutation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: <a herf="mailto:jarodchao@126.com>jarod </a>
 * @date: 2019-02-26
 */
public class IndexRepository {

    public static final String RESERVED_WORD = "id";

    String loaderName;

    public String entityName;

    public String[] primaryIndex;

    public String[] indexKeys;

    public Map<String, Object> lookup = new HashMap<>();

    public KeyGeneratorStrategy keyGeneratorStrategy;

    public KeyPermutationCombinationStrategy keyPermutationCombinationStrategy;

    public int peek;

    private static final Logger LOG = LoggerFactory.getLogger(IndexRepository.class);

    public static IndexRepository create(String loaderName, String[] primaryIndex, String[] indexKeys,
                                         KeyGeneratorStrategy keyGeneratorStrategy, int peek) {

        return new IndexRepository(loaderName, primaryIndex, indexKeys, keyGeneratorStrategy, peek);
    }


    public IndexRepository(String loaderName, String[] primaryIndex,
                           String[] indexKeys, KeyGeneratorStrategy keyGeneratorStrategy,
                           int peek) {
        this.loaderName = loaderName;
        this.primaryIndex = primaryIndex;
        this.indexKeys = indexKeys;
        this.keyGeneratorStrategy = keyGeneratorStrategy;
        this.peek = peek;

    }

    public String buildUniqueKey(String[] primaryIndexValues){
        return keyGeneratorStrategy.toPrimaryKey(Lists.asList(entityName,"Primary", primaryIndexValues));
    }

    public Map<String, String> buildLookup(String[] findIndexValues) {


        int size = indexKeys.length;
        Map<String, String> curLookup = new HashMap<>(size);

        if (size == 1) {
            lookup.put(findIndexValues[0], findIndexValues[0]);
        }

        if (keyPermutationCombinationStrategy.equals(KeyPermutationCombinationStrategy.PERMUTATION)) {

            for (int i = peek; i <= size; i++) {

                Permutation.of(Arrays.asList(findIndexValues), size).forEach(indexes -> {

                    String key = toCacheKey(entityName, indexes);

                    lookup.put(key, key);
                    curLookup.put(key, key);

                });
            }
        } else if (keyPermutationCombinationStrategy.equals(KeyPermutationCombinationStrategy.COMBINATION)) {

            for (int i = peek; i <= size; i++) {

                Combination.of(Arrays.asList(findIndexValues), i).forEach(indexes -> {

                    String key = toCacheKey(entityName, indexes);

                    lookup.put(key, key);
                    curLookup.put(key, key);

                });
            }
        }

        return curLookup;

    }

    public String toCacheKey(String... keys) {
        return keyGeneratorStrategy.toKey(Arrays.asList(keys));
    }

    public String toCacheKey(String key,String... keys) {

        List<String> cacheKeys = Lists.newArrayList(key);
        cacheKeys.addAll(Arrays.asList(keys));

        return keyGeneratorStrategy.toKey(cacheKeys);
    }

    public String toCacheKey(String key,List<String> keys) {

        List<String> cacheKeys = Lists.newArrayList(key);
        cacheKeys.addAll(keys);

        return keyGeneratorStrategy.toKey(cacheKeys);
    }

    public void validate() {

        if (Arrays.binarySearch(this.indexKeys, RESERVED_WORD) > 0
                || Arrays.binarySearch(this.indexKeys, RESERVED_WORD.toUpperCase()) > 0
                || Arrays.binarySearch(this.indexKeys, CacheUtils.upCase(RESERVED_WORD)) > 0) {

            throw new ConfigurationException("HambombCacheConfiguration containing reserved word.");

        }

    }
}
