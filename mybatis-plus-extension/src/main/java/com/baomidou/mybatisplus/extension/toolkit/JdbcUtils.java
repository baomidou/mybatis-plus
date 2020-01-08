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
package com.baomidou.mybatisplus.extension.toolkit;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

/**
 * JDBC 工具类
 *
 * @author nieqiurong
 * @since 2016-12-05
 */
public class JdbcUtils {

    private static final Log logger = LogFactory.getLog(JdbcUtils.class);

    /**
     * 根据连接地址判断数据库类型
     *
     * @param jdbcUrl 连接地址
     * @return ignore
     */
    public static DbType getDbType(String jdbcUrl) {
        Assert.isFalse(StringUtils.isBlank(jdbcUrl), "Error: The jdbcUrl is Null, Cannot read database type");
        if (jdbcUrl.contains(":mysql:") || jdbcUrl.contains(":cobar:")) {
            return DbType.MYSQL;
        } else if (jdbcUrl.contains(":mariadb:")) {
            return DbType.MARIADB;
        } else if (jdbcUrl.contains(":oracle:")) {
            return DbType.ORACLE;
        } else if (jdbcUrl.contains(":sqlserver:") || jdbcUrl.contains(":microsoft:")) {
            return DbType.SQL_SERVER2005;
        } else if (jdbcUrl.contains(":sqlserver2012:")) {
            return DbType.SQL_SERVER;
        } else if (jdbcUrl.contains(":postgresql:")) {
            return DbType.POSTGRE_SQL;
        } else if (jdbcUrl.contains(":hsqldb:")) {
            return DbType.HSQL;
        } else if (jdbcUrl.contains(":db2:")) {
            return DbType.DB2;
        } else if (jdbcUrl.contains(":sqlite:")) {
            return DbType.SQLITE;
        } else if (jdbcUrl.contains(":h2:")) {
            return DbType.H2;
        } else if (jdbcUrl.contains(":dm:")) {
            return DbType.DM;
        } else if (jdbcUrl.contains(":xugu:")) {
            return DbType.XU_GU;
        } else if (jdbcUrl.contains(":kingbase:") || jdbcUrl.contains(":kingbase8:")) {
            return DbType.KINGBASE_ES;
        } else if (jdbcUrl.contains(":phoenix:")) {
            return DbType.PHOENIX;
        } else {
            logger.warn("The jdbcUrl is " + jdbcUrl + ", Mybatis Plus Cannot Read Database type or The Database's Not Supported!");
            return DbType.OTHER;
        }
    }

}
