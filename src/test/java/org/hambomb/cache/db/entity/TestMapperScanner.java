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
import org.hambomb.cache.index.IndexFactory;
import org.hambomb.cache.storage.RedisKeyGeneratorStrategy;
import org.junit.Test;
import org.reflections.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.reflections.ReflectionUtils.*;

/**
 * @author: <a herf="mailto:jarodchao@126.com>jarod </a>
 * @date: 2019-02-26
 */
public class TestMapperScanner {

    @Test
    public void testScanPackage() {

        MapperScanner scanner = new MapperScanner("org.hambomb.cache.db.entity");

        Set<Class<? extends CacheObjectMapper>> mappers = scanner.scanMapper();

        mappers.stream().forEach((Class<? extends CacheObjectMapper> aClass) -> {
            System.out.println(aClass.getSimpleName());

            CacheObjectMapper mapper = null;

            try {
                mapper = aClass.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            EntityLoader entityLoader = new EntityLoader(mapper);

            Class entityClass = mapper.getSubEntityClass();
            entityLoader.entityClazz = entityClass;

            /** 取@的值 */
            Cachekey cachekey = (Cachekey) entityClass.getAnnotation(Cachekey.class);

            String[] pk = cachekey.primaryKeys();
            String[] fk = cachekey.findKeys();

            IndexFactory indexFactory = IndexFactory.create("", pk, fk, new RedisKeyGeneratorStrategy());
            entityLoader.addIndexFactory(indexFactory);

            List<Method> pkGetter = new ArrayList<>(pk.length);

            for (String p : pk) {
                Set<Method> getters = ReflectionUtils.getAllMethods(entityClass,
                        withModifier(Modifier.PUBLIC), withName(CacheUtils.getter(p)), withParametersCount(0));

                pkGetter.add(getters.stream().findFirst().get());
            }

            List<Method> fkGetter = new ArrayList<>(fk.length);

            for (String f : fk) {
                Set<Method> getters = ReflectionUtils.getAllMethods(entityClass,
                        withModifier(Modifier.PUBLIC), withName(CacheUtils.getter(f)), withParametersCount(0));

                fkGetter.add(getters.stream().findFirst().get());
            }

            mapper.selectAllCacheObject().stream().forEach(o -> {

                for (Method method : pkGetter) {
//                    try {
//                        System.out.println(method.invoke(o, null));
//                    } catch (IllegalAccessException e) {
//                        e.printStackTrace();
//                    } catch (InvocationTargetException e) {
//                        e.printStackTrace();
//                    }
                }

                for (Method method : fkGetter) {
                    try {
                        System.out.println(method.invoke(o, null));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            });

        });

    }
}
