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
package com.baomidou.mybatisplus.test;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.test.mysql.entity.CommonData;
import com.baomidou.mybatisplus.test.mysql.entity.CommonLogicData;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaClass;
import org.junit.jupiter.api.Test;

class SampleTest {

    @Test
    void testTableInfoHelper2() {
        TableInfo info = TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), CommonLogicData.class);
//        System.out.println("----------- AllInsertSqlColumn -----------");
//        System.out.println(info.getAllInsertSqlColumn());
//        System.out.println("----------- AllInsertSqlProperty -----------");
//        System.out.println(info.getAllInsertSqlProperty());
        System.out.println("----------- AllSqlSet -----------");
        System.out.println(info.getAllSqlSet(true, "ew.entity."));
        System.out.println("----------- AllSqlWhere -----------");
        System.out.println(info.getAllSqlWhere(false, true, "ew.entity."));
    }

    @Test
    void testTableInfoHelper3() {
        MetaClass metaClass = MetaClass.forClass(CommonData.class, new DefaultReflectorFactory());
        String property = metaClass.findProperty("TESTINT", true);
        System.out.println(property);
    }

    @Test
    void testWrapperOrderBy() {
        System.out.println(Wrappers.query().orderByAsc("1", "2", "3", "4").getSqlSegment());
        System.out.println(Wrappers.query().orderByDesc("1", "2", "3", "4").getSqlSegment());
    }

    @Test
    void testClone() {
        QueryWrapper<Object> wrapper = Wrappers.query().orderByAsc("1", "2", "3", "4");
        QueryWrapper<Object> clone = wrapper.clone().orderByDesc("5", "6", "7");
        System.out.println(wrapper.getSqlSegment());
        System.out.println(clone.getSqlSegment());
    }

    @Test
    void testPrefixOrder() {
        System.out.println(Wrappers.query().eq("order_id", 1).getSqlSegment());
    }
}
