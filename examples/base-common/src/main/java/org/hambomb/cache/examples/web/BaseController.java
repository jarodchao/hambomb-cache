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

import org.hambomb.cache.examples.entity.BPerson;
import org.hambomb.cache.examples.entity.Person;
import org.hambomb.cache.examples.entity.Phone;
import org.hambomb.cache.examples.service.PersonService;
import org.hambomb.cache.examples.service.PhoneCond;
import org.hambomb.cache.examples.service.PhoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * @author: <a herf="mailto:jarodchao@126.com>jarod </a>
 * @date: 2019-03-08
 */
@RestController
public class BaseController {

    @Autowired
    private PersonService personService;

    @Autowired
    private PhoneService phoneService;

    @GetMapping(path = "/hambomb/person/{id}")
    public Mono<Person> getPerson(@PathVariable Long id) {

        return Mono.just(personService.getPersonById(id));
    }

    @GetMapping(path = "/hambomb/cardIds/{cardId}")
    public Person getPersonByCardId(@PathVariable String cardId) {

        return personService.getPersonByCardId(cardId);

    }

    @PutMapping(path = "/hambomb/persons")
    public String putPersonById(@RequestBody Person person) {
        try {
            personService.modifyAddressById(person);
        } catch (Exception e) {
            return "Fail";
        }

        return "isOk";
    }

    @PostMapping(path = "/hambomb/persons")
    public String postPersonById(@RequestBody Person person) {
        try {
            personService.insertPerson(person);
        } catch (Exception e) {
            return "Fail";
        }

        return "isOk";
    }

    @PostMapping(path = "/hambomb/bpersons")
    public String postPersonById(@RequestBody BPerson person) {
        try {
            personService.insertPerson(person);
        } catch (Exception e) {
            return "Fail";
        }

        return "isOk";
    }

    @DeleteMapping(path = "/hambomb/persons/{id}")
    public void deletePerson(@PathVariable Long id) {

        personService.deletePersonById(id);
    }

    @GetMapping(path = "/hambomb/phones")
    public Mono<Phone> getPhoneByCond(@RequestParam String brand,
                                       @RequestParam String model,
                                      @RequestParam Integer memory,
                                      @RequestParam String color) {

        PhoneCond cond = new PhoneCond(brand, model, memory, color);

        return Mono.just(phoneService.getPhoneByCond(cond));
    }

    @PutMapping(path = "/hambomb/phones")
    public Mono<String> putPhoneByObject(@RequestBody Phone phone) {

        phoneService.modifyPhone(phone);
        return Mono.just("isOK");

    }

    @DeleteMapping(path = "/hambomb/phones")
    public Mono<String> deletePhoneByObject(@RequestParam String brand,
                                         @RequestParam String model,
                                         @RequestParam Integer memory,
                                         @RequestParam String color) {
        PhoneCond cond = new PhoneCond(brand, model, memory, color);
        phoneService.deletePhone(cond);
        return Mono.just("isOK");
    }

    @DeleteMapping(path = "/hambomb/phones/{brand}/{model}/{memory}/{color}")
    public Mono<String> deletePhoneByObject1(@PathVariable(value = "brand") String brand,
                                         @PathVariable(value = "model") String model,
                                         @PathVariable(value = "memory") Integer memory,
                                         @PathVariable(value = "color") String color) {
        PhoneCond cond = new PhoneCond(brand, model, memory, color);
        phoneService.deletePhone(cond);
        return Mono.just("isOK");
    }

}
