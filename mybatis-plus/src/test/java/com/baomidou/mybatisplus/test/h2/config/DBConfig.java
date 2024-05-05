package com.baomidou.mybatisplus.test.h2.config;

import org.h2.Driver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.io.IOException;

/**
 * H2 Memory Database config
 *
 * @author Caratacus
 * @since 2017/4/1
 */
@Configuration
@EnableTransactionManagement
public class DBConfig {

    private String locationPattern = "classpath:/h2/*.sql";

    @Bean
    public DataSource dataSource(){
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
        dataSource.setDriver(new Driver());
        dataSource.setUrl("jdbc:h2:mem:test;MODE=mysql;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
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
            new PathMatchingResourcePatternResolver().getResources(locationPattern)
        );
        return resourceDatabasePopulator;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource ds){
        return new JdbcTemplate(ds);
    }

    public String getLocationPattern() {
        return locationPattern;
    }

    public void setLocationPattern(String locationPattern) {
        this.locationPattern = locationPattern;
    }
}
