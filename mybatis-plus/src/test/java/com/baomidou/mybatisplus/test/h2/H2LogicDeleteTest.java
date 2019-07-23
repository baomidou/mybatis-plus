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

import com.baomidou.mybatisplus.test.h2.entity.H2User;
import com.baomidou.mybatisplus.test.h2.entity.H2UserLogicDelete;
import com.baomidou.mybatisplus.test.h2.enums.AgeEnum;
import com.baomidou.mybatisplus.test.h2.mapper.H2UserLogicDeleteMapper;

/**
 * Mybatis Plus H2 Junit Test
 *
 * @author hubin
 * @since 2018-06-05
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:h2/spring-logic-delete-h2.xml"})
class H2LogicDeleteTest extends BaseTest {

    @Resource
    protected H2UserLogicDeleteMapper logicDeleteMapper;

    @Test
    @Order(1)
    void crudTest() {
        String name = "LogicDelete4Date";
        H2UserLogicDelete user = new H2UserLogicDelete();
        user.setAge(AgeEnum.ONE);
        user.setName(name);
        logicDeleteMapper.insert(user);
        Assertions.assertNotNull(user.getTestId(), "id should not be null");
        Assertions.assertNull(user.getLastUpdatedDt());

        logicDeleteMapper.deleteById(user.getTestId());

        List<H2User> userList = queryByName(name);
        Assertions.assertTrue(userList!=null && !userList.isEmpty());
        Assertions.assertNotNull(userList.get(0).getLastUpdatedDt(),"lastUpdateDt should not be null after logic delete");

    }

}
