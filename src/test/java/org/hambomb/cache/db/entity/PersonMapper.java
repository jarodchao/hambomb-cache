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
package org.hambomb.cache.db.entity;


import org.springframework.stereotype.Component;

import java.net.Inet4Address;
import java.util.Arrays;
import java.util.List;

/**
 * @author: <a herf="matilto:jarodchao@126.com>jarod </a>
 * @date: 2019-02-26
 */
@Component
public class PersonMapper implements CacheObjectMapper<Person> {


    @Override
    public List<Person> selectAllCacheObject() {

        Person person1 = new Person(1L, "mike", 10,"男","北京");
        Person person2 = new Person(2L, "hambomb", 3,"男","北京");
        Person person3 = new Person(3L, "Tom", 15,"男","北京");


        return Arrays.asList(person1, person2, person3);
    }


    public Person selectPersion(String name, Integer age, String sex) {
        return new Person(1L, "mike", 10, "男","上海");
    }

    public void updatePerson() {

    }

}
