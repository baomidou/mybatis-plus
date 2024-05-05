package com.baomidou.mybatisplus.test.phoenix.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Driver;
import java.util.Properties;

/**
 * @author miemie
 * @since 2018/6/7
 */
@Configuration
@SuppressWarnings("unchecked")
@EnableTransactionManagement
public class DBConfig {

    @Bean("dataSource")
    public DataSource dataSource() throws ClassNotFoundException {
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
        dataSource.setDriverClass((Class<? extends Driver>) Class.forName("org.apache.phoenix.jdbc.PhoenixDriver"));
        dataSource.setUrl("jdbc:phoenix:dnode28,dnode29,dnode30:2181");
        Properties properties = new Properties();
        properties.setProperty("schema", "TEST");
        dataSource.setConnectionProperties(properties);
        return dataSource;
    }

    @Bean
    public DataSourceTransactionManager transactionManager(DataSource ds) {
        return new DataSourceTransactionManager(ds);
    }

    @Bean
    public DataSourceInitializer dataSourceInitializer(DataSource dataSource) throws IOException {
        final DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);
        initializer.setDatabasePopulator(databasePopulator());
        initializer.setEnabled(true);
        return initializer;
    }

    private DatabasePopulator databasePopulator() throws IOException {
        ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator();
        resourceDatabasePopulator.setContinueOnError(false);
        resourceDatabasePopulator.addScripts(
            new PathMatchingResourcePatternResolver().getResources("classpath:/phoenix/*.sql")
        );
        return resourceDatabasePopulator;
    }
}
