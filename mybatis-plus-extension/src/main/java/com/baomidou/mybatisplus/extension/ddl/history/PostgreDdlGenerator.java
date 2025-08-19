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
import lombok.Setter;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Function;


/**
 * PostgreSQL DDL 生成器
 *
 * @author hubin
 * @since 2021-06-22
 */
public class PostgreDdlGenerator implements IDdlGenerator {

    /**
     * schema模式(默认:public)
     * <p>为了兼容,默认使用public,但指定为null时,使用jdbc指定的schema</p>
     * @since 3.5.13
     */
    @Setter
    private String schema = "public";

    /**
     * @deprecated 3.5.13 {@link #newInstance}
     */
    @Deprecated
    public PostgreDdlGenerator() {
    }

    /**
     * 创建PostgreDdlGenerator实例
     * @since 3.5.13
     * @param schema schema (可为null,当为null时为自动识别数据库连接的schema)
     */
    public PostgreDdlGenerator(String schema) {
        this.schema = schema;
    }

    /**
     * 默认实例 (基于public模式)
     * @return PostgreDdlGenerator
     */
    public static IDdlGenerator newInstance() {
        return newInstanceWithSchema("public");
    }

    /**
     * 手动指定schema
     * @param schema schema
     * @since 3.5.13
     *  @return PostgreDdlGenerator
     */
    public static IDdlGenerator newInstanceWithSchema(String schema) {
        return new PostgreDdlGenerator(schema);
    }

    /**
     * 基于数据库连接自动识别schema
     * @since 3.5.13
     * @return PostgreDdlGenerator
     */
    public static IDdlGenerator newInstanceWithAutoSchema() {
        return new PostgreDdlGenerator(StringPool.EMPTY);
    }


    @Override
    public boolean existTable(String databaseName, Function<String, Boolean> executeFunction) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT COUNT(1) AS NUM from INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME='ddl_history' AND TABLE_TYPE='BASE TABLE'");
        if (StringUtils.isNotBlank(this.getSchema())) {
            sql.append(" AND TABLE_SCHEMA='").append(this.getSchema()).append("'");
        }
        return executeFunction.apply(sql.toString());
    }

    @Override
    public boolean existTable(Connection connection) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        String tableName = getDdlHistory();
        int index = tableName.lastIndexOf(StringPool.DOT);
        if (index > 0) {
            tableName = tableName.substring(index + 1);
        }
        tableName = tableName.replace(StringPool.QUOTE, StringPool.EMPTY);
        try (ResultSet resultSet = metaData.getTables(connection.getCatalog(),
            StringUtils.isBlank(getSchema()) ? connection.getSchema() : getSchema(), tableName, new String[]{"TABLE"})) {
            return resultSet.next();
        }
    }

    @Override
    public String getDdlHistory() {
        String schema = getSchema();
        if (StringUtils.isNotBlank(schema)) {
            return "\"" + schema + "\".\"ddl_history\"";
        }
        return "\"ddl_history\"";
    }

    @Override
    public String createDdlHistory() {
        StringBuffer sql = new StringBuffer();
        String ddlHistory = this.getDdlHistory();
        sql.append("CREATE TABLE IF NOT EXISTS ").append(ddlHistory).append(" (");
        sql.append("\"script\" varchar(500) NOT NULL,");
        sql.append("\"type\" varchar(30) NOT NULL,");
        sql.append("\"version\" varchar(30) NOT NULL");
        sql.append(");");
        sql.append("COMMENT ON COLUMN ").append(ddlHistory).append(".\"script\" IS '脚本';");
        sql.append("COMMENT ON COLUMN ").append(ddlHistory).append(".\"type\" IS '类型';");
        sql.append("COMMENT ON COLUMN ").append(ddlHistory).append(".\"version\" IS '版本';");
        sql.append("COMMENT ON TABLE ").append(ddlHistory).append(" IS 'DDL 版本';");
        return sql.toString();
    }

    /**
     * @return scheme
     * @deprecated 3.5.13 指定请使用 {@link #setSchema(String)}
     */
    @Deprecated
    protected String getSchema() {
        return schema;
    }

}
