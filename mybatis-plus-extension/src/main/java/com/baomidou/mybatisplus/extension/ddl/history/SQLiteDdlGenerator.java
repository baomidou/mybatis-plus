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
 * SQLite DDL 生成器
 *
 * @author 呆猫
 * @since 2024-04-01
 */
public class SQLiteDdlGenerator implements IDdlGenerator {

    public static IDdlGenerator newInstance() {
        return new SQLiteDdlGenerator();
    }

    @Override
    public boolean existTable(String databaseName, Function<String, Boolean> executeFunction) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT count(1) NUM FROM sqlite_master WHERE name='");
        sql.append(getDdlHistory()).append("' AND type='table'");
        return executeFunction.apply(sql.toString());
    }

    @Override
    public String createDdlHistory() {
        StringBuffer sql = new StringBuffer();
        sql.append("CREATE TABLE IF NOT EXISTS `").append(getDdlHistory()).append("` (");
        sql.append("script  TEXT primary key,");
        sql.append("type    TEXT,");
        sql.append("version TEXT");
        sql.append(");");
        return sql.toString();
    }
}
