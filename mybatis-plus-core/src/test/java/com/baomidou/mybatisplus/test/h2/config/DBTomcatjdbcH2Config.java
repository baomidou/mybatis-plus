package com.baomidou.mybatisplus.test.h2.config;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.tomcat.jdbc.pool.PoolConfiguration;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * <p>
 * Tomcatjdbc数据源
 * </p>
 *
 * @author yuxiaobin
 * @date 2018/1/3
 */
@Configuration
@EnableTransactionManagement
public class DBTomcatjdbcH2Config {


    @Bean
    public DataSource dataSource() throws SQLException {
        PoolConfiguration configuration = new PoolProperties();
        configuration.setUrl("jdbc:h2:mem:test;MODE=mysql;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
        configuration.setUsername("sa");
        configuration.setPassword("");
        configuration.setDriverClassName(org.h2.Driver.class.getCanonicalName());
        return new org.apache.tomcat.jdbc.pool.DataSource(configuration);
    }

    @Bean
    public DataSourceTransactionManager transactionManager(DataSource ds) {
        return new DataSourceTransactionManager(ds);
    }
}
