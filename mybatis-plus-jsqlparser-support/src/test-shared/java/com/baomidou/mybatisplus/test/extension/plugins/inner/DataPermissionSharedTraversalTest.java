package com.baomidou.mybatisplus.test.extension.plugins.inner;

import com.baomidou.mybatisplus.extension.plugins.handler.MultiDataPermissionHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.DataPermissionInterceptor;
import com.baomidou.mybatisplus.jsqlparser.enums.ExpressionAppendMode;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.schema.Column;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("unit")
class DataPermissionSharedTraversalTest {

    private final DataPermissionInterceptor interceptor = new DataPermissionInterceptor(
            (MultiDataPermissionHandler) (table, where, mappedStatementId) -> {
                if (table.getName().startsWith("cte_")) {
                    return null;
                }
                String qualifier = table.getAlias() == null ? table.getName() : table.getAlias().getName();
                return new EqualsTo(new Column(qualifier + ".allowed"), new LongValue(1));
            });

    @Test
    void injectsIntoEachCteWithoutReorderingParameters() {
        String sql = "WITH cte_a AS (SELECT id FROM account a WHERE a.id = :first), "
                + "cte_b AS (SELECT id FROM orders o WHERE o.id = :second) " + "SELECT * FROM cte_a WHERE id = :third";

        assertThat(interceptor.parserSingle(sql, "test")).isEqualTo(
                "WITH cte_a AS (SELECT id FROM account a WHERE a.id = :first AND a.allowed = 1), "
                        + "cte_b AS (SELECT id FROM orders o WHERE o.id = :second AND o.allowed = 1) "
                        + "SELECT * FROM cte_a WHERE id = :third");
    }

    @Test
    void injectsIntoSelectAndNestedFunctionSubqueriesWithoutReorderingParameters() {
        String sql = "SELECT (SELECT max(a.id) FROM account a WHERE a.id = :first), "
                + "coalesce(abs((SELECT max(o.id) FROM orders o WHERE o.id = :second)), 0) "
                + "FROM customer c WHERE c.id = :third";

        assertThat(interceptor.parserSingle(sql, "test")).isEqualTo(
                "SELECT (SELECT max(a.id) FROM account a WHERE a.id = :first AND a.allowed = 1), "
                        + "coalesce(abs((SELECT max(o.id) FROM orders o WHERE o.id = :second AND o.allowed = 1)), 0) "
                        + "FROM customer c WHERE c.id = :third AND c.allowed = 1");
    }

    static Stream<Arguments> dmlSubqueries() {
        return Stream.of(
                Arguments.of(
                        "UPDATE account a SET balance = (SELECT MAX(o.amount) FROM orders o WHERE o.id = :first) WHERE a.id = :second",
                        "UPDATE account a SET balance = (SELECT MAX(o.amount) FROM orders o WHERE o.id = :first AND o.allowed = 1) WHERE a.id = :second AND a.allowed = 1"),
                Arguments.of(
                        "UPDATE account a SET balance = COALESCE((SELECT MAX(o.amount) FROM orders o), 0), quota = (SELECT MAX(c.quota) FROM customer c) WHERE a.id = :id",
                        "UPDATE account a SET balance = COALESCE((SELECT MAX(o.amount) FROM orders o WHERE o.allowed = 1), 0), quota = (SELECT MAX(c.quota) FROM customer c WHERE c.allowed = 1) WHERE a.id = :id AND a.allowed = 1"),
                Arguments.of(
                        "UPDATE account a SET balance = :amount WHERE a.id IN (SELECT o.account_id FROM orders o WHERE o.id = :id)",
                        "UPDATE account a SET balance = :amount WHERE a.id IN (SELECT o.account_id FROM orders o WHERE o.id = :id AND o.allowed = 1) AND a.allowed = 1"),
                Arguments.of(
                        "DELETE FROM account a WHERE EXISTS (SELECT 1 FROM orders o WHERE o.account_id = a.id)",
                        "DELETE FROM account a WHERE EXISTS (SELECT 1 FROM orders o WHERE o.account_id = a.id AND o.allowed = 1) AND a.allowed = 1"),
                Arguments.of(
                        "DELETE FROM account a WHERE CASE WHEN EXISTS (SELECT 1 FROM orders o WHERE o.account_id = a.id) THEN 1 ELSE 0 END = 1",
                        "DELETE FROM account a WHERE CASE WHEN EXISTS (SELECT 1 FROM orders o WHERE o.account_id = a.id AND o.allowed = 1) THEN 1 ELSE 0 END = 1 AND a.allowed = 1"),
                Arguments.of(
                        "UPDATE cte_account a SET balance = (SELECT MAX(o.amount) FROM orders o) WHERE a.id = :id",
                        "UPDATE cte_account a SET balance = (SELECT MAX(o.amount) FROM orders o WHERE o.allowed = 1) WHERE a.id = :id"),
                Arguments.of(
                        "DELETE FROM account a WHERE a.id IN (SELECT o.account_id FROM cte_orders o)",
                        "DELETE FROM account a WHERE a.id IN (SELECT o.account_id FROM cte_orders o) AND a.allowed = 1"));
    }

    @ParameterizedTest
    @MethodSource("dmlSubqueries")
    void injectsIntoDmlSubqueries(String sql, String expected) {
        assertThat(interceptor.parserMulti(sql, "test")).isEqualTo(expected);
    }

    @Test
    void dmlSubqueriesVisitEachTableOnceWithTheStatementId() {
        List<String> visited = new ArrayList<>();
        DataPermissionInterceptor interceptor = new DataPermissionInterceptor(
                (MultiDataPermissionHandler) (table, where, mappedStatementId) -> {
                    assertThat(mappedStatementId).isEqualTo("mapper.updateBalance");
                    visited.add(table.getName());
                    return new EqualsTo(new Column(table.getAlias().getName() + ".allowed"), new LongValue(1));
                });
        interceptor.setExpressionAppendMode(ExpressionAppendMode.FIRST);
        String sql = "UPDATE account a SET balance = (SELECT MAX(o.amount) FROM orders o WHERE o.id = :first) "
                + "WHERE a.id IN (SELECT c.account_id FROM customer c WHERE c.id = :second)";
        assertThat(interceptor.parserMulti(sql, "mapper.updateBalance")).isEqualTo(
                "UPDATE account a SET balance = (SELECT MAX(o.amount) FROM orders o WHERE o.allowed = 1 AND o.id = :first) "
                        + "WHERE a.allowed = 1 AND a.id IN (SELECT c.account_id FROM customer c WHERE c.allowed = 1 AND c.id = :second)");
        assertThat(visited).containsExactly("orders", "customer", "account");
    }

    @ParameterizedTest
    @ValueSource(strings = {"UPDATE account a SET balance = (SELECT MAX(o.amount) FROM orders o) WHERE a.id = 1",
            "DELETE FROM account a WHERE a.id IN (SELECT o.account_id FROM orders o)"})
    void keepsLegacyAndAbsentHandlersUnchanged(String sql) {
        List<String> visited = new ArrayList<>();
        DataPermissionInterceptor legacy = new DataPermissionInterceptor((where, mappedStatementId) -> {
            assertThat(mappedStatementId).isEqualTo("mapper.legacy");
            visited.add(where.toString());
            return where;
        });
        assertThat(legacy.parserMulti(sql, "mapper.legacy")).isEqualTo(sql);
        assertThat(visited).hasSize(1);
        assertThat(new DataPermissionInterceptor().parserMulti(sql, "test")).isEqualTo(sql);
    }

}
