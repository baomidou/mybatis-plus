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
import com.baomidou.mybatisplus.extension.plugins.pagination.dialects.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author nieqiuqiu
 */
class DbTypeTest {

    private static final Map<DbType, Class<? extends IDialect>> DIALECT_MAP = new ConcurrentHashMap<>();

    static {
        DIALECT_MAP.put(DbType.DB2, DB2Dialect.class);
        DIALECT_MAP.put(DbType.DM, DmDialect.class);
        DIALECT_MAP.put(DbType.H2, H2Dialect.class);
        DIALECT_MAP.put(DbType.MYSQL, MySqlDialect.class);
        DIALECT_MAP.put(DbType.MARIADB, MariaDBDialect.class);
        DIALECT_MAP.put(DbType.ORACLE, OracleDialect.class);
        DIALECT_MAP.put(DbType.POSTGRE_SQL, PostgreDialect.class);
        DIALECT_MAP.put(DbType.SQL_SERVER, SQLServerDialect.class);
        DIALECT_MAP.put(DbType.SQL_SERVER2005, SQLServer2005Dialect.class);
        DIALECT_MAP.put(DbType.SQLITE, SQLiteDialect.class);
        DIALECT_MAP.put(DbType.HSQL, HSQLDialect.class);
        DIALECT_MAP.put(DbType.XU_GU, XuGuDialect.class);
        DIALECT_MAP.put(DbType.KINGBASE_ES, KingbaseDialect.class);
        DIALECT_MAP.put(DbType.OTHER, UnknownDialect.class);
    }

    @Test
    void test() throws ClassNotFoundException {
        DbType[] values = DbType.values();
        Assertions.assertEquals(values.length, DIALECT_MAP.size());
        for (DbType dbType : values) {
            Class<?> aClass = Class.forName(dbType.getDialect());
            Assertions.assertEquals(aClass, DIALECT_MAP.get(dbType));
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
