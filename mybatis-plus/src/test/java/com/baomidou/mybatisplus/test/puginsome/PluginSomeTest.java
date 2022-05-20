package com.baomidou.mybatisplus.test.puginsome;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.baomidou.mybatisplus.test.BaseDbTest;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author miemie
 * @since 2020-06-24
 */
public class PluginSomeTest extends BaseDbTest<AMapper> {

    @Test
    void test() {
        doTest(m -> {
            List<A> a = m.list();
            a.forEach(System.out::println);
        });
    }

    @Override
    protected List<Interceptor> interceptors() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new InnerInterceptor() {
            @Override
            public boolean willDoQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
                System.out.println("willDoQuery");
                return true;
            }

            @Override
            public void beforeQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
                System.out.println("beforeQuery");
            }

            @Override
            public boolean willDoUpdate(Executor executor, MappedStatement ms, Object parameter) throws SQLException {
                System.out.println("willDoUpdate");
                return true;
            }

            @Override
            public void beforeUpdate(Executor executor, MappedStatement ms, Object parameter) throws SQLException {
                System.out.println("beforeUpdate");
            }

            @Override
            public void beforePrepare(StatementHandler sh, Connection connection, Integer transactionTimeout) {
                System.out.println("beforePrepare");
            }

            @Override
            public void beforeGetBoundSql(StatementHandler sh) {
                System.out.println("beforeGetBoundSql");
            }
        });
        return Collections.singletonList(interceptor);
    }

    @Override
    protected List<Class<?>> otherMapper() {
        return Collections.singletonList(BMapper.class);
    }

    @Override
    protected String tableDataSql() {
        return "insert into a (id,name) values (1,'1'),(2,'2');" +
            "insert into b (id,a_id,name) values (1,'1','1-1'),(2,'2','2-2')";
    }

    @Override
    protected List<String> tableSql() {
        return Arrays.asList("drop table if exists a",
            "CREATE TABLE IF NOT EXISTS a (\n" +
                "id integer NOT NULL,\n" +
                "name VARCHAR(30) NULL DEFAULT NULL,\n" +
                "PRIMARY KEY (id)" +
                ")", "drop table if exists b",
            "CREATE TABLE IF NOT EXISTS b (\n" +
                "id integer NOT NULL,\n" +
                "a_id integer NOT NULL,\n" +
                "name VARCHAR(30) NULL DEFAULT NULL,\n" +
                "PRIMARY KEY (id)" +
                ")");
    }
}
