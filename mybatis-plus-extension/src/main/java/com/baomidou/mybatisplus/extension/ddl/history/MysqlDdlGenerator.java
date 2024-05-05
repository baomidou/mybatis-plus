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

import com.baomidou.mybatisplus.core.toolkit.StringUtils;

import java.util.function.Function;

/**
 * Mysql DDL 生成器
 *
 * @author hubin
 * @since 2021-06-22
 */
public class MysqlDdlGenerator implements IDdlGenerator {

    public static IDdlGenerator newInstance() {
        return new MysqlDdlGenerator();
    }

    @Override
    public boolean existTable(String databaseName, Function<String, Boolean> executeFunction) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT COUNT(1) AS NUM from INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME='");
        sql.append(getDdlHistory()).append("' AND TABLE_TYPE='BASE TABLE'");
        if (StringUtils.isNotBlank(databaseName)) {
            sql.append(" AND TABLE_SCHEMA='").append(databaseName).append("'");
        }
        return executeFunction.apply(sql.toString());
    }

    @Override
    public String createDdlHistory() {
        StringBuffer sql = new StringBuffer();
        sql.append("CREATE TABLE IF NOT EXISTS `").append(getDdlHistory()).append("` (");
        sql.append("`script` varchar(500) NOT NULL COMMENT '脚本',");
        sql.append("`type` varchar(30) NOT NULL COMMENT '类型',");
        sql.append("`version` varchar(30) NOT NULL COMMENT '版本',");
        sql.append("PRIMARY KEY (`script`)");
        sql.append(") COMMENT = 'DDL 版本';");
        return sql.toString();
    }
}
