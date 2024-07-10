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
package com.baomidou.mybatisplus.extension.toolkit;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.ExceptionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * JDBC 工具类
 *
 * @author nieqiurong
 * @since 2016-12-05
 */
public class JdbcUtils {

    private static final Log logger = LogFactory.getLog(JdbcUtils.class);
    private static final Map<String, DbType> JDBC_DB_TYPE_CACHE = new ConcurrentHashMap<>();

    /**
     * 不关闭 Connection,因为是从事务里获取的,sqlSession会负责关闭
     *
     * @param executor Executor
     * @return DbType
     */
    public static DbType getDbType(Executor executor) {
        try {
            Connection conn = executor.getTransaction().getConnection();
            return CollectionUtils.computeIfAbsent(JDBC_DB_TYPE_CACHE, conn.getMetaData().getURL(), JdbcUtils::getDbType);
        } catch (SQLException e) {
            throw ExceptionUtils.mpe(e);
        }
    }

    /**
     * 根据连接地址判断数据库类型
     *
     * @param jdbcUrl 连接地址
     * @return ignore
     */
    public static DbType getDbType(String jdbcUrl) {
        Assert.isFalse(StringUtils.isBlank(jdbcUrl), "Error: The jdbcUrl is Null, Cannot read database type");
        String url = jdbcUrl.toLowerCase();
        if (url.contains(":mysql:") || url.contains(":cobar:")) {
            return DbType.MYSQL;
        } else if (url.contains(":mariadb:")) {
            return DbType.MARIADB;
        } else if (url.contains(":oracle:")) {
            return DbType.ORACLE;
        } else if (url.contains(":sqlserver:") || url.contains(":microsoft:")) {
            return DbType.SQL_SERVER;
        } else if (url.contains(":postgresql:")) {
            return DbType.POSTGRE_SQL;
        } else if (url.contains(":hsqldb:")) {
            return DbType.HSQL;
        } else if (url.contains(":db2:")) {
            return DbType.DB2;
        } else if (url.contains(":sqlite:")) {
            return DbType.SQLITE;
        } else if (url.contains(":h2:")) {
            return DbType.H2;
        } else if (url.contains(":lealone:")) {
            return DbType.LEALONE;
        } else if (regexFind(":dm\\d*:", url)) {
            return DbType.DM;
        } else if (url.contains(":xugu:")) {
            return DbType.XU_GU;
        } else if (regexFind(":kingbase\\d*:", url)) {
            return DbType.KINGBASE_ES;
        } else if (url.contains(":phoenix:")) {
            return DbType.PHOENIX;
        } else if (url.contains(":zenith:")) {
            return DbType.GAUSS;
        } else if (url.contains(":gbase:")) {
            return DbType.GBASE;
        } else if (url.contains(":gbasedbt-sqli:") || url.contains(":informix-sqli:")) {
            return DbType.GBASE_8S;
        } else if (url.contains(":gbase8s-pg:")){
            return DbType.GBASE8S_PG;
        } else if (url.contains(":gbase8c:")) {
            return DbType.GBASE_8C;
        } else if (url.contains(":ch:") || url.contains(":clickhouse:")) {
            return DbType.CLICK_HOUSE;
        } else if (url.contains(":oscar:")) {
            return DbType.OSCAR;
        } else if (url.contains(":sybase:")) {
            return DbType.SYBASE;
        } else if (url.contains(":oceanbase:")) {
            return DbType.OCEAN_BASE;
        } else if (url.contains(":highgo:")) {
            return DbType.HIGH_GO;
        } else if (url.contains(":cubrid:")) {
            return DbType.CUBRID;
        } else if (url.contains(":sundb:")) {
            return DbType.SUNDB;
        } else if (url.contains(":sap:")) {
            return DbType.SAP_HANA;
        } else if (url.contains(":impala:")) {
            return DbType.IMPALA;
        } else if (url.contains(":vertica:")) {
            return DbType.VERTICA;
        } else if (url.contains(":xcloud:")) {
            return DbType.XCloud;
        } else if (url.contains(":firebirdsql:")) {
            return DbType.FIREBIRD;
        } else if (url.contains(":redshift:")) {
            return DbType.REDSHIFT;
        } else if (url.contains(":opengauss:")) {
            return DbType.OPENGAUSS;
        } else if (url.contains(":taos:") || url.contains(":taos-rs:")) {
            return DbType.TDENGINE;
        } else if (url.contains(":informix")) {
            return DbType.INFORMIX;
        } else if (url.contains(":sinodb")) {
            return DbType.SINODB;
        } else if (url.contains(":uxdb:")) {
            return DbType.UXDB;
        } else if (url.contains(":trino:")) {
            return DbType.TRINO;
        } else if (url.contains(":presto:")) {
            return DbType.PRESTO;
        } else if (url.contains(":derby:")) {
            return DbType.DERBY;
        } else if (url.contains(":vastbase:")) {
            return DbType.VASTBASE;
        } else if (url.contains(":goldendb:")) {
            return DbType.GOLDENDB;
        } else if (url.contains(":duckdb:")){
            return DbType.DUCKDB;
        } else {
            logger.warn("The jdbcUrl is " + jdbcUrl + ", Mybatis Plus Cannot Read Database type or The Database's Not Supported!");
            return DbType.OTHER;
        }
    }

    /**
     * 正则匹配
     *
     * @param regex 正则
     * @param input 字符串
     * @return 验证成功返回 true，验证失败返回 false
     */
    public static boolean regexFind(String regex, CharSequence input) {
        if (null == input) {
            return false;
        }
        return Pattern.compile(regex).matcher(input).find();
    }
}
