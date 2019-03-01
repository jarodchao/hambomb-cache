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

import org.hambomb.cache.index.IndexFactory;
import com.google.common.reflect.Reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: <a herf="mailto:jarodchao@126.com>jarod </a>
 * @date: 2019-02-26
 */
public class EntityLoader<T> {

    CacheObjectMapper<T> cacheObjectMapper;

    public List<Method> pkGetter;

    public List<Method> fkGetter;

    Class<T> entityClazz;

    String entityClassName;

    String entityPackageName;

    public IndexFactory indexFactory;

    public EntityLoader(CacheObjectMapper<T> cacheObjectMapper) {
        this.cacheObjectMapper = cacheObjectMapper;
    }

    public EntityLoader addIndexFactory(IndexFactory indexFactory) {
        this.indexFactory = indexFactory;
        return this;
    }


    public void initializeLoader() {

        entityClazz = cacheObjectMapper.getSubEntityClass();

        Reflection.initialize(entityClazz);

        entityClassName = entityClazz.getSimpleName();
        entityPackageName = Reflection.getPackageName(entityClazz);

        pkGetter = new ArrayList<>(indexFactory.primaryIndex.length);
        fkGetter = new ArrayList<>(indexFactory.indexKeys.length);

    }


    public List<T> loadEntities() {

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

    public void getPkey(T t) {

        String[] pkValues = new String[pkGetter.size()];

        for (int i = 0; i < pkGetter.size(); i++) {
            pkValues[i] = getValueByMethod(t, pkGetter.get(i));
        }

        indexFactory.buildUniqueKey(pkValues);

    }

    public void getFKeys(T t) {

        String[] fkValues = new String[fkGetter.size()];

        for (int i = 0; i < fkGetter.size(); i++) {
            fkValues[i] = getValueByMethod(t, fkGetter.get(i));
        }

        indexFactory.buildLookup(fkValues);

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
