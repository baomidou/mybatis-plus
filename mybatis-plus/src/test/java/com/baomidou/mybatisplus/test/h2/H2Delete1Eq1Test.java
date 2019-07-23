/*
 * Copyright (c) 2011-2019, hubin (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.test.h2;

import java.util.List;

import javax.annotation.Resource;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.test.h2.entity.H2Student;
import com.baomidou.mybatisplus.test.h2.entity.H2User;
import com.baomidou.mybatisplus.test.h2.enums.AgeEnum;
import com.baomidou.mybatisplus.test.h2.mapper.H2StudentMapper;
import com.baomidou.mybatisplus.test.h2.mapper.H2UserMapper;

/**
 * Mybatis Plus H2 Junit Test
 *
 * @author hubin
 * @since 2018-06-05
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:h2/spring-test-h2.xml"})
class H2Delete1Eq1Test extends BaseTest {

    @Resource
    protected H2UserMapper logicDeleteMapper;
    @Resource
    protected H2StudentMapper defaultMapper;

    @Test
    @Order(1)
    void crudTest() {
        for (int i = 0; i < 10; i++) {
            logicDeleteMapper.insert(new H2User("mp" + i, AgeEnum.ONE));
        }
        log(logicDeleteMapper.selectList(new QueryWrapper<H2User>().orderByAsc("`desc`")));
        log(logicDeleteMapper.selectOne(new QueryWrapper<H2User>().last("limit 1")));

        H2User h2User = new H2User();
        h2User.setDesc("1");
        h2User.setName("2");
        log(logicDeleteMapper.selectList(new QueryWrapper<>(h2User).orderByAsc("name")));

        for (long i = 0; i < 10L; i++) {
            defaultMapper.insert(new H2Student(i, "Tom长大了", 1));
        }
        log(logicDeleteMapper.selectList(new QueryWrapper<>(h2User).eq("name", "2").orderByAsc("name")));
        log(defaultMapper.selectList(new QueryWrapper<H2Student>().orderByAsc("id")));
        log(defaultMapper.selectOne(new QueryWrapper<H2Student>().last("limit 1")));

        H2Student h2Student = new H2Student();
        h2Student.setId(1L);
        h2Student.setAge(2);
        log(defaultMapper.selectList(new QueryWrapper<>(h2Student).orderByAsc("id")));
    }

    @Test
    @Order(Integer.MAX_VALUE)
    void delete() {
        logicDeleteMapper.delete(new QueryWrapper<>());
        defaultMapper.delete(new QueryWrapper<>());
        Assertions.assertEquals(0, (int) logicDeleteMapper.selectCount(new QueryWrapper<>()));
        List<H2User> userList = queryByName(null);
        System.out.println(userList.size());
        Assertions.assertNotEquals(0, userList.size());
    }
}
