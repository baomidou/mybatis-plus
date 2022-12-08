/*
 * Copyright (c) 2011-2021, baomidou (jobob@qq.com).
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
        return "SELECT A.tablename, obj_description(relfilenode, 'pg_class') AS comments FROM pg_tables A, pg_class B WHERE A.schemaname='%s' AND A.tablename = B.relname";
    }

    @Override
    public String tableFieldsSql() {
        return "SELECT\n" +
            "   A.attname AS name,format_type (A.atttypid,A.atttypmod) AS type,col_description (A.attrelid,A.attnum) AS comment,\n" +
            "\t D.column_default,\n" +
            "   CASE WHEN length(B.attname) > 0 THEN 'PRI' ELSE '' END AS key\n" +
            "FROM\n" +
            "   pg_attribute A\n" +
            "LEFT JOIN (\n" +
            "    SELECT\n" +
            "        pg_attribute.attname\n" +
            "    FROM\n" +
            "        pg_index,\n" +
            "        pg_class,\n" +
            "        pg_attribute\n" +
            "    WHERE\n" +
            "        pg_class.oid ='\"%s\"' :: regclass\n" +
            "    AND pg_index.indrelid = pg_class.oid\n" +
            "    AND pg_attribute.attrelid = pg_class.oid\n" +
            "    AND pg_attribute.attnum = ANY (pg_index.indkey)\n" +
            ") B ON A.attname = b.attname\n" +
            "INNER JOIN pg_class C on A.attrelid = C.oid\n" +
            "INNER JOIN information_schema.columns D on A.attname = D.column_name\n" +
            "WHERE A.attrelid ='\"%s\"' :: regclass AND A.attnum> 0 AND NOT A.attisdropped AND D.table_name = '%s'\n" +
            "ORDER BY A.attnum;";
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
        return StringUtils.isNotBlank(results.getString("column_default")) && results.getString("column_default").contains("nextval");
    }
}
