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
package com.baomidou.mybatisplus.extension.plugins.pagination.dialects;

import com.baomidou.mybatisplus.annotation.DbType;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

/**
 * 数据库方言注入
 *
 * @author nieqiuqiu
 * @since 2020-01-09
 */
public class DialectRegistry {

    private final Map<DbType, IDialect> dialect_enum_map = new EnumMap<>(DbType.class);

    public DialectRegistry() {
        // mysql and children
        dialect_enum_map.put(DbType.MYSQL, new MySqlDialect());
        dialect_enum_map.put(DbType.MARIADB, new MySqlDialect());
        dialect_enum_map.put(DbType.GBASE, new MySqlDialect());
        dialect_enum_map.put(DbType.OSCAR, new MySqlDialect());
        dialect_enum_map.put(DbType.XU_GU, new MySqlDialect());
        dialect_enum_map.put(DbType.CLICK_HOUSE, new MySqlDialect());
        dialect_enum_map.put(DbType.OCEAN_BASE, new MySqlDialect());
        // postgresql and children
        dialect_enum_map.put(DbType.POSTGRE_SQL, new PostgreDialect());
        dialect_enum_map.put(DbType.H2, new PostgreDialect());
        dialect_enum_map.put(DbType.SQLITE, new PostgreDialect());
        dialect_enum_map.put(DbType.HSQL, new PostgreDialect());
        dialect_enum_map.put(DbType.KINGBASE_ES, new PostgreDialect());
        dialect_enum_map.put(DbType.PHOENIX, new PostgreDialect());
        // oracle and children
        dialect_enum_map.put(DbType.ORACLE, new OracleDialect());
        dialect_enum_map.put(DbType.DM, new OracleDialect());
        dialect_enum_map.put(DbType.GAUSS, new OracleDialect());
        // other
        dialect_enum_map.put(DbType.ORACLE_12C, new Oracle12cDialect());
        dialect_enum_map.put(DbType.DB2, new DB2Dialect());
        dialect_enum_map.put(DbType.SQL_SERVER2005, new SQLServer2005Dialect());
        dialect_enum_map.put(DbType.SQL_SERVER, new SQLServerDialect());
        dialect_enum_map.put(DbType.SYBASE, new SybaseDialect());
    }

    public IDialect getDialect(DbType dbType) {
        return dialect_enum_map.get(dbType);
    }

    public Collection<IDialect> getDialects() {
        return Collections.unmodifiableCollection(dialect_enum_map.values());
    }
}
