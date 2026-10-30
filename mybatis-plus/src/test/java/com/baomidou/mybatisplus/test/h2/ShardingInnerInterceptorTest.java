package com.baomidou.mybatisplus.test.h2;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.extension.plugins.datasource.DataSourceProperty;
import com.baomidou.mybatisplus.extension.plugins.datasource.DbCluster;
import com.baomidou.mybatisplus.extension.plugins.datasource.DriverManagerDataSourceProvider;
import com.baomidou.mybatisplus.extension.plugins.datasource.IShardingProcessor;
import com.baomidou.mybatisplus.extension.plugins.datasource.Sharding;
import com.baomidou.mybatisplus.extension.plugins.datasource.ShardingContext;
import com.baomidou.mybatisplus.extension.plugins.datasource.ShardingDatasource;
import com.baomidou.mybatisplus.extension.plugins.datasource.ShardingHelper;
import com.baomidou.mybatisplus.extension.plugins.datasource.ShardingProperties;
import com.baomidou.mybatisplus.extension.plugins.inner.ShardingInnerInterceptor;
import org.apache.ibatis.builder.StaticSqlSource;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.RowBounds;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * Tests the executor hooks used by {@link ShardingInnerInterceptor}.
 */
@Tag("integration")
class ShardingInnerInterceptorTest {

    private static final MybatisConfiguration CONFIGURATION = new MybatisConfiguration();
    private static final BoundSql BOUND_SQL = new BoundSql(CONFIGURATION, "select 1", Collections.emptyList(), null);

    @AfterEach
    void clearShardingContext() {
        ShardingContext.clear();
    }

    @Test
    void beforeQueryPassesStatementAndParameterToTheProcessor() throws Exception {
        Executor executor = mock(Executor.class);
        MappedStatement mappedStatement = statement("query", SqlCommandType.SELECT);
        AtomicReference<MappedStatement> capturedStatement = new AtomicReference<>();
        AtomicReference<Object> capturedParameter = new AtomicReference<>();
        AtomicReference<String> capturedDatasourceKey = new AtomicReference<>();
        IShardingProcessor processor = (ms, parameter, datasourceKey) -> {
            capturedStatement.set(ms);
            capturedParameter.set(parameter);
            capturedDatasourceKey.set(datasourceKey);
            return true;
        };
        ShardingContext.set("orders#reader");

        new ShardingInnerInterceptor(processor)
                .beforeQuery(executor, mappedStatement, "parameter", RowBounds.DEFAULT, null, BOUND_SQL);

        assertThat(capturedStatement).hasValue(mappedStatement);
        assertThat(capturedParameter).hasValue("parameter");
        assertThat(capturedDatasourceKey).hasValue("orders#reader");
        verify(executor).getTransaction();
        verifyNoMoreInteractions(executor);
    }

    @Test
    void beforeUpdatePassesStatementAndParameterToTheProcessor() throws Exception {
        Executor executor = mock(Executor.class);
        MappedStatement statement = statement("update", SqlCommandType.UPDATE);
        AtomicReference<MappedStatement> capturedStatement = new AtomicReference<>();
        AtomicReference<Object> capturedParameter = new AtomicReference<>();
        IShardingProcessor processor = (ms, parameter, datasourceKey) -> {
            capturedStatement.set(ms);
            capturedParameter.set(parameter);
            return true;
        };

        new ShardingInnerInterceptor(processor).beforeUpdate(executor, statement, "parameter");

        assertThat(capturedStatement).hasValue(statement);
        assertThat(capturedParameter).hasValue("parameter");
        verify(executor).getTransaction();
        verifyNoMoreInteractions(executor);
    }

    @Test
    void fallsBackToTheConfiguredDatasourceMappingWhenProcessorDoesNotHandleQueryOrUpdate() throws Exception {
        initializeSharding();
        Executor executor = mock(Executor.class);
        MappedStatement select = statement(AnnotatedMapper.class.getName() + ".select", SqlCommandType.SELECT);
        MappedStatement update = statement(AnnotatedMapper.class.getName() + ".update", SqlCommandType.UPDATE);

        ShardingInnerInterceptor interceptor = new ShardingInnerInterceptor((ms, parameter, key) -> false);
        interceptor.beforeQuery(executor, select, null, RowBounds.DEFAULT, null, BOUND_SQL);
        assertThat(ShardingContext.get()).isEqualTo("orders#reader");

        interceptor.beforeUpdate(executor, update, null);
        assertThat(ShardingContext.get()).isEqualTo("orders#writer#IUD");
    }

    @Test
    void routesWithoutExplicitHelperInitAfterConstructingShardingDatasource() throws Exception {
        ShardingProperties properties = new ShardingProperties();
        properties.setPrimary("orders");
        properties.setDatasource(
                Collections.singletonMap("orders", Collections.singletonList(datasource("writer", DbCluster.master))));
        new ShardingDatasource(new DriverManagerDataSourceProvider(), Collections.emptyMap(), Collections.emptyMap(),
                properties);

        Executor executor = mock(Executor.class);
        MappedStatement update = statement(AnnotatedMapper.class.getName() + ".update", SqlCommandType.UPDATE);
        new ShardingInnerInterceptor((ms, parameter, key) -> false).beforeUpdate(executor, update, null);

        assertThat(ShardingContext.get()).isEqualTo("orders#writer#IUD");
    }

    @Test
    void doesNotRouteUnknownOrFlushStatements() throws Exception {
        AtomicReference<SqlCommandType> routedCommand = new AtomicReference<>();
        IShardingProcessor processor = (ms, parameter, datasourceKey) -> {
            routedCommand.set(ms.getSqlCommandType());
            return true;
        };
        ShardingInnerInterceptor interceptor = new ShardingInnerInterceptor(processor);
        Executor executor = mock(Executor.class);

        interceptor.beforeUpdate(executor, statement("unknown", SqlCommandType.UNKNOWN), null);
        interceptor.beforeUpdate(executor, statement("flush", SqlCommandType.FLUSH), null);

        assertThat(routedCommand).hasValue(null);
    }

    private static void initializeSharding() {
        ShardingProperties properties = new ShardingProperties();
        properties.setPrimary("orders");
        properties.setDatasource(
                Collections.singletonMap(
                        "orders",
                        Arrays.asList(datasource("writer", DbCluster.master), datasource("reader", DbCluster.slave))));
        ShardingHelper.initDbSharding(properties);
    }

    private static DataSourceProperty datasource(String key, DbCluster cluster) {
        DataSourceProperty property = new DataSourceProperty();
        property.setKey(key);
        property.setCluster(cluster);
        return property;
    }

    private static MappedStatement statement(String id, SqlCommandType commandType) {
        return new MappedStatement.Builder(CONFIGURATION, id, new StaticSqlSource(CONFIGURATION, "select 1"),
                commandType).build();
    }

    @Sharding("orders")
    interface AnnotatedMapper {

        void select();

        void update();
    }
}
