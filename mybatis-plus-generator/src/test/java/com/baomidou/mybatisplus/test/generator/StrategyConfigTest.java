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
import java.util.Set;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.test.generator.entity.BaseEntity;
import com.baomidou.mybatisplus.test.generator.entity.SuperEntity;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <p>
 * 策略测试
 * </p>
 *
 * @author hubin
 * @since 2019-02-20
 */
class StrategyConfigTest {

    @Test
    void baseEntity() {
        StrategyConfig strategyConfig = new StrategyConfig();
        strategyConfig.setSuperEntityClass(BaseEntity.class);
        Set<String> columns = strategyConfig.getSuperEntityColumns();
        columns.forEach(System.out::println);
        assertThat(columns).containsAll(Arrays.asList("deleted", "createTime", "id"));
        Assertions.assertEquals(columns.size(), 3);
    }

    @Test
    void baseEntityNaming() {
        StrategyConfig strategyConfig = new StrategyConfig();
        strategyConfig.setSuperEntityClass(BaseEntity.class, NamingStrategy.underline_to_camel);
        Set<String> columns = strategyConfig.getSuperEntityColumns();
        columns.forEach(System.out::println);
        assertThat(columns).containsAll(Arrays.asList("deleted", "create_time", "id"));
        Assertions.assertEquals(columns.size(), 3);

        strategyConfig = new StrategyConfig();
        strategyConfig.setSuperEntityColumns("aa", "bb").setSuperEntityClass(BaseEntity.class, NamingStrategy.underline_to_camel);
        Assertions.assertEquals(strategyConfig.getSuperEntityColumns().size(), 5);
        assertThat(strategyConfig.getSuperEntityColumns()).containsAll(Arrays.asList("aa", "bb", "deleted", "create_time", "id"));

        strategyConfig = new StrategyConfig();
        strategyConfig.setSuperEntityClass(BaseEntity.class, NamingStrategy.underline_to_camel).setSuperEntityColumns("aa", "bb");
        Assertions.assertEquals(strategyConfig.getSuperEntityColumns().size(), 5);
        assertThat(strategyConfig.getSuperEntityColumns()).containsAll(Arrays.asList("aa", "bb", "deleted", "create_time", "id"));
    }

    @Test
    void superEntity() {
        StrategyConfig strategyConfig = new StrategyConfig();
        strategyConfig.setSuperEntityClass(SuperEntity.class);
        Set<String> columns = strategyConfig.getSuperEntityColumns();
        columns.forEach(System.out::println);
        assertThat(columns).containsAll(Arrays.asList("deleted", "id"));
        Assertions.assertEquals(columns.size(), 2);
    }

    @Test
    void testSuperAnnotation() {
        StrategyConfig strategyConfig;

        strategyConfig = new StrategyConfig();
        strategyConfig.setSuperEntityClass(SuperBean.class).setColumnNaming(NamingStrategy.no_change);
        assertThat(strategyConfig.getSuperEntityColumns()).containsAll(Arrays.asList("test_id", "aa_name", "ok", "testName"));

        strategyConfig = new StrategyConfig();
        strategyConfig.setSuperEntityClass(SuperBean.class, NamingStrategy.no_change);
        assertThat(strategyConfig.getSuperEntityColumns()).containsAll(Arrays.asList("test_id", "aa_name", "ok", "testName"));

        strategyConfig = new StrategyConfig();
        strategyConfig.setSuperEntityClass(SuperBean.class).setColumnNaming(NamingStrategy.underline_to_camel);
        assertThat(strategyConfig.getSuperEntityColumns()).containsAll(Arrays.asList("test_id", "aa_name", "ok", "test_name"));

        strategyConfig = new StrategyConfig();
        strategyConfig.setSuperEntityClass(SuperBean.class, NamingStrategy.underline_to_camel);
        assertThat(strategyConfig.getSuperEntityColumns()).containsAll(Arrays.asList("test_id", "aa_name", "ok", "test_name"));

    }

    @Test
    void startsWithTablePrefixTest(){
        StrategyConfig strategyConfig = new StrategyConfig();
        Assertions.assertFalse(strategyConfig.startsWithTablePrefix("t_name"));
        strategyConfig.setTablePrefix("a_","t_");
        Assertions.assertTrue(strategyConfig.startsWithTablePrefix("t_name"));
    }

    @Data
    static class SuperBean {

        @TableId(value = "test_id")
        private String id;

        @TableField(value = "aa_name")
        private String name;

        private String ok;

        private String testName;

    }
}
