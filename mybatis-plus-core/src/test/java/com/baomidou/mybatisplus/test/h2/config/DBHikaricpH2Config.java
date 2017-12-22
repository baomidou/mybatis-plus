package com.baomidou.mybatisplus.test.h2.config;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.h2.Driver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * <p>
 * H2 Memory Database config
 * </p>
 *
 * @author Caratacus
 * @date 2017/4/1
 */
@Configuration
@EnableTransactionManagement
public class DBHikaricpH2Config {

    @Bean
    public DataSource dataSource() throws SQLException {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(Driver.class.getName());
        hikariConfig.setUsername("sa");
        hikariConfig.setPassword("");
        hikariConfig.setJdbcUrl("jdbc:h2:mem:test;MODE=mysql;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
//        dataSource.setJdbcUrl();
//        dataSource.setUsername("sa");
//        dataSource.setPassword("");
        return new HikariDataSource(hikariConfig);
    }

    @Bean
    public DataSourceTransactionManager transactionManager(DataSource ds) {
        return new DataSourceTransactionManager(ds);
    }

}
