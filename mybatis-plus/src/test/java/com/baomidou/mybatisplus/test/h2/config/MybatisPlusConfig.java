/*
 * Copyright (c) 2011-2020, baomidou (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.test.h2.config;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.parser.AbstractJsqlParser;
import com.baomidou.mybatisplus.core.parser.ISqlParser;
import com.baomidou.mybatisplus.extension.injector.methods.AlwaysUpdateSomeColumnById;
import com.baomidou.mybatisplus.extension.injector.methods.InsertBatchSomeColumn;
import com.baomidou.mybatisplus.extension.injector.methods.LogicDeleteByIdWithFill;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.OptimisticLockerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.SqlExplainInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.baomidou.mybatisplus.test.h2.H2MetaObjectHandler;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.update.Update;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.EnumOrdinalTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

import javax.sql.DataSource;
import java.util.ArrayList;
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
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        SqlExplainInterceptor sqlExplainInterceptor = new SqlExplainInterceptor();
        List<ISqlParser> sqlParserList = new ArrayList<>();
        sqlParserList.add(new AbstractJsqlParser() {

            @Override
            public void processInsert(Insert insert) {

            }

            @Override
            public void processDelete(Delete delete) {

            }

            @Override
            public void processUpdate(Update update) {

            }

            @Override
            public void processSelectBody(SelectBody selectBody) {

            }
        });
        sqlExplainInterceptor.setSqlParserList(sqlParserList);
        OptimisticLockerInterceptor optLock = new OptimisticLockerInterceptor();
        sqlSessionFactory.setPlugins(interceptor,
            optLock,
            sqlExplainInterceptor);
        globalConfig.setMetaObjectHandler(new H2MetaObjectHandler());
        globalConfig.setSqlInjector(new DefaultSqlInjector() {

            /**
             * 测试注入自定义方法
             */
            @Override
            public List<AbstractMethod> getMethodList(Class<?> mapperClass) {
                List<AbstractMethod> methodList = super.getMethodList(mapperClass);
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
                .setLogicDeleteValue("1")
                .setLogicNotDeleteValue("0")
                .setIdType(IdType.ID_WORKER));
        return conf;
    }
}
