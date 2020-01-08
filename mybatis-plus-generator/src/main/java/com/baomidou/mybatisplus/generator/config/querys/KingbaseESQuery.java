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
package com.baomidou.mybatisplus.generator.config.querys;

import com.baomidou.mybatisplus.annotation.DbType;

/**
 * KingbaseES 表数据查询
 *
 * @author kingbase
 * @since 2019-10-12
 */
public class KingbaseESQuery extends AbstractDbQuery {

    @Override
    public String tablesSql() {
        return "SELECT A.tablename, obj_description(relfilenode, 'sys_class') AS comments FROM sys_tables A, sys_class B WHERE A.schemaname='%s' AND A.tablename = B.relname";
    }


    @Override
    public String tableFieldsSql() {
        return "SELECT A.attname AS name, format_type(A.atttypid, A.atttypmod) AS type,col_description(A.attrelid, A.attnum) AS comment, (CASE C.contype WHEN 'p' THEN 'PRI' ELSE '' END) AS key " +
            "FROM sys_attribute A LEFT JOIN sys_constraint C ON A.attnum = C.conkey[1] AND A.attrelid = C.conrelid " +
            "WHERE  A.attrelid = '%s.%s'::regclass AND A.attnum > 0 AND NOT A.attisdropped ORDER  BY A.attnum";
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

}
