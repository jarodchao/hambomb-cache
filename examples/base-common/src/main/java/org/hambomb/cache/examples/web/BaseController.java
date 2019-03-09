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
package org.hambomb.cache.examples.web;

import org.hambomb.cache.examples.entity.Person;
import org.hambomb.cache.examples.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: <a herf="mailto:jarodchao@126.com>jarod </a>
 * @date: 2019-03-08
 */
@RestController
public class BaseController {

    @Autowired
    private PersonService personService;

    @GetMapping(path = "/hambomb/person/{id}")
    public Person getPerson(@PathVariable Long id) {

        return personService.getPersonById(id);
    }

    @GetMapping(path = "/hambomb/cardIds/{cardId}")
    public Person getPersonByCardId(@PathVariable String cardId) {

        return personService.getPersonByCardId(cardId);

    }

}
