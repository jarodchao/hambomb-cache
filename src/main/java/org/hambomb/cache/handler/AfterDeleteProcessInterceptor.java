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
package org.hambomb.cache.handler;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.hambomb.cache.context.HanmbombRuntimeException;
import org.hambomb.cache.handler.annotation.AfterDeleteProcess;
import org.hambomb.cache.loader.CacheObjectLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author: <a herf="mailto:jarodchao@126.com>jarod </a>
 * @date: 2019-03-26
 */
@Aspect
@Component
public class AfterDeleteProcessInterceptor extends CacheLoaderProcessInterceptor<AfterDeleteProcess> {

    private static final Logger LOG = LoggerFactory.getLogger(AfterDeleteProcessInterceptor.class);

    @Around("@annotation(org.hambomb.cache.handler.annotation.AfterDeleteProcess)")
    public Object afterDeleteServiceProcess(ProceedingJoinPoint joinPoint) throws Throwable {

        return process(joinPoint);

    }

    private void deleteCacheObject(ProceedingJoinPoint joinPoint,CacheObjectLoader cacheObjectLoader,
                                   AfterDeleteProcess afterDeleteProcess) {

        Object[] argValue = joinPoint.getArgs();

        if (argValue.length > 1) {
            throw new RuntimeException("不支持！");
        }

        String id;

        if (afterDeleteProcess.byPrimaryKey()) {
            String[] pValues = new String[]{String.valueOf(argValue[0])};

            id = cacheObjectLoader.indexRepository.buildUniqueKey(pValues);
        } else {

            String[] args = afterDeleteProcess.attrs();

            String[] values = cacheObjectLoader.getEntityCacheKey(argValue[0], args);

            String cacheKey = cacheObjectLoader.indexRepository.toCacheKey(cacheObjectLoader.cacheObjectClassName, values);

            id = (String) cacheObjectLoader.cacheHandler.getRealKey(cacheKey);
        }

        Object cacheObject = cacheObjectLoader.cacheHandler.getRealKey(id);

        cacheObjectLoader.cacheHandler.delete(id);

        Map<String, String> lookup = cacheObjectLoader.getFKeys(cacheObject, null);

        lookup.forEach((key, value) -> cacheObjectLoader.cacheHandler.delete(key));


    }

    @Override
    Class getMethodAnnotation() {
        return AfterDeleteProcess.class;
    }

    @Override
    String getLoaderName(InterceptorMetaData<AfterDeleteProcess> metaData, Object[] argValue) {

        return metaData.methodAnnotation.cacheObjectClass().getSimpleName();
    }


    @Override
    void preProcess(InterceptorRuntimeData runtimeData) {
    }

    @Override
    void postProcess(InterceptorRuntimeData runtimeData) {
        try {
            deleteCacheObject(runtimeData.joinPoint, runtimeData.metaData.cacheObjectLoader,
                    runtimeData.metaData.methodAnnotation);
        } catch (HanmbombRuntimeException e) {
            LOG.warn("If a runtime exception occurs, processing is skipped.");
        }
    }
}
