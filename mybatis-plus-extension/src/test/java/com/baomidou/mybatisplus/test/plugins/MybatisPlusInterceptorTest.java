package com.baomidou.mybatisplus.test.plugins;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import org.apache.ibatis.builder.StaticSqlSource;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.RowBounds;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

class MybatisPlusInterceptorTest {

    @Test
    void shouldInvokeBeforeQueryForCursorQueries() throws Throwable {
        AtomicInteger beforeQueryCount = new AtomicInteger();
        AtomicInteger queryCursorCount = new AtomicInteger();
        Cursor<Object> expectedCursor = new TestCursor<>(Collections.singletonList(1));
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new InnerInterceptor() {
            @Override
            public void beforeQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds,
                                    org.apache.ibatis.session.ResultHandler resultHandler,
                                    org.apache.ibatis.mapping.BoundSql boundSql) {
                beforeQueryCount.incrementAndGet();
            }
        });
        Executor executor = executorProxy(queryCursorCount, expectedCursor);

        Object result = interceptor.intercept(new Invocation(executor, queryCursorMethod(),
            new Object[]{mappedStatement(), null, RowBounds.DEFAULT}));

        Assertions.assertSame(expectedCursor, result);
        Assertions.assertEquals(1, beforeQueryCount.get());
        Assertions.assertEquals(1, queryCursorCount.get());
    }

    @Test
    void shouldReturnEmptyCursorWhenQueryIsRejected() throws Throwable {
        AtomicInteger queryCursorCount = new AtomicInteger();
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new InnerInterceptor() {
            @Override
            public boolean willDoQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds,
                                       org.apache.ibatis.session.ResultHandler resultHandler,
                                       org.apache.ibatis.mapping.BoundSql boundSql) {
                return false;
            }
        });
        Executor executor = executorProxy(queryCursorCount, new TestCursor<>(Collections.singletonList(1)));

        Object result = interceptor.intercept(new Invocation(executor, queryCursorMethod(),
            new Object[]{mappedStatement(), null, RowBounds.DEFAULT}));

        Assertions.assertInstanceOf(Cursor.class, result);
        Cursor<?> cursor = (Cursor<?>) result;
        Assertions.assertFalse(cursor.isOpen());
        Assertions.assertTrue(cursor.isConsumed());
        Assertions.assertFalse(cursor.iterator().hasNext());
        Assertions.assertEquals(0, queryCursorCount.get());
    }

    private static Executor executorProxy(AtomicInteger queryCursorCount, Cursor<Object> cursor) {
        return (Executor) Proxy.newProxyInstance(
            MybatisPlusInterceptorTest.class.getClassLoader(),
            new Class[]{Executor.class},
            (proxy, method, args) -> {
                if ("queryCursor".equals(method.getName())) {
                    queryCursorCount.incrementAndGet();
                    return cursor;
                }
                if ("toString".equals(method.getName())) {
                    return "ExecutorProxy";
                }
                return primitiveDefault(method.getReturnType());
            }
        );
    }

    private static MappedStatement mappedStatement() {
        Configuration configuration = new Configuration();
        StaticSqlSource sqlSource = new StaticSqlSource(configuration, "SELECT 1");
        return new MappedStatement.Builder(configuration, "testCursor", sqlSource, SqlCommandType.SELECT).build();
    }

    private static Method queryCursorMethod() throws NoSuchMethodException {
        return Executor.class.getMethod("queryCursor", MappedStatement.class, Object.class, RowBounds.class);
    }

    private static Object primitiveDefault(Class<?> returnType) {
        if (!returnType.isPrimitive()) {
            return null;
        }
        if (boolean.class == returnType) {
            return false;
        }
        if (byte.class == returnType) {
            return (byte) 0;
        }
        if (short.class == returnType) {
            return (short) 0;
        }
        if (int.class == returnType) {
            return 0;
        }
        if (long.class == returnType) {
            return 0L;
        }
        if (float.class == returnType) {
            return 0F;
        }
        if (double.class == returnType) {
            return 0D;
        }
        if (char.class == returnType) {
            return '\0';
        }
        throw new IllegalArgumentException("Unsupported primitive type: " + returnType);
    }

    private static final class TestCursor<T> implements Cursor<T> {
        private final Iterable<T> values;

        private TestCursor(Iterable<T> values) {
            this.values = values;
        }

        @Override
        public boolean isOpen() {
            return true;
        }

        @Override
        public boolean isConsumed() {
            return false;
        }

        @Override
        public int getCurrentIndex() {
            return -1;
        }

        @Override
        public Iterator<T> iterator() {
            return values.iterator();
        }

        @Override
        public void close() throws IOException {
        }
    }
}
