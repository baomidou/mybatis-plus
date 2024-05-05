package com.baomidou.mybatisplus.test.phoenix.config;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.extension.MybatisMapWrapperFactory;
import com.baomidou.mybatisplus.extension.injector.methods.Upsert;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.List;

/**
 * Mybatis Plus Config
 *
 * @author Caratacus
 * @since 2017/4/1
 */
@Configuration
@MapperScan("com.baomidou.mybatisplus.test.phoenix.mapper")
public class MybatisPlusConfig {

    @Bean("mybatisSqlSession")
    public SqlSessionFactory sqlSessionFactory(
        DataSource dataSource,
        GlobalConfig globalConfig
    ) throws Exception {
        MybatisSqlSessionFactoryBean sqlSessionFactory = new MybatisSqlSessionFactoryBean();
        /* 数据源 */
        sqlSessionFactory.setDataSource(dataSource);
        /* xml扫描 */
//        sqlSessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver()
//            .getResources("classpath:/mapper/*.xml"));
        /* 扫描 typeHandler */
        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setJdbcTypeForNull(JdbcType.NULL);
        /* 驼峰转下划线 */
        configuration.setMapUnderscoreToCamelCase(true);
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        mybatisPlusInterceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        sqlSessionFactory.setPlugins(mybatisPlusInterceptor);
        /* map 下划线转驼峰 */
        configuration.setObjectWrapperFactory(new MybatisMapWrapperFactory());
        sqlSessionFactory.setConfiguration(configuration);
        sqlSessionFactory.setGlobalConfig(globalConfig);
        return sqlSessionFactory.getObject();
    }

    @Bean
    public GlobalConfig globalConfig() {
        GlobalConfig conf = new GlobalConfig();
//        conf.setDbConfig(new GlobalConfig.DbConfig()
//            .setColumnFormat("`%s`"));
        DefaultSqlInjector phoenixSqlInjector = new DefaultSqlInjector() {
            /**
             * 注入自定义全局方法
             */
            @Override
            public List<AbstractMethod> getMethodList(org.apache.ibatis.session.Configuration configuration, Class<?> mapperClass, TableInfo tableInfo) {
                List<AbstractMethod> methodList = super.getMethodList(configuration, mapperClass, tableInfo);
                methodList.add(new Upsert());
                return methodList;
            }
        };
        conf.setSqlInjector(phoenixSqlInjector);
        return conf;
    }
}
