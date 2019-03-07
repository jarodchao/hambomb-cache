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

import org.hambomb.cache.handler.annotation.AfterUpdateProcess;
import org.hambomb.cache.handler.annotation.PostGetProcess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: <a herf="mailto:jarodchao@126.com>jarod </a>
 * @date: 2019-03-05
 */
@Service
public class PersonService {


    @Autowired
    private PersonMapper personMapper;

    @PostGetProcess(args = {"#0","#1","#2"})
    public Person getPerson(String name, Integer age, String sex) {

        return personMapper.selectPersion(name, age, sex);

    }

    @PostGetProcess(args = {"#0","#1"})
    public Person getPerson(String name, Integer age) {

        return personMapper.selectPersion(name, age, "");

    }

    @PostGetProcess(keys = {"name","age","sex"})
    public Person getPerson(FindPerson findPerson) {
        return personMapper.selectPersion(findPerson.name, findPerson.age, findPerson.sex);
    }

    @AfterUpdateProcess
    public void updatePerson(Person person) {

    }

    public void modifyPerson(String name, Integer age, String sex,String address) {
        Person person = personMapper.selectPersion(name, age, sex);

        person.setAddress(address);

        personMapper.updatePerson();

    }

}
