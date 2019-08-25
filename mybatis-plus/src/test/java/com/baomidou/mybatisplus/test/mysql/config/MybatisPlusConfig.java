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
package com.baomidou.mybatisplus.test.mysql.config;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.parser.ISqlParser;
import com.baomidou.mybatisplus.extension.MybatisMapWrapperFactory;
import com.baomidou.mybatisplus.extension.injector.methods.additional.AlwaysUpdateSomeColumnById;
import com.baomidou.mybatisplus.extension.injector.methods.additional.InsertBatchSomeColumn;
import com.baomidou.mybatisplus.extension.injector.methods.additional.LogicDeleteByIdWithFill;
import com.baomidou.mybatisplus.extension.plugins.OptimisticLockerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.tenant.TenantHandler;
import com.baomidou.mybatisplus.extension.plugins.tenant.TenantSqlParser;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.baomidou.mybatisplus.test.mysql.MysqlMetaObjectHandler;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

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
@MapperScan({"com.baomidou.mybatisplus.test.base.mapper.children", "com.baomidou.mybatisplus.test.base.mapper.commons", "com.baomidou.mybatisplus.test.mysql.mapper"})
public class MybatisPlusConfig {

    @Bean("mybatisSqlSession")
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource, GlobalConfig globalConfig,
                                               PaginationInterceptor paginationInterceptor) throws Exception {
        MybatisSqlSessionFactoryBean sqlSessionFactory = new MybatisSqlSessionFactoryBean();
        /* 数据源 */
        sqlSessionFactory.setDataSource(dataSource);
        /* 枚举扫描 */
        sqlSessionFactory.setTypeEnumsPackage("com.baomidou.mybatisplus.test.base.enums");
        /* xml扫描 */
        sqlSessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver()
            .getResources("classpath:/mapper/*.xml"));
        /* 扫描 typeHandler */
//        sqlSessionFactory.setTypeHandlersPackage("com.baomidou.mybatisplus.test.base.type");
        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setJdbcTypeForNull(JdbcType.NULL);
        /* 驼峰转下划线 */
        configuration.setMapUnderscoreToCamelCase(true);
        /* 分页插件 */
        configuration.addInterceptor(paginationInterceptor);
        /* 乐观锁插件 */
        configuration.addInterceptor(new OptimisticLockerInterceptor());
        /* map 下划线转驼峰 */
        configuration.setObjectWrapperFactory(new MybatisMapWrapperFactory());
        sqlSessionFactory.setConfiguration(configuration);
        /* 自动填充插件 */
        globalConfig.setMetaObjectHandler(new MysqlMetaObjectHandler());
        sqlSessionFactory.setGlobalConfig(globalConfig);
        return sqlSessionFactory.getObject();
    }

    @Bean
    public GlobalConfig globalConfig() {
        GlobalConfig conf = new GlobalConfig();
        conf.setDbConfig(new GlobalConfig.DbConfig()
            .setColumnFormat("`%s`"));
        DefaultSqlInjector logicSqlInjector = new DefaultSqlInjector() {
            /**
             * 注入自定义全局方法
             */
            @Override
            public List<AbstractMethod> getMethodList(Class<?> mapperClass) {
                List<AbstractMethod> methodList = super.getMethodList(mapperClass);
                methodList.add(new LogicDeleteByIdWithFill());
                // 不要逻辑删除字段, 不要乐观锁字段, 不要填充策略是 UPDATE 的字段
                methodList.add(new InsertBatchSomeColumn(t -> !t.isLogicDelete() && !t.isVersion() && t.getFieldFill() != FieldFill.UPDATE));
                // 不要填充策略是 INSERT 的字段, 不要字段名是 column4 的字段
                methodList.add(new AlwaysUpdateSomeColumnById(t -> t.getFieldFill() != FieldFill.INSERT && !t.getProperty().equals("column4")));
                return methodList;
            }
        };
        conf.setSqlInjector(logicSqlInjector);
        return conf;
    }

    @Bean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        /*
         * 【测试多租户】 SQL 解析处理拦截器<br>
         * 这里固定写成住户 1 实际情况你可以从cookie读取，因此数据看不到 【 麻花藤 】 这条记录（ 注意观察 SQL ）<br>
         */
        List<ISqlParser> sqlParserList = new ArrayList<>();
        TenantSqlParser tenantSqlParser = new TenantSqlParser();
        tenantSqlParser.setTenantHandler(new TenantHandler() {

            @Override
            public Expression getTenantId(boolean where) {
                return new LongValue(1L);
            }

            @Override
            public String getTenantIdColumn() {
                return "tenant_id";
            }

            @Override
            public boolean doTableFilter(String tableName) {
                // 这里可以判断是否过滤表
                return "common_logic_data".equals(tableName) || "mysql_data".equals(tableName)
                    || "result_map_entity".equals(tableName);
            }
        });
        sqlParserList.add(tenantSqlParser);
        paginationInterceptor.setSqlParserList(sqlParserList);
        return paginationInterceptor;
    }
}
