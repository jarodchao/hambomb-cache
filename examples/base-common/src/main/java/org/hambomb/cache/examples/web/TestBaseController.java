package org.hambomb.cache.examples.web;/*
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

import org.hambomb.cache.examples.entity.Person;
import org.hambomb.cache.examples.entity.Phone;
import org.hambomb.cache.examples.service.PhoneCond;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * @author: <a herf="mailto:jarodchao@126.com>jarod </a>
 * @date: 2019-03-09
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class TestBaseController {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    @Ignore
    public void test_getPersonByCardId() throws Exception {

        String cardId = "10000000000000005";

        this.webTestClient.get().uri("/hambomb/cardIds/{cardId}", cardId)
                .exchange().expectStatus().isOk().expectBody(String.class).value(s1 -> System.out.println(s1));
    }

    @Test
    @Ignore
    public void test_putPersonById() {
        Person person = new Person();

        person.setId(1L);
        person.setAddress("中国河北西伯坡");
        this.webTestClient.put().uri("/hambomb/persons").syncBody(person)
                .exchange().expectStatus().isOk()
                .expectBody(String.class).value(s -> System.out.println(s));
    }

    @Test
    public void test_PostPeron() {

        Person person = new Person();
        person.setAddress("ddd");
        person.setAge(11);
        person.setCardId("11111111111111");
        person.setGender("nan");
        person.setHeight("111");
        person.setWeight(100.0);

        this.webTestClient.post().uri("/hambomb/persons").syncBody(person)
                .exchange().expectStatus().isOk()
                .expectBody(String.class).value(s -> System.out.println(s));

    }

    @Test
    @Ignore
    public void test_deletePerson(){
        this.webTestClient.delete().uri("/hambomb/persons/{id}", 2L)
                .exchange().expectStatus().isOk();
    }

    @Test
    @Ignore
    public void test_getPhoneByCond(){

        PhoneCond cond = new PhoneCond("华为", "Mate 20", 16, "黑色");
        this.webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/hambomb/phones")
                        .queryParam("brand", cond.getBrand())
                        .queryParam("model", cond.getModel())
                        .queryParam("memory", cond.getMemory())
                        .queryParam("color", cond.getColor()).build()).exchange().expectStatus().isOk();
    }

    @Test
    @Ignore
    public void test_putPhoneByObject() {

        Phone phone = new Phone("华为", "Mate 20", 32, "黑色","拉萨");

        this.webTestClient.put().uri("/hambomb/phones").syncBody(phone)
                .exchange().expectStatus().isOk()
                .expectBody(String.class).value(s -> System.out.println(s));


    }

    @Test
    @Ignore
    public void test_deletePhoneByObject() {

        PhoneCond cond = new PhoneCond("华为", "Mate 20", 16, "黑色");

        this.webTestClient.delete().uri(uriBuilder -> uriBuilder.path("/hambomb/phones")
                .queryParam("brand", cond.getBrand())
                .queryParam("model", cond.getModel())
                .queryParam("memory", cond.getMemory())
                .queryParam("color", cond.getColor()).build()).exchange().expectStatus().isOk();


    }

    @Test
    @Ignore
    public void test_deletePhoneByObject1() {

        PhoneCond cond = new PhoneCond("华为", "Mate 20", 16, "银色");

        this.webTestClient.delete().uri(uriBuilder -> uriBuilder.path("/hambomb/phones")
                .pathSegment(cond.getBrand(),cond.getModel(),String.valueOf(cond.getMemory()),cond.getColor())
                .build()).exchange().expectStatus().isOk();


    }

}
