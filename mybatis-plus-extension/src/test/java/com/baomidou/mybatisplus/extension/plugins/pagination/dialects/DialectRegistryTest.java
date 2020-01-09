package com.baomidou.mybatisplus.extension.plugins.pagination.dialects;

import com.baomidou.mybatisplus.annotation.DbType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DialectRegistryTest {

    private DialectRegistry dialectRegistry = new DialectRegistry();

    @Test
    void test() {
        Assertions.assertEquals(dialectRegistry.getDialect(DbType.MYSQL).getClass(), MySqlDialect.class);
        Assertions.assertEquals(dialectRegistry.getDialect(DbType.MARIADB).getClass(), MariaDBDialect.class);
        Assertions.assertEquals(dialectRegistry.getDialect(DbType.ORACLE).getClass(), OracleDialect.class);
        Assertions.assertEquals(dialectRegistry.getDialect(DbType.ORACLE_12C).getClass(), Oracle12cDialect.class);
        Assertions.assertEquals(dialectRegistry.getDialect(DbType.DB2).getClass(), DB2Dialect.class);
        Assertions.assertEquals(dialectRegistry.getDialect(DbType.H2).getClass(), H2Dialect.class);
        Assertions.assertEquals(dialectRegistry.getDialect(DbType.HSQL).getClass(), HSQLDialect.class);
        Assertions.assertEquals(dialectRegistry.getDialect(DbType.SQLITE).getClass(), SQLiteDialect.class);
        Assertions.assertEquals(dialectRegistry.getDialect(DbType.POSTGRE_SQL).getClass(), PostgreDialect.class);
        Assertions.assertEquals(dialectRegistry.getDialect(DbType.SQL_SERVER2005).getClass(), SQLServer2005Dialect.class);
        Assertions.assertEquals(dialectRegistry.getDialect(DbType.SQL_SERVER).getClass(), SQLServerDialect.class);
        Assertions.assertEquals(dialectRegistry.getDialect(DbType.DM).getClass(), DmDialect.class);
        Assertions.assertEquals(dialectRegistry.getDialect(DbType.XU_GU).getClass(), XuGuDialect.class);
        Assertions.assertEquals(dialectRegistry.getDialect(DbType.KINGBASE_ES).getClass(), KingbaseDialect.class);
        Assertions.assertEquals(dialectRegistry.getDialect(DbType.PHOENIX).getClass(), PhoenixDialect.class);
        Assertions.assertEquals(dialectRegistry.getDialect(DbType.OTHER).getClass(), UnknownDialect.class);
    }

}
