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
 * @author nieqiuqiu
 * @date 2020-01-09
 * @since 3.3.1
 */
public class DialectRegistry {

    private final Map<DbType, IDialect> dialect_enum_map = new EnumMap<>(DbType.class);

    public DialectRegistry() {
        dialect_enum_map.put(DbType.MYSQL, new MySqlDialect());
        dialect_enum_map.put(DbType.MARIADB, new MariaDBDialect());
        dialect_enum_map.put(DbType.ORACLE, new OracleDialect());
        dialect_enum_map.put(DbType.ORACLE_12C, new Oracle12cDialect());
        dialect_enum_map.put(DbType.DB2, new DB2Dialect());
        dialect_enum_map.put(DbType.H2, new H2Dialect());
        dialect_enum_map.put(DbType.HSQL, new HSQLDialect());
        dialect_enum_map.put(DbType.SQLITE, new SQLiteDialect());
        dialect_enum_map.put(DbType.POSTGRE_SQL, new PostgreDialect());
        dialect_enum_map.put(DbType.SQL_SERVER2005, new SQLServer2005Dialect());
        dialect_enum_map.put(DbType.SQL_SERVER, new SQLServerDialect());
        dialect_enum_map.put(DbType.DM, new DmDialect());
        dialect_enum_map.put(DbType.XU_GU, new XuGuDialect());
        dialect_enum_map.put(DbType.KINGBASE_ES, new KingbaseDialect());
        dialect_enum_map.put(DbType.PHOENIX, new PhoenixDialect());
        dialect_enum_map.put(DbType.OTHER, new UnknownDialect());
    }

    public IDialect getDialect(DbType dbType) {
        return dialect_enum_map.get(dbType);
    }

    public Collection<IDialect> getDialects() {
        return Collections.unmodifiableCollection(dialect_enum_map.values());
    }
}
