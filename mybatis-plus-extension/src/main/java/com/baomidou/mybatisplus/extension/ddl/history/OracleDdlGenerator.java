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

import java.util.function.Function;

/**
 * Oracle DDL 生成器
 *
 * @author hubin
 * @since 2021-06-22
 */
public class OracleDdlGenerator implements IDdlGenerator {

    public static IDdlGenerator newInstance() {
        return new OracleDdlGenerator();
    }

    @Override
    public boolean existTable(String databaseName, Function<String, Boolean> executeFunction) {
        return executeFunction.apply("SELECT COUNT(1) AS NUM FROM user_tables WHERE table_name='"
                + getDdlHistory() + "'");
    }

    @Override
    public String getDdlHistory() {
        return "DDL_HISTORY";
    }

    @Override
    public String createDdlHistory() {
        StringBuffer sql = new StringBuffer();
        sql.append("CREATE TABLE ").append(getDdlHistory()).append("(");
        sql.append("script NVARCHAR2(500) NOT NULL,");
        sql.append("type NVARCHAR2(30) NOT NULL,");
        sql.append("version NVARCHAR2(30) NOT NULL");
        sql.append(");");
        return sql.toString();
    }
}
