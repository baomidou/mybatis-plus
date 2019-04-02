/*
 * Copyright (c) 2011-2019, hubin (jobob@qq.com).
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
package com.baomidou.mybatisplus.generator.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.toolkit.ExceptionUtils;
import com.baomidou.mybatisplus.generator.config.converts.*;
import com.baomidou.mybatisplus.generator.config.querys.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 数据库配置
 *
 * @author YangHu
 * @since 2016/8/30
 */
@Data
@Accessors(chain = true)
public class DataSourceConfig {

    /**
     * 数据库信息查询
     */
    private IDbQuery dbQuery;
    /**
     * 数据库类型
     */
    private DbType dbType;
    /**
     * PostgreSQL schemaName
     */
    private String schemaName;
    /**
     * 类型转换
     */
    private ITypeConvert typeConvert;
    /**
     * 驱动连接的URL
     */
    private String url;
    /**
     * 驱动名称
     */
    private String driverName;
    /**
     * 数据库连接用户名
     */
    private String username;
    /**
     * 数据库连接密码
     */
    private String password;

    public IDbQuery getDbQuery() {
        if (null == dbQuery) {
            switch (getDbType()) {
                case ORACLE:
                    dbQuery = new OracleQuery();
                    break;
                case SQL_SERVER:
                    dbQuery = new SqlServerQuery();
                    break;
                case POSTGRE_SQL:
                    dbQuery = new PostgreSqlQuery();
                    break;
                case DB2:
                    dbQuery = new DB2Query();
                    break;
                case MARIADB:
                    dbQuery = new MariadbQuery();
                    break;
                case H2:
                    dbQuery = new H2Query();
                    break;
                default:
                    // 默认 MYSQL
                    dbQuery = new MySqlQuery();
                    break;
            }
        }
        return dbQuery;
    }

    /**
     * 判断数据库类型
     *
     * @return 类型枚举值
     */
    public DbType getDbType() {
        if (null == this.dbType) {
            this.dbType = this.getDbType(this.driverName);
            if (null == this.dbType) {
                this.dbType = this.getDbType(this.url.toLowerCase());
                if (null == this.dbType) {
                    throw ExceptionUtils.mpe("Unknown type of database!");
                }
            }
        }

        return this.dbType;
    }

    /**
     * 判断数据库类型
     *
     * @param str 用于寻找特征的字符串，可以是 driverName 或小写后的 url
     * @return 类型枚举值，如果没找到，则返回 null
     */
    private DbType getDbType(String str) {
        if (str.contains("mysql")) {
            return DbType.MYSQL;
        } else if (str.contains("oracle")) {
            return DbType.ORACLE;
        } else if (str.contains("postgresql")) {
            return DbType.POSTGRE_SQL;
        } else if (str.contains("sqlserver")) {
            return DbType.SQL_SERVER;
        } else if (str.contains("db2")) {
            return DbType.DB2;
        } else if (str.contains("mariadb")) {
            return DbType.MARIADB;
        } else if (str.contains("h2")) {
            return DbType.H2;
        } else {
            return null;
        }
    }

    public ITypeConvert getTypeConvert() {
        if (null == typeConvert) {
            switch (getDbType()) {
                case ORACLE:
                    typeConvert = new OracleTypeConvert();
                    break;
                case SQL_SERVER:
                    typeConvert = new SqlServerTypeConvert();
                    break;
                case POSTGRE_SQL:
                    typeConvert = new PostgreSqlTypeConvert();
                    break;
                case DB2:
                    typeConvert = new DB2TypeConvert();
                    break;
                case MARIADB:
                    typeConvert = new MySqlTypeConvert();
                    break;
                default:
                    // 默认 MYSQL
                    typeConvert = new MySqlTypeConvert();
                    break;
            }
        }
        return typeConvert;
    }

    /**
     * 创建数据库连接对象
     *
     * @return Connection
     */
    public Connection getConn() {
        Connection conn = null;
        try {
            Class.forName(driverName);
            conn = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }
}
