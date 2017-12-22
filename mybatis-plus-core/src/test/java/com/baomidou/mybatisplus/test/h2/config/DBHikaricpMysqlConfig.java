package com.baomidou.mybatisplus.test.h2.config;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * <p>
 * MySql Memory Database config
 * </p>
 *
 * @author Caratacus
 * @date 2017/4/1
 */
@Configuration
@EnableTransactionManagement
public class DBHikaricpMysqlConfig {

    @Bean
    public DataSource dataSource() throws SQLException {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName("com.mysql.jdbc.Driver");
        hikariConfig.setUsername("root");
        hikariConfig.setPassword("");
        hikariConfig.setJdbcUrl("jdbc:mysql://localhost:3306/mybatis-plus");
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
