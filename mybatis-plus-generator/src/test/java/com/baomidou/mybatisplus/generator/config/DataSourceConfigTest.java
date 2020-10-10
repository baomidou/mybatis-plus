package com.baomidou.mybatisplus.generator.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.generator.config.converts.PostgreSqlTypeConvert;
import com.baomidou.mybatisplus.generator.config.querys.H2Query;
import com.baomidou.mybatisplus.generator.config.querys.MySqlQuery;
import com.baomidou.mybatisplus.generator.keywords.MySqlKeyWordsHandler;
import org.h2.Driver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 *
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
        Assertions.assertEquals(dataSourceConfig.getDbType(), DbType.H2);
        Assertions.assertEquals(dataSourceConfig.getDbQuery().getClass(), H2Query.class);

        dataSourceConfig = new DataSourceConfig.Builder("jdbc:h2:mem:test;MODE=mysql;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE", "sa", "").driver(Driver.class).build();
        Assertions.assertNotNull(dataSourceConfig.getDbType());
        Assertions.assertNotNull(dataSourceConfig.getConn());
        Assertions.assertNotNull(dataSourceConfig.getTypeConvert());
        Assertions.assertEquals(Driver.class.getName(), dataSourceConfig.getDriverName());
        Assertions.assertEquals(dataSourceConfig.getDbType(), DbType.H2);
        Assertions.assertEquals(dataSourceConfig.getDbQuery().getClass(), H2Query.class);

        dataSourceConfig = new DataSourceConfig.Builder("jdbc:h2:mem:test;MODE=mysql;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE", "sa", "")
            .dbType(DbType.MYSQL).dbQuery(new MySqlQuery()).schema("mp").keyWordsHandler(new MySqlKeyWordsHandler()).typeConvert(new PostgreSqlTypeConvert())
            .driver(Driver.class).build();
        Assertions.assertEquals(dataSourceConfig.getSchemaName(), "mp");
        Assertions.assertEquals(dataSourceConfig.getDriverName(), Driver.class.getName());
        Assertions.assertEquals(dataSourceConfig.getDbType(), DbType.MYSQL);
        Assertions.assertEquals(dataSourceConfig.getDbQuery().getClass(), MySqlQuery.class);
        Assertions.assertEquals(dataSourceConfig.getKeyWordsHandler().getClass(), MySqlKeyWordsHandler.class);
        Assertions.assertEquals(dataSourceConfig.getTypeConvert().getClass(), PostgreSqlTypeConvert.class);
    }

}
