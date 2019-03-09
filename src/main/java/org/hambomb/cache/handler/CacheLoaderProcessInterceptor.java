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
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.hambomb.cache.CacheUtils;
import org.hambomb.cache.HambombCacheProcessor;
import org.hambomb.cache.db.entity.CacheObjectMapper;
import org.hambomb.cache.db.entity.EntityLoader;
import org.hambomb.cache.handler.annotation.PostGetProcess;
import org.reflections.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * @author: <a herf="mailto:jarodchao@126.com>jarod </a>
 * @date: 2019-03-05
 */
@Aspect
@Component
public class CacheLoaderProcessInterceptor {

    @Autowired
    @Lazy
    private HambombCacheProcessor processor;

    private static final Logger LOG = LoggerFactory.getLogger(CacheLoaderProcessInterceptor.class);

    @Around("@annotation(org.hambomb.cache.handler.annotation.PostGetProcess)")
    public Object postServiceProcess(ProceedingJoinPoint joinPoint) throws Throwable {

        Object result = getCacheObject(joinPoint);

        return result != null ? log(result): joinPoint.proceed();

    }

    private Object log(Object result) {

        if (LOG.isDebugEnabled()) {

            LOG.debug("This operation hits the cache.Result is {}",result);
        }

        return result;
    }

    @Around("@annotation(org.hambomb.cache.handler.annotation.AfterUpdateProcess)")
    public Object afterUpdateServiceProcess(ProceedingJoinPoint joinPoint) throws Throwable {

        Object object = joinPoint.proceed();

        updateCacheObject(joinPoint);

        return object;

    }

    @Around("@annotation(org.hambomb.cache.handler.annotation.AfterDeleteProcess)")
    public Object afterDeleteServiceProcess(ProceedingJoinPoint joinPoint) throws Throwable {

        Object object = joinPoint.proceed();

        deleteCacheObject(joinPoint);

        return object;

    }

    private void deleteCacheObject(ProceedingJoinPoint joinPoint) {

        Object[] argValue = joinPoint.getArgs();

        if (argValue.length > 1) {
            throw new RuntimeException("不支持！");
        }

        EntityLoader entityLoader = processor.getEntityLoader(argValue[0].getClass().getSimpleName());

        processor.getCacheHandler().delete("");


    }

    private void updateCacheObject(ProceedingJoinPoint joinPoint) {
        Object[] argValue = joinPoint.getArgs();

        if (argValue.length > 1) {
            throw new RuntimeException("不支持！");
        }

        EntityLoader entityLoader = processor.getEntityLoader(argValue[0].getClass().getSimpleName());

        String id = entityLoader.getPkey(argValue[0]);

        Object cacheObject = processor.getCacheHandler().get(id);

        BeanUtils.copyProperties(argValue[0], cacheObject, CacheUtils.getNullPropertyNames(argValue[0]));

        processor.getCacheHandler().update(id, cacheObject);


    }

    private Object getCacheObject(ProceedingJoinPoint joinPoint) {


        Object[] argValue = joinPoint.getArgs();

        InterceptorMetaData metaData = getInterceptorAnnotation(joinPoint);

        EntityLoader entityLoader = processor.getEntityLoader(metaData.method.getReturnType().getSimpleName());
        String[] values = null;

        if (metaData.postGetProcess.args() != null ) {
            String[] args = metaData.postGetProcess.args();

            values = new String[args.length];

            for (int i = 0; i < args.length; i++) {

                Integer holder = Integer.valueOf(args[i].replace("#", ""));

                values[i] = argValue[holder] != null ? argValue[holder].toString() : null;

            }
        } else if (argValue.length == 1 && metaData.postGetProcess.keys() != null ){
            String[] args = metaData.postGetProcess.keys();

            values = entityLoader.getEntityCacheKey(argValue[0], args);

        }

        String cacheKey = entityLoader.indexFactory.toCacheKey(entityLoader.entityClassName, values);

//        if (cacheKey.equals(entityLoader.indexFactory.uniqueKey)) {
//            return processor.getCacheHandler().get(cacheKey);
//        }

        String uniqueKey = (String) processor.getCacheHandler().get(cacheKey);

        return processor.getCacheHandler().get(uniqueKey);
    }

    private InterceptorMetaData getInterceptorAnnotation(ProceedingJoinPoint joinPoint) {

        Signature signature = joinPoint.getSignature();

        Object target = joinPoint.getTarget();

        MethodSignature methodSignature = (MethodSignature) signature;

        Set<Method> methods = ReflectionUtils.getMethods(target.getClass()
                , ReflectionUtils.withName(signature.getName())
                , ReflectionUtils.withParameters(methodSignature.getMethod().getParameterTypes()));

        Method method = methods.stream().findFirst().get();

        PostGetProcess postGetProcess = (PostGetProcess) ReflectionUtils.getAnnotations(method).stream().findFirst().get();

        InterceptorMetaData metaData = new InterceptorMetaData();
        metaData.method = method;
        metaData.postGetProcess = postGetProcess;
        metaData.entityLoaderClass = postGetProcess.loaderClass();

        return metaData;
    }

    private class InterceptorMetaData {

        PostGetProcess postGetProcess;

        Method method;

        Class<CacheObjectMapper> entityLoaderClass;


        @Override
        public String toString() {
            return "InterceptorMetaData{" +
                    "postGetProcess=" + postGetProcess +
                    ", method=" + method +
                    ", entityLoaderClass=" + entityLoaderClass +
                    '}';
        }
    }

}
