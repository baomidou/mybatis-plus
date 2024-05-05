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
package com.baomidou.mybatisplus.generator.config.querys;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Sybase库表信息查询
 *
 * @author lroyia
 * @since 2022/1/19 17:08
 **/
public class SybaseQuery extends AbstractDbQuery {

    @Override
    public String tablesSql() {
        return "select name TABLE_NAME, '' TABLE_COMMENT from sysobjects ";
    }

    @Override
    public String tableFieldsSql() {
        return "select o.name TABLE_NAME, c.name FIELD_NAME, upper(t.name) as FIELD_TYPE, " +
            "(CONVERT(varchar(10),c.id)+ '_' + CONVERT(varchar(10), c.colid)) FIELD_KEY, " +
            "c.length as COL_LENGTH, c.status FIELD_STATUS, '' FIELD_COMMENT, c.colid SORT_INDEX " +
            "FROM syscolumns c left join systypes t " +
            "on c.usertype=t.usertype " +
            "inner join sysobjects o " +
            "on c.id=o.id and o.type='U' " +
            "WHERE o.name = '%s' " +
            "ORDER BY c.colid";
    }

    @Override
    public String tableName() {
        return "TABLE_NAME";
    }

    @Override
    public String tableComment() {
        return "TABLE_COMMENT";
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
        return "FIELD_KEY";
    }

    @Override
    public boolean isKeyIdentity(ResultSet results) throws SQLException {
        // TODO:目前没有找到准确的判断方式，如果有大佬知道，请补充
        return results.getInt("SORT_INDEX") == 1 && results.getInt("FIELD_STATUS") == 0;
    }
}
