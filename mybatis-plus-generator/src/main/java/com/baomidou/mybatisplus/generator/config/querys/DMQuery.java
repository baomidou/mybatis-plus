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
 * DM 表数据查询
 *
 * @author halower
 * @since 2019-06-27
 */
public class DMQuery  extends AbstractDbQuery{

    @Override
    public String tablesSql() {
        return "SELECT DISTINCT T1.TABLE_NAME,T2.COMMENTS AS TABLE_COMMENT FROM USER_TAB_COLUMNS T1 " +
            "INNER JOIN USER_TAB_COMMENTS T2 ON T1.TABLE_NAME = T2.TABLE_NAME WHERE 1=1 ";
    }

    @Override
    public String tableFieldsSql() {
        return
            "SELECT T2.COLUMN_NAME,T1.COMMENTS,T2.DATA_TYPE ," +
                "CASE WHEN CONSTRAINT_TYPE='P' THEN 'PRI' END AS KEY " +
                "FROM USER_COL_COMMENTS T1, USER_TAB_COLUMNS T2, " +
                "(SELECT T4.TABLE_NAME, T4.COLUMN_NAME ,T5.CONSTRAINT_TYPE " +
                "FROM USER_CONS_COLUMNS T4, USER_CONSTRAINTS T5 " +
                "WHERE T4.CONSTRAINT_NAME = T5.CONSTRAINT_NAME " +
                "AND T5.CONSTRAINT_TYPE = 'P')T3 " +
                "WHERE T1.TABLE_NAME = T2.TABLE_NAME AND " +
                "T1.COLUMN_NAME=T2.COLUMN_NAME AND " +
                "T1.TABLE_NAME = T3.TABLE_NAME(+) AND " +
                "T1.COLUMN_NAME=T3.COLUMN_NAME(+)   AND " +
                "T1.TABLE_NAME = '%s' " +
                "ORDER BY T2.TABLE_NAME,T2.COLUMN_ID";
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
        return "COLUMN_NAME";
    }

    @Override
    public String fieldType() {
        return "DATA_TYPE";
    }

    @Override
    public String fieldComment() {
        return "COMMENTS";
    }

    @Override
    public String fieldKey() {
        return "KEY";
    }
}
