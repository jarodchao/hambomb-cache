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

import org.hambomb.cache.cluster.HambombCacheConfigForDevelop1;
import org.hambomb.cache.db.entity.FindPerson;
import org.hambomb.cache.db.entity.Person;
import org.hambomb.cache.db.entity.PersonService;
import org.hambomb.cache.handler.CacheHandler;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author: <a herf="mailto:jarodchao@126.com>jarod </a>
 * @date: 2019-03-05
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {HambombCacheConfigForDevelop1.class})
public class TestHambombCacheDevelop1 {

    @Autowired
    private PersonService personService;

    @Autowired
    private CacheHandler cacheHandler;


    @Test
    public void test_getPerson1() {


        FindPerson findPerson = new FindPerson();
        findPerson.name = "hambomb";
        findPerson.age = 3;
        findPerson.sex = "男";

        Person person = personService.getPerson(findPerson);

        Assert.assertTrue("没有命中cache", !person.getAddress().equals(""));
    }

    @Test
    public void test_getPerson2() {


        Person findPerson = new Person(2L,null,null,null,"纽约");

        personService.updatePerson(findPerson);

        Person cacheObject = (Person) cacheHandler.get("Person-Primary-2");

        Assert.assertEquals("不等于", cacheObject.getAddress(), findPerson.getAddress());

    }
}
