package com.baomidou.mybatisplus.test.extension.plugins.inner;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 验证分页拦截器生成的 COUNT SQL 和排序 SQL。
 *
 * @author miemie
 * @since 2020-06-28
 */
@Tag("unit")
class PaginationInnerInterceptorSqlTest {

    private final PaginationInnerInterceptor interceptor = new PaginationInnerInterceptor();

    // COUNT 可以移除未被 WHERE 引用且 ON 不含绑定参数的 LEFT JOIN。
    @Test
    void removesUnusedLeftJoinsFromCount() {
        assertCountSql(
                "select * from user u LEFT JOIN role r ON r.id = u.role_id", "SELECT COUNT(*) AS total FROM user u");

        assertCountSql(
                "select * from user u LEFT JOIN role r ON r.id = u.role_id WHERE u.xx = ?",
                "SELECT COUNT(*) AS total FROM user u WHERE u.xx = ?");

        assertCountSql("""
                SELECT * FROM user u
                LEFT JOIN role r ON r.id = u.role_id
                LEFT JOIN permission p ON p.id = u.per_id
                """, "SELECT COUNT(*) AS total FROM user u");

        assertCountSql("""
                SELECT * FROM user u
                LEFT JOIN role r ON r.id = u.role_id
                LEFT JOIN permission p ON p.id = u.per_id
                WHERE u.xx = ?
                """, "SELECT COUNT(*) AS total FROM user u WHERE u.xx = ?");
    }

    // ON 带参数或 WHERE 引用了右表时保留 JOIN，匹配表别名时忽略大小写。
    @Test
    void retainsLeftJoinsReferencedByWhereOrContainingParameters() {
        assertCountSql(
                "select * from user u LEFT JOIN role r ON r.id = u.role_id AND r.name = ? where u.xx = ?",
                "SELECT COUNT(*) AS total FROM user u LEFT JOIN role r ON r.id = u.role_id AND r.name = ? WHERE u.xx = ?");

        assertCountSql(
                "select * from user u LEFT JOIN role r ON r.id = u.role_id where R.NAME = ?",
                "SELECT COUNT(*) AS total FROM user u LEFT JOIN role r ON r.id = u.role_id WHERE R.NAME = ?");

        assertCountSql("""
                SELECT * FROM user u
                LEFT JOIN role r ON r.id = u.role_id
                WHERE u.xax = ? AND r.cc = ? AND r.qq = ?
                """, """
                SELECT COUNT(*) AS total FROM user u \
                LEFT JOIN role r ON r.id = u.role_id \
                WHERE u.xax = ? AND r.cc = ? AND r.qq = ?""");
    }

    // 在这些未被 WHERE 引用的 JOIN 中，保留带绑定参数的子查询。
    @Test
    void removesLeftJoinSubqueryOnlyWhenItHasNoParameters() {
        assertCountSql("""
                SELECT r.id, r.name, r.phone, rlr.total_top_up
                FROM reseller r
                LEFT JOIN (
                    SELECT ral.reseller_id, SUM(ral.top_up_money) AS total_top_up,
                           SUM(ral.acquire_money) AS total_acquire
                    FROM reseller_acquire_log ral
                    GROUP BY ral.reseller_id
                ) rlr ON r.id = rlr.reseller_id
                ORDER BY r.created_at DESC
                """, "SELECT COUNT(*) AS total FROM reseller r");

        assertCountSql(
                "SELECT f.ca, f.cb FROM table_a f LEFT JOIN (SELECT ca FROM table_b WHERE cc = ?) rf on rf.ca = f.ca",
                "SELECT COUNT(*) AS total FROM table_a f LEFT JOIN (SELECT ca FROM table_b WHERE cc = ?) rf ON rf.ca = f.ca");

        assertCountSql("""
                SELECT * FROM order_info
                LEFT JOIN (
                    SELECT count(1) FROM order_info WHERE create_time BETWEEN ? AND ?
                ) tt ON 1 = 1
                WHERE equipment_id = ?
                """, """
                SELECT COUNT(*) AS total FROM order_info \
                LEFT JOIN (SELECT count(1) FROM order_info WHERE create_time BETWEEN ? AND ?) tt ON 1 = 1 \
                WHERE equipment_id = ?""");
    }

    // 移除普通排序后包裹 DISTINCT 查询，按去重后的结果计数。
    @Test
    void wrapsDistinctQueryForCount() {
        assertCountSql(
                "select distinct id from table order by id",
                "SELECT COUNT(*) FROM (SELECT DISTINCT id FROM table) TOTAL");

        assertCountSql("select distinct id from table", "SELECT COUNT(*) FROM (SELECT DISTINCT id FROM table) TOTAL");
    }

    // 包裹 GROUP BY 查询，按分组结果计数。
    @Test
    void wrapsGroupByQueryForCount() {
        assertCountSql(
                "SELECT * FROM record_1 WHERE id = ? GROUP BY date(date_time)",
                "SELECT COUNT(*) FROM (SELECT * FROM record_1 WHERE id = ? GROUP BY date(date_time)) TOTAL");
    }

    // 无 GROUP BY 的 HAVING 也需要包裹计数，并保留 HAVING 依赖的别名和 JOIN。
    @Test
    void retainsSelectAliasesAndJoinsNeededByHaving() {
        assertCountSql(
                "SELECT SUM(amount) AS amount_total FROM payment HAVING amount_total > ? ORDER BY amount_total",
                "SELECT COUNT(*) FROM (SELECT SUM(amount) AS amount_total FROM payment HAVING amount_total > ?) TOTAL");
        assertCountSql("""
                SELECT SUM(p.amount) FROM account a
                LEFT JOIN payment p ON a.id = p.account_id
                HAVING SUM(p.amount) > ?
                """, """
                SELECT COUNT(*) FROM (SELECT SUM(p.amount) FROM account a \
                LEFT JOIN payment p ON a.id = p.account_id \
                HAVING SUM(p.amount) > ?) TOTAL""");
    }

    @Test
    void preservesWindowAliasAndJoinedTableUsedByQualify() {
        String ranked = "SELECT a.id, ROW_NUMBER() OVER (ORDER BY p.amount DESC) AS rn FROM account a "
                + "LEFT JOIN payment p ON a.id = p.account_id WHERE a.active = ? QUALIFY rn <= ?";
        assertCountSql(ranked + " ORDER BY a.id", "SELECT COUNT(*) FROM (" + ranked + ") TOTAL");
    }

    // 普通列排序不影响计数，生成 COUNT SQL 时可以移除。
    @Test
    void removesUnparameterizedOrderByFromCount() {
        assertCountSql("SELECT * FROM comment ORDER BY name", "SELECT COUNT(*) AS total FROM comment");
    }

    @Test
    void retainsDialectSpecificRowLimitsInCount() {
        for (String sql : Arrays.asList(
                "SELECT FIRST 2 id FROM product ORDER BY id", "SELECT SKIP 1 id FROM product ORDER BY id",
                "SELECT id FROM product ORDER BY id LIMIT 1 BY category")) {
            assertCountSql(sql, "SELECT COUNT(*) FROM (" + sql + ") TOTAL");
        }
    }

    // 带绑定参数的排序表达式留在 COUNT 子查询中，保留原有占位符。
    @Test
    void retainsParameterizedOrderByInCount() {
        assertCountSql(
                "SELECT * FROM comment ORDER BY (CASE WHEN creator = ? THEN 0 ELSE 1 END)",
                "SELECT COUNT(*) FROM (SELECT * FROM comment ORDER BY (CASE WHEN creator = ? THEN 0 ELSE 1 END)) TOTAL");
    }

    // 保留带参数排序引用的右表和 SELECT 别名。
    @Test
    void retainsColumnsAndJoinsNeededByParameterizedOrderBy() {
        assertCountSql("""
                SELECT u.id, r.name AS role_name FROM app_user u
                LEFT JOIN app_role r ON r.id = u.role_id
                WHERE u.active = ?
                ORDER BY CASE WHEN r.name = ? THEN 0 ELSE 1 END, role_name
                """, """
                SELECT COUNT(*) FROM (SELECT u.id, r.name AS role_name FROM app_user u \
                LEFT JOIN app_role r ON r.id = u.role_id \
                WHERE u.active = ? \
                ORDER BY CASE WHEN r.name = ? THEN 0 ELSE 1 END, role_name) TOTAL""");
    }

    // 生成 COUNT SQL 时保留 WITH 定义，只改写外层 SELECT。
    @Test
    void preservesCteWhenGeneratingCount() {
        assertCountSql(
                "with A as (select * from class) select * from A",
                "WITH A AS (SELECT * FROM class) SELECT COUNT(*) AS total FROM A");
    }

    @Test
    void keepsCtesOutsideBoundedCountForSqlServer() {
        String ctes = "WITH a AS (SELECT id FROM product WHERE id > ?), b AS (SELECT id FROM a) ";
        String bounded = "SELECT TOP 2 id FROM b WHERE id < ? ORDER BY id";
        assertCountSql(ctes + bounded, ctes + "SELECT COUNT(*) FROM (" + bounded + ") TOTAL");
    }

    // 分页排序追加到 CTE 外层查询。
    @Test
    void preservesCteWhenAppendingOrderBy() {
        assertOrderBySql(
                "with A as (select * from class) select * from A",
                "WITH A AS (SELECT * FROM class) SELECT * FROM A ORDER BY column ASC", OrderItem.asc("column"));
    }

    // 生成 COUNT 和追加分页排序时都保留表别名后的 ClickHouse FINAL（#5831）。
    @Test
    void preservesClickHouseFinalAfterTableAlias() {
        assertOrderBySql(
                "SELECT e.id FROM events AS e FINAL WHERE e.status = ?",
                "SELECT e.id FROM events AS e FINAL WHERE e.status = ? ORDER BY e.id DESC", OrderItem.desc("e.id"));
        assertCountSql(
                "SELECT e.id FROM events AS e FINAL WHERE e.status = ?",
                "SELECT COUNT(*) AS total FROM events AS e FINAL WHERE e.status = ?");
    }

    private void assertCountSql(String sql, String targetSql) {
        assertThat(interceptor.autoCountSql(new Page<>(), sql)).isEqualTo(targetSql);
    }

    private void assertOrderBySql(String sql, String targetSql, OrderItem... orderItems) {
        assertThat(interceptor.concatOrderBy(sql, Arrays.asList(orderItems))).isEqualTo(targetSql);
    }
}
