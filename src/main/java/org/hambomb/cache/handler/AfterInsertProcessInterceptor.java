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
import org.hambomb.cache.handler.annotation.AfterInsertProcess;
import org.hambomb.cache.loader.CacheObjectLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * @author: <a herf="mailto:jarodchao@126.com>jarod </a>
 * @date: 2019-03-26
 */
@Aspect
@Component
public class AfterInsertProcessInterceptor extends CacheLoaderProcessInterceptor<AfterInsertProcess> {

    private static final Logger LOG = LoggerFactory.getLogger(AfterDeleteProcessInterceptor.class);

    @Around("@annotation(org.hambomb.cache.handler.annotation.AfterInsertProcess)")
    public Object afterInsertServiceProcess(ProceedingJoinPoint joinPoint) throws Throwable {

        return process(joinPoint);
    }

    private void insertCacheObject(ProceedingJoinPoint joinPoint,CacheObjectLoader cacheObjectLoader) {
        Object[] argValue = joinPoint.getArgs();

        if (argValue.length > 1) {
            throw new RuntimeException("不支持！");
        }

        if (argValue[0].getClass() == cacheObjectLoader.cacheObjectClazz) {

            cacheObjectLoader.cacheObject(argValue[0]);

        } else {

            Object cacheObject = null;

            try {
                cacheObject = cacheObjectLoader.cacheObjectClazz.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            BeanUtils.copyProperties(argValue[0], cacheObject);

            cacheObjectLoader.cacheObject(cacheObject);
        }

    }

    @Override
    Class getMethodAnnotation() {
        return AfterInsertProcess.class;
    }


    @Override
    String getLoaderName(InterceptorMetaData<AfterInsertProcess> metaData, Object[] argValue) {

        AfterInsertProcess afterInsertProcess = metaData.methodAnnotation;

        String loaderName = afterInsertProcess.cacheObject() == Object.class ?
                metaData.cacheObjectLoader.cacheObjectClazz.getSimpleName() :
                afterInsertProcess.cacheObject().getSimpleName();

        return loaderName;

    }

    @Override
    void preProcess(InterceptorRuntimeData runtimeData) {

    }

    @Override
    void postProcess(InterceptorRuntimeData runtimeData) {
        try {
            insertCacheObject(runtimeData.joinPoint, runtimeData.metaData.cacheObjectLoader);
        } catch (HanmbombRuntimeException e) {
            LOG.warn("If a runtime exception occurs, processing is skipped.");
        }
    }
}
