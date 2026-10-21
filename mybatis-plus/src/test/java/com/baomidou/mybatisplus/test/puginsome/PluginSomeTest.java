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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author miemie
 * @since 2020-06-24
 */
public class PluginSomeTest extends BaseDbTest<AMapper> {

    /** 每个钩子被调用的次数；父类构造里建的拦截器持有本实例，钩子执行时字段已初始化。 */
    private final Map<String, AtomicInteger> calls = new ConcurrentHashMap<>();

    @Test
    void test() {
        doTest(m -> assertThat(m.list()).hasSize(2));
        // 一次查询：查询钩子各一次，一对多结果集要预编译三条语句
        assertThat(calls).containsOnlyKeys("willDoQuery", "beforeQuery", "beforePrepare");
        assertThat(calls.get("willDoQuery")).hasValue(1);
        assertThat(calls.get("beforeQuery")).hasValue(1);
        assertThat(calls.get("beforePrepare")).hasValue(3);

        calls.clear();
        doTest(m -> assertThat(m.deleteById(1)).isEqualTo(1));
        // 更新只走 update 那组钩子
        assertThat(calls).containsOnlyKeys("willDoUpdate", "beforeUpdate", "beforePrepare");
        assertThat(calls.get("willDoUpdate")).hasValue(1);
        assertThat(calls.get("beforeUpdate")).hasValue(1);
    }

    @Override
    protected List<Interceptor> interceptors() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new InnerInterceptor() {

            @Override
            public boolean willDoQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds,
                    ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
                calls.computeIfAbsent("willDoQuery", k -> new AtomicInteger()).incrementAndGet();
                return true;
            }

            @Override
            public void beforeQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds,
                    ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
                calls.computeIfAbsent("beforeQuery", k -> new AtomicInteger()).incrementAndGet();
            }

            @Override
            public boolean willDoUpdate(Executor executor, MappedStatement ms, Object parameter) throws SQLException {
                calls.computeIfAbsent("willDoUpdate", k -> new AtomicInteger()).incrementAndGet();
                return true;
            }

            @Override
            public void beforeUpdate(Executor executor, MappedStatement ms, Object parameter) throws SQLException {
                calls.computeIfAbsent("beforeUpdate", k -> new AtomicInteger()).incrementAndGet();
            }

            @Override
            public void beforePrepare(StatementHandler sh, Connection connection, Integer transactionTimeout) {
                calls.computeIfAbsent("beforePrepare", k -> new AtomicInteger()).incrementAndGet();
            }

            @Override
            public void beforeGetBoundSql(StatementHandler sh) {
                calls.computeIfAbsent("beforeGetBoundSql", k -> new AtomicInteger()).incrementAndGet();
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
        return "insert into a (id,name) values (1,'1'),(2,'2');"
                + "insert into b (id,a_id,name) values (1,'1','1-1'),(2,'2','2-2')";
    }

    @Override
    protected List<String> tableSql() {
        return Arrays.asList(
                "drop table if exists a",
                "CREATE TABLE IF NOT EXISTS a (\n" + "id integer NOT NULL,\n" + "name VARCHAR(30) NULL DEFAULT NULL,\n"
                        + "PRIMARY KEY (id)" + ")",
                "drop table if exists b",
                "CREATE TABLE IF NOT EXISTS b (\n" + "id integer NOT NULL,\n" + "a_id integer NOT NULL,\n"
                        + "name VARCHAR(30) NULL DEFAULT NULL,\n" + "PRIMARY KEY (id)" + ")");
    }
}
