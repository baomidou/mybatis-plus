package com.baomidou.mybatisplus.generator.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.generator.config.querys.ClickHouseQuery;
import com.baomidou.mybatisplus.generator.config.querys.DB2Query;
import com.baomidou.mybatisplus.generator.config.querys.DMQuery;
import com.baomidou.mybatisplus.generator.config.querys.DbQueryRegistry;
import com.baomidou.mybatisplus.generator.config.querys.FirebirdQuery;
import com.baomidou.mybatisplus.generator.config.querys.GaussDBSqlQuery;
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
import com.baomidou.mybatisplus.generator.config.querys.ZenithQuery;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author nieqiurong
 */
public class DbQueryRegistryTest {

    @Test
    void test() {
        DbQueryRegistry dbQueryRegistry = new DbQueryRegistry();
        dbQueryRegistry.getDbQuery(DbType.GAUSS_DB);
        assertThat(dbQueryRegistry.getDbQuery(DbType.ORACLE)).isInstanceOf(OracleQuery.class);
        assertThat(dbQueryRegistry.getDbQuery(DbType.POLARDB)).isInstanceOf(OracleQuery.class);
        assertThat(dbQueryRegistry.getDbQuery(DbType.SQL_SERVER)).isInstanceOf(SqlServerQuery.class);
        assertThat(dbQueryRegistry.getDbQuery(DbType.POSTGRE_SQL)).isInstanceOf(PostgreSqlQuery.class);
        assertThat(dbQueryRegistry.getDbQuery(DbType.DB2)).isInstanceOf(DB2Query.class);
        assertThat(dbQueryRegistry.getDbQuery(DbType.MARIADB)).isInstanceOf(MariadbQuery.class);
        assertThat(dbQueryRegistry.getDbQuery(DbType.H2)).isInstanceOf(H2Query.class);
        assertThat(dbQueryRegistry.getDbQuery(DbType.LEALONE)).isInstanceOf(H2Query.class);
        assertThat(dbQueryRegistry.getDbQuery(DbType.SQLITE)).isInstanceOf(SqliteQuery.class);
        assertThat(dbQueryRegistry.getDbQuery(DbType.DM)).isInstanceOf(DMQuery.class);
        assertThat(dbQueryRegistry.getDbQuery(DbType.KINGBASE_ES)).isInstanceOf(KingbaseESQuery.class);
        assertThat(dbQueryRegistry.getDbQuery(DbType.MYSQL)).isInstanceOf(MySqlQuery.class);
        assertThat(dbQueryRegistry.getDbQuery(DbType.GAUSS)).isInstanceOf(ZenithQuery.class);
        assertThat(dbQueryRegistry.getDbQuery(DbType.GAUSS_DB)).isInstanceOf(GaussDBSqlQuery.class);
        assertThat(dbQueryRegistry.getDbQuery(DbType.OSCAR)).isInstanceOf(OscarQuery.class);
        assertThat(dbQueryRegistry.getDbQuery(DbType.FIREBIRD)).isInstanceOf(FirebirdQuery.class);
        assertThat(dbQueryRegistry.getDbQuery(DbType.XU_GU)).isInstanceOf(XuguQuery.class);
        assertThat(dbQueryRegistry.getDbQuery(DbType.CLICK_HOUSE)).isInstanceOf(ClickHouseQuery.class);
        assertThat(dbQueryRegistry.getDbQuery(DbType.GBASE)).isInstanceOf(GbaseQuery.class);
        assertThat(dbQueryRegistry.getDbQuery(DbType.SYBASE)).isInstanceOf(SybaseQuery.class);

    }
}
