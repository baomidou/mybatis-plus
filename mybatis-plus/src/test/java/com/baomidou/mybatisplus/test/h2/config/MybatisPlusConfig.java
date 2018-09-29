package com.baomidou.mybatisplus.test.h2.config;

import javax.sql.DataSource;

import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.injector.LogicSqlInjector;
import com.baomidou.mybatisplus.extension.plugins.OptimisticLockerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.PerformanceInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.baomidou.mybatisplus.test.h2.H2MetaObjectHandler;

/**
 * <p>
 * Mybatis Plus Config
 * </p>
 *
 * @author Caratacus
 * @since 2017/4/1
 */
@Configuration
@MapperScan("com.baomidou.mybatisplus.test.h2.entity.mapper")
public class MybatisPlusConfig {

    @Bean("mybatisSqlSession")
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource, ResourceLoader resourceLoader, GlobalConfig globalConfiguration) throws Exception {
        MybatisSqlSessionFactoryBean sqlSessionFactory = new MybatisSqlSessionFactoryBean();
        sqlSessionFactory.setDataSource(dataSource);
//        sqlSessionFactory.setConfigLocation(resourceLoader.getResource("classpath:mybatis-config.xml"));
        sqlSessionFactory.setTypeAliasesPackage("com.baomidou.mybatisplus.test.h2.entity.persistent");
        MybatisConfiguration configuration = new MybatisConfiguration();
//        configuration.setDefaultScriptingLanguage(MybatisXMLLanguageDriver.class);
//        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setJdbcTypeForNull(JdbcType.NULL);
        /**
         * 下划线转驼峰开启
         */
        configuration.setMapUnderscoreToCamelCase(true);
        sqlSessionFactory.setConfiguration(configuration);
        PaginationInterceptor pagination = new PaginationInterceptor();
        OptimisticLockerInterceptor optLock = new OptimisticLockerInterceptor();
        sqlSessionFactory.setPlugins(new Interceptor[]{
            pagination,
            optLock,
            new PerformanceInterceptor()
        });
        globalConfiguration.setMetaObjectHandler(new H2MetaObjectHandler());
        sqlSessionFactory.setGlobalConfig(globalConfiguration);
        sqlSessionFactory.setTypeEnumsPackage("com.baomidou.mybatisplus.test.h2.entity.enums");
        return sqlSessionFactory.getObject();
    }

    @Bean
    public GlobalConfig globalConfiguration() {
        GlobalConfig conf = new GlobalConfig();
        conf.setSqlInjector(new LogicSqlInjector());
        conf.setDbConfig(new GlobalConfig.DbConfig()
            .setLogicDeleteValue("1")
            .setLogicNotDeleteValue("0")
            .setIdType(IdType.ID_WORKER));
        return conf;
    }
}
