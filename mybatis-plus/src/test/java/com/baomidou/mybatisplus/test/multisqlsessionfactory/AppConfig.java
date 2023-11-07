package com.baomidou.mybatisplus.test.multisqlsessionfactory;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.session.SqlSessionFactory;
import org.h2.Driver;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.annotation.MapperScans;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;

/**
 * @author nieqiurong
 */
@ComponentScan("com.baomidou.mybatisplus.test.multisqlsessionfactory")
@MapperScans(
    {
        @MapperScan(value = "com.baomidou.mybatisplus.test.multisqlsessionfactory.a.mapper", sqlSessionFactoryRef = "sqlSessionFactory1"),
        @MapperScan(value = "com.baomidou.mybatisplus.test.multisqlsessionfactory.b.mapper", sqlSessionFactoryRef = "sqlSessionFactory2")
    }
)
@Configuration
public class AppConfig {

    @Bean
    public DataSource dataSourceA() {
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
        dataSource.setDriver(new Driver());
        dataSource.setUrl("jdbc:h2:mem:testa;MODE=mysql;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        return dataSource;
    }

    @Bean
    public DataSourceTransactionManager transactionManagerA(DataSource dataSourceA) {
        return new DataSourceTransactionManager(dataSourceA);
    }

    @Bean
    public TransactionTemplate transactionTemplateA(DataSourceTransactionManager transactionManagerA) {
        return new TransactionTemplate(transactionManagerA);
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory1(DataSource dataSourceA) throws Exception {
        MybatisSqlSessionFactoryBean mybatisSqlSessionFactoryBean = new MybatisSqlSessionFactoryBean();
        mybatisSqlSessionFactoryBean.setDataSource(dataSourceA);
        mybatisSqlSessionFactoryBean.setConfiguration(new MybatisConfiguration());
        return mybatisSqlSessionFactoryBean.getObject();
    }

    @Bean
    public DataSource dataSourceB() {
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
        dataSource.setDriver(new Driver());
        dataSource.setUrl("jdbc:h2:mem:testb;MODE=mysql;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        return dataSource;
    }

    @Bean
    public DataSourceTransactionManager transactionManagerB(DataSource dataSourceB) {
        return new DataSourceTransactionManager(dataSourceB);
    }

    @Bean
    public TransactionTemplate transactionTemplateB(DataSourceTransactionManager transactionManagerB) {
        return new TransactionTemplate(transactionManagerB);
    }


    @Bean
    public SqlSessionFactory sqlSessionFactory2(DataSource dataSourceB) throws Exception {
        MybatisSqlSessionFactoryBean mybatisSqlSessionFactoryBean = new MybatisSqlSessionFactoryBean();
        mybatisSqlSessionFactoryBean.setDataSource(dataSourceB);
        mybatisSqlSessionFactoryBean.setConfiguration(new MybatisConfiguration());
        return mybatisSqlSessionFactoryBean.getObject();
    }

}
