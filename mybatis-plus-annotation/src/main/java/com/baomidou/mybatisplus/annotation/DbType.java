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
package com.baomidou.mybatisplus.annotation;

import lombok.Getter;

/**
 * MybatisPlus 数据库类型
 *
 * @author hubin
 * @since 2018-06-23
 */
@Getter
public enum DbType {
    /**
     * MYSQL
     */
    MYSQL("mysql", "%s LIKE CONCAT('%%',#{%s},'%%')", "MySql数据库"),
    /**
     * MARIADB
     */
    MARIADB("mariadb", "%s LIKE CONCAT('%%',#{%s},'%%')", "MariaDB数据库"),
    /**
     * ORACLE
     */
    ORACLE("oracle", "%s LIKE CONCAT(CONCAT('%%',#{%s}),'%%')", "Oracle数据库"),
    /**
     * DB2
     */
    DB2("db2", "%s LIKE CONCAT(CONCAT('%%',#{%s}),'%%')", "DB2数据库"),
    /**
     * H2
     */
    H2("h2", "%s LIKE CONCAT('%%',#{%s},'%%')", "H2数据库"),
    /**
     * HSQL
     */
    HSQL("hsql", "%s LIKE CONCAT('%%',#{%s},'%%')", "HSQL数据库"),
    /**
     * SQLITE
     */
    SQLITE("sqlite", "%s LIKE CONCAT('%%',#{%s},'%%')", "SQLite数据库"),
    /**
     * POSTGRE
     */
    POSTGRE_SQL("postgresql", "%s LIKE CONCAT('%%',#{%s},'%%')", "Postgre数据库"),
    /**
     * SQLSERVER2005
     */
    SQL_SERVER2005("sqlserver2005", "%s LIKE '%%'+#{%s}+'%%'", "SQLServer2005数据库"),
    /**
     * SQLSERVER
     */
    SQL_SERVER("sqlserver", "%s LIKE '%%'+#{%s}+'%%'", "SQLServer数据库"),
    /**
     * DM
     */
    DM("dm", null, "达梦数据库"),
    /**
     * UNKONWN DB
     */
    OTHER("other", null, "其他数据库");

    /**
     * 数据库名称
     */
    private final String db;
    /**
     * LIKE 拼接模式
     */
    private final String like;
    /**
     * 描述
     */
    private final String desc;


    DbType(String db, String like, String desc) {
        this.db = db;
        this.like = like;
        this.desc = desc;
    }

    /**
     * 获取数据库类型（默认 MySql）
     *
     * @param dbType 数据库类型字符串
     */
    public static DbType getDbType(String dbType) {
        DbType[] dts = DbType.values();
        for (DbType dt : dts) {
            if (dt.getDb().equalsIgnoreCase(dbType)) {
                return dt;
            }
        }
        return OTHER;
    }
}
