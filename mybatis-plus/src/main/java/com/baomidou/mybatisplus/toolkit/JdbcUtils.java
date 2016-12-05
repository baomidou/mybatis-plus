package com.baomidou.mybatisplus.toolkit;

/**
 * Created by nieqiurong on 2016/12/5.
 */
public class JdbcUtils implements JdbcConstants {
    public static String getDbType(String jdbcUrl) {
        if (StringUtils.isEmpty(jdbcUrl)) {
            return null;
        }
        if (jdbcUrl.startsWith("jdbc:derby:") || jdbcUrl.startsWith("jdbc:log4jdbc:derby:")) {
            return DERBY;
        } else if (jdbcUrl.startsWith("jdbc:mysql:") || jdbcUrl.startsWith("jdbc:cobar:")
                || jdbcUrl.startsWith("jdbc:log4jdbc:mysql:")) {
            return MYSQL;
        } else if (jdbcUrl.startsWith("jdbc:mariadb:")) {
            return MARIADB;
        } else if (jdbcUrl.startsWith("jdbc:oracle:") || jdbcUrl.startsWith("jdbc:log4jdbc:oracle:")) {
            return ORACLE;
        } else if (jdbcUrl.startsWith("jdbc:alibaba:oracle:")) {
            return ALI_ORACLE;
        } else if (jdbcUrl.startsWith("jdbc:microsoft:") || jdbcUrl.startsWith("jdbc:log4jdbc:microsoft:")) {
            return SQL_SERVER;
        } else if (jdbcUrl.startsWith("jdbc:sqlserver:") || jdbcUrl.startsWith("jdbc:log4jdbc:sqlserver:")) {
            return SQL_SERVER;
        } else if (jdbcUrl.startsWith("jdbc:sybase:Tds:") || jdbcUrl.startsWith("jdbc:log4jdbc:sybase:")) {
            return SYBASE;
        } else if (jdbcUrl.startsWith("jdbc:jtds:") || jdbcUrl.startsWith("jdbc:log4jdbc:jtds:")) {
            return JTDS;
        } else if (jdbcUrl.startsWith("jdbc:fake:") || jdbcUrl.startsWith("jdbc:mock:")) {
            return MOCK;
        } else if (jdbcUrl.startsWith("jdbc:postgresql:") || jdbcUrl.startsWith("jdbc:log4jdbc:postgresql:")) {
            return POSTGRESQL;
        } else if (jdbcUrl.startsWith("jdbc:hsqldb:") || jdbcUrl.startsWith("jdbc:log4jdbc:hsqldb:")) {
            return HSQL;
        } else if (jdbcUrl.startsWith("jdbc:odps:")) {
            return ODPS;
        } else if (jdbcUrl.startsWith("jdbc:db2:")) {
            return DB2;
        } else if (jdbcUrl.startsWith("jdbc:sqlite:")) {
            return "sqlite";
        } else if (jdbcUrl.startsWith("jdbc:ingres:")) {
            return "ingres";
        } else if (jdbcUrl.startsWith("jdbc:h2:") || jdbcUrl.startsWith("jdbc:log4jdbc:h2:")) {
            return H2;
        } else if (jdbcUrl.startsWith("jdbc:mckoi:")) {
            return "mckoi";
        } else if (jdbcUrl.startsWith("jdbc:cloudscape:")) {
            return "cloudscape";
        } else if (jdbcUrl.startsWith("jdbc:informix-sqli:") || jdbcUrl.startsWith("jdbc:log4jdbc:informix-sqli:")) {
            return "informix";
        } else if (jdbcUrl.startsWith("jdbc:timesten:")) {
            return "timesten";
        } else if (jdbcUrl.startsWith("jdbc:as400:")) {
            return "as400";
        } else if (jdbcUrl.startsWith("jdbc:sapdb:")) {
            return "sapdb";
        } else if (jdbcUrl.startsWith("jdbc:JSQLConnect:")) {
            return "JSQLConnect";
        } else if (jdbcUrl.startsWith("jdbc:JTurbo:")) {
            return "JTurbo";
        } else if (jdbcUrl.startsWith("jdbc:firebirdsql:")) {
            return "firebirdsql";
        } else if (jdbcUrl.startsWith("jdbc:interbase:")) {
            return "interbase";
        } else if (jdbcUrl.startsWith("jdbc:pointbase:")) {
            return "pointbase";
        } else if (jdbcUrl.startsWith("jdbc:edbc:")) {
            return "edbc";
        } else if (jdbcUrl.startsWith("jdbc:mimer:multi1:")) {
            return "mimer";
        } else if (jdbcUrl.startsWith("jdbc:dm:")) {
            return JdbcConstants.DM;
        } else if (jdbcUrl.startsWith("jdbc:kingbase:")) {
            return JdbcConstants.KINGBASE;
        } else if (jdbcUrl.startsWith("jdbc:log4jdbc:")) {
            return LOG4JDBC;
        } else if (jdbcUrl.startsWith("jdbc:hive:")) {
            return HIVE;
        } else if (jdbcUrl.startsWith("jdbc:hive2:")) {
            return HIVE;
        } else if (jdbcUrl.startsWith("jdbc:phoenix:")) {
            return PHOENIX;
        } else {
            return null;
        }
    }

}
