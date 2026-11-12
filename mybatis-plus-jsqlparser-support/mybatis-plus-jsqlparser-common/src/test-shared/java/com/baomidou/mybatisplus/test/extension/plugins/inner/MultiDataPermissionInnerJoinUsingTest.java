package com.baomidou.mybatisplus.test.extension.plugins.inner;

import com.baomidou.mybatisplus.extension.plugins.handler.MultiDataPermissionHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.DataPermissionInterceptor;
import com.baomidou.mybatisplus.jsqlparser.enums.ExpressionAppendMode;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.schema.Column;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("unit")
class MultiDataPermissionInnerJoinUsingTest {

    private static final Map<String, String> LAST_RESULT_TMAP = new LinkedHashMap<>();

    static {
        LAST_RESULT_TMAP.put(
                "SELECT b.amount FROM accounts a INNER JOIN details b USING (link_id) WHERE a.id = 1",
                "SELECT b.amount FROM accounts a INNER JOIN details b USING (link_id) WHERE a.id = 1 AND a.tenant_id = 1 AND b.tenant_id = 1");
        LAST_RESULT_TMAP.put(
                "SELECT b.amount FROM accounts a INNER JOIN details b USING (link_id, tenant_id) WHERE a.id = 1",
                "SELECT b.amount FROM accounts a INNER JOIN details b USING (link_id, tenant_id) WHERE a.id = 1 AND a.tenant_id = 1 AND b.tenant_id = 1");
        LAST_RESULT_TMAP.put(
                "SELECT b.amount FROM accounts a JOIN details b USING (link_id) WHERE a.id = 1",
                "SELECT b.amount FROM accounts a JOIN details b USING (link_id) WHERE a.id = 1 AND a.tenant_id = 1 AND b.tenant_id = 1");
        LAST_RESULT_TMAP.put(
                "SELECT b.amount FROM accounts a JOIN details b USING (link_id, tenant_id) WHERE a.id = 1",
                "SELECT b.amount FROM accounts a JOIN details b USING (link_id, tenant_id) WHERE a.id = 1 AND a.tenant_id = 1 AND b.tenant_id = 1");
        LAST_RESULT_TMAP.put(
                "SELECT c.amount FROM accounts a INNER JOIN details b USING (link_id) INNER JOIN secrets c USING (link_id) WHERE a.id = 1",
                "SELECT c.amount FROM accounts a INNER JOIN details b USING (link_id) INNER JOIN secrets c USING (link_id) WHERE a.id = 1 AND a.tenant_id = 1 AND b.tenant_id = 1 AND c.tenant_id = 1");
        LAST_RESULT_TMAP.put(
                "SELECT c.amount FROM accounts a INNER JOIN details b USING (link_id) INNER JOIN secrets c ON b.link_id = c.link_id WHERE a.id = 1",
                "SELECT c.amount FROM accounts a INNER JOIN details b USING (link_id) INNER JOIN secrets c ON b.link_id = c.link_id AND a.tenant_id = 1 AND b.tenant_id = 1 AND c.tenant_id = 1 WHERE a.id = 1");
        LAST_RESULT_TMAP.put(
                "SELECT c.amount FROM accounts a INNER JOIN details b USING (link_id) LEFT JOIN secrets c ON b.link_id = c.link_id AND c.id = 23 WHERE a.id = 1",
                "SELECT c.amount FROM accounts a INNER JOIN details b USING (link_id) LEFT JOIN secrets c ON b.link_id = c.link_id AND c.id = 23 AND c.tenant_id = 1 WHERE a.id = 1 AND a.tenant_id = 1 AND b.tenant_id = 1");
        LAST_RESULT_TMAP.put(
                "SELECT b.amount FROM accounts a INNER JOIN details b USING (link_id) RIGHT JOIN secrets c ON b.link_id = c.link_id ORDER BY c.id",
                "SELECT b.amount FROM accounts a INNER JOIN details b USING (link_id) RIGHT JOIN secrets c ON b.link_id = c.link_id AND a.tenant_id = 1 AND b.tenant_id = 1 WHERE c.tenant_id = 1 ORDER BY c.id");
        LAST_RESULT_TMAP.put(
                "SELECT c.amount FROM (accounts a INNER JOIN details b USING (link_id)) INNER JOIN secrets c ON b.link_id = c.link_id WHERE a.id = 1",
                "SELECT c.amount FROM (accounts a INNER JOIN details b USING (link_id)) INNER JOIN secrets c ON b.link_id = c.link_id AND a.tenant_id = 1 AND b.tenant_id = 1 AND c.tenant_id = 1 WHERE a.id = 1");
        LAST_RESULT_TMAP.put(
                "SELECT c.amount FROM accounts a LEFT JOIN (details b INNER JOIN secrets c USING (link_id)) ON a.link_id = b.link_id WHERE a.id = 1",
                "SELECT c.amount FROM accounts a LEFT JOIN (details b INNER JOIN secrets c USING (link_id)) ON a.link_id = b.link_id AND b.tenant_id = 1 AND c.tenant_id = 1 WHERE a.id = 1 AND a.tenant_id = 1");
        LAST_RESULT_TMAP.put(
                "SELECT c.amount FROM accounts a RIGHT JOIN (details b INNER JOIN secrets c USING (link_id)) ON a.link_id = b.link_id ORDER BY c.id",
                "SELECT c.amount FROM accounts a RIGHT JOIN (details b INNER JOIN secrets c USING (link_id)) ON a.link_id = b.link_id AND a.tenant_id = 1 WHERE b.tenant_id = 1 AND c.tenant_id = 1 ORDER BY c.id");
        LAST_RESULT_TMAP.put(
                "SELECT b.amount FROM accounts a INNER JOIN details b ON a.link_id = b.link_id WHERE a.id = 1",
                "SELECT b.amount FROM accounts a INNER JOIN details b ON a.link_id = b.link_id AND a.tenant_id = 1 AND b.tenant_id = 1 WHERE a.id = 1");
        LAST_RESULT_TMAP.put(
                "SELECT * FROM accounts a INNER JOIN details b USING (link_id) WHERE a.id = 1",
                "SELECT * FROM accounts a INNER JOIN details b USING (link_id) WHERE a.id = 1 AND a.tenant_id = 1 AND b.tenant_id = 1");
        LAST_RESULT_TMAP.put(
                "SELECT link_id FROM accounts a INNER JOIN details b USING (link_id) WHERE a.id = 1",
                "SELECT link_id FROM accounts a INNER JOIN details b USING (link_id) WHERE a.id = 1 AND a.tenant_id = 1 AND b.tenant_id = 1");
        LAST_RESULT_TMAP.put(
                "SELECT b.amount FROM with_as_accounts a INNER JOIN details b USING (link_id) WHERE a.id = 3",
                "SELECT b.amount FROM with_as_accounts a INNER JOIN details b USING (link_id) WHERE a.id = 3 AND b.tenant_id = 1");
    }

    @Test
    void innerJoinUsing() {
        List<String> tables = new ArrayList<>();
        DataPermissionInterceptor interceptor = new DataPermissionInterceptor(
                (MultiDataPermissionHandler) (table, where, id) -> {
                    assertThat(id).isEqualTo("mapper.query");
                    tables.add(table.getName());
                    if (table.getName().startsWith("with_as")) {
                        return null;
                    }
                    return new EqualsTo(new Column(table.getAlias().getName() + ".tenant_id"), new LongValue(1));
                });
        interceptor.setExpressionAppendMode(ExpressionAppendMode.LAST);
        LAST_RESULT_TMAP.forEach((sql, targetSql) -> {
            tables.clear();
            assertThat(interceptor.parserSingle(sql, "mapper.query")).isEqualTo(targetSql);
            assertThat(tables).doesNotHaveDuplicates();
        });
    }
}
