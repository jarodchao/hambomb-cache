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
import org.springframework.stereotype.Component;

/**
 * @author: <a herf="mailto:jarodchao@126.com>jarod </a>
 * @date: 2019-03-26
 */
@Aspect
@Component
public class AfterInsertProcessInterceptor extends AbstractCacheLoaderProcessInterceptor<AfterInsertProcess> {

    private static final Logger LOG = LoggerFactory.getLogger(AfterDeleteProcessInterceptor.class);

    @Around("@annotation(org.hambomb.cache.handler.annotation.AfterInsertProcess)")
    public Object afterInsertServiceProcess(ProceedingJoinPoint joinPoint) throws Throwable {

        log(joinPoint);

        Object result = invokeProcess(joinPoint);

        try {
            insertCacheObject(joinPoint);
        } catch (HanmbombRuntimeException e) {
            LOG.warn("If a runtime exception occurs, processing is skipped.");
        }

        return result;
    }

    private void insertCacheObject(ProceedingJoinPoint joinPoint) {
        Object[] argValue = joinPoint.getArgs();

        if (argValue.length > 1) {
            throw new RuntimeException("不支持！");
        }

        InterceptorMetaData metaData = getInterceptorAnnotation(joinPoint);

        if (metaData == null) {
            LOG.warn("Skip HambombCache related processing.");
            return;
        }

        AfterInsertProcess afterInsertProcess = (AfterInsertProcess) metaData.methodAnnotation;

        String loaderName = afterInsertProcess.cacheObject() == Object.class ?
                argValue[0].getClass().getSimpleName() : afterInsertProcess.cacheObject().getSimpleName();

        CacheObjectLoader cacheObjectLoader = processor.getEntityLoader(loaderName);

        if (argValue[0].getClass() == cacheObjectLoader.cacheObjectClazz) {

            cacheObjectLoader.cacheObject(argValue[0]);

        } else {

            cacheObjectLoader.cacheOtherObject(argValue[0]);
        }

    }

    @Override
    Class getMethodAnnotation() {
        return AfterInsertProcess.class;
    }


    @Override
    String getLoaderName(InterceptorMetaData<AfterInsertProcess> metaData) {

        AfterInsertProcess afterInsertProcess = metaData.methodAnnotation;

        String loaderName = afterInsertProcess.cacheObject() == Object.class ?
                metaData.cacheObjectLoader.cacheObjectClazz.getSimpleName() :
                afterInsertProcess.cacheObject().getSimpleName();

        return loaderName;

    }

    @Override
    String getCacheKey(ProceedingJoinPoint joinPoint, InterceptorMetaData<AfterInsertProcess> metaData) {

        Object[] argValue = joinPoint.getArgs();

        AfterInsertProcess afterInsertProcess = metaData.methodAnnotation;

        String loaderName = afterInsertProcess.cacheObject() == Object.class ?
                argValue[0].getClass().getSimpleName() : afterInsertProcess.cacheObject().getSimpleName();

        return loaderName;
    }
}
