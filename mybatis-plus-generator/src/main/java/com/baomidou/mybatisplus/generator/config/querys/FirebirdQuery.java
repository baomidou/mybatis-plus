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

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * MySql 表数据查询
 *
 * @author steven ma
 * @since 2020-08-20
 */
public class FirebirdQuery extends AbstractDbQuery {

    @Override
    public String tablesSql() {
        return "select rdb$relation_name as NAME " +
            "from rdb$relations " +
            "where rdb$view_blr is null " +
            "and (rdb$system_flag is null or rdb$system_flag = 0)";
    }


    @Override
    public String tableFieldsSql() {
        return "select f.rdb$relation_name AS NAME, f.rdb$field_name AS FIELD, t.rdb$type_name AS  TYPE " +
            "from rdb$relation_fields f " +
            "join rdb$relations r on f.rdb$relation_name = r.rdb$relation_name " +
            "JOIN rdb$fields fs ON f.rdb$field_source = fs.rdb$field_name " +
            "JOIN rdb$types  t ON fs.rdb$field_type = t.rdb$type " +
            "and r.rdb$view_blr is NULL " +
            "AND t.rdb$field_name = 'RDB$FIELD_TYPE' " +
            "and (r.rdb$system_flag is null or r.rdb$system_flag = 0) " +
            "AND f.rdb$relation_name = `%s` " +
            "order by 1, f.rdb$field_position";
    }


    @Override
    public String tableName() {
        return "NAME";
    }


    @Override
    public String tableComment() {
        return "COMMENT";
    }


    @Override
    public String fieldName() {
        return "FIELD";
    }


    @Override
    public String fieldType() {
        return "TYPE";
    }


    @Override
    public String fieldComment() {
        return "COMMENT";
    }


    @Override
    public String fieldKey() {
        return "KEY";
    }

}
