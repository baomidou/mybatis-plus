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
package com.baomidou.mybatisplus.generator.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.toolkit.ExceptionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.generator.config.converts.MySqlTypeConvert;
import com.baomidou.mybatisplus.generator.config.converts.TypeConverts;
import com.baomidou.mybatisplus.generator.config.querys.DbQueryRegistry;
import com.baomidou.mybatisplus.generator.config.querys.DecoratorDbQuery;
import lombok.Data;
import lombok.experimental.Accessors;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;

/**
 * 数据库配置
 *
 * @author YangHu, hcl
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
     * schemaName
     */
    private String schemaName;
    /**
     * 类型转换
     */
    private ITypeConvert typeConvert;
    /**
     * 关键字处理器
     *
     * @since 3.3.2
     */
    private IKeyWordsHandler keyWordsHandler;
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

    /**
     * 后续不再公开次构造方法
     *
     * @see Builder#Builder(String, String, String)
     * @deprecated 3.4.1
     */
    @Deprecated
    public DataSourceConfig() {
    }

    /**
     * 设置表数据查询实现类
     *
     * @param dbQuery 表数据查询
     * @return this
     * @see Builder#dbQuery(IDbQuery)
     * @deprecated 3.4.1
     */
    @Deprecated
    public DataSourceConfig setDbQuery(IDbQuery dbQuery) {
        this.dbQuery = dbQuery;
        return this;
    }

    /**
     * 设置数据库类型
     *
     * @param dbType 数据库类型
     * @return this
     * @see Builder#dbType(DbType)
     * @deprecated 3.4.1
     */
    @Deprecated
    public DataSourceConfig setDbType(DbType dbType) {
        this.dbType = dbType;
        return this;
    }

    /**
     * 设置数据库schema
     *
     * @param schemaName 指定schema
     * @return this
     * @see Builder#schema(String)
     * @deprecated 3.4.1
     */
    @Deprecated
    public DataSourceConfig setSchemaName(String schemaName) {
        this.schemaName = schemaName;
        return this;
    }

    /**
     * 设置数据库字段转换实现
     *
     * @param typeConvert 数据库字段转换实现
     * @return this
     * @see Builder#typeConvert(ITypeConvert)
     * @deprecated 3.4.1
     */
    @Deprecated
    public DataSourceConfig setTypeConvert(ITypeConvert typeConvert) {
        this.typeConvert = typeConvert;
        return this;
    }

    /**
     * 设置关键字处理器
     *
     * @param keyWordsHandler 关键字处理器
     * @return this
     * @see Builder#keyWordsHandler(IKeyWordsHandler)
     * @deprecated 3.4.1
     */
    @Deprecated
    public DataSourceConfig setKeyWordsHandler(IKeyWordsHandler keyWordsHandler) {
        this.keyWordsHandler = keyWordsHandler;
        return this;
    }

    /**
     * 设置数据库连接地址
     *
     * @param url 数据库连接地址
     * @return this
     * @see Builder#Builder(java.lang.String, java.lang.String, java.lang.String)
     * @deprecated 3.4.1
     */
    @Deprecated
    public DataSourceConfig setUrl(String url) {
        this.url = url;
        return this;
    }

    /**
     * 设置驱动名称
     *
     * @param driverName 驱动名
     * @return this
     * @see Builder#driver(String)
     * @deprecated 3.4.1
     */
    @Deprecated
    public DataSourceConfig setDriverName(String driverName) {
        this.driverName = driverName;
        return this;
    }

    /**
     * 设置数据库账号
     *
     * @param username 数据库账号
     * @return this
     * @see Builder#Builder(java.lang.String, java.lang.String, java.lang.String)
     * @deprecated 3.4.1
     */
    @Deprecated
    public DataSourceConfig setUsername(String username) {
        this.username = username;
        return this;
    }

    /**
     * 设置数据库密码
     *
     * @param password 数据库密码
     * @return this
     * @see Builder#Builder(java.lang.String, java.lang.String, java.lang.String)
     * @deprecated 3.4.1
     */
    @Deprecated
    public DataSourceConfig setPassword(String password) {
        this.password = password;
        return this;
    }

    public IDbQuery getDbQuery() {
        if (null == dbQuery) {
            DbType dbType = getDbType();
            DbQueryRegistry dbQueryRegistry = new DbQueryRegistry();
            // 默认 MYSQL
            dbQuery = Optional.ofNullable(dbQueryRegistry.getDbQuery(dbType))
                .orElseGet(() -> dbQueryRegistry.getDbQuery(DbType.MYSQL));
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
            this.dbType = this.getDbType(this.url.toLowerCase());
            if (null == this.dbType) {
                this.dbType = this.getDbType(this.driverName);
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
        if (url.contains(":mysql:") || url.contains(":cobar:")) {
            return DbType.MYSQL;
        } else if (str.contains(":oracle:")) {
            return DbType.ORACLE;
        } else if (str.contains(":postgresql:")) {
            return DbType.POSTGRE_SQL;
        } else if (str.contains(":sqlserver:")) {
            return DbType.SQL_SERVER;
        } else if (str.contains(":db2:")) {
            return DbType.DB2;
        } else if (str.contains(":mariadb:")) {
            return DbType.MARIADB;
        } else if (str.contains(":sqlite:")) {
            return DbType.SQLITE;
        } else if (str.contains(":h2:")) {
            return DbType.H2;
        } else if (str.contains(":kingbase:") || str.contains(":kingbase8:")) {
            return DbType.KINGBASE_ES;
        } else if (str.contains(":dm:")) {
            return DbType.DM;
        } else if (str.contains(":zenith:")) {
            return DbType.GAUSS;
        } else if (str.contains(":oscar:")) {
            return DbType.OSCAR;
        } else if (str.contains(":firebird:")) {
            return DbType.FIREBIRD;
        } else {
            return DbType.OTHER;
        }
    }

    public ITypeConvert getTypeConvert() {
        if (null == typeConvert) {
            DbType dbType = getDbType();
            // 默认 MYSQL
            typeConvert = TypeConverts.getTypeConvert(dbType);
            if (null == typeConvert) {
                typeConvert = MySqlTypeConvert.INSTANCE;
            }
        }
        return typeConvert;
    }

    /**
     * 创建数据库连接对象
     * 这方法建议只调用一次，毕竟只是代码生成，用一个连接就行。
     *
     * @return Connection
     * @see DecoratorDbQuery#getConnection()
     */
    public Connection getConn() {
        Connection conn;
        try {
            if (StringUtils.isNotBlank(this.driverName)) {
                Class.forName(this.driverName);
            }
            conn = DriverManager.getConnection(this.url, this.username, this.password);
            String schema = StringUtils.isNotBlank(this.schemaName) ? this.schemaName : getDefaultSchema();
            if (StringUtils.isNotBlank(schema)) {
                this.schemaName = schema;
                conn.setSchema(schema);
            }
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
        return conn;
    }

    /**
     * 获取数据库默认schema
     *
     * @return 默认schema
     * @since 3.4.1
     */
    private String getDefaultSchema() {
        DbType dbType = getDbType();
        String schema = null;
        if (DbType.POSTGRE_SQL == dbType) {
            //pg 默认 schema=public
            schema = "public";
        } else if (DbType.KINGBASE_ES == dbType) {
            //kingbase 默认 schema=PUBLIC
            schema = "PUBLIC";
        } else if (DbType.DB2 == dbType) {
            //db2 默认 schema=current schema
            schema = "current schema";
        } else if (DbType.ORACLE == dbType) {
            //oracle 默认 schema=username
            schema = this.username.toUpperCase();
        }
        return schema;
    }

    /**
     * 数据库配置构建者
     *
     * @author nieqiurong 2020/10/10.
     * @since 3.4.1
     */
    public static class Builder {

        private final DataSourceConfig dataSourceConfig = new DataSourceConfig();

        /**
         * 构造初始化方法
         *
         * @param url      数据库连接地址
         * @param username 数据库账号
         * @param password 数据库密码
         */
        public Builder(String url, String username, String password) {
            this.dataSourceConfig.url = url;
            this.dataSourceConfig.username = username;
            this.dataSourceConfig.password = password;
        }

        /**
         * 设置数据库查询实现
         *
         * @param dbQuery 数据库查询实现
         * @return this
         */
        public Builder dbQuery(IDbQuery dbQuery) {
            this.dataSourceConfig.dbQuery = dbQuery;
            return this;
        }

        /**
         * 设置数据库类型
         *
         * @param dbType 数据库类型
         * @return this
         */
        public Builder dbType(DbType dbType) {
            this.dataSourceConfig.dbType = dbType;
            return this;
        }

        /**
         * 设置数据库schema
         *
         * @param schemaName 数据库schema
         * @return this
         */
        public Builder schema(String schemaName) {
            this.dataSourceConfig.schemaName = schemaName;
            return this;
        }

        /**
         * 设置数据库驱动
         *
         * @param driverName 驱动名
         * @return this
         */
        public Builder driver(String driverName) {
            this.dataSourceConfig.driverName = driverName;
            return this;
        }

        /**
         * 设置数据库驱动
         *
         * @param driver 驱动类
         * @return this
         */
        public Builder driver(Class<? extends Driver> driver) {
            return driver(driver.getName());
        }

        /**
         * 设置类型转换器
         *
         * @param typeConvert 类型转换器
         * @return this
         */
        public Builder typeConvert(ITypeConvert typeConvert) {
            this.dataSourceConfig.typeConvert = typeConvert;
            return this;
        }

        /**
         * 设置数据库关键字处理器
         *
         * @param keyWordsHandler 关键字处理器
         * @return this
         */
        public Builder keyWordsHandler(IKeyWordsHandler keyWordsHandler) {
            this.dataSourceConfig.keyWordsHandler = keyWordsHandler;
            return this;
        }

        /**
         * 构建数据库配置
         *
         * @return 数据库配置
         */
        public DataSourceConfig build() {
            return this.dataSourceConfig;
        }
    }
}
