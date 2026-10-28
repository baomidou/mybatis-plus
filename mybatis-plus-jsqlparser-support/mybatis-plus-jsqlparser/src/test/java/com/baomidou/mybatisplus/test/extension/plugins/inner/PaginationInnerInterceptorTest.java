package com.baomidou.mybatisplus.test.extension.plugins.inner;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.builder.StaticSqlSource;
import org.apache.ibatis.cache.impl.PerpetualCache;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

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

    @Test
    void autoCountStatementsAreScopedToTheirSourceStatement() {
        CountStatementInterceptor counts = new CountStatementInterceptor();
        Configuration firstConfiguration = new Configuration();
        Configuration secondConfiguration = new Configuration();
        String id = "countCache.auto.selectPage";
        MappedStatement first = new MappedStatement.Builder(firstConfiguration, id,
                new StaticSqlSource(firstConfiguration, "select 1"), SqlCommandType.SELECT)
                .cache(new PerpetualCache("first")).build();
        MappedStatement second = new MappedStatement.Builder(secondConfiguration, id,
                new StaticSqlSource(secondConfiguration, "select 2"), SqlCommandType.SELECT)
                .cache(new PerpetualCache("second")).build();

        MappedStatement firstCount = counts.autoCount(first);
        MappedStatement secondCount = counts.autoCount(second);

        assertThat(firstCount.getConfiguration()).isSameAs(firstConfiguration);
        assertThat(secondCount.getConfiguration()).isSameAs(secondConfiguration);
        assertThat(secondCount.getCache()).isSameAs(second.getCache());
        assertThat(secondCount).isNotSameAs(firstCount);
        assertThat(counts.autoCount(first)).isSameAs(firstCount);
        assertThat(counts.autoCount(second)).isSameAs(secondCount);
    }

    @Test
    void customCountStatementsUseTheCurrentConfiguration() {
        CountStatementInterceptor counts = new CountStatementInterceptor();
        Configuration firstConfiguration = new Configuration();
        Configuration secondConfiguration = new Configuration();
        String namespace = "countCache.custom.";
        MappedStatement first = statement(firstConfiguration, namespace + "selectPage", "select 1");
        MappedStatement second = statement(secondConfiguration, namespace + "selectPage", "select 2");
        MappedStatement firstCount = statement(firstConfiguration, namespace + "count", "select 10");
        MappedStatement secondCount = statement(secondConfiguration, namespace + "count", "select 20");
        firstConfiguration.addMappedStatement(firstCount);
        secondConfiguration.addMappedStatement(secondCount);

        assertThat(counts.customCount(first, "count")).isSameAs(firstCount);
        assertThat(counts.customCount(second, "count")).isSameAs(secondCount);
        assertThat(counts.customCount(second, namespace + "count")).isSameAs(secondCount);
        assertThat(counts.customCount(first, "count")).isSameAs(firstCount);
    }

    @Test
    void missingCustomCountDoesNotReuseAnotherConfigurationsStatement() {
        CountStatementInterceptor counts = new CountStatementInterceptor();
        Configuration firstConfiguration = new Configuration();
        Configuration secondConfiguration = new Configuration();
        String namespace = "countCache.missing.";
        MappedStatement first = statement(firstConfiguration, namespace + "selectPage", "select 1");
        MappedStatement second = statement(secondConfiguration, namespace + "selectPage", "select 2");
        MappedStatement firstCount = statement(firstConfiguration, namespace + "count", "select 10");
        firstConfiguration.addMappedStatement(firstCount);

        assertThat(counts.customCount(first, "count")).isSameAs(firstCount);
        assertThat(counts.customCount(second, "count")).isNull();
    }

    @Test
    void paginationUsesEachSessionFactorysCustomCountSql() {
        PaginationInnerInterceptor pagination = new PaginationInnerInterceptor();
        SqlSessionFactory first = countSessionFactory(2, pagination);
        SqlSessionFactory second = countSessionFactory(3, pagination);

        assertPageCount(first, 2);
        assertPageCount(second, 3);
        assertPageCount(first, 2);
    }

    private SqlSessionFactory countSessionFactory(int rows, PaginationInnerInterceptor pagination) {
        Configuration configuration = new Configuration(new Environment("count-cache", new JdbcTransactionFactory(),
                new UnpooledDataSource("org.h2.Driver", "jdbc:h2:mem:count_cache", "sa", "")));
        MybatisPlusInterceptor plugin = new MybatisPlusInterceptor();
        plugin.addInnerInterceptor(pagination);
        configuration.addInterceptor(plugin);
        String namespace = "countCache.integration.";
        configuration.addMappedStatement(
                new MappedStatement.Builder(configuration, namespace + "selectPage",
                        new StaticSqlSource(configuration, "select x from system_range(1, " + rows + ")"),
                        SqlCommandType.SELECT)
                        .resultMaps(
                                Collections.singletonList(
                                        new ResultMap.Builder(configuration, namespace + "result", Integer.class,
                                                Collections.emptyList()).build()))
                        .build());
        configuration
                .addMappedStatement(
                        new MappedStatement.Builder(configuration, namespace + "count",
                                new StaticSqlSource(configuration,
                                        "select count(*) from system_range(1, " + rows + ")"),
                                SqlCommandType.SELECT)
                                .resultMaps(
                                        Collections.singletonList(
                                                new ResultMap.Builder(configuration, namespace + "countResult",
                                                        Long.class, Collections.emptyList()).build()))
                                .build());
        return new SqlSessionFactoryBuilder().build(configuration);
    }

    private void assertPageCount(SqlSessionFactory factory, long total) {
        try (SqlSession session = factory.openSession()) {
            Page<Integer> page = new Page<>(1, 1);
            page.setCountId("count");
            assertThat(session.<Integer>selectList("countCache.integration.selectPage", page)).containsExactly(1);
            assertThat(page.getTotal()).isEqualTo(total);
        }
    }

    private MappedStatement statement(Configuration configuration, String id, String sql) {
        return new MappedStatement.Builder(configuration, id, new StaticSqlSource(configuration, sql),
                SqlCommandType.SELECT).build();
    }

    private static class CountStatementInterceptor extends PaginationInnerInterceptor {

        MappedStatement autoCount(MappedStatement statement) {
            return buildAutoCountMappedStatement(statement);
        }

        MappedStatement customCount(MappedStatement statement, String countId) {
            return buildCountMappedStatement(statement, countId);
        }
    }

    void assertsCountSql(String sql, String targetSql) {
        assertThat(interceptor.autoCountSql(new Page<>(), sql)).isEqualTo(targetSql);
    }

    void assertsConcatOrderBy(String sql, String targetSql, OrderItem... orderItems) {
        assertThat(interceptor.concatOrderBy(sql, Arrays.asList(orderItems))).isEqualTo(targetSql);
    }
}
