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

/**
 * GaussDB数据库查询
 *
 * @author nieqiurong
 * @since 3.5.13
 */
public class GaussDBSqlQuery extends AbstractDbQuery {

    @Override
    public String tablesSql() {
        return "SELECT * FROM ( SELECT \n" +
            "  c.relname AS TABLE_NAME,\n" +
            "  d.description AS REMARKS\n" +
            "FROM\n" +
            "  pg_catalog.pg_namespace n,\n" +
            "  pg_catalog.pg_class c\n" +
            "  LEFT JOIN pg_catalog.pg_description d ON (c.OID = d.objoid AND d.objsubid = 0)\n" +
            "  LEFT JOIN pg_catalog.pg_class dc ON (d.classoid = dc.OID AND dc.relname = 'pg_class')\n" +
            "  LEFT JOIN pg_catalog.pg_namespace dn ON (dn.OID = dc.relnamespace AND dn.nspname = 'pg_catalog')\n" +
            "WHERE c.relkind IN ('r', 'p', 'v') AND\n" +
            "  c.relnamespace = n.OID AND n.nspname = '%s' ) WHERE 1=1 \n";
    }

    @Override
    public String tableFieldsSql() {
        return "SELECT\n" +
            "  c.relname,\n" +
            "  a.attname AS FIELD_NAME,\n" +
            "  dsc.description AS FIELD_COMMENT,\n" +
//            "  t.typname AS FIELD_TYPE,\n" +
            "  format_type(a.atttypid,a.atttypmod) AS FIELD_TYPE,\n" +
            "  CASE WHEN idx.indisprimary THEN 'PRI' ELSE NULL END AS KEY\n" +
            "FROM\n" +
            "  pg_catalog.pg_namespace n\n" +
            "  JOIN pg_catalog.pg_class c ON (c.relnamespace = n.OID)\n" +
            "  JOIN pg_catalog.pg_attribute a ON (a.attrelid = c.OID)\n" +
            "  JOIN pg_catalog.pg_type t ON (a.atttypid = t.OID)\n" +
            "  LEFT JOIN pg_catalog.pg_attrdef def ON (a.attrelid = def.adrelid AND a.attnum = def.adnum)\n" +
            "  LEFT JOIN pg_catalog.pg_description dsc ON (c.OID = dsc.objoid AND a.attnum = dsc.objsubid)\n" +
            "  LEFT JOIN pg_catalog.pg_class dc ON (dc.OID = dsc.classoid AND dc.relname = 'pg_class')\n" +
            "  LEFT JOIN pg_catalog.pg_namespace dn ON (dc.relnamespace = dn.OID AND dn.nspname = 'pg_catalog')\n" +
            "  LEFT JOIN pg_catalog.pg_index idx ON idx.indrelid = c.oid AND a.attrelid = idx.indrelid AND a.attnum = ANY(idx.indkey)\n" +
            "WHERE n.nspname = '%s' AND c.relname = '%s' AND \n" +
            "  c.relkind IN ('r', 'p', 'v')\n" +
            "  AND a.attnum > 0\n" +
            "  AND NOT a.attisdropped";
    }

    @Override
    public String tableName() {
        return "TABLE_NAME";
    }

    @Override
    public String tableComment() {
        return "REMARKS";
    }

    @Override
    public String fieldName() {
        return "FIELD_NAME";
    }

    @Override
    public String fieldType() {
        return "FIELD_TYPE";
    }

    @Override
    public String fieldComment() {
        return "FIELD_COMMENT";
    }

    @Override
    public String fieldKey() {
        return "KEY";
    }

}
