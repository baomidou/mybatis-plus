package com.baomidou.mybatisplus.core.executor;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import org.apache.ibatis.builder.StaticSqlSource;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.BatchExecutor;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.ReuseExecutor;
import org.apache.ibatis.executor.SimpleExecutor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.transaction.jdbc.JdbcTransaction;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.sql.Connection;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("unit")
class CacheKeyCustomizerTest {

    @ParameterizedTest
    @EnumSource(ExecutorType.class)
    void customizesKeysWithAndWithoutSecondLevelCache(ExecutorType type) {
        for (boolean cacheEnabled : new boolean[]{false, true}) {
            MybatisConfiguration configuration = new MybatisConfiguration();
            configuration.setCacheEnabled(cacheEnabled);
            ContextTransaction transaction = new ContextTransaction();
            Executor executor = configuration.newExecutor(transaction, type);
            MappedStatement statement = new MappedStatement.Builder(configuration, "contextQuery",
                    new StaticSqlSource(configuration, "SELECT 1"), SqlCommandType.SELECT).build();
            BoundSql sql = statement.getBoundSql(null);
            CacheKey first = executor.createCacheKey(statement, null, RowBounds.DEFAULT, sql);
            assertThat(executor.createCacheKey(statement, null, RowBounds.DEFAULT, sql)).isEqualTo(first);
            transaction.context = "second";
            assertThat(executor.createCacheKey(statement, null, RowBounds.DEFAULT, sql)).isNotEqualTo(first);
            transaction.context = "first";
            assertThat(executor.createCacheKey(statement, null, RowBounds.DEFAULT, sql)).isEqualTo(first);
        }
    }

    @Test
    void ordinaryTransactionsKeepStandardExecutors() {
        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setCacheEnabled(false);
        JdbcTransaction transaction = new JdbcTransaction((Connection) null);
        assertThat(configuration.newExecutor(transaction, ExecutorType.SIMPLE))
                .isExactlyInstanceOf(SimpleExecutor.class);
        assertThat(configuration.newExecutor(transaction, ExecutorType.REUSE)).isExactlyInstanceOf(ReuseExecutor.class);
        assertThat(configuration.newExecutor(transaction, ExecutorType.BATCH)).isExactlyInstanceOf(BatchExecutor.class);
    }

    private static class ContextTransaction extends JdbcTransaction implements CacheKeyCustomizer {

        String context = "first";

        ContextTransaction() {
            super((Connection) null);
        }

        @Override
        public void customizeCacheKey(CacheKey cacheKey) {
            cacheKey.update(context);
        }
    }
}
