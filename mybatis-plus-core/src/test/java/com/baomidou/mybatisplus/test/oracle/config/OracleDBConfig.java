package com.baomidou.mybatisplus.test.oracle.config;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import oracle.jdbc.driver.OracleDriver;


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
        dataSource.setUrl("jdbc:oracle:thin:@192.168.10.169:1521:orcl");
        dataSource.setUsername("sa");
        dataSource.setPassword("sa");
        return dataSource;
//        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
//        dataSource.setDriver(new Driver());
//        dataSource.setUrl("jdbc:h2:mem:AZ;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
//        dataSource.setUsername("sa");
//        dataSource.setPassword("");
//        return dataSource;
    }

    @Bean
    public DataSourceTransactionManager transactionManager(DataSource ds) {
        return new DataSourceTransactionManager(ds);
    }

}
