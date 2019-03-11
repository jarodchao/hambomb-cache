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

import java.io.Serializable;

/**
 * @author: <a herf="mailto:jarodchao@126.com>jarod </a>
 * @date: 2019-03-11
 */
public class PhoneCond implements Serializable {


    private static final long serialVersionUID = -505998710747530462L;

    private String brand;

    private String model;

    private Integer memory;

    private String color;

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getMemory() {
        return memory;
    }

    public void setMemory(Integer memory) {
        this.memory = memory;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public PhoneCond(String brand, String model, Integer memory, String color) {
        this.brand = brand;
        this.model = model;
        this.memory = memory;
        this.color = color;
    }
}
