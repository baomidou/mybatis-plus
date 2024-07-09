/*
 * Copyright (c) 2011-2024, baomidou (jobob@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baomidou.mybatisplus.extension.plugins.pagination;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.toolkit.ExceptionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.dialects.*;

import java.util.EnumMap;
import java.util.Map;

/**
 * 分页方言工厂类
 *
 * @author hubin
 * @since 2016-01-23
 */
public class DialectFactory {
    private static final Map<DbType, IDialect> DIALECT_ENUM_MAP = new EnumMap<>(DbType.class);

    public static IDialect getDialect(DbType dbType) {
        IDialect dialect = DIALECT_ENUM_MAP.get(dbType);
        if (null == dialect) {
            if (dbType == DbType.OTHER) {
                throw ExceptionUtils.mpe("%s database not supported.", dbType.getDb());
            }
            // mysql same type
            else if (dbType.mysqlSameType()) {
                dialect = new MySqlDialect();
            }
            // oracle same type
            else if (dbType.oracleSameType()) {
                dialect = new OracleDialect();
            }
            // postgresql same type
            else if (dbType.postgresqlSameType()) {
                dialect = new PostgreDialect();
            }
            // other types
            else if (dbType == DbType.ORACLE_12C
                || dbType == DbType.FIREBIRD
                || dbType == DbType.SQL_SERVER
                || dbType == DbType.DERBY) {
                dialect = new Oracle12cDialect();
            } else if (dbType == DbType.DB2) {
                dialect = new DB2Dialect();
            } else if (dbType == DbType.SQL_SERVER2005) {
                dialect = new SQLServer2005Dialect();
            } else if (dbType == DbType.SYBASE) {
                dialect = new SybaseDialect();
            } else if (dbType == DbType.XCloud) {
                dialect = new XCloudDialect();
            } else if (dbType == DbType.GBASE_8S
                || dbType == DbType.GBASEDBT
                || dbType == DbType.GBASE_INFORMIX
                || dbType == DbType.SINODB) {
                dialect = new GBase8sDialect();
            } else if (dbType == DbType.INFORMIX) {
                dialect = new InformixDialect();
            } else if (dbType == DbType.TRINO
                || dbType == DbType.PRESTO) {
                dialect = new TrinoDialect();
            }
            DIALECT_ENUM_MAP.put(dbType, dialect);
        }
        return dialect;
    }
}
