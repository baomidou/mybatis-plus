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
 * DB2 表数据查询
 *
 * @author zhanyao
 * @since 2018-05-16
 */
public class DB2Query extends AbstractDbQuery {

    @Override
    public String tablesSql() {
        return "SELECT * FROM SYSCAT.TABLES where tabschema=%s";
    }


    @Override
    public String tableFieldsSql() {
        return "SELECT * FROM syscat.columns WHERE tabschema=%s AND tabname='%s'";
    }


    @Override
    public String tableName() {
        return "TABNAME";
    }


    @Override
    public String tableComment() {
        return "REMARKS";
    }


    @Override
    public String fieldName() {
        return "COLNAME";
    }


    @Override
    public String fieldType() {
        return "TYPENAME";
    }


    @Override
    public String fieldComment() {
        return "REMARKS";
    }


    @Override
    public String fieldKey() {
        return "IDENTITY";
    }

}
