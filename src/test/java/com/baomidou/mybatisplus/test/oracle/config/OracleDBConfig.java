package com.baomidou.mybatisplus.test.oracle.config;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import oracle.jdbc.OracleDriver;


/**
 * 对应的数据库配置
 *
 * @author yuxiaobin
 */
@Configuration
@EnableTransactionManagement
public class OracleDBConfig {

    @Bean
    public DataSource dataSource() throws SQLException {
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
        dataSource.setDriver(new OracleDriver());
        dataSource.setUrl("jdbc:oracle:thin:@localhost:1521:xe");
        dataSource.setUsername("mp");
        dataSource.setPassword("mp");
        return dataSource;
    }

    @Bean
    public DataSourceTransactionManager transactionManager(DataSource ds) {
        return new DataSourceTransactionManager(ds);
    }

}
