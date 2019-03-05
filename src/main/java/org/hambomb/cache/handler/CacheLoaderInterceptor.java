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

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.reflections.ReflectionUtils;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * @author: <a herf="mailto:jarodchao@126.com>jarod </a>
 * @date: 2019-03-05
 */
@Aspect
@Component
public class CacheLoaderInterceptor {

//    @Autowired
//    private CacheHandler cacheHandler;

    @Around("@annotation(PostProcess)")
    public Object postServiceProcess(ProceedingJoinPoint joinPoint) throws Throwable {

        System.out.println("拦截到了" + joinPoint.getSignature().getName() +"方法...");

        Object[] args = joinPoint.getArgs();

        Object target = joinPoint.getTarget();
        Object t = joinPoint.getThis();

        Set<Method> methods = ReflectionUtils.getMethods(target.getClass(), ReflectionUtils.withName(joinPoint.getSignature().getName()));

        Method method = methods.stream().findFirst().get();

        PostProcess postProcess = (PostProcess) ReflectionUtils.getAnnotations(method).stream().findFirst().get();


        return null;

    }

}
