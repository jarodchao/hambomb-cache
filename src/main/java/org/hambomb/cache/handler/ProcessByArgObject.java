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

import org.hambomb.cache.CacheUtils;
import org.hambomb.cache.context.HanmbombRuntimeException;

import java.lang.reflect.Method;

/**
 * @author: <a herf="mailto:jarodchao@126.com>jarod </a>
 * @date: 2019-03-27
 */
public class ProcessByArgObject implements MethodArguments {

    @Override
    public String[] values(Object[] argValues, String[] args) {


        String[] values = new String[args.length];

        for (int i = 0; i < args.length; i++) {

            Method getter = CacheUtils.getGetterMethod(args[i], argValues[0].getClass());

            if (getter == null) {
                throw new HanmbombRuntimeException(String.format("The get method for {} was not found in the class[{}]."
                        , CacheUtils.getter(args[i]), argValues[0].getClass().getSimpleName()));
            }

            values[i] = CacheUtils.getValueByMethod(argValues[0], getter);
        }

        return values;

    }
}
