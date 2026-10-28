package com.baomidou.mybatisplus.generator.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.generator.config.converts.PostgreSqlTypeConvert;
import com.baomidou.mybatisplus.generator.config.querys.H2Query;
import com.baomidou.mybatisplus.generator.config.querys.MySqlQuery;
import com.baomidou.mybatisplus.generator.keywords.MySqlKeyWordsHandler;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;
import org.h2.Driver;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author nieqiurong 2020/10/10.
 */
@Tag("unit")
public class DataSourceConfigTest {

    @Test
    void buildTest() {
        DataSourceConfig dataSourceConfig;
        dataSourceConfig = new DataSourceConfig.Builder(
                "jdbc:h2:mem:test;MODE=mysql;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE", "sa", "").build();
        assertThat(dataSourceConfig.getDbType()).isNotNull();
        assertThat(dataSourceConfig.getConn()).isNotNull();
        assertThat(dataSourceConfig.getTypeConvert()).isNotNull();
        assertThat(dataSourceConfig.getDbType()).isEqualTo(DbType.H2);
        assertThat(dataSourceConfig.getDbQuery().getClass()).isEqualTo(H2Query.class);

        dataSourceConfig = new DataSourceConfig.Builder(
                "jdbc:h2:mem:test;MODE=mysql;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE", "sa", "")
                .dbQuery(new MySqlQuery()).schema("mp").keyWordsHandler(new MySqlKeyWordsHandler())
                .typeConvert(new PostgreSqlTypeConvert()).build();
        assertThat(dataSourceConfig.getSchemaName()).isEqualTo("mp");
        assertThat(dataSourceConfig.getDbType()).isEqualTo(DbType.H2);
        assertThat(dataSourceConfig.getDbQuery().getClass()).isEqualTo(MySqlQuery.class);
        assertThat(dataSourceConfig.getKeyWordsHandler()).isNotNull();
        assertThat(dataSourceConfig.getKeyWordsHandler().getClass()).isEqualTo(MySqlKeyWordsHandler.class);
        assertThat(dataSourceConfig.getTypeConvert().getClass()).isEqualTo(PostgreSqlTypeConvert.class);

        dataSourceConfig = new DataSourceConfig.Builder(
                "jdbc:h2:mem:test;MODE=mysql;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE", "sa", "")
                .dbQuery(new MySqlQuery()).schema("mp").keyWordsHandler(new MySqlKeyWordsHandler())
                .typeConvert(new PostgreSqlTypeConvert()).driverClassName("org.h2.Driver").build();
        assertThat(dataSourceConfig.getSchemaName()).isEqualTo("mp");
        assertThat(dataSourceConfig.getDbType()).isEqualTo(DbType.H2);
        assertThat(dataSourceConfig.getDbQuery().getClass()).isEqualTo(MySqlQuery.class);
        assertThat(dataSourceConfig.getKeyWordsHandler()).isNotNull();
        assertThat(dataSourceConfig.getDriverClassName()).isEqualTo("org.h2.Driver");
        assertThat(dataSourceConfig.getKeyWordsHandler().getClass()).isEqualTo(MySqlKeyWordsHandler.class);
        assertThat(dataSourceConfig.getTypeConvert().getClass()).isEqualTo(PostgreSqlTypeConvert.class);
    }

    @Test
    void dataSourceTest() {
        UnpooledDataSource dataSource = new UnpooledDataSource(Driver.class.getName(),
                "jdbc:h2:mem:test;MODE=mysql;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE", "sa", "");
        DataSourceConfig dataSourceConfig = new DataSourceConfig.Builder(dataSource).build();
        assertThat(dataSourceConfig.getConn()).isNotNull();
        assertThat(dataSourceConfig.getDbType()).isNotNull();
        assertThat(dataSourceConfig.getTypeConvert()).isNotNull();
        assertThat(dataSourceConfig.getDbType()).isEqualTo(DbType.H2);
        assertThat(dataSourceConfig.getDbQuery().getClass()).isEqualTo(H2Query.class);
    }
}
