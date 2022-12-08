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
 * ClickHouse 表数据查询
 *
 * @author gaosheng
 * @since 2021-03-10
 */
public class ClickHouseQuery extends AbstractDbQuery {

    @Override
    public String tablesSql() {
        return "SELECT * FROM system.tables WHERE 1=1 ";
    }

    @Override
    public String tableFieldsSql() {
        return "select * from system.columns where table='%s'";
    }

    @Override
    public String tableName() {
        return "name";
    }

    @Override
    public String tableComment() {
        return "name";
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
        return "is_in_primary_key";
    }

    @Override
    public boolean isKeyIdentity(ResultSet results) throws SQLException {
        return "1".equals(results.getString("is_in_primary_key"));
    }

}
