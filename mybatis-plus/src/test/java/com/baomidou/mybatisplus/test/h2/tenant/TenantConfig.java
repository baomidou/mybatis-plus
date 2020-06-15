package com.baomidou.mybatisplus.test.h2.tenant;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.executor.MybatisBatchExecutor;
import com.baomidou.mybatisplus.core.executor.MybatisReuseExecutor;
import com.baomidou.mybatisplus.core.executor.MybatisSimpleExecutor;
import com.baomidou.mybatisplus.core.parser.ISqlParser;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.tenant.TenantHandler;
import com.baomidou.mybatisplus.extension.plugins.tenant.TenantSqlParser;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.transaction.Transaction;
import org.apache.ibatis.type.EnumOrdinalTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author nieqiuqiu 2019/12/8
 */
@Configuration
@MapperScan("com.baomidou.mybatisplus.test.h2.tenant.mapper")
public class TenantConfig {

    //模拟用户切换
    static Long TENANT_ID = 1L;

    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean sqlSessionFactory = new MybatisSqlSessionFactoryBean();
        sqlSessionFactory.setDataSource(dataSource);
        TenantSqlParser tenantSqlParser = new TenantSqlParser();
        tenantSqlParser.setTenantHandler(new TenantHandler() {
            @Override
            public Expression getTenantId(boolean select) {
                return new LongValue(TENANT_ID);
            }

            @Override
            public String getTenantIdColumn() {
                return "tenant_id";
            }

            @Override
            public boolean doTableFilter(String tableName) {
                return false;
            }
        });
        MybatisConfiguration configuration = new MybatisConfiguration() {
            @Override
            public Executor newExecutor(Transaction transaction, ExecutorType executorType) {
                executorType = executorType == null ? defaultExecutorType : executorType;
                executorType = executorType == null ? ExecutorType.SIMPLE : executorType;
                Executor executor;
                if (ExecutorType.BATCH == executorType) {
                    executor = new MybatisBatchExecutor(this, transaction);
                } else if (ExecutorType.REUSE == executorType) {
                    executor = new MybatisReuseExecutor(this, transaction);
                } else {
                    executor = new MybatisSimpleExecutor(this, transaction);
                }
                if (cacheEnabled) {
                    executor = new CustomCacheExecutor(executor, tenantSqlParser.getTenantHandler());
                }
                executor = (Executor) interceptorChain.pluginAll(executor);
                return executor;
            }
        };
        configuration.setJdbcTypeForNull(JdbcType.NULL);
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setDefaultExecutorType(ExecutorType.REUSE);
        configuration.setDefaultEnumTypeHandler(EnumOrdinalTypeHandler.class);
        configuration.setCacheEnabled(true);
        sqlSessionFactory.setConfiguration(configuration);
        PaginationInterceptor pagination = new PaginationInterceptor();
        List<ISqlParser> sqlParserList = new ArrayList<>();
        sqlParserList.add(tenantSqlParser);
        pagination.setSqlParserList(sqlParserList);
        sqlSessionFactory.setPlugins(pagination);
        return sqlSessionFactory.getObject();
    }

}
