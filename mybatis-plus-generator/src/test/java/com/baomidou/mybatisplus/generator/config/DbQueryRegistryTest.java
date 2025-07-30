package com.baomidou.mybatisplus.generator.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.generator.config.querys.ClickHouseQuery;
import com.baomidou.mybatisplus.generator.config.querys.DB2Query;
import com.baomidou.mybatisplus.generator.config.querys.DMQuery;
import com.baomidou.mybatisplus.generator.config.querys.DbQueryRegistry;
import com.baomidou.mybatisplus.generator.config.querys.FirebirdQuery;
import com.baomidou.mybatisplus.generator.config.querys.GaussDBSqlQuery;
import com.baomidou.mybatisplus.generator.config.querys.GaussQuery;
import com.baomidou.mybatisplus.generator.config.querys.GbaseQuery;
import com.baomidou.mybatisplus.generator.config.querys.H2Query;
import com.baomidou.mybatisplus.generator.config.querys.KingbaseESQuery;
import com.baomidou.mybatisplus.generator.config.querys.MariadbQuery;
import com.baomidou.mybatisplus.generator.config.querys.MySqlQuery;
import com.baomidou.mybatisplus.generator.config.querys.OracleQuery;
import com.baomidou.mybatisplus.generator.config.querys.OscarQuery;
import com.baomidou.mybatisplus.generator.config.querys.PostgreSqlQuery;
import com.baomidou.mybatisplus.generator.config.querys.SqlServerQuery;
import com.baomidou.mybatisplus.generator.config.querys.SqliteQuery;
import com.baomidou.mybatisplus.generator.config.querys.SybaseQuery;
import com.baomidou.mybatisplus.generator.config.querys.XuguQuery;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author nieqiurong
 */
public class DbQueryRegistryTest {

    @Test
    void test() {
        DbQueryRegistry dbQueryRegistry = new DbQueryRegistry();
        dbQueryRegistry.getDbQuery(DbType.GAUSS_DB);
        Assertions.assertInstanceOf(OracleQuery.class, dbQueryRegistry.getDbQuery(DbType.ORACLE));
        Assertions.assertInstanceOf(SqlServerQuery.class, dbQueryRegistry.getDbQuery(DbType.SQL_SERVER));
        Assertions.assertInstanceOf(PostgreSqlQuery.class, dbQueryRegistry.getDbQuery(DbType.POSTGRE_SQL));
        Assertions.assertInstanceOf(DB2Query.class, dbQueryRegistry.getDbQuery(DbType.DB2));
        Assertions.assertInstanceOf(MariadbQuery.class, dbQueryRegistry.getDbQuery(DbType.MARIADB));
        Assertions.assertInstanceOf(H2Query.class, dbQueryRegistry.getDbQuery(DbType.H2));
        Assertions.assertInstanceOf(H2Query.class, dbQueryRegistry.getDbQuery(DbType.LEALONE));
        Assertions.assertInstanceOf(SqliteQuery.class, dbQueryRegistry.getDbQuery(DbType.SQLITE));
        Assertions.assertInstanceOf(DMQuery.class, dbQueryRegistry.getDbQuery(DbType.DM));
        Assertions.assertInstanceOf(KingbaseESQuery.class, dbQueryRegistry.getDbQuery(DbType.KINGBASE_ES));
        Assertions.assertInstanceOf(MySqlQuery.class, dbQueryRegistry.getDbQuery(DbType.MYSQL));
        Assertions.assertInstanceOf(GaussQuery.class, dbQueryRegistry.getDbQuery(DbType.GAUSS));
        Assertions.assertInstanceOf(GaussDBSqlQuery.class, dbQueryRegistry.getDbQuery(DbType.GAUSS_DB));
        Assertions.assertInstanceOf(OscarQuery.class, dbQueryRegistry.getDbQuery(DbType.OSCAR));
        Assertions.assertInstanceOf(FirebirdQuery.class, dbQueryRegistry.getDbQuery(DbType.FIREBIRD));
        Assertions.assertInstanceOf(XuguQuery.class, dbQueryRegistry.getDbQuery(DbType.XU_GU));
        Assertions.assertInstanceOf(ClickHouseQuery.class, dbQueryRegistry.getDbQuery(DbType.CLICK_HOUSE));
        Assertions.assertInstanceOf(GbaseQuery.class, dbQueryRegistry.getDbQuery(DbType.GBASE));
        Assertions.assertInstanceOf(SybaseQuery.class, dbQueryRegistry.getDbQuery(DbType.SYBASE));

    }
}
