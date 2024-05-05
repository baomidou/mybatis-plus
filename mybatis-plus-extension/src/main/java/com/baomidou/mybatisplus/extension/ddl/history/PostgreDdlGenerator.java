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
package com.baomidou.mybatisplus.extension.ddl.history;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;

import java.util.function.Function;


/**
 * PostgreSQL DDL 生成器
 *
 * @author hubin
 * @since 2021-06-22
 */
public class PostgreDdlGenerator implements IDdlGenerator {

    public static IDdlGenerator newInstance() {
        return new PostgreDdlGenerator();
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
    public String getDdlHistory() {
        return "\"" + this.getSchema() + "\".\"ddl_history\"";
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

    protected String getSchema() {
        return "public";
    }
}
