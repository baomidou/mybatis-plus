package com.baomidou.mybatisplus.generator.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.generator.config.converts.PostgreSqlTypeConvert;
import com.baomidou.mybatisplus.generator.config.querys.H2Query;
import com.baomidou.mybatisplus.generator.config.querys.MySqlQuery;
import com.baomidou.mybatisplus.generator.keywords.MySqlKeyWordsHandler;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;
import org.h2.Driver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * @author nieqiurong 2020/10/10.
 */
public class DataSourceConfigTest {

    @Test
    void buildTest() {
        DataSourceConfig dataSourceConfig;
        dataSourceConfig = new DataSourceConfig.Builder("jdbc:h2:mem:test;MODE=mysql;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE", "sa", "").build();
        Assertions.assertNotNull(dataSourceConfig.getDbType());
        Assertions.assertNotNull(dataSourceConfig.getConn());
        Assertions.assertNotNull(dataSourceConfig.getTypeConvert());
        Assertions.assertEquals(DbType.H2, dataSourceConfig.getDbType());
        Assertions.assertEquals(H2Query.class, dataSourceConfig.getDbQuery().getClass());

        dataSourceConfig = new DataSourceConfig.Builder("jdbc:h2:mem:test;MODE=mysql;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE", "sa", "")
            .dbQuery(new MySqlQuery()).schema("mp").keyWordsHandler(new MySqlKeyWordsHandler()).typeConvert(new PostgreSqlTypeConvert())
            .build();
        Assertions.assertEquals("mp", dataSourceConfig.getSchemaName());
        Assertions.assertEquals(DbType.H2, dataSourceConfig.getDbType());
        Assertions.assertEquals(MySqlQuery.class, dataSourceConfig.getDbQuery().getClass());
        Assertions.assertNotNull(dataSourceConfig.getKeyWordsHandler());
        Assertions.assertEquals(MySqlKeyWordsHandler.class, dataSourceConfig.getKeyWordsHandler().getClass());
        Assertions.assertEquals(PostgreSqlTypeConvert.class, dataSourceConfig.getTypeConvert().getClass());

        dataSourceConfig = new DataSourceConfig.Builder("jdbc:h2:mem:test;MODE=mysql;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE", "sa", "")
            .dbQuery(new MySqlQuery()).schema("mp").keyWordsHandler(new MySqlKeyWordsHandler()).typeConvert(new PostgreSqlTypeConvert())
            .driverClassName("org.h2.Driver")
            .build();
        Assertions.assertEquals("mp", dataSourceConfig.getSchemaName());
        Assertions.assertEquals(DbType.H2, dataSourceConfig.getDbType());
        Assertions.assertEquals(MySqlQuery.class, dataSourceConfig.getDbQuery().getClass());
        Assertions.assertNotNull(dataSourceConfig.getKeyWordsHandler());
        Assertions.assertEquals("org.h2.Driver", dataSourceConfig.getDriverClassName());
        Assertions.assertEquals(MySqlKeyWordsHandler.class, dataSourceConfig.getKeyWordsHandler().getClass());
        Assertions.assertEquals(PostgreSqlTypeConvert.class, dataSourceConfig.getTypeConvert().getClass());
    }

    @Test
    void dataSourceTest() {
        UnpooledDataSource dataSource = new UnpooledDataSource(Driver.class.getName(), "jdbc:h2:mem:test;MODE=mysql;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE", "sa", "");
        DataSourceConfig dataSourceConfig = new DataSourceConfig.Builder(dataSource).build();
        Assertions.assertNotNull(dataSourceConfig.getConn());
        Assertions.assertNotNull(dataSourceConfig.getDbType());
        Assertions.assertNotNull(dataSourceConfig.getTypeConvert());
        Assertions.assertEquals(DbType.H2, dataSourceConfig.getDbType());
        Assertions.assertEquals(H2Query.class, dataSourceConfig.getDbQuery().getClass());
    }
}
