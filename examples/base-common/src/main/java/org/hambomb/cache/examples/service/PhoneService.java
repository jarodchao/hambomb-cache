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
package org.hambomb.cache.examples.service;

import org.hambomb.cache.examples.entity.Phone;
import org.hambomb.cache.examples.mapper.PhoneMapper;
import org.hambomb.cache.handler.annotation.AfterDeleteProcess;
import org.hambomb.cache.handler.annotation.AfterUpdateProcess;
import org.hambomb.cache.handler.annotation.PostGetProcess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: <a herf="mailto:jarodchao@126.com>jarod </a>
 * @date: 2019-03-11
 */
@Service
public class PhoneService {

    @Autowired
    private PhoneMapper phoneMapper;


    @PostGetProcess(attrs = {"brand", "model", "memory", "color"} )
    public Phone getPhoneByCond(PhoneCond cond) {

        return phoneMapper.selectPhoneByCond(cond);
    }

    @AfterUpdateProcess(byPrimaryKey = false, attrs = {"brand", "model", "memory", "color"})
    public void modifyPhone(Phone modify) {

        phoneMapper.updatePhoneByCond(modify);

    }

    @AfterDeleteProcess(byPrimaryKey = false, attrs = {"brand", "model", "memory", "color"}, cacheObjectClass = Phone.class)
    public void deletePhone(PhoneCond cond) {
        phoneMapper.deletePhoneByCond(cond);
    }

    @AfterDeleteProcess(byPrimaryKey = false, attrs = {"brand", "model", "memory", "color"}, cacheObjectClass = Phone.class)
    public void deletePhone(String brand, String model, Integer memory, String color) {

        phoneMapper.deletePhoneByCond(new PhoneCond(brand, model, memory, color));
    }
}
