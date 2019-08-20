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

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.test.h2.entity.H2UserStrategy;
import com.baomidou.mybatisplus.test.h2.mapper.H2UserStrategyMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Mybatis Plus H2 Junit Test
 *
 * @author Caratacus
 * @since 2017/4/1
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:h2/spring-test-h2.xml"})
class H2UserStrategyTest extends BaseTest {

    @Autowired
    protected H2UserStrategyMapper userStrategyMapper;

    @Test
    @Order(1)
    void testNewlyAdded3Strategy() {
        H2UserStrategy insertUser = new H2UserStrategy();
        insertUser.setName("userStrategy").setDesc("updateStrategy=IGNORE").setVersion(1);
        int row = userStrategyMapper.insert(insertUser);
        Long id = insertUser.getTestId();
        Assertions.assertEquals(1, row);
        Assertions.assertEquals(3, userStrategyMapper.selectById(id).getTestType(), "autofilled with 3");

        QueryWrapper<H2UserStrategy> wrapper = new QueryWrapper<>(new H2UserStrategy().setDesc("updateStrategy=IGNORE").setTestType(3));
        Assertions.assertEquals(0, userStrategyMapper.selectCount(wrapper), "name is whereStrategy=IGNORE, so should have where name=null which cause count=0");

        H2UserStrategy updateUser = new H2UserStrategy().setName("update");
        updateUser.setTestId(id);
        Assertions.assertEquals(1, userStrategyMapper.updateById(updateUser));

        H2UserStrategy selectUser = userStrategyMapper.selectById(id);
        Assertions.assertEquals(selectUser.getName(), "update");
        Assertions.assertNull(selectUser.getDesc(), "desc is updateStrategy=IGNORE, so should have set desc=null when updateById");
        Assertions.assertNull(selectUser.getTestType(), "handle: strategy=IGNORED, should be set test_type=null when updateById ");

    }


}
