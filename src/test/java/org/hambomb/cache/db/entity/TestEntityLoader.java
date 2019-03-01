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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * @author: <a herf="mailto:jarodchao@126.com>jarod </a>
 * @date: 2019-02-26
 */

public class TestEntityLoader {


    private EntityLoader<Person> personLoader;


    @Before
    public void setUp() throws Exception {

        personLoader = new EntityLoader<>(new PersonMapper());
    }

    @Test
    public void test_initializeLoader() {

        Assert.assertEquals("entityClassName is error",personLoader.entityClassName,"Person");
        Assert.assertEquals("entityPackageName is error",personLoader.entityPackageName,"c.j.hambomb.cache.db.entity");
    }

    @Test
    public void test_loadEntities(){

        List<Person> data = personLoader.loadEntities();

        data.stream().forEach(person -> System.out.println(person));

        Assert.assertFalse("data is null", data == null);

    }

}
