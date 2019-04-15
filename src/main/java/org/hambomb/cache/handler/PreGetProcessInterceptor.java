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
import org.hambomb.cache.handler.annotation.PreGetProcess;
import org.hambomb.cache.loader.CacheObjectLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author: <a herf="mailto:jarodchao@126.com>jarod </a>
 * @date: 2019-03-26
 */
@Aspect
@Component
public class PreGetProcessInterceptor extends CacheLoaderProcessInterceptor<PreGetProcess> {

    private static final Logger LOG = LoggerFactory.getLogger(PreGetProcessInterceptor.class);

    @Around("@annotation(org.hambomb.cache.handler.annotation.PreGetProcess)")
    public Object postServiceProcess(ProceedingJoinPoint joinPoint) throws Throwable {

        return process(joinPoint);

    }

    private Object getCacheObject(ProceedingJoinPoint joinPoint,CacheObjectLoader cacheObjectLoader,
                                  PreGetProcess preGetProcess) {


        String[] values = null;
        Object[] argValue = joinPoint.getArgs();

        String uniqueKey;

        if (preGetProcess.byPrimaryKey()) {

            uniqueKey = cacheObjectLoader.indexRepository.buildUniqueKey(new String[]{String.valueOf(argValue[0])});

            return cacheObjectLoader.cacheHandler.getByRealKey(uniqueKey);

        }else {
            if (preGetProcess.args() != null && preGetProcess.args().length > 0) {
                String[] args = preGetProcess.args();

                values = new String[args.length];

                for (int i = 0; i < args.length; i++) {

                    Integer holder = Integer.valueOf(args[i].replace("#", ""));

                    values[i] = argValue[holder] != null ? argValue[holder].toString() : null;

                }
            } else if (argValue.length == 1 && preGetProcess.attrs() != null && preGetProcess.attrs().length > 0) {

                String[] args = preGetProcess.attrs();

                values = cacheObjectLoader.getEntityCacheKey(argValue[0], args);

            }

            String cacheKey = cacheObjectLoader.indexRepository.toCacheKey(cacheObjectLoader.cacheObjectClassName, values);

            return cacheObjectLoader.cacheHandler.getByIndexKey(cacheKey);
        }

    }

    @Override
    Class<PreGetProcess> getMethodAnnotation() {
        return PreGetProcess.class;
    }


    @Override
    String getLoaderName(InterceptorMetaData<PreGetProcess> metaData, Object[] argValue) {

        return metaData.method.getReturnType().getSimpleName();
    }

    @Override
    void preProcess(InterceptorRuntimeData runtimeData) {

        Object result = getCacheObject(runtimeData.joinPoint,
                runtimeData.metaData.cacheObjectLoader, runtimeData.metaData.methodAnnotation);

        if (result != null) {
            runtimeData.callMethod = false;
            runtimeData.cacheObject = Optional.of(result);
        }
    }

    @Override
    void postProcess(InterceptorRuntimeData runtimeData) {

        CacheObjectLoader cacheObjectLoader = runtimeData.metaData.cacheObjectLoader;

        try {
            if (runtimeData.callMethod && runtimeData.cacheObject.isPresent() ) {

                cacheObjectLoader.cacheObject(runtimeData.cacheObject);
            }
        } catch (HanmbombRuntimeException e) {
            LOG.warn("If a runtime exception occurs, processing is skipped.");
        }
    }
}
