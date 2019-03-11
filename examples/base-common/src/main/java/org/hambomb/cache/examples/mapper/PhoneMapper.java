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
package org.hambomb.cache.examples.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.hambomb.cache.examples.entity.Phone;
import org.hambomb.cache.examples.service.PhoneCond;

/**
 * @author: <a herf="matilto:jarodchao@126.com>jarod </a>
 * @date: 2019-03-11
 */
@Mapper
public interface PhoneMapper {


    @Select("select id,Brand, model, memory, color, weight, pattern, origin " +
            "from t_phone " +
            "where brand = #{brand,jdbcType=VARCHAR} " +
            "and model = #{model,jdbcType=VARCHAR} " +
            "and memory = #{memory,jdbcType=DECIMAL} " +
            "and color = #{color,jdbcType=VARCHAR}")
    Phone selectPhoneByCond(PhoneCond cond);


    @Update("update t_phone " +
            "set origin = #{brand,jdbcType=VARCHAR} " +
            "where brand = #{brand,jdbcType=VARCHAR} " +
            "and model = #{model,jdbcType=VARCHAR} " +
            "and memory = #{memory,jdbcType=DECIMAL} " +
            "and color = #{color,jdbcType=VARCHAR}")
    void updatePhoneByCond(Phone phone);


    @Delete("delete from t_phone " +
            "where brand = #{brand,jdbcType=VARCHAR} " +
            "and model = #{model,jdbcType=VARCHAR} " +
            "and memory = #{memory,jdbcType=DECIMAL} " +
            "and color = #{color,jdbcType=VARCHAR}")
    void deletePhoneByCond(PhoneCond cond);
}
