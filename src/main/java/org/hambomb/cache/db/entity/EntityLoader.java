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

import org.hambomb.cache.CacheUtils;
import org.hambomb.cache.handler.CacheHandler;
import org.hambomb.cache.index.IndexRepository;
import com.google.common.reflect.Reflection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author: <a herf="mailto:jarodchao@126.com>jarod </a>
 * @date: 2019-02-26
 */
public class EntityLoader<T> {

    CacheObjectMapper<T> cacheObjectMapper;

    public List<Method> pkGetter;

    public List<Method> fkGetter;

    Class<T> entityClazz;

    public String entityClassName;

    String entityPackageName;

    public CacheHandler cacheHandler;

    public IndexRepository indexRepository;

    private static final Logger LOG = LoggerFactory.getLogger(EntityLoader.class);

    public EntityLoader(CacheObjectMapper<T> cacheObjectMapper) {
        this.cacheObjectMapper = cacheObjectMapper;
    }

    public EntityLoader addIndexFactory(IndexRepository indexRepository) {
        this.indexRepository = indexRepository;
        return this;
    }


    public void initializeLoader() {

        entityClazz = cacheObjectMapper.getSubEntityClass();

        Reflection.initialize(entityClazz);

        entityClassName = entityClazz.getSimpleName();
        entityPackageName = Reflection.getPackageName(entityClazz);

        pkGetter = new ArrayList<>(indexRepository.primaryIndex.length);
        fkGetter = new ArrayList<>(indexRepository.indexKeys.length);

        indexRepository.entityName = entityClassName;

    }

    public void loadData() {

        loadEntities().stream().forEach(o -> {
            String uniqueKey = getPkey(o);
            Map<String, String> lookup =  getFKeys(o);

            cacheHandler.put(uniqueKey, o);

            lookup.forEach((key, value) -> {

                if (LOG.isDebugEnabled()) {
                    LOG.debug("EntityLoader[{}] was cache data: Key[{}] Value[{}]", this.entityClassName,
                            key, uniqueKey);
                }
                cacheHandler.put(key, uniqueKey);
            });

        });
    }


    private List<T> loadEntities() {

        AllCacheObjectHandler<T> handler = new AllCacheObjectHandler<>(cacheObjectMapper);

        CacheObjectMapper<T> mapperProxy = Reflection.newProxy(CacheObjectMapper.class, handler);

        List<T> data = mapperProxy.selectAllCacheObject();

        return data;
    }

    public void addPkGetter(Method getter) {
        this.pkGetter.add(getter);
    }

    public void addFkGetter(Method getter) {
        this.fkGetter.add(getter);
    }

    public String getPkey(T t) {

        String[] pkValues = new String[pkGetter.size()];

        for (int i = 0; i < pkGetter.size(); i++) {
            pkValues[i] = getValueByMethod(t, pkGetter.get(i));
        }

        return indexRepository.buildUniqueKey(pkValues);

    }

    public Map<String, String> getFKeys(T t) {

        String[] fkValues = new String[fkGetter.size()];

        for (int i = 0; i < fkGetter.size(); i++) {
            fkValues[i] = getValueByMethod(t, fkGetter.get(i));
        }

        return indexRepository.buildLookup(fkValues);

    }

    public String[] getEntityCacheKey(T t, String[] keys) {

        String[] fkValues = new String[keys.length];

        for (int i = 0; i < keys.length; i++) {

            fkValues[i] = getValueByMethod(t, CacheUtils.getGetterMethod(keys[i], t.getClass()));
        }

        return fkValues;
    }

    private String getValueByMethod(T t, Method method) {
        try {
            return method.invoke(t, null).toString();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
