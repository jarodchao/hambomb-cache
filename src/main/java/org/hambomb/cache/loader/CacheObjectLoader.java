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
package org.hambomb.cache.loader;

import org.hambomb.cache.CacheUtils;
import org.hambomb.cache.context.CacheLoaderContext;
import org.hambomb.cache.context.CacheServerStrategy;
import org.hambomb.cache.context.HanmbombRuntimeException;
import org.hambomb.cache.handler.CacheHandler;
import com.google.common.reflect.Reflection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author: <a herf="mailto:jarodchao@126.com>jarod </a>
 * @date: 2019-02-26
 */
public class CacheObjectLoader<T> {

    CacheObjectMapper<T> cacheObjectMapper;

    public List<Method> pkGetter;

    public List<Method> fkGetter;

    public String[] pk;

    public String[] fk;

    public Class<T> cacheObjectClazz;

    public String cacheObjectClassName;

    String cacheObjectPackageName;

    public CacheHandler cacheHandler;

    public IndexRepository indexRepository;

    private static final Logger LOG = LoggerFactory.getLogger(CacheObjectLoader.class);

    public CacheObjectLoader(CacheObjectMapper<T> cacheObjectMapper) {
        this.cacheObjectMapper = cacheObjectMapper;
    }

    public CacheObjectLoader addIndexFactory(IndexRepository indexRepository) {
        this.indexRepository = indexRepository;
        return this;
    }


    public void initializeLoader() {

        cacheObjectClazz = cacheObjectMapper.getSubCacheObjectClass();

        Reflection.initialize(cacheObjectClazz);

        cacheObjectClassName = cacheObjectClazz.getSimpleName();
        cacheObjectPackageName = Reflection.getPackageName(cacheObjectClazz);

        pkGetter = new ArrayList<>(indexRepository.primaryIndex.length);
        fkGetter = new ArrayList<>(indexRepository.indexKeys.length);

        indexRepository.cacheObjectName = cacheObjectClassName;

    }

    public void loadData(CacheLoaderContext cacheLoaderContext) {

        boolean loadFlag = false;

        if (!CacheServerStrategy.MULTI.equals(cacheLoaderContext.cacheServerStrategy)) {
            if (cacheLoaderContext == null || cacheLoaderContext.masterFlag) {

                loadFlag = true;
            }
        } else {
            loadFlag = true;
        }

        if (loadFlag) {
            loadEntities().stream().forEach(o -> cacheObject(o));
            LOG.info("CacheObjectLoader[{}] has finished loading.", cacheObjectClassName);
        }

    }

    public List<Method> buildGetters(String[] keys,Class clazz) {

        Class handlerClazz = clazz == null ? this.cacheObjectClazz : clazz;

        List<Method> getters = new ArrayList<>(keys.length);

        for (String p : keys) {

            Method getter = CacheUtils.getGetterMethod(p, handlerClazz);

            if (getter == null) {
                LOG.error("The get method for {} was not found in the class[{}].", CacheUtils.getter(p), handlerClazz);
            }

            getters.add(getter);
        }

        return getters;
    }

    public void cacheObject(T t) {

        if (LOG.isDebugEnabled()) {
            LOG.debug("CacheObjectLoader[{}] will building cache.", this.cacheObjectClassName);
        }

        String uniqueKey = getPkey(t, null);
        Map<String, String> lookup =  getFKeys(t,null);

        cacheHandler.load(uniqueKey, t);

        lookup.forEach((key, value) -> {

            if (LOG.isDebugEnabled()) {
                LOG.debug("CacheObjectLoader[{}] was cache data: Key[{}] Value[{}]", this.cacheObjectClassName,
                        key, uniqueKey);
            }
            cacheHandler.load(key, uniqueKey);
        });
    }

    public void cacheOtherObject(Object t) {

        if (LOG.isDebugEnabled()) {
            LOG.debug("CacheObjectLoader[{}] will building cache.", this.cacheObjectClassName);
        }

        List<Method> pkGetters = buildGetters(pk, t.getClass());
        List<Method> fkGetters = buildGetters(fk, t.getClass());

        String uniqueKey = getPkey(t, pkGetters);
        Map<String, String> lookup =  getFKeys(t,fkGetters);

        cacheHandler.put(uniqueKey, t);

        lookup.forEach((key, value) -> {

            if (LOG.isDebugEnabled()) {
                LOG.debug("CacheObjectLoader[{}] was cache data: Key[{}] Value[{}]", this.cacheObjectClassName,
                        key, uniqueKey);
            }
            cacheHandler.put(key, uniqueKey);
        });


    }


    private List<T> loadEntities() {

        AllCacheObjectHandler<T> handler = new AllCacheObjectHandler<>(cacheObjectMapper);

        CacheObjectMapper<T> mapperProxy = Reflection.newProxy(CacheObjectMapper.class, handler);

        List<T> data = mapperProxy.selectAllCacheObject();

        LOG.info("CacheObjectLoader[{}] queried [{}] pieces of data .", cacheObjectClassName, data.size());

        return data;
    }

    public String getPkey(Object t, List<Method> methods) {

        int size = methods == null || methods.size() == 0 ? pkGetter.size() : methods.size();

        String[] pkValues = new String[pkGetter.size()];

        for (int i = 0; i < size; i++) {

            Method method =  methods == null || methods.size() == 0 ? pkGetter.get(i) : methods.get(i);

            pkValues[i] = CacheUtils.getValueByMethod(t, method);
        }

        return indexRepository.buildUniqueKey(pkValues);

    }

    public Map<String, String> getFKeys(Object t, List<Method> methods) {

        int size = methods == null || methods.size() == 0 ? fkGetter.size() : methods.size();

        String[] fkValues = new String[size];

        for (int i = 0; i < size; i++) {
            Method method = methods == null || methods.size() == 0 ? fkGetter.get(i) : methods.get(i);
            fkValues[i] = CacheUtils.getValueByMethod(t, method);
        }

        return indexRepository.buildLookup(fkValues);

    }

    public String[] getEntityCacheKey(T t, String[] keys) {

        String[] fkValues = new String[keys.length];

        for (int i = 0; i < keys.length; i++) {

            Method getter = CacheUtils.getGetterMethod(keys[i], t.getClass());

            if (getter == null) {
                LOG.error("The get method for {} was not found in the class[{}].", CacheUtils.getter(keys[i]), t.getClass().getSimpleName());
                throw new HanmbombRuntimeException("The get method was not found in the class.");
            }

            fkValues[i] = CacheUtils.getValueByMethod(t, getter);
        }

        return fkValues;
    }


}
