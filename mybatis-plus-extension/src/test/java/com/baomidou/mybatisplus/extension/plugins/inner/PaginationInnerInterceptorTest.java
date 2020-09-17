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
            "SELECT COUNT(1) FROM user u");

        assertsCountSql("select * from user u LEFT JOIN role r ON r.id = u.role_id WHERE u.xx = ?",
            "SELECT COUNT(1) FROM user u WHERE u.xx = ?");

        assertsCountSql("select * from user u LEFT JOIN role r ON r.id = u.role_id LEFT JOIN permission p on p.id = u.per_id",
            "SELECT COUNT(1) FROM user u");

        assertsCountSql("select * from user u LEFT JOIN role r ON r.id = u.role_id LEFT JOIN permission p on p.id = u.per_id WHERE u.xx = ?",
            "SELECT COUNT(1) FROM user u WHERE u.xx = ?");
    }

    @Test
    void notOptimizeCount() {
        /* 不能进行优化的 SQL */
        assertsCountSql("select * from user u LEFT JOIN role r ON r.id = u.role_id AND r.name = ? where u.xx = ?",
            "SELECT COUNT(1) FROM user u LEFT JOIN role r ON r.id = u.role_id AND r.name = ? WHERE u.xx = ?");

        assertsCountSql("select * from user u LEFT JOIN role r ON r.id = u.role_id WHERE u.xax = ? AND r.cc = ? AND r.qq = ?",
            "SELECT COUNT(1) FROM user u LEFT JOIN role r ON r.id = u.role_id WHERE u.xax = ? AND r.cc = ? AND r.qq = ?");
    }

    @Test
    void optimizeCountOrderBy() {
        /* order by 里不带参数,去除order by */
        assertsCountSql("SELECT * FROM comment ORDER BY name",
            "SELECT COUNT(1) FROM comment");

        /* order by 里带参数,不去除order by */
        assertsCountSql("SELECT * FROM comment ORDER BY (CASE WHEN creator = ? THEN 0 ELSE 1 END)",
            "SELECT COUNT(1) FROM comment ORDER BY (CASE WHEN creator = ? THEN 0 ELSE 1 END)");
    }

    @Test
    void withAsCount() {
        assertsCountSql("with A as (select * from class) select * from A",
            "WITH A AS (SELECT * FROM class) SELECT COUNT(1) FROM A");
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
            "SELECT COUNT(1) FROM (SELECT * FROM record_1 WHERE id = ? GROUP BY date(date_time)) TOTAL");
    }

    void assertsCountSql(String sql, String targetSql) {
        assertThat(interceptor.autoCountSql(true, sql)).isEqualTo(targetSql);
    }

    void assertsConcatOrderBy(String sql, String targetSql, OrderItem... orderItems) {
        assertThat(interceptor.concatOrderBy(sql, Arrays.asList(orderItems))).isEqualTo(targetSql);
    }
}
