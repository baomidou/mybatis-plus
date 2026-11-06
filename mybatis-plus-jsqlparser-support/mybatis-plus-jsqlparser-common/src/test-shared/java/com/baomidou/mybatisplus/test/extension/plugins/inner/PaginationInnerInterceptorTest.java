package com.baomidou.mybatisplus.test.extension.plugins.inner;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.builder.StaticSqlSource;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.RowBounds;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author miemie
 * @since 2020-06-28
 */
@Tag("unit")
class PaginationInnerInterceptorTest {

    private final PaginationInnerInterceptor interceptor = new PaginationInnerInterceptor();

    @Test
    void optimizeCount() {
        /* 能进行优化的 SQL */
        assertsCountSql(
                "select * from user u LEFT JOIN role r ON r.id = u.role_id", "SELECT COUNT(*) AS total FROM user u");

        assertsCountSql(
                "select * from user u LEFT JOIN role r ON r.id = u.role_id WHERE u.xx = ?",
                "SELECT COUNT(*) AS total FROM user u WHERE u.xx = ?");

        assertsCountSql(
                "select * from user u LEFT JOIN role r ON r.id = u.role_id LEFT JOIN permission p on p.id = u.per_id",
                "SELECT COUNT(*) AS total FROM user u");

        assertsCountSql(
                "select * from user u LEFT JOIN role r ON r.id = u.role_id LEFT JOIN permission p on p.id = u.per_id WHERE u.xx = ?",
                "SELECT COUNT(*) AS total FROM user u WHERE u.xx = ?");

        assertsCountSql(
                "select distinct id from table order by id",
                "SELECT COUNT(*) FROM (SELECT DISTINCT id FROM table) TOTAL");

        assertsCountSql("select distinct id from table", "SELECT COUNT(*) FROM (SELECT DISTINCT id FROM table) TOTAL");
    }

    @Test
    void notOptimizeCount() {
        /* 不能进行优化的 SQL */
        assertsCountSql(
                "select * from user u LEFT JOIN role r ON r.id = u.role_id AND r.name = ? where u.xx = ?",
                "SELECT COUNT(*) AS total FROM user u LEFT JOIN role r ON r.id = u.role_id AND r.name = ? WHERE u.xx = ?");

        /* join 表与 where 条件大小写不同的情况 */
        assertsCountSql(
                "select * from user u LEFT JOIN role r ON r.id = u.role_id where R.NAME = ?",
                "SELECT COUNT(*) AS total FROM user u LEFT JOIN role r ON r.id = u.role_id WHERE R.NAME = ?");

        assertsCountSql(
                "select * from user u LEFT JOIN role r ON r.id = u.role_id WHERE u.xax = ? AND r.cc = ? AND r.qq = ?",
                "SELECT COUNT(*) AS total FROM user u LEFT JOIN role r ON r.id = u.role_id WHERE u.xax = ? AND r.cc = ? AND r.qq = ?");
    }

    @Test
    void optimizeCountOrderBy() {
        /* order by 里不带参数,去除order by */
        assertsCountSql("SELECT * FROM comment ORDER BY name", "SELECT COUNT(*) AS total FROM comment");

        /* order by 里带参数,不去除order by */
        assertsCountSql(
                "SELECT * FROM comment ORDER BY (CASE WHEN creator = ? THEN 0 ELSE 1 END)",
                "SELECT COUNT(*) AS total FROM comment ORDER BY (CASE WHEN creator = ? THEN 0 ELSE 1 END)");
    }

    @Test
    void withAsCount() {
        assertsCountSql(
                "with A as (select * from class) select * from A",
                "WITH A AS (SELECT * FROM class) SELECT COUNT(*) AS total FROM A");
    }

    @Test
    void withAsOrderBy() {
        assertsConcatOrderBy(
                "with A as (select * from class) select * from A",
                "WITH A AS (SELECT * FROM class) SELECT * FROM A ORDER BY column ASC", OrderItem.asc("column"));
    }

    /**
     * ClickHouse 表别名后的 FINAL 修饰符（#5831）
     */
    @Test
    void clickHouseFinalWithAlias() {
        assertsConcatOrderBy(
                "SELECT e.id FROM events AS e FINAL WHERE e.status = ?",
                "SELECT e.id FROM events AS e FINAL WHERE e.status = ? ORDER BY e.id DESC", OrderItem.desc("e.id"));
        assertsCountSql(
                "SELECT e.id FROM events AS e FINAL WHERE e.status = ?",
                "SELECT COUNT(*) AS total FROM events AS e FINAL WHERE e.status = ?");
    }

    @Test
    void groupByCount() {
        assertsCountSql(
                "SELECT * FROM record_1 WHERE id = ? GROUP BY date(date_time)",
                "SELECT COUNT(*) FROM (SELECT * FROM record_1 WHERE id = ? GROUP BY date(date_time)) TOTAL");
    }

    @Test
    void leftJoinSelectCount() {
        assertsCountSql(
                "select r.id, r.name, r.phone,rlr.total_top_up from reseller r "
                        + "left join (select ral.reseller_id, sum(ral.top_up_money) as total_top_up, sum(ral.acquire_money) as total_acquire "
                        + "from reseller_acquire_log ral " + "group by ral.reseller_id) rlr on r.id = rlr.reseller_id "
                        + "order by r.created_at desc",
                "SELECT COUNT(*) AS total FROM reseller r");

        // 不优化
        assertsCountSql(
                "SELECT f.ca, f.cb FROM table_a f LEFT JOIN "
                        + "(SELECT ca FROM table_b WHERE cc = ?) rf on rf.ca = f.ca",
                "SELECT COUNT(*) AS total FROM table_a f LEFT JOIN (SELECT ca FROM table_b WHERE cc = ?) rf ON rf.ca = f.ca");

        assertsCountSql(
                "select * from order_info left join (select count(1) from order_info where create_time between ? and ?) tt on 1=1 WHERE equipment_id=?",
                "SELECT COUNT(*) AS total FROM order_info LEFT JOIN (SELECT count(1) FROM order_info WHERE create_time BETWEEN ? AND ?) tt ON 1 = 1 WHERE equipment_id = ?");
    }

    void assertsCountSql(String sql, String targetSql) {
        assertThat(interceptor.autoCountSql(new Page<>(), sql)).isEqualTo(targetSql);
    }

    void assertsConcatOrderBy(String sql, String targetSql, OrderItem... orderItems) {
        assertThat(interceptor.concatOrderBy(sql, Arrays.asList(orderItems))).isEqualTo(targetSql);
    }

    /**
     * SQL 末尾锁定子句(FOR UPDATE 等)与分页改写的组合行为,见 issue#4536。
     * <p>
     * MySQL/PostgreSQL 方言把分页子句追加到原 SQL 末尾,而标准语法要求锁定子句位于 LIMIT 之后, 分页改写必须先剥离再重新追加,否则生成 {@code ... FOR UPDATE LIMIT ?,?}
     * 这类非法 SQL
     */
    @Test
    void paginationSqlWithoutLockingClauseStaysUnchanged() {
        assertThat(paginated(DbType.MYSQL, "SELECT * FROM t WHERE c = ?", 1, 10))
                .isEqualTo("SELECT * FROM t WHERE c = ? LIMIT ?");
        assertThat(paginated(DbType.MYSQL, "SELECT * FROM t WHERE c = ?", 2, 10))
                .isEqualTo("SELECT * FROM t WHERE c = ? LIMIT ?,?");
    }

    @Test
    void paginationSqlKeepsTrailingForUpdateAfterLimit() {
        assertThat(paginated(DbType.MYSQL, "SELECT * FROM t WHERE c = ? FOR UPDATE", 1, 10))
                .isEqualTo("SELECT * FROM t WHERE c = ? LIMIT ? FOR UPDATE");
        assertThat(paginated(DbType.MYSQL, "SELECT * FROM t WHERE c = ? FOR UPDATE", 2, 10))
                .isEqualTo("SELECT * FROM t WHERE c = ? LIMIT ?,? FOR UPDATE");
        /* 锁定子句保持原大小写 */
        assertThat(paginated(DbType.MYSQL, "SELECT * FROM t WHERE c = ? for update", 2, 10))
                .isEqualTo("SELECT * FROM t WHERE c = ? LIMIT ?,? for update");
    }

    @Test
    void paginationSqlKeepsTrailingLockingClauseVariants() {
        assertThat(paginated(DbType.MYSQL, "SELECT * FROM t WHERE c = ? FOR UPDATE SKIP LOCKED", 2, 10))
                .isEqualTo("SELECT * FROM t WHERE c = ? LIMIT ?,? FOR UPDATE SKIP LOCKED");
        assertThat(paginated(DbType.MYSQL, "SELECT * FROM t WHERE c = ? FOR UPDATE OF t NOWAIT", 2, 10))
                .isEqualTo("SELECT * FROM t WHERE c = ? LIMIT ?,? FOR UPDATE OF t NOWAIT");
        assertThat(paginated(DbType.MYSQL, "SELECT * FROM t WHERE c = ? FOR SHARE", 2, 10))
                .isEqualTo("SELECT * FROM t WHERE c = ? LIMIT ?,? FOR SHARE");
        assertThat(paginated(DbType.MYSQL, "SELECT * FROM t WHERE c = ? LOCK IN SHARE MODE", 2, 10))
                .isEqualTo("SELECT * FROM t WHERE c = ? LIMIT ?,? LOCK IN SHARE MODE");
    }

    @Test
    void paginationSqlKeepsTrailingLockingClauseOnPostgreSql() {
        assertThat(paginated(DbType.POSTGRE_SQL, "SELECT * FROM t WHERE c = ? FOR UPDATE SKIP LOCKED", 2, 10))
                .isEqualTo("SELECT * FROM t WHERE c = ? LIMIT ? OFFSET ? FOR UPDATE SKIP LOCKED");
    }

    @Test
    void nonTrailingLockingClauseInsideSubqueryStaysUntouched() {
        /* 锁定子句位于子查询内部(非 SQL 末尾)时,外层追加 LIMIT 本身合法,不应被移动 */
        assertThat(
                paginated(DbType.MYSQL, "SELECT * FROM t WHERE id IN (SELECT id FROM u WHERE x = ? FOR UPDATE)", 1, 10))
                .isEqualTo("SELECT * FROM t WHERE id IN (SELECT id FROM u WHERE x = ? FOR UPDATE) LIMIT ?");
    }

    @Test
    void forUpdateCountSql() {
        /* count 改写不追加分页子句,锁定子句位置天然合法 */
        assertsCountSql(
                "SELECT * FROM t WHERE c = ? FOR UPDATE", "SELECT COUNT(*) AS total FROM t WHERE c = ? FOR UPDATE");
    }

    private String paginated(DbType dbType, String sql, long current, long size) {
        Configuration configuration = new Configuration();
        MappedStatement ms = new MappedStatement.Builder(configuration, "testMapper.selectPage",
                new StaticSqlSource(configuration, sql), SqlCommandType.SELECT).build();
        IPage<?> page = new Page<>(current, size);
        BoundSql boundSql = new BoundSql(configuration, sql, Collections.emptyList(), page);
        PaginationInnerInterceptor dbTypeInterceptor = new PaginationInnerInterceptor(dbType);
        dbTypeInterceptor.beforeQuery(null, ms, page, RowBounds.DEFAULT, Executor.NO_RESULT_HANDLER, boundSql);
        return boundSql.getSql();
    }
}
