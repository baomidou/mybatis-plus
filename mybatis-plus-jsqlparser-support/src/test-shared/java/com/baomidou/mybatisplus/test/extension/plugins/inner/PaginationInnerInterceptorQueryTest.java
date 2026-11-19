package com.baomidou.mybatisplus.test.extension.plugins.inner;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.builder.StaticSqlSource;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.RowBounds;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
class PaginationInnerInterceptorQueryTest {

    private static final String BASE_SQL = "SELECT id FROM product ORDER BY id";
    private static final String LIMIT_PARAMETER = "mybatis_plus_first";
    private static final String OFFSET_PARAMETER = "mybatis_plus_second";

    private final Configuration configuration = new Configuration();
    private final PaginationInnerInterceptor pagination = new PaginationInnerInterceptor(DbType.H2);
    private final Executor executor = mock(Executor.class);

    // 先应用每页条数上限，再计算总页数并判断越界。Page 上的配置优先于拦截器配置。
    @ParameterizedTest
    @CsvSource({"false,false", "false,true", "true,false", "true,true"})
    void appliesMaxLimitBeforeCheckingPageOverflow(boolean pageLimit, boolean overflow) throws SQLException {
        pagination.setMaxLimit(pageLimit ? 5L : 10L);
        pagination.setOverflow(overflow);
        Page<Integer> page = new Page<>(2, 100);
        if (pageLimit) {
            page.setMaxLimit(10L);
        }

        stubCount(20);
        query("pageLimit", BASE_SQL, page);

        List<BoundSql> queries = capturedQueries(2);
        assertSql(queries.get(0), "SELECT COUNT(*) AS total FROM product");
        assertSql(queries.get(1), BASE_SQL + " LIMIT ? OFFSET ?", LIMIT_PARAMETER, OFFSET_PARAMETER);
        assertThat(queries.get(1).getAdditionalParameter(LIMIT_PARAMETER)).isEqualTo(10L);
        assertThat(queries.get(1).getAdditionalParameter(OFFSET_PARAMETER)).isEqualTo(10L);
        assertThat(page.getCurrent()).isEqualTo(2);
        assertThat(page.getSize()).isEqualTo(10);
        assertThat(page.getTotal()).isEqualTo(20);
        assertThat(page.getPages()).isEqualTo(2);
    }

    // 应用上限后页码仍越界时，按 overflow 配置回到第一页或跳过数据查询。
    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void stillHandlesOverflowAfterApplyingMaxLimit(boolean overflow) throws SQLException {
        pagination.setMaxLimit(10L);
        pagination.setOverflow(overflow);
        Page<Integer> page = new Page<>(3, 100);

        stubCount(20);
        query("overflow", BASE_SQL, page);

        List<BoundSql> queries = capturedQueries(overflow ? 2 : 1);
        assertSql(queries.get(0), "SELECT COUNT(*) AS total FROM product");
        if (overflow) {
            assertSql(queries.get(1), BASE_SQL + " LIMIT ?", LIMIT_PARAMETER);
            assertThat(queries.get(1).getAdditionalParameter(LIMIT_PARAMETER)).isEqualTo(10L);
        }
        assertThat(page.getCurrent()).isEqualTo(overflow ? 1 : 3);
        assertThat(page.getSize()).isEqualTo(10);
        assertThat(page.getTotal()).isEqualTo(20);
    }

    // 关闭 COUNT 或使用负数 size 时跳过计数，数据查询仍应用已配置的条数上限。
    @ParameterizedTest
    @CsvSource({"2,100,false", "1,-1,true"})
    void skipsCountButAppliesMaxLimit(long current, long size, boolean searchCount) throws SQLException {
        pagination.setMaxLimit(10L);
        Page<Integer> page = new Page<>(current, size, searchCount);

        query("skipCount", BASE_SQL, page);

        BoundSql dataSql = capturedQueries(1).get(0);
        if (current == 1) {
            assertSql(dataSql, BASE_SQL + " LIMIT ?", LIMIT_PARAMETER);
        } else {
            assertSql(dataSql, BASE_SQL + " LIMIT ? OFFSET ?", LIMIT_PARAMETER, OFFSET_PARAMETER);
            assertThat(dataSql.getAdditionalParameter(OFFSET_PARAMETER)).isEqualTo(10L);
        }
        assertThat(dataSql.getAdditionalParameter(LIMIT_PARAMETER)).isEqualTo(10L);
        assertThat(page.getSize()).isEqualTo(10);
        assertThat(page.getTotal()).isZero();
    }

    // 无 GROUP BY 时，COUNT SQL 保留 HAVING 中的聚合表达式或别名及参数。总数为零时跳过数据查询。
    @ParameterizedTest
    @CsvSource({"SUM(amount),true,10,1", "SUM(amount),true,20,0", "SUM(amount),false,10,1", "SUM(amount),false,20,0",
            "amount_total,true,10,1", "amount_total,true,20,0", "amount_total,false,10,1", "amount_total,false,20,0"})
    void preservesHavingWithoutGroupByInCount(String expression, boolean optimize, int minimum, long total)
            throws SQLException {
        String sql = "SELECT SUM(amount) AS amount_total FROM payment WHERE amount > ? HAVING " + expression + " > ?";
        Page<Integer> page = new Page<Integer>(1, 1).setOptimizeCountSql(optimize);

        stubCount(total);
        query("having." + expression, sql, Map.of("page", page, "start", 1, "minimum", minimum), "start", "minimum");

        List<BoundSql> queries = capturedQueries(total == 0 ? 1 : 2);
        assertSql(queries.get(0), "SELECT COUNT(*) FROM (" + sql + ") TOTAL", "start", "minimum");
        if (total > 0) {
            assertSql(queries.get(1), sql + " LIMIT ?", "start", "minimum", LIMIT_PARAMETER);
            assertThat(queries.get(1).getAdditionalParameter(LIMIT_PARAMETER)).isEqualTo(1L);
        }
        assertThat(page.getTotal()).isEqualTo(total);
    }

    // COUNT 和数据查询都保留带参数的排序，分页参数追加在原有参数之后。
    @ParameterizedTest
    @CsvSource({"1,false", "1,true", "2,false", "2,true"})
    void preservesParameterizedOrderByInPagination(long current, boolean searchCount) throws SQLException {
        String sql = "SELECT id FROM product WHERE id > ? ORDER BY CASE WHEN id = ? THEN 0 ELSE 1 END, id";
        Page<Integer> page = new Page<>(current, 2, searchCount);
        if (searchCount) {
            stubCount(4);
        }

        query("orderBy", sql, Map.of("page", page, "minimum", 1, "priority", 4), "minimum", "priority");

        List<BoundSql> queries = capturedQueries(searchCount ? 2 : 1);
        if (searchCount) {
            assertSql(queries.get(0), "SELECT COUNT(*) FROM (" + sql + ") TOTAL", "minimum", "priority");
        }
        BoundSql dataSql = queries.get(searchCount ? 1 : 0);
        if (current == 1) {
            assertSql(dataSql, sql + " LIMIT ?", "minimum", "priority", LIMIT_PARAMETER);
        } else {
            assertSql(dataSql, sql + " LIMIT ? OFFSET ?", "minimum", "priority", LIMIT_PARAMETER, OFFSET_PARAMETER);
            assertThat(dataSql.getAdditionalParameter(OFFSET_PARAMETER)).isEqualTo(2L);
        }
        assertThat(dataSql.getAdditionalParameter(LIMIT_PARAMETER)).isEqualTo(2L);
        assertThat(page.getTotal()).isEqualTo(searchCount ? 4 : 0);
    }

    // SQL 末尾的锁定子句(FOR UPDATE 等)必须被移到分页子句之后,否则生成 "… FOR UPDATE LIMIT ? OFFSET ?"
    // 这类非法 SQL(MySQL/PostgreSQL 语法要求锁定子句位于 LIMIT 之后),见 issue#4536。
    @ParameterizedTest
    @ValueSource(strings = {"FOR UPDATE", "FOR UPDATE SKIP LOCKED", "FOR SHARE", "LOCK IN SHARE MODE"})
    void reappendsTrailingLockingClauseAfterPagination(String lockingClause) throws SQLException {
        Page<Integer> page = new Page<>(2, 10);

        stubCount(20);
        query("lockingClause", BASE_SQL + " " + lockingClause, page);

        List<BoundSql> queries = capturedQueries(2);
        assertSql(queries.get(1), BASE_SQL + " LIMIT ? OFFSET ? " + lockingClause, LIMIT_PARAMETER, OFFSET_PARAMETER);
    }

    // 锁定子句不在 SQL 末尾(位于子查询内部)时不移动,外层直接追加分页子句即为合法 SQL。
    @Test
    void keepsLockingClauseInsideSubqueryUntouched() throws SQLException {
        String sql = "SELECT id FROM product WHERE id IN (SELECT id FROM stock WHERE n = ? FOR UPDATE) ORDER BY id";
        Page<Integer> page = new Page<>(1, 10);

        stubCount(20);
        query("subqueryLocking", sql, page);

        List<BoundSql> queries = capturedQueries(2);
        assertSql(queries.get(1), sql + " LIMIT ?", LIMIT_PARAMETER);
    }

    private void query(String id, String sql, Object parameter, String... parameterNames) throws SQLException {
        List<ParameterMapping> mappings = Arrays.stream(parameterNames)
                .map(name -> new ParameterMapping.Builder(configuration, name, Integer.class).build()).toList();
        MappedStatement statement = new MappedStatement.Builder(configuration, getClass().getName() + "." + id,
                new StaticSqlSource(configuration, sql, mappings), SqlCommandType.SELECT).build();
        MybatisPlusInterceptor plugin = new MybatisPlusInterceptor();
        plugin.addInnerInterceptor(pagination);
        Executor intercepted = (Executor) plugin.plugin(executor);
        intercepted.query(statement, parameter, RowBounds.DEFAULT, Executor.NO_RESULT_HANDLER);
    }

    private void stubCount(long total) throws SQLException {
        when(
                executor.query(
                        any(), any(), any(), any(), any(), argThat(sql -> sql.getSql().startsWith("SELECT COUNT("))))
                .thenReturn(List.of(total));
    }

    private List<BoundSql> capturedQueries(int count) throws SQLException {
        ArgumentCaptor<BoundSql> sql = ArgumentCaptor.forClass(BoundSql.class);
        verify(executor, times(count)).query(any(), any(), any(), any(), any(), sql.capture());
        return sql.getAllValues();
    }

    private void assertSql(BoundSql boundSql, String sql, String... parameterNames) {
        assertThat(boundSql.getSql()).isEqualTo(sql);
        assertThat(boundSql.getParameterMappings()).extracting(ParameterMapping::getProperty)
                .containsExactly(parameterNames);
    }
}
