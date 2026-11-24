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
package com.baomidou.mybatisplus.generator.config.querys;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * PostgreSql 表数据查询
 *
 * @author hubin
 * @since 2018-01-16
 */
public class PostgreSqlQuery extends AbstractDbQuery {

    @Override
    public String tablesSql() {
        return "SELECT t.tablename, obj_description(c.oid, 'pg_class') AS comments"
                + " FROM pg_catalog.pg_tables t JOIN pg_catalog.pg_namespace n ON n.nspname = t.schemaname"
                + " JOIN pg_catalog.pg_class c ON c.relnamespace = n.oid AND c.relname = t.tablename"
                + " WHERE t.schemaname = '%s'";
    }

    @Override
    public String tableFieldsSql() {
        return "SELECT a.attname AS name, format_type(a.atttypid, a.atttypmod) AS type,"
                + " col_description(a.attrelid, a.attnum) AS comment, d.column_default,"
                + " CASE WHEN a.attnum = ANY(pk.conkey) THEN 'PRI' ELSE '' END AS key"
                + " FROM pg_class c JOIN pg_namespace n ON n.oid = c.relnamespace"
                + " JOIN pg_attribute a ON a.attrelid = c.oid"
                + " JOIN information_schema.columns d ON d.table_schema = n.nspname"
                + " AND d.table_name = c.relname AND d.column_name = a.attname"
                + " LEFT JOIN pg_constraint pk ON pk.conrelid = c.oid AND pk.contype = 'p'"
                + " WHERE c.relname = '%1$s' AND n.nspname = '%4$s' AND a.attnum > 0 AND NOT a.attisdropped"
                + " ORDER BY a.attnum";
    }

    @Override
    public String tableName() {
        return "tablename";
    }

    @Override
    public String tableComment() {
        return "comments";
    }

    @Override
    public String fieldName() {
        return "name";
    }

    @Override
    public String fieldType() {
        return "type";
    }

    @Override
    public String fieldComment() {
        return "comment";
    }

    @Override
    public String fieldKey() {
        return "key";
    }

    @Override
    public boolean isKeyIdentity(ResultSet results) throws SQLException {
        return StringUtils.isNotBlank(results.getString("column_default"))
                && results.getString("column_default").contains("nextval");
    }
}
