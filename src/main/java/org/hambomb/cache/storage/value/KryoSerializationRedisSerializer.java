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
package org.hambomb.cache.storage.value;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;
import org.objenesis.strategy.StdInstantiatorStrategy;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.SerializationUtils;
import org.springframework.lang.Nullable;

import java.io.ByteArrayOutputStream;

/**
 * @author: <a herf="mailto:jarodchao@126.com>jarod </a>
 * @date: 2019-03-13
 */
public class KryoSerializationRedisSerializer<T> implements RedisSerializer<T> {

    private static final KryoFactory factory = () -> {
        Kryo kryo = new Kryo();
        try {

            kryo.setRegistrationRequired(false);
            ((Kryo.DefaultInstantiatorStrategy) kryo.getInstantiatorStrategy())
                    .setFallbackInstantiatorStrategy(new StdInstantiatorStrategy());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return kryo;
    };

    private static final KryoPool pool = new KryoPool.Builder(factory).softReferences().build();

    @Nullable
    @Override
    public byte[] serialize(@Nullable T t) throws SerializationException {

        return pool.run(kryo -> {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Output output = new Output(stream);
            kryo.writeClassAndObject(output, t);
            output.close();
            return stream.toByteArray();
        });

    }

    @Nullable
    @Override
    public T deserialize(@Nullable byte[] bytes) throws SerializationException {

        if ((bytes == null || bytes.length == 0)) {
            return null;
        }

        return pool.run(kryo -> {
            Input input = new Input(bytes);
            return (T) kryo.readClassAndObject(input);
        });

    }


}
