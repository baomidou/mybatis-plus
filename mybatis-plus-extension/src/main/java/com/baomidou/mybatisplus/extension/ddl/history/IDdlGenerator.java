/*
 * Copyright (c) 2011-2025, baomidou (jobob@qq.com).
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
package com.baomidou.mybatisplus.extension.ddl.history;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Function;

/**
 * DDL 生成器接口
 *
 * @author hubin
 * @since 2021-06-22
 */
public interface IDdlGenerator {

    /**
     * 表是否存在
     *
     * @param databaseName    数据库名称
     * @param executeFunction 执行判断函数
     * @return exist or no
     * @deprecated 3.5.13 {@link #existTable(Connection)}
     */
    @Deprecated
    default boolean existTable(String databaseName, Function<String, Boolean> executeFunction) {
        return false;
    }

    /**
     *
     * 检查{@link #getDdlHistory()}表是否存在
     * @since 3.5.13
     * @param connection 数据库连接
     * @return 是否存在
     * @throws SQLException SQLException
     */
    default boolean existTable(Connection connection) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        try (ResultSet resultSet = metaData.getTables(connection.getCatalog(), connection.getSchema(), getDdlHistory(), new String[]{"TABLE"})) {
            return resultSet.next();
        }
    }

    /**
     * 返回 DDL_HISTORY 表名
     *
     * @return SQL
     */
    default String getDdlHistory() {
        return "ddl_history";
    }

    /**
     * ddl_history sql
     *
     * @return SQL
     */
    String createDdlHistory();

    /**
     * select ddl_history sql
     *
     * @param script Sql Script
     * @param type   Execute Type
     * @return SQL
     */
    default String selectDdlHistory(String script, String type) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT version FROM ").append(getDdlHistory()).append(" WHERE script='").append(script);
        sql.append("' AND type='").append(type).append("'");
        return sql.toString();
    }

    /**
     * insert ddl_history sql
     *
     * @param script  Sql Script
     * @param type    Execute Type
     * @param version Execute Version
     * @return SQL
     */
    default String insertDdlHistory(String script, String type, String version) {
        StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO ").append(getDdlHistory()).append("(script,type,version) VALUES ('");
        sql.append(script).append("','").append(type).append("','").append(version).append("')");
        return sql.toString();
    }

}
