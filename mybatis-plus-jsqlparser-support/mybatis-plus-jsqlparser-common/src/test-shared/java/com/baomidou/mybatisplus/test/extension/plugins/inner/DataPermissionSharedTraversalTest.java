package com.baomidou.mybatisplus.test.extension.plugins.inner;

import com.baomidou.mybatisplus.extension.plugins.handler.MultiDataPermissionHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.DataPermissionInterceptor;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.schema.Column;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

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
}
