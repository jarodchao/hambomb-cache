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

import com.google.common.base.Predicate;
import org.hambomb.cache.context.HanmbombRuntimeException;
import org.hambomb.cache.loader.CacheObjectLoader;
import org.reflections.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.util.*;

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

        Optional<Method> method = getters.stream().findFirst();

        if (method.isPresent()) {
            return method.get();
        }

        return null;
    }

    public static <T extends Annotation> Annotation getAnnotation(Method method, Class<T> reflectAnnotation) {

        Predicate<Annotation> findAnnotation = input -> input != null && input.annotationType() == reflectAnnotation;


        Set<Annotation> annotations = ReflectionUtils.getAnnotations(method, findAnnotation);

        Optional<Annotation> annotation = annotations.stream().findFirst();

        if (annotation.isPresent()) {
            return annotation.get();
        }

        return null;
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

    public static String toStringForDate(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        DateFormat dateFormat;

        int h = c.get(Calendar.HOUR);
        int m = c.get(Calendar.MINUTE);
        int s = c.get(Calendar.SECOND);

        if (h == m && m == s && s == 0) {
            dateFormat = DateFormat.getDateInstance();
            dateFormat = DateFormat.getInstance();
        }else {
            dateFormat = DateFormat.getDateInstance();
        }

//        return dateFormat.format(date);

        return String.valueOf(date.getTime());

    }

    public static String getValueByMethod(Object t, Method method) {
        try {

            Type type = method.getGenericReturnType();

            if (type == Date.class) {
                return CacheUtils.toStringForDate((Date) method.invoke(t, null));
            } else {

                return method.invoke(t, null).toString();
            }

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            throw new HanmbombRuntimeException(String.format("There is no method %s on the object. %s"
                    , method.getName(), e.getMessage()));
        } catch (NullPointerException e) {
            throw new HanmbombRuntimeException(String.format("Executes method %s with no value on the object. %s"
                    , method.getName(), e.getMessage()));
        }
        return null;
    }
}
