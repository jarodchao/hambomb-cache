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
package org.hambomb.cache;

import org.reflections.ReflectionUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import static org.reflections.ReflectionUtils.withModifier;
import static org.reflections.ReflectionUtils.withName;
import static org.reflections.ReflectionUtils.withParametersCount;

/**
 * @author: <a herf="mailto:jarodchao@126.com>jarod </a>
 * @date: 2019-02-27
 */
public class CacheUtils {

    public static String setter(String v){

        return "set" + upCase(v);

    }

    public static String getter(String v){

        return "get" + upCase(v);

    }

    public static String upCase(String v) {
        String f = v.substring(0, 1).toUpperCase();

        return f + v.substring(1, v.length());
    }

    public static Method getGetterMethod(String name, Class entityClazz) {
        Set<Method> getters = ReflectionUtils.getAllMethods(entityClazz,
                withModifier(Modifier.PUBLIC), withName(CacheUtils.getter(name)), withParametersCount(0));

        return getters.stream().findFirst().get();
    }

    public static <T extends Annotation> Annotation getAnnotation(Method method, Class<T> reflectAnnotation) {

        return ReflectionUtils.getAnnotations(method).stream()
                .filter(annotation -> annotation.annotationType() == reflectAnnotation).findFirst().get();

    }

    public static String[] getNullPropertyNames (Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<>();

        for(java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) {
                emptyNames.add(pd.getName());
            }
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }
}
