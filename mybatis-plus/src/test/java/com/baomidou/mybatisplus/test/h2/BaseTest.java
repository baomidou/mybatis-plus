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

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import org.junit.jupiter.api.AfterAll;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;

import com.baomidou.mybatisplus.test.h2.entity.H2User;
import com.baomidou.mybatisplus.test.h2.enums.AgeEnum;

@DirtiesContext
public class BaseTest {

    @Resource
    protected JdbcTemplate jdbcTemplate;

    protected static final String NQQ = "聂秋秋";

    protected void log(Object object) {
        System.out.println(object);
    }

    protected List<H2User> queryByName(String name) {
        String sql = "select TEST_ID, NAME, AGE,LAST_UPDATED_DT from h2user ";
        if (name != null) {
            sql += "where name='" + name + "'";
        }
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            H2User u = new H2User();
            u.setTestId(rs.getLong("TEST_ID"));
            u.setName(rs.getString("NAME"));
            u.setAge(AgeEnum.parseValue(rs.getInt("AGE")));
            u.setLastUpdatedDt(rs.getDate("LAST_UPDATED_DT"));
            return u;
        });
    }

    @AfterAll
    public static void afterAll() throws NoSuchFieldException, IllegalAccessException {
        Field tableInfoCache = TableInfoHelper.class.getDeclaredField("TABLE_INFO_CACHE");
        tableInfoCache.setAccessible(true);
        Map<Class<?>, TableInfo> tableInfoMap = (Map<Class<?>, TableInfo>) tableInfoCache.get(TableInfoHelper.class);
        tableInfoMap.clear();
    }
}
