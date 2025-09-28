package com.baomidou.mybatisplus.test.plugins.pagination;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.pagination.DialectFactory;
import com.baomidou.mybatisplus.extension.plugins.pagination.dialects.GaussDBDialect;
import com.baomidou.mybatisplus.extension.plugins.pagination.dialects.MySqlDialect;
import com.baomidou.mybatisplus.extension.plugins.pagination.dialects.OracleDialect;
import com.baomidou.mybatisplus.extension.plugins.pagination.dialects.PostgreDialect;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author nieqiurong
 */
public class DialectFactoryTest {

    @Test
    void test() {
        Assertions.assertInstanceOf(MySqlDialect.class, DialectFactory.getDialect(DbType.MYSQL));
        Assertions.assertInstanceOf(MySqlDialect.class, DialectFactory.getDialect(DbType.MARIADB));
        Assertions.assertInstanceOf(MySqlDialect.class, DialectFactory.getDialect(DbType.GBASE));
        Assertions.assertInstanceOf(MySqlDialect.class, DialectFactory.getDialect(DbType.OSCAR));
        Assertions.assertInstanceOf(MySqlDialect.class, DialectFactory.getDialect(DbType.XU_GU));
        Assertions.assertInstanceOf(MySqlDialect.class, DialectFactory.getDialect(DbType.CLICK_HOUSE));
        Assertions.assertInstanceOf(MySqlDialect.class, DialectFactory.getDialect(DbType.OCEAN_BASE));
        Assertions.assertInstanceOf(MySqlDialect.class, DialectFactory.getDialect(DbType.CUBRID));
        Assertions.assertInstanceOf(MySqlDialect.class, DialectFactory.getDialect(DbType.SUNDB));
        Assertions.assertInstanceOf(MySqlDialect.class, DialectFactory.getDialect(DbType.GOLDENDB));
        Assertions.assertInstanceOf(MySqlDialect.class, DialectFactory.getDialect(DbType.YASDB));

        Assertions.assertInstanceOf(OracleDialect.class, DialectFactory.getDialect(DbType.DM));
        Assertions.assertInstanceOf(OracleDialect.class, DialectFactory.getDialect(DbType.ORACLE));
        Assertions.assertInstanceOf(OracleDialect.class, DialectFactory.getDialect(DbType.GAUSS));

        Assertions.assertInstanceOf(PostgreDialect.class, DialectFactory.getDialect(DbType.POSTGRE_SQL));
        Assertions.assertInstanceOf(PostgreDialect.class, DialectFactory.getDialect(DbType.H2));
        Assertions.assertInstanceOf(PostgreDialect.class, DialectFactory.getDialect(DbType.LEALONE));
        Assertions.assertInstanceOf(PostgreDialect.class, DialectFactory.getDialect(DbType.SQLITE));
        Assertions.assertInstanceOf(PostgreDialect.class, DialectFactory.getDialect(DbType.HSQL));
        Assertions.assertInstanceOf(PostgreDialect.class, DialectFactory.getDialect(DbType.KINGBASE_ES));
        Assertions.assertInstanceOf(PostgreDialect.class, DialectFactory.getDialect(DbType.PHOENIX));
        Assertions.assertInstanceOf(PostgreDialect.class, DialectFactory.getDialect(DbType.SAP_HANA));
        Assertions.assertInstanceOf(PostgreDialect.class, DialectFactory.getDialect(DbType.IMPALA));
        Assertions.assertInstanceOf(PostgreDialect.class, DialectFactory.getDialect(DbType.HIGH_GO));
        Assertions.assertInstanceOf(PostgreDialect.class, DialectFactory.getDialect(DbType.VERTICA));
        Assertions.assertInstanceOf(PostgreDialect.class, DialectFactory.getDialect(DbType.REDSHIFT));
        Assertions.assertInstanceOf(PostgreDialect.class, DialectFactory.getDialect(DbType.OPENGAUSS));
        Assertions.assertInstanceOf(PostgreDialect.class, DialectFactory.getDialect(DbType.TDENGINE));
        Assertions.assertInstanceOf(PostgreDialect.class, DialectFactory.getDialect(DbType.UXDB));
        Assertions.assertInstanceOf(PostgreDialect.class, DialectFactory.getDialect(DbType.GBASE8S_PG));
        Assertions.assertInstanceOf(PostgreDialect.class, DialectFactory.getDialect(DbType.GBASE_8C));
        Assertions.assertInstanceOf(PostgreDialect.class, DialectFactory.getDialect(DbType.VASTBASE));
        Assertions.assertInstanceOf(PostgreDialect.class, DialectFactory.getDialect(DbType.DUCKDB));

        Assertions.assertInstanceOf(GaussDBDialect.class, DialectFactory.getDialect(DbType.GAUSS_DB));
    }

}
