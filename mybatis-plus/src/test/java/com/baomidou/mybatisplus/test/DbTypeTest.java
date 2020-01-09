/*
 * Copyright (c) 2011-2020, baomidou (jobob@qq.com).
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

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.pagination.DialectFactory;
import com.baomidou.mybatisplus.extension.plugins.pagination.dialects.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * @author nieqiuqiu
 */
class DbTypeTest {

    private DialectRegistry dialectRegistry = new DialectRegistry();

    @Test
    void test() throws ClassNotFoundException {
        DbType[] values = DbType.values();
        Assertions.assertEquals(values.length, dialectRegistry.getDialects().size());
        for (DbType dbType : values) {
            Class<?> aClass = Class.forName(dbType.getDialect());
            IDialect dialect = DialectFactory.getDialect(dbType);
            Assertions.assertEquals(dialect.getClass(), aClass);
            Assertions.assertEquals(aClass, dialectRegistry.getDialect(dbType).getClass());
        }
    }

    @Test
    void testGetDbType() {
        Assertions.assertEquals(DbType.MYSQL, DbType.getDbType("mysql"));
        Assertions.assertEquals(DbType.MYSQL, DbType.getDbType("Mysql"));
        Assertions.assertEquals(DbType.OTHER, DbType.getDbType("other"));
        Assertions.assertEquals(DbType.OTHER, DbType.getDbType("unknown"));
    }
}
