package com.baomidou.mybatisplus.test.extension.plugins.inner;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.builder.StaticSqlSource;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author miemie
 * @since 2020-06-28
 */
@Tag("integration")
class PaginationInnerInterceptorTest {

    private final PaginationInnerInterceptor interceptor = new PaginationInnerInterceptor();

    @ParameterizedTest
    @CsvSource({"false,false", "false,true", "true,false", "true,true"})
    void appliesMaxLimitBeforeCheckingPageOverflow(boolean pageLimit, boolean overflow) {
        PaginationInnerInterceptor pagination = new PaginationInnerInterceptor(DbType.H2);
        pagination.setMaxLimit(pageLimit ? 5L : 10L);
        pagination.setOverflow(overflow);
        Page<Integer> page = new Page<>(2, 100);
        if (pageLimit) {
            page.setMaxLimit(10L);
        }
        try (SqlSession session = limitSessionFactory(pagination).openSession()) {
            assertThat(session.<Integer>selectList("limit.selectPage", page))
                    .containsExactly(11, 12, 13, 14, 15, 16, 17, 18, 19, 20);
            assertThat(page.getCurrent()).isEqualTo(2);
            assertThat(page.getSize()).isEqualTo(10);
            assertThat(page.getTotal()).isEqualTo(20);
            assertThat(page.getPages()).isEqualTo(2);
        }
    }

    @ParameterizedTest
    @CsvSource({"false", "true"})
    void stillHandlesOverflowAfterApplyingMaxLimit(boolean overflow) {
        PaginationInnerInterceptor pagination = new PaginationInnerInterceptor(DbType.H2);
        pagination.setMaxLimit(10L);
        pagination.setOverflow(overflow);
        Page<Integer> page = new Page<>(3, 100);
        try (SqlSession session = limitSessionFactory(pagination).openSession()) {
            assertThat(session.<Integer>selectList("limit.selectPage", page)).containsExactlyElementsOf(
                    overflow ? Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10) : Collections.emptyList());
            assertThat(page.getCurrent()).isEqualTo(overflow ? 1 : 3);
            assertThat(page.getSize()).isEqualTo(10);
            assertThat(page.getTotal()).isEqualTo(20);
        }
    }

    @Test
    void keepsCountDisabledAndNegativeSizeBehavior() {
        PaginationInnerInterceptor pagination = new PaginationInnerInterceptor(DbType.H2);
        pagination.setMaxLimit(10L);
        try (SqlSession session = limitSessionFactory(pagination).openSession()) {
            Page<Integer> noCount = new Page<>(2, 100, false);
            assertThat(session.<Integer>selectList("limit.selectPage", noCount))
                    .containsExactly(11, 12, 13, 14, 15, 16, 17, 18, 19, 20);
            assertThat(noCount.getTotal()).isZero();
            Page<Integer> negativeSize = new Page<>(1, -1);
            assertThat(session.<Integer>selectList("limit.selectPage", negativeSize))
                    .containsExactly(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
            assertThat(negativeSize.getTotal()).isZero();
        }
    }

    private SqlSessionFactory limitSessionFactory(PaginationInnerInterceptor pagination) {
        return limitSessionFactory(pagination, "SELECT x FROM SYSTEM_RANGE(1, 20) ORDER BY x");
    }

    private SqlSessionFactory limitSessionFactory(PaginationInnerInterceptor pagination, String sql,
            String... parameterNames) {
        Configuration configuration = new Configuration(new Environment("limit", new JdbcTransactionFactory(),
                new UnpooledDataSource("org.h2.Driver", "jdbc:h2:mem:pagination_limit", "sa", "")));
        MybatisPlusInterceptor plugin = new MybatisPlusInterceptor();
        plugin.addInnerInterceptor(pagination);
        configuration.addInterceptor(plugin);
        List<ParameterMapping> mappings = Arrays.stream(parameterNames)
                .map(name -> new ParameterMapping.Builder(configuration, name, Integer.class).build())
                .collect(Collectors.toList());
        configuration.addMappedStatement(
                new MappedStatement.Builder(configuration, "limit.selectPage",
                        new StaticSqlSource(configuration, sql, mappings), SqlCommandType.SELECT)
                        .resultMaps(
                                Collections.singletonList(
                                        new ResultMap.Builder(configuration, "limit.result", Integer.class,
                                                Collections.emptyList()).build()))
                        .build());
        return new SqlSessionFactoryBuilder().build(configuration);
    }

    @ParameterizedTest
    @CsvSource({"false,10,1", "false,20,0", "true,10,1", "true,20,0"})
    void countsRowsAfterHavingWithoutGroupBy(boolean useAlias, int minimum, long expectedTotal) {
        String sql = "SELECT SUM(x) AS amount FROM SYSTEM_RANGE(1, 5) WHERE x > ? HAVING "
                + (useAlias ? "amount" : "SUM(x)") + " > ?";
        PaginationInnerInterceptor pagination = new PaginationInnerInterceptor(DbType.H2);
        try (SqlSession session = limitSessionFactory(pagination, sql, "start", "minimum").openSession()) {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("start", 1);
            parameters.put("minimum", minimum);
            for (boolean optimize : new boolean[]{false, true}) {
                Page<Integer> page = new Page<Integer>(1, 1).setOptimizeCountSql(optimize);
                parameters.put("page", page);
                assertThat(session.<Integer>selectList("limit.selectPage", parameters)).containsExactlyElementsOf(
                        expectedTotal == 1 ? Collections.singletonList(14) : Collections.emptyList());
                assertThat(page.getTotal()).isEqualTo(expectedTotal);
            }
        }
    }

    @Test
    void retainsSelectAliasesAndJoinsNeededByHaving() {
        assertsCountSql(
                "SELECT SUM(amount) AS amount_total FROM payment HAVING amount_total > ? ORDER BY amount_total",
                "SELECT COUNT(*) FROM (SELECT SUM(amount) AS amount_total FROM payment HAVING amount_total > ?) TOTAL");
        assertsCountSql(
                "SELECT SUM(p.amount) FROM account a LEFT JOIN payment p ON a.id = p.account_id HAVING SUM(p.amount) > ?",
                "SELECT COUNT(*) FROM (SELECT SUM(p.amount) FROM account a LEFT JOIN payment p ON a.id = p.account_id HAVING SUM(p.amount) > ?) TOTAL");
    }

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
                "SELECT COUNT(*) FROM (SELECT * FROM comment ORDER BY (CASE WHEN creator = ? THEN 0 ELSE 1 END)) TOTAL");
    }

    @Test
    void parameterizedOrderByKeepsReferencedColumnsAndJoins() {
        assertsCountSql(
                "SELECT u.id, r.name AS role_name FROM app_user u LEFT JOIN app_role r ON r.id = u.role_id "
                        + "WHERE u.active = ? ORDER BY CASE WHEN r.name = ? THEN 0 ELSE 1 END, role_name",
                "SELECT COUNT(*) FROM (SELECT u.id, r.name AS role_name FROM app_user u LEFT JOIN app_role r ON r.id = u.role_id "
                        + "WHERE u.active = ? ORDER BY CASE WHEN r.name = ? THEN 0 ELSE 1 END, role_name) TOTAL");
    }

    @Test
    void paginatesParameterizedOrderByWithAutomaticCount() {
        Configuration configuration = new Configuration(new Environment("parameterized-order",
                new JdbcTransactionFactory(),
                new UnpooledDataSource("org.h2.Driver", "jdbc:h2:mem:pagination_parameterized_order", "sa", "")));
        MybatisPlusInterceptor plugin = new MybatisPlusInterceptor();
        plugin.addInnerInterceptor(new PaginationInnerInterceptor(DbType.H2));
        configuration.addInterceptor(plugin);
        configuration.addMapper(ParameterizedOrderMapper.class);
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(configuration);
        try (SqlSession session = factory.openSession()) {
            ParameterizedOrderMapper mapper = session.getMapper(ParameterizedOrderMapper.class);
            Page<Integer> withoutCount = new Page<>(1, 2, false);
            assertThat(mapper.selectPrioritized(withoutCount, 1, 4)).containsExactly(4, 2);
            Page<Integer> firstPage = new Page<>(1, 2);
            assertThat(mapper.selectPrioritized(firstPage, 1, 4)).containsExactly(4, 2);
            assertThat(firstPage.getTotal()).isEqualTo(4);
            Page<Integer> secondPage = new Page<>(2, 2);
            assertThat(mapper.selectPrioritized(secondPage, 1, 4)).containsExactly(3, 5);
            assertThat(secondPage.getTotal()).isEqualTo(4);
        }
    }

    interface ParameterizedOrderMapper {

        @Select("SELECT x FROM SYSTEM_RANGE(1, 5) WHERE x > #{minimum} "
                + "ORDER BY CASE WHEN x = #{priority} THEN 0 ELSE 1 END, x")
        List<Integer> selectPrioritized(Page<Integer> page, @Param("minimum") int minimum,
                @Param("priority") int priority);
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
}
