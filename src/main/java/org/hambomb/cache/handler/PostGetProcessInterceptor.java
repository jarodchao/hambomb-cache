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
import org.hambomb.cache.handler.annotation.PostGetProcess;
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
public class PostGetProcessInterceptor extends AbstractCacheLoaderProcessInterceptor<PostGetProcess> {

    private static final Logger LOG = LoggerFactory.getLogger(PostGetProcessInterceptor.class);

    @Around("@annotation(org.hambomb.cache.handler.annotation.PostGetProcess)")
    public Object postServiceProcess(ProceedingJoinPoint joinPoint) throws Throwable {

        log(joinPoint);

        Object result = getCacheObject(joinPoint);

        if (result == null) {

            result = invokeProcess(joinPoint);

            InterceptorMetaData metaData = getInterceptorAnnotation(joinPoint);

            if (metaData == null) {
                LOG.warn("Skip HambombCache related processing.");
                return null;
            }

            CacheObjectLoader cacheObjectLoader = processor.getEntityLoader(metaData.method.getReturnType().getSimpleName());

            try {
                cacheObjectLoader.cacheObject(result);
            } catch (HanmbombRuntimeException e) {
                LOG.warn("If a runtime exception occurs, processing is skipped.");
            }
        }

        return result;

    }

    private Object getCacheObject(ProceedingJoinPoint joinPoint) {


        InterceptorMetaData<PostGetProcess> metaData = getInterceptorAnnotation(joinPoint);

        if (metaData == null) {
            LOG.warn("Skip HambombCache related processing,cause of the Annotation could not be found.");
            return null;
        }

        CacheObjectLoader cacheObjectLoader = metaData.cacheObjectLoader;

        if (cacheObjectLoader == null) {
            LOG.warn("Skip HambombCache related processing,cause of the loader could not be found.");
            return null;
        }

        String[] values = null;
        Object[] argValue = joinPoint.getArgs();

        PostGetProcess postGetProcess = metaData.methodAnnotation;

        String uniqueKey;

        if (postGetProcess.byPrimaryKey()) {

            uniqueKey = cacheObjectLoader.indexRepository.buildUniqueKey(new String[]{String.valueOf(argValue[0])});

        }else {
            if (postGetProcess.args() != null && postGetProcess.args().length > 0) {
                String[] args = postGetProcess.args();

                values = new String[args.length];

                for (int i = 0; i < args.length; i++) {

                    Integer holder = Integer.valueOf(args[i].replace("#", ""));

                    values[i] = argValue[holder] != null ? argValue[holder].toString() : null;

                }
            } else if (argValue.length == 1 && postGetProcess.attrs() != null && postGetProcess.attrs().length > 0) {

                String[] args = postGetProcess.attrs();

                values = cacheObjectLoader.getEntityCacheKey(argValue[0], args);

            }

            String cacheKey = cacheObjectLoader.indexRepository.toCacheKey(cacheObjectLoader.cacheObjectClassName, values);

            uniqueKey = (String) cacheObjectLoader.cacheHandler.get(cacheKey);
        }


        return uniqueKey == null ? null : cacheObjectLoader.cacheHandler.get(uniqueKey);
    }

    @Override
    Class<PostGetProcess> getMethodAnnotation() {
        return PostGetProcess.class;
    }


    @Override
    String getLoaderName(InterceptorMetaData<PostGetProcess> metaData) {
        return metaData.method.getReturnType().getSimpleName();
    }

    @Override
    String getCacheKey(ProceedingJoinPoint joinPoint, InterceptorMetaData<PostGetProcess> metaData) {


        String[] values = null;
        Object[] argValue = joinPoint.getArgs();

        PostGetProcess postGetProcess = metaData.methodAnnotation;
        CacheObjectLoader cacheObjectLoader = metaData.cacheObjectLoader;

        String uniqueKey;

        if (postGetProcess.byPrimaryKey()) {

            uniqueKey = cacheObjectLoader.indexRepository.buildUniqueKey(new String[]{String.valueOf(argValue[0])});

        } else {
            if (postGetProcess.args() != null && postGetProcess.args().length > 0) {
                String[] args = postGetProcess.args();

                values = new String[args.length];

                for (int i = 0; i < args.length; i++) {

                    Integer holder = Integer.valueOf(args[i].replace("#", ""));

                    values[i] = argValue[holder] != null ? argValue[holder].toString() : null;

                }
            } else if (argValue.length == 1 && postGetProcess.attrs() != null && postGetProcess.attrs().length > 0) {

                String[] args = postGetProcess.attrs();

                values = cacheObjectLoader.getEntityCacheKey(argValue[0], args);

            }

            String cacheKey = cacheObjectLoader.indexRepository.toCacheKey(cacheObjectLoader.cacheObjectClassName, values);

            uniqueKey = (String) cacheObjectLoader.cacheHandler.get(cacheKey);
        }


        return uniqueKey;
    }


    @Override
    Object processCache(String cacheKey, Object[] cacheObject, InterceptorMetaData<PostGetProcess> metaData) {
        return metaData.cacheObjectLoader.cacheHandler.get(cacheKey);
    }
}
