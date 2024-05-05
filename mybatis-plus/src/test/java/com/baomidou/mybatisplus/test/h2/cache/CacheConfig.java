package com.baomidou.mybatisplus.test.h2.cache;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.EnumOrdinalTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@MapperScan("com.baomidou.mybatisplus.test.h2.cache.mapper")
public class CacheConfig {

    @Bean("mybatisSqlSession")
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean sqlSessionFactory = new MybatisSqlSessionFactoryBean();
        sqlSessionFactory.setDataSource(dataSource);
        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setJdbcTypeForNull(JdbcType.NULL);
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setDefaultExecutorType(ExecutorType.REUSE);
        configuration.setDefaultEnumTypeHandler(EnumOrdinalTypeHandler.class);
        configuration.setCacheEnabled(true);
        sqlSessionFactory.setConfiguration(configuration);
        MybatisPlusInterceptor pagination = new MybatisPlusInterceptor();
        pagination.addInnerInterceptor(new PaginationInnerInterceptor());

        sqlSessionFactory.setPlugins(pagination);
        return sqlSessionFactory.getObject();
    }

}
