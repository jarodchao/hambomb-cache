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
import org.aspectj.lang.reflect.MethodSignature;
import org.hambomb.cache.CacheUtils;
import org.hambomb.cache.HambombCacheProcessor;
import org.hambomb.cache.loader.CacheObjectLoader;
import org.reflections.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.Set;

/**
 * @author: <a herf="mailto:jarodchao@126.com>jarod </a>
 * @date: 2019-03-05
 */
public abstract class CacheLoaderProcessInterceptor<T extends Annotation> {

    @Autowired
    @Lazy
    protected HambombCacheProcessor processor;

    private static final Logger LOG = LoggerFactory.getLogger(CacheLoaderProcessInterceptor.class);

    protected void log(ProceedingJoinPoint joinPoint) {
        if (LOG.isDebugEnabled()) {

            Signature signature = joinPoint.getSignature();

            LOG.debug("The Method for {} into CacheLoaderProcessInterceptor.", signature.getName());
        }
    }

    protected Object log(Object result) {

        if (LOG.isDebugEnabled()) {

            LOG.debug("This operation hits the cache.Result is {}", result);
        }

        return result;
    }

    protected void invokeProcess(InterceptorRuntimeData runtimeData) throws Throwable {

        if (runtimeData.callMethod) {
            Object object;

            try {

                object = runtimeData.joinPoint.proceed();
            } catch (Exception e) {
                LOG.error(e.getMessage());
                throw e;
            }

            runtimeData.cacheObject = Optional.ofNullable(object);
        }

    }


    protected InterceptorMetaData<T> getInterceptorAnnotation(
            ProceedingJoinPoint joinPoint) {

        Signature signature = joinPoint.getSignature();

        Object target = joinPoint.getTarget();

        Class<T> methodAnnotation = getMethodAnnotation();

        MethodSignature methodSignature = (MethodSignature) signature;

        Set<Method> methods = ReflectionUtils.getMethods(target.getClass()
                , ReflectionUtils.withName(signature.getName())
                , ReflectionUtils.withParameters(methodSignature.getMethod().getParameterTypes()));

        Method method = methods.stream().findFirst().get();

        InterceptorMetaData<T> metaData = new InterceptorMetaData();
        metaData.method = method;

        T annotation = (T) CacheUtils.getAnnotation(method, methodAnnotation);

        if (annotation == null) {
            LOG.error("The method for name[{}] was not found annotation[{}].", signature.getName(), methodAnnotation.getSimpleName());
            return null;
        }

        metaData.methodAnnotation = annotation;

        metaData.cacheObjectLoader = processor.getEntityLoader(getLoaderName(metaData, joinPoint.getArgs()));

        return metaData;
    }

    abstract Class<T> getMethodAnnotation();

    abstract String getLoaderName(InterceptorMetaData<T> metaData, Object[] argValue);

    public class InterceptorMetaData<T> {

        T methodAnnotation;

        Method method;

        CacheObjectLoader cacheObjectLoader;

        @Override
        public String toString() {
            return "InterceptorMetaData{" +
                    "methodAnnotation=" + methodAnnotation +
                    ", method=" + method +
                    '}';
        }
    }

    public class InterceptorRuntimeData {

        ProceedingJoinPoint joinPoint;

        boolean callMethod = true;

        InterceptorMetaData<T> metaData;

        Optional<Object> cacheObject;

        @Override
        public String toString() {
            return "InterceptorRuntimeData{" +
                    "joinPoint=" + joinPoint +
                    ", callMethod=" + callMethod +
                    ", metaData=" + metaData +
                    ", cacheObject=" + cacheObject +
                    '}';
        }
    }


    protected Object process(ProceedingJoinPoint joinPoint) throws Throwable {

        log(joinPoint);

        InterceptorMetaData<T> metaData = getInterceptorAnnotation(joinPoint);

        if (metaData == null) {
            LOG.warn("Skip HambombCache related processing,cause of the Annotation could not be found.");
            return null;
        }

        CacheObjectLoader cacheObjectLoader = metaData.cacheObjectLoader;

        if (cacheObjectLoader == null) {
            LOG.warn("Skip HambombCache related processing,cause of the loader could not be found.");
            return null;
        }

        InterceptorRuntimeData runtimeData = new InterceptorRuntimeData();
        runtimeData.joinPoint = joinPoint;
        runtimeData.metaData = metaData;

        preProcess(runtimeData);

        invokeProcess(runtimeData);

        postProcess(runtimeData);

        return runtimeData.cacheObject.isPresent() ? runtimeData.cacheObject.get() : null;
    }

    abstract void preProcess(InterceptorRuntimeData runtimeData);

    abstract void postProcess(InterceptorRuntimeData runtimeData);

}
