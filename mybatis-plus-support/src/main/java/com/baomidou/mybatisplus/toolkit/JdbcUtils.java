/**
 * Copyright (c) 2011-2014, hubin (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.toolkit;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

import com.baomidou.mybatisplus.enums.DBType;
import com.baomidou.mybatisplus.exceptions.MybatisPlusException;

/**
 * <p>
 * JDBC 工具类
 * </p>
 *
 * @author nieqiurong
 * @Date 2016-12-05
 */
public class JdbcUtils {

    private static final Log logger = LogFactory.getLog(JdbcUtils.class);

    /**
     * <p>
     * 根据连接地址判断数据库类型
     * </p>
     *
     * @param jdbcUrl 连接地址
     * @return
     */
    public static DBType getDbType(String jdbcUrl) {
        if (StringUtils.isEmpty(jdbcUrl)) {
            throw new MybatisPlusException("Error: The jdbcUrl is Null, Cannot read database type");
        }
        if (jdbcUrl.startsWith("jdbc:mysql:") || jdbcUrl.startsWith("jdbc:cobar:")
            || jdbcUrl.startsWith("jdbc:log4jdbc:mysql:")) {
            return DBType.MYSQL;
        } else if (jdbcUrl.startsWith("jdbc:mariadb:")) {
            return DBType.MARIADB;
        } else if (jdbcUrl.startsWith("jdbc:oracle:") || jdbcUrl.startsWith("jdbc:log4jdbc:oracle:")) {
            return DBType.ORACLE;
        } else if (jdbcUrl.startsWith("jdbc:sqlserver:") || jdbcUrl.startsWith("jdbc:microsoft:")) {
            return DBType.SQLSERVER2005;
        } else if (jdbcUrl.startsWith("jdbc:sqlserver2012:")) {
            return DBType.SQLSERVER;
        } else if (jdbcUrl.startsWith("jdbc:postgresql:") || jdbcUrl.startsWith("jdbc:log4jdbc:postgresql:")) {
            return DBType.POSTGRE;
        } else if (jdbcUrl.startsWith("jdbc:hsqldb:") || jdbcUrl.startsWith("jdbc:log4jdbc:hsqldb:")) {
            return DBType.HSQL;
        } else if (jdbcUrl.startsWith("jdbc:db2:")) {
            return DBType.DB2;
        } else if (jdbcUrl.startsWith("jdbc:sqlite:")) {
            return DBType.SQLITE;
        } else if (jdbcUrl.startsWith("jdbc:h2:") || jdbcUrl.startsWith("jdbc:log4jdbc:h2:")) {
            return DBType.H2;
        } else {
            logger.warn("The jdbcUrl is " + jdbcUrl + ", Mybatis Plus Cannot Read Database type or The Database's Not Supported!");
            return DBType.OTHER;
        }
    }

}
