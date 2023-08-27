package com.baomidou.mybatisplus.test.toolkit;

import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import org.apache.ibatis.builder.StaticSqlSource;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.SimpleExecutor;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.transaction.jdbc.JdbcTransaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.sql.Connection;

/**
 * @author nieqiurong 2023/8/5 10:28
 */
public class PluginUtilsTest {

    @Intercepts({
        @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})}
    )
    static class TestInterceptor implements Interceptor {

        @Override
        public Object intercept(Invocation invocation) throws InvocationTargetException, IllegalAccessException {
            return invocation.proceed();
        }

    }

    @Test
    void testRealTargetByProxy() {
        Configuration configuration = new Configuration();
        Executor executor = new SimpleExecutor(configuration, new JdbcTransaction(Mockito.mock(Connection.class)));
        MappedStatement mappedStatement = new MappedStatement.Builder(configuration, "test", new StaticSqlSource(configuration, "-------------"), SqlCommandType.SELECT).build();
        RoutingStatementHandler statementHandler = new RoutingStatementHandler(executor, mappedStatement, new Object(), new RowBounds(), null, Mockito.mock(BoundSql.class));
        Object plugin = new TestInterceptor().plugin(statementHandler);
        Assertions.assertTrue(Proxy.isProxyClass(plugin.getClass()));
        Assertions.assertEquals(statementHandler, PluginUtils.realTarget(plugin));
    }

}
