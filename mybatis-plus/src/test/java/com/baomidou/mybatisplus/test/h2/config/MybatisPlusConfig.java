package com.baomidou.mybatisplus.test.h2.config;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.extension.injector.methods.AlwaysUpdateSomeColumnById;
import com.baomidou.mybatisplus.extension.injector.methods.InsertBatchSomeColumn;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DataChangeRecorderInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.baomidou.mybatisplus.test.h2.H2MetaObjectHandler;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.EnumOrdinalTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

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
public class MybatisPlusConfig {

    @Bean("mybatisSqlSession")
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource, GlobalConfig globalConfig) throws Exception {
        MybatisSqlSessionFactoryBean sqlSessionFactory = new MybatisSqlSessionFactoryBean();
        sqlSessionFactory.setDataSource(dataSource);
        sqlSessionFactory.setTypeAliasesPackage("com.baomidou.mybatisplus.test.h2.entity.persistent");
        MybatisConfiguration configuration = new MybatisConfiguration();
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
        // mybatisPlusInterceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());
        mybatisPlusInterceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        mybatisPlusInterceptor.addInnerInterceptor(new DataChangeRecorderInnerInterceptor());
        sqlSessionFactory.setPlugins(mybatisPlusInterceptor);

        globalConfig.setMetaObjectHandler(new H2MetaObjectHandler());
        globalConfig.setSqlInjector(new DefaultSqlInjector() {

            /**
             * 测试注入自定义方法
             */
            @Override
            public List<AbstractMethod> getMethodList(org.apache.ibatis.session.Configuration configuration, Class<?> mapperClass, TableInfo tableInfo) {
                List<AbstractMethod> methodList = super.getMethodList(configuration, mapperClass, tableInfo);
//                methodList.add(new LogicDeleteByIdWithFill());
                methodList.add(new AlwaysUpdateSomeColumnById(t -> t.getFieldFill() != FieldFill.INSERT));
                methodList.add(new InsertBatchSomeColumn(t -> !(t.getFieldFill() == FieldFill.UPDATE
                    || t.isLogicDelete() || t.getProperty().equals("version"))));
                return methodList;
            }
        });
        sqlSessionFactory.setGlobalConfig(globalConfig);
        return sqlSessionFactory.getObject();
    }

    @Bean
    public GlobalConfig globalConfiguration() {
        GlobalConfig conf = new GlobalConfig();
        conf.setEnableSqlRunner(true)
            .setDbConfig(new GlobalConfig.DbConfig()
                .setLogicDeleteValue("1")
                .setLogicNotDeleteValue("0"));
        return conf;
    }

    @Bean
    public TransactionTemplate transactionTemplate(PlatformTransactionManager platformTransactionManager){
        return new TransactionTemplate(platformTransactionManager);
    }

}
