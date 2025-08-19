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

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Function;

/**
 * Oracle DDL 生成器
 *
 * @author hubin
 * @since 2021-06-22
 */
public class OracleDdlGenerator implements IDdlGenerator {

    /**
     * 默认使用当前用户模式
     * @since 3.5.13
     */
    private String schema;

    public OracleDdlGenerator() {
    }

    public OracleDdlGenerator(String schema) {
        this.schema = schema;
    }

    /**
     * 基于当前用户模式实例
     * @return OracleDdlGenerator
     */
    public static IDdlGenerator newInstance() {
        return new OracleDdlGenerator();
    }

    @Override
    public boolean existTable(Connection connection) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        String schema = StringUtils.isNotBlank(this.schema) ? this.schema : connection.getSchema();
        String tableName = getDdlHistory();
        int index = tableName.lastIndexOf(StringPool.DOT);
        if (index > 0) {
            tableName = tableName.substring(index + 1);
        }
        tableName = tableName.replace(StringPool.QUOTE, StringPool.EMPTY);
        try (ResultSet resultSet = metaData.getTables(connection.getCatalog(), schema, tableName, new String[]{"TABLE"})) {
            return resultSet.next();
        }
    }

    @Override
    public boolean existTable(String databaseName, Function<String, Boolean> executeFunction) {
        return executeFunction.apply("SELECT COUNT(1) AS NUM FROM user_tables WHERE table_name='"
            + getDdlHistory() + "'");
    }

    @Override
    public String getDdlHistory() {
        if (StringUtils.isNotBlank(schema)) {
            return schema + ".DDL_HISTORY";
        }
        return "DDL_HISTORY";
    }

    @Override
    public String createDdlHistory() {
        StringBuffer sql = new StringBuffer();
        sql.append("CREATE TABLE ").append(getDdlHistory()).append("(");
        sql.append("script NVARCHAR2(500) NOT NULL,");
        sql.append("type NVARCHAR2(30) NOT NULL,");
        sql.append("version NVARCHAR2(30) NOT NULL");
        sql.append(");");
        return sql.toString();
    }
}
