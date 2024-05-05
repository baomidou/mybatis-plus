package com.baomidou.mybatisplus.test.h2.config;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.extension.injector.methods.AlwaysUpdateSomeColumnById;
import com.baomidou.mybatisplus.extension.injector.methods.InsertBatchSomeColumn;
import com.baomidou.mybatisplus.extension.injector.methods.LogicDeleteByIdWithFill;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.EnumOrdinalTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

import javax.sql.DataSource;
import java.util.List;

/**
 * Mybatis Plus Config
 *
 * @author Caratacus
 * @since 2017/4/1
 */
@Configuration
@MapperScan("com.baomidou.mybatisplus.test.h2.mapper")
public class MybatisPlusConfigLogicDelete {

    @Bean("mybatisSqlSession")
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource, ResourceLoader resourceLoader, GlobalConfig globalConfig) throws Exception {
        MybatisSqlSessionFactoryBean sqlSessionFactory = new MybatisSqlSessionFactoryBean();
        sqlSessionFactory.setDataSource(dataSource);
//        sqlSessionFactory.setConfigLocation(resourceLoader.getResource("classpath:mybatis-config-object-factory.xml"));
        sqlSessionFactory.setTypeAliasesPackage("com.baomidou.mybatisplus.test.h2.entity.persistent");
        MybatisConfiguration configuration = new MybatisConfiguration();
//        configuration.setDefaultScriptingLanguage(MybatisXMLLanguageDriver.class);
//        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setJdbcTypeForNull(JdbcType.NULL);
        /*
         * 下划线转驼峰开启
         */
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setDefaultExecutorType(ExecutorType.REUSE);
        configuration.setDefaultEnumTypeHandler(EnumOrdinalTypeHandler.class);  //默认枚举处理
        sqlSessionFactory.setConfiguration(configuration);

        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        mybatisPlusInterceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        sqlSessionFactory.setPlugins(mybatisPlusInterceptor);

        globalConfig.setSqlInjector(new DefaultSqlInjector() {

            /**
             * 测试注入自定义方法
             */
            @Override
            public List<AbstractMethod> getMethodList(org.apache.ibatis.session.Configuration configuration, Class<?> mapperClass, TableInfo tableInfo) {
                List<AbstractMethod> methodList = super.getMethodList(configuration, mapperClass, tableInfo);
                methodList.add(new LogicDeleteByIdWithFill());
                methodList.add(new AlwaysUpdateSomeColumnById(t -> t.getFieldFill() != FieldFill.INSERT));
                methodList.add(new InsertBatchSomeColumn(t -> !(t.getFieldFill() == FieldFill.UPDATE
                    || t.isLogicDelete() || t.getProperty().equals("version"))));
                return methodList;
            }
        });
        sqlSessionFactory.setGlobalConfig(globalConfig);
        sqlSessionFactory.setTypeEnumsPackage("com.baomidou.mybatisplus.test.h2.enums");
        return sqlSessionFactory.getObject();
    }

    @Bean
    public GlobalConfig globalConfiguration() {
        GlobalConfig conf = new GlobalConfig();
        conf.setEnableSqlRunner(true)
            .setDbConfig(new GlobalConfig.DbConfig()
                .setLogicDeleteValue("NOW()")
                .setLogicNotDeleteValue("NULL")
                .setIdType(IdType.ASSIGN_ID));
        return conf;
    }
}
