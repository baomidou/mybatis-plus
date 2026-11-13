package com.baomidou.mybatisplus.test.extension.plugins.inner;

import com.baomidou.mybatisplus.extension.plugins.handler.DataPermissionHandler;
import com.baomidou.mybatisplus.extension.plugins.handler.MultiDataPermissionHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.DataPermissionInterceptor;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.PlainSelect;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("unit")
class DataPermissionParenthesizedSelectTest {

    private static final String STATEMENT_ID = "mapper.query";
    private static final Map<String, String> RESULT_MAP = new LinkedHashMap<>();
    private static final DataPermissionHandler HANDLER = (where, id) -> {
        assertThat(id).isEqualTo(STATEMENT_ID);
        try {
            Expression permission = CCJSqlParserUtil.parseCondExpression("r.companyId IN (1, 2, 3)");
            return where == null ? permission : new AndExpression(where, permission);
        } catch (JSQLParserException e) {
            throw new IllegalStateException(e);
        }
    };

    static {
        RESULT_MAP.put("SELECT r.id FROM sys_role r", "SELECT r.id FROM sys_role r WHERE r.companyId IN (1, 2, 3)");
        RESULT_MAP.put("(SELECT r.id FROM sys_role r)", "(SELECT r.id FROM sys_role r WHERE r.companyId IN (1, 2, 3))");
        RESULT_MAP.put(
                "(((SELECT r.id FROM sys_role r)))",
                "(((SELECT r.id FROM sys_role r WHERE r.companyId IN (1, 2, 3))))");
        RESULT_MAP.put(
                "(SELECT r.id FROM sys_role r) UNION ALL (SELECT r.id FROM sys_role r)",
                "(SELECT r.id FROM sys_role r WHERE r.companyId IN (1, 2, 3)) UNION ALL (SELECT r.id FROM sys_role r WHERE r.companyId IN (1, 2, 3))");
        RESULT_MAP.put(
                "((SELECT r.id FROM sys_role r) UNION (SELECT r.id FROM sys_role r))",
                "((SELECT r.id FROM sys_role r WHERE r.companyId IN (1, 2, 3)) UNION (SELECT r.id FROM sys_role r WHERE r.companyId IN (1, 2, 3)))");
        RESULT_MAP.put(
                "(SELECT r.id FROM sys_role r) UNION ALL ((SELECT r.id FROM sys_role r) UNION ALL (SELECT r.id FROM sys_role r))",
                "(SELECT r.id FROM sys_role r WHERE r.companyId IN (1, 2, 3)) UNION ALL ((SELECT r.id FROM sys_role r WHERE r.companyId IN (1, 2, 3)) UNION ALL (SELECT r.id FROM sys_role r WHERE r.companyId IN (1, 2, 3)))");
        RESULT_MAP.put(
                "(SELECT r.id FROM sys_role r WHERE r.id = 2)",
                "(SELECT r.id FROM sys_role r WHERE r.id = 2 AND r.companyId IN (1, 2, 3))");
        RESULT_MAP.put(
                "((SELECT r.id FROM sys_role r WHERE r.id = 1) UNION ALL (SELECT r.id FROM sys_role r WHERE r.id = 2)) ORDER BY 1",
                "((SELECT r.id FROM sys_role r WHERE r.id = 1 AND r.companyId IN (1, 2, 3)) UNION ALL (SELECT r.id FROM sys_role r WHERE r.id = 2 AND r.companyId IN (1, 2, 3))) ORDER BY 1");
    }

    @Test
    void parenthesizedSelectAndUnion() {
        DataPermissionInterceptor interceptor = new DataPermissionInterceptor(HANDLER);
        RESULT_MAP.forEach(
                (sql, targetSql) -> assertThat(interceptor.parserSingle(sql, STATEMENT_ID)).isEqualTo(targetSql));
    }

    @Test
    void parenthesizedSelectWithoutPermission() {
        String sql = "((SELECT r.id FROM sys_role r WHERE r.id = 2))";
        assertThat(new DataPermissionInterceptor().parserSingle(sql, STATEMENT_ID)).isEqualTo(sql);
        DataPermissionInterceptor interceptor = new DataPermissionInterceptor((where, id) -> null);
        assertThat(interceptor.parserSingle(sql, STATEMENT_ID)).isEqualTo(sql);
    }

    @Test
    void parenthesizedSelectKeepsSetWhereHookAndStatementId() {
        List<String> ids = new ArrayList<>();
        DataPermissionInterceptor interceptor = new DataPermissionInterceptor(HANDLER) {

            @Override
            protected void setWhere(PlainSelect select, String id) {
                ids.add(id);
                super.setWhere(select, id);
            }
        };
        assertThat(
                interceptor.parserSingle(
                        "(SELECT r.id FROM sys_role r) UNION ALL (SELECT r.id FROM sys_role r)", STATEMENT_ID))
                .isEqualTo(
                        "(SELECT r.id FROM sys_role r WHERE r.companyId IN (1, 2, 3)) UNION ALL (SELECT r.id FROM sys_role r WHERE r.companyId IN (1, 2, 3))");
        assertThat(ids).containsExactly(STATEMENT_ID, STATEMENT_ID);
        ids.clear();
        assertThat(
                interceptor
                        .parserSingle("(SELECT r.id FROM (SELECT s.id, s.companyId FROM sys_role s) r)", STATEMENT_ID))
                .isEqualTo(
                        "(SELECT r.id FROM (SELECT s.id, s.companyId FROM sys_role s) r WHERE r.companyId IN (1, 2, 3))");
        assertThat(ids).containsExactly(STATEMENT_ID);
    }

    @Test
    void multiHandlerParenthesizedUnion() {
        DataPermissionInterceptor interceptor = new DataPermissionInterceptor((MultiDataPermissionHandler) (table,
                where, id) -> new EqualsTo(new Column(table.getAlias().getName() + ".companyId"), new LongValue(1)));
        assertThat(
                interceptor.parserSingle(
                        "(SELECT r.id FROM sys_role r) UNION ALL (SELECT s.id FROM sys_role s)", STATEMENT_ID))
                .isEqualTo(
                        "(SELECT r.id FROM sys_role r WHERE r.companyId = 1) UNION ALL (SELECT s.id FROM sys_role s WHERE s.companyId = 1)");
    }
}
