/*
 * Copyright (c) 2011-2025, baomidou (jobob@qq.com).
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * JDBC 工具类
 *
 * @author nieqiurong
 * @since 2016-12-05
 */
public class JdbcUtils {

    private static final Log logger = LogFactory.getLog(JdbcUtils.class);
    public static final List<Function<String, DbType>> TYPES = new ArrayList<>();
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

    static {
        TYPES.add(contains(DbType.MYSQL, ":mysql:", ":cobar:"));
        TYPES.add(contains(DbType.MARIADB, ":mariadb:"));
        TYPES.add(contains(DbType.ORACLE, ":oracle:"));
        TYPES.add(contains(DbType.SQL_SERVER, ":sqlserver:", ":microsoft:"));
        TYPES.add(contains(DbType.POSTGRE_SQL, ":postgresql:"));
        TYPES.add(contains(DbType.HSQL, ":hsqldb:"));
        TYPES.add(contains(DbType.DB2, ":db2:"));
        TYPES.add(contains(DbType.SQLITE, ":sqlite:"));
        TYPES.add(contains(DbType.H2, ":h2:"));
        TYPES.add(contains(DbType.LEALONE, ":lealone:"));
        TYPES.add(regexFind(DbType.DM, ":dm\\d*:"));
        TYPES.add(contains(DbType.XU_GU, ":xugu:"));
        TYPES.add(regexFind(DbType.KINGBASE_ES, ":kingbase\\d*:"));
        TYPES.add(contains(DbType.PHOENIX, ":phoenix:"));
        TYPES.add(contains(DbType.GAUSS, ":gaussdb:", ":zenith:"));
        TYPES.add(contains(DbType.GBASE, ":gbase:"));
        TYPES.add(contains(DbType.GBASE_8S, ":gbasedbt-sqli:", ":informix-sqli:"));
        TYPES.add(contains(DbType.GBASE8S_PG, ":gbase8s-pg:"));
        TYPES.add(contains(DbType.GBASE_8C, ":gbase8c:"));
        TYPES.add(contains(DbType.CLICK_HOUSE, ":ch:", ":clickhouse:"));
        TYPES.add(contains(DbType.OSCAR, ":oscar:"));
        TYPES.add(contains(DbType.SYBASE, ":sybase:"));
        TYPES.add(contains(DbType.OCEAN_BASE, ":oceanbase:"));
        TYPES.add(contains(DbType.HIGH_GO, ":highgo:"));
        TYPES.add(contains(DbType.CUBRID, ":cubrid:"));
        TYPES.add(contains(DbType.SUNDB, ":sundb:"));
        TYPES.add(contains(DbType.SAP_HANA, ":sap:"));
        TYPES.add(contains(DbType.IMPALA, ":impala:"));
        TYPES.add(contains(DbType.VERTICA, ":vertica:"));
        TYPES.add(contains(DbType.XCloud, ":xcloud:"));
        TYPES.add(contains(DbType.FIREBIRD, ":firebirdsql:"));
        TYPES.add(contains(DbType.REDSHIFT, ":redshift:"));
        TYPES.add(contains(DbType.OPENGAUSS, ":opengauss:"));
        TYPES.add(contains(DbType.TDENGINE, ":taos:", ":taos-rs:", ":taos-ws:"));
        TYPES.add(contains(DbType.INFORMIX, ":informix"));
        TYPES.add(contains(DbType.SINODB, ":sinodb"));
        TYPES.add(contains(DbType.UXDB, ":uxdb:"));
        TYPES.add(contains(DbType.TRINO, ":trino:"));
        TYPES.add(contains(DbType.PRESTO, ":presto:"));
        TYPES.add(contains(DbType.DERBY, ":derby:"));
        TYPES.add(contains(DbType.VASTBASE, ":vastbase:"));
        TYPES.add(contains(DbType.GOLDENDB, ":goldendb:"));
        TYPES.add(contains(DbType.DUCKDB, ":duckdb:"));
        TYPES.add(contains(DbType.YASDB, ":yasdb:"));
        TYPES.add(contains(DbType.HIVE2, ":hive2:", ":inceptor2:"));
        TYPES.add(jdbcUrl -> {
            logger.warn("The jdbcUrl is " + jdbcUrl + ", Mybatis Plus Cannot Read Database type or The Database's Not" +
                " Supported!");
            return DbType.OTHER;
        });
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
        DbType type;
        for (Function<String, DbType> func : TYPES) {
            type = func.apply(url);
            if (type != null) {
                return type;
            }
        }
        logger.warn("The jdbcUrl is " + jdbcUrl + ", Mybatis Plus Cannot Read Database type or The Database's Not " +
            "Supported!");
        return DbType.OTHER;
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

    /**
     * 正则匹配
     *
     * @param regex  正则
     * @param dbType 对应的dbType
     *
     * @return url校验器
     */
    public static Function<String, DbType> regexFind(DbType dbType, String regex) {
        return url -> {
            if (regexFind(regex, url)) {
                return dbType;
            }
            return null;
        };
    }

    /**
     * 字符串匹配
     *
     * @param strings 匹配url
     * @param dbType  对应的dbType
     *
     * @return url校验器
     */
    public static Function<String, DbType> contains(DbType dbType, String... strings) {
        return (url) -> {
            for (String str : strings) {
                if (url.contains(str)) {
                    return dbType;
                }
            }
            return null;
        };
    }
}
