package com.baomidou.mybatisplus.test.extension.plugins.inner;

import com.baomidou.mybatisplus.extension.plugins.handler.MultiDataPermissionHandler;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.DataPermissionInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.baomidou.mybatisplus.jsqlparser.enums.ExpressionAppendMode;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 租户与数据权限条件追加到 xor 条件时的括号回归测试
 * <p>
 * {@code xor} 的优先级低于 {@code and},原条件不加括号会被数据库解析成 {@code WHERE 1 XOR (0 AND tenant_id = 7)},租户条件失效.
 *
 * @author hubin
 * @since 3.5.18
 */
class MultiTableXorExpressionTest {

    private final TenantLineInnerInterceptor tenantInterceptor = new TenantLineInnerInterceptor(
            new TenantLineHandler() {

                @Override
                public Expression getTenantId() {
                    return new LongValue(7);
                }
            });

    private final DataPermissionInterceptor permissionInterceptor = new DataPermissionInterceptor(
            new MultiDataPermissionHandler() {

                @Override
                public Expression getSqlSegment(Table table, Expression where, String mappedStatementId) {
                    return new EqualsTo(new Column("owner_id"), new LongValue(7));
                }
            });

    @ParameterizedTest
    @EnumSource(ExpressionAppendMode.class)
    void select(ExpressionAppendMode appendMode) {
        tenantInterceptor.setExpressionAppendMode(appendMode);
        if (ExpressionAppendMode.LAST == appendMode) {
            assertSql(
                    "SELECT id FROM records WHERE 1 XOR 0", "SELECT id FROM records WHERE (1 XOR 0) AND tenant_id = 7");
            assertSql(
                    "SELECT id FROM records WHERE status = 1 XOR archived = 1",
                    "SELECT id FROM records WHERE (status = 1 XOR archived = 1) AND tenant_id = 7");
        } else {
            assertSql(
                    "SELECT id FROM records WHERE 1 XOR 0", "SELECT id FROM records WHERE tenant_id = 7 AND (1 XOR 0)");
            assertSql(
                    "SELECT id FROM records WHERE status = 1 XOR archived = 1",
                    "SELECT id FROM records WHERE tenant_id = 7 AND (status = 1 XOR archived = 1)");
        }
    }

    @ParameterizedTest
    @EnumSource(ExpressionAppendMode.class)
    void updateAndDelete(ExpressionAppendMode appendMode) {
        tenantInterceptor.setExpressionAppendMode(appendMode);
        if (ExpressionAppendMode.LAST == appendMode) {
            assertSql(
                    "UPDATE records SET status = 2 WHERE 1 XOR 0",
                    "UPDATE records SET status = 2 WHERE (1 XOR 0) AND tenant_id = 7");
            assertSql("DELETE FROM records WHERE 1 XOR 0", "DELETE FROM records WHERE (1 XOR 0) AND tenant_id = 7");
        } else {
            assertSql(
                    "UPDATE records SET status = 2 WHERE 1 XOR 0",
                    "UPDATE records SET status = 2 WHERE tenant_id = 7 AND (1 XOR 0)");
            assertSql("DELETE FROM records WHERE 1 XOR 0", "DELETE FROM records WHERE tenant_id = 7 AND (1 XOR 0)");
        }
    }

    @ParameterizedTest
    @EnumSource(ExpressionAppendMode.class)
    void parenthesizedXor(ExpressionAppendMode appendMode) {
        tenantInterceptor.setExpressionAppendMode(appendMode);
        // 已经显式括号的条件不受影响,保持原有的安全括号结构
        assertSql(
                "SELECT id FROM records WHERE (1 XOR 0)",
                ExpressionAppendMode.LAST == appendMode
                        ? "SELECT id FROM records WHERE (1 XOR 0) AND tenant_id = 7"
                        : "SELECT id FROM records WHERE tenant_id = 7 AND (1 XOR 0)");
    }

    @ParameterizedTest
    @EnumSource(ExpressionAppendMode.class)
    void orNotAffected(ExpressionAppendMode appendMode) {
        tenantInterceptor.setExpressionAppendMode(appendMode);
        assertSql(
                "SELECT id FROM records WHERE id = 1 OR name = 'a'",
                ExpressionAppendMode.LAST == appendMode
                        ? "SELECT id FROM records WHERE (id = 1 OR name = 'a') AND tenant_id = 7"
                        : "SELECT id FROM records WHERE tenant_id = 7 AND (id = 1 OR name = 'a')");
    }

    @ParameterizedTest
    @EnumSource(ExpressionAppendMode.class)
    void dataPermission(ExpressionAppendMode appendMode) {
        permissionInterceptor.setExpressionAppendMode(appendMode);
        assertSql(
                null, "SELECT id FROM records WHERE 1 XOR 0",
                ExpressionAppendMode.LAST == appendMode
                        ? "SELECT id FROM records WHERE (1 XOR 0) AND owner_id = 7"
                        : "SELECT id FROM records WHERE owner_id = 7 AND (1 XOR 0)");
    }

    void assertSql(String sql, String expect) {
        assertThat(tenantInterceptor.parserSingle(sql, null)).as(sql).isEqualTo(expect);
    }

    void assertSql(String mappedStatementId, String sql, String expect) {
        assertThat(
                mappedStatementId == null
                        ? permissionInterceptor.parserSingle(sql, null)
                        : permissionInterceptor.parserSingle(sql, mappedStatementId))
                .as(sql).isEqualTo(expect);
    }
}
