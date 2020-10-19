package com.baomidou.mybatisplus.extension.plugins.inner;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author miemie
 * @since 2020-06-28
 */
class PaginationInnerInterceptorTest {

    private final PaginationInnerInterceptor interceptor = new PaginationInnerInterceptor();

    @Test
    void optimizeCount() {
        /* 能进行优化的 SQL */
        assertsCountSql("select * from user u LEFT JOIN role r ON r.id = u.role_id",
            "SELECT COUNT(*) FROM user u");

        assertsCountSql("select * from user u LEFT JOIN role r ON r.id = u.role_id WHERE u.xx = ?",
            "SELECT COUNT(*) FROM user u WHERE u.xx = ?");

        assertsCountSql("select * from user u LEFT JOIN role r ON r.id = u.role_id LEFT JOIN permission p on p.id = u.per_id",
            "SELECT COUNT(*) FROM user u");

        assertsCountSql("select * from user u LEFT JOIN role r ON r.id = u.role_id LEFT JOIN permission p on p.id = u.per_id WHERE u.xx = ?",
            "SELECT COUNT(*) FROM user u WHERE u.xx = ?");
    }

    @Test
    void notOptimizeCount() {
        /* 不能进行优化的 SQL */
        assertsCountSql("select * from user u LEFT JOIN role r ON r.id = u.role_id AND r.name = ? where u.xx = ?",
            "SELECT COUNT(*) FROM user u LEFT JOIN role r ON r.id = u.role_id AND r.name = ? WHERE u.xx = ?");

        /* join 表与 where 条件大小写不同的情况 */
        assertsCountSql("select * from user u LEFT JOIN role r ON r.id = u.role_id where R.NAME = ?",
            "SELECT COUNT(*) FROM user u LEFT JOIN role r ON r.id = u.role_id WHERE R.NAME = ?");

        assertsCountSql("select * from user u LEFT JOIN role r ON r.id = u.role_id WHERE u.xax = ? AND r.cc = ? AND r.qq = ?",
            "SELECT COUNT(*) FROM user u LEFT JOIN role r ON r.id = u.role_id WHERE u.xax = ? AND r.cc = ? AND r.qq = ?");
    }

    @Test
    void optimizeCountOrderBy() {
        /* order by 里不带参数,去除order by */
        assertsCountSql("SELECT * FROM comment ORDER BY name",
            "SELECT COUNT(*) FROM comment");

        /* order by 里带参数,不去除order by */
        assertsCountSql("SELECT * FROM comment ORDER BY (CASE WHEN creator = ? THEN 0 ELSE 1 END)",
            "SELECT COUNT(*) FROM comment ORDER BY (CASE WHEN creator = ? THEN 0 ELSE 1 END)");
    }

    @Test
    void withAsCount() {
        assertsCountSql("with A as (select * from class) select * from A",
            "WITH A AS (SELECT * FROM class) SELECT COUNT(*) FROM A");
    }

    @Test
    void withAsOrderBy() {
        assertsConcatOrderBy("with A as (select * from class) select * from A",
            "WITH A AS (SELECT * FROM class) SELECT * FROM A ORDER BY column ASC",
            OrderItem.asc("column"));
    }

    @Test
    void groupByCount() {
        assertsCountSql("SELECT * FROM record_1 WHERE id = ? GROUP BY date(date_time)",
            "SELECT COUNT(*) FROM (SELECT * FROM record_1 WHERE id = ? GROUP BY date(date_time)) TOTAL");
    }

    @Test
    void leftJoinSelectCount() {
        assertsCountSql("select r.id, r.name, r.phone,rlr.total_top_up from reseller r " +
                "left join (select ral.reseller_id, sum(ral.top_up_money) as total_top_up, sum(ral.acquire_money) as total_acquire " +
                "from reseller_acquire_log ral " +
                "group by ral.reseller_id) rlr on r.id = rlr.reseller_id " +
                "order by r.created_at desc",
            "SELECT COUNT(*) FROM reseller r");
    }

    void assertsCountSql(String sql, String targetSql) {
        assertThat(interceptor.autoCountSql(true, sql)).isEqualTo(targetSql);
    }

    void assertsConcatOrderBy(String sql, String targetSql, OrderItem... orderItems) {
        assertThat(interceptor.concatOrderBy(sql, Arrays.asList(orderItems))).isEqualTo(targetSql);
    }
}
