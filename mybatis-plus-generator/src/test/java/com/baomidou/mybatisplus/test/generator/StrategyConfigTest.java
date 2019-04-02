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
package com.baomidou.mybatisplus.test.generator;

import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.test.generator.entity.BaseEntity;
import com.baomidou.mybatisplus.test.generator.entity.SuperEntity;

/**
 * <p>
 * 策略测试
 * </p>
 *
 * @author hubin
 * @since 2019-02-20
 */
public class StrategyConfigTest {

    @Test
    public void baseEntity() {
        StrategyConfig strategyConfig = new StrategyConfig();
        strategyConfig.setSuperEntityClass(BaseEntity.class);
        String[] columns = strategyConfig.getSuperEntityColumns();
        Arrays.stream(columns).forEach(column -> System.out.println(column));
        Assertions.assertEquals(columns.length, 3);
    }

    @Test
    public void baseEntityNaming() {
        StrategyConfig strategyConfig = new StrategyConfig();
        strategyConfig.setSuperEntityClass(BaseEntity.class, NamingStrategy.underline_to_camel);
        String[] columns = strategyConfig.getSuperEntityColumns();
        Arrays.stream(columns).forEach(column -> System.out.println(column));
        Assertions.assertEquals(columns.length, 3);
    }

    @Test
    public void superEntity() {
        StrategyConfig strategyConfig = new StrategyConfig();
        strategyConfig.setSuperEntityClass(SuperEntity.class);
        String[] columns = strategyConfig.getSuperEntityColumns();
        Arrays.stream(columns).forEach(column -> System.out.println(column));
        Assertions.assertEquals(columns.length, 2);
    }
}
