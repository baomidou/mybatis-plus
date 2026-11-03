package com.baomidou.mybatisplus.test.extension.plugins.inner;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.MybatisSqlSessionFactoryBuilder;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.DataPermissionHandler;
import com.baomidou.mybatisplus.extension.plugins.handler.MultiDataPermissionHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.DataPermissionInterceptor;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.select.PlainSelect;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.sql.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("integration")
class LegacyPermissionParenthesesTest {

    private Connection connection;
    private String url;
    @BeforeEach
    void setup() throws Exception {
        url = "jdbc:h2:mem:" + UUID.randomUUID();
        connection = DriverManager.getConnection(url, "sa", "");
        try (Statement s = connection.createStatement()) {
            s.execute("CREATE TABLE zz_legacy_permission_invoice(id INT,dept_id INT)");
            s.execute("INSERT INTO zz_legacy_permission_invoice VALUES(1,1),(2,2)");
        }
    }
    @AfterEach
    void close() throws Exception {
        connection.close();
    }
    static Stream<Object[]> queries() {
        String select = "SELECT id FROM zz_legacy_permission_invoice";
        return Stream.of(
                new Object[]{select, 1, Arrays.asList(1)}, new Object[]{"(" + select + ")", 1, Arrays.asList(1)},
                new Object[]{"(((" + select + ")))", 1, Arrays.asList(1)},
                new Object[]{"(" + select + ") UNION ALL (" + select + ")", 2, Arrays.asList(1, 1)},
                new Object[]{"((" + select + ") UNION (" + select + "))", 2, Arrays.asList(1)},
                new Object[]{"(" + select + ") UNION ALL ((" + select + ") UNION ALL (" + select + "))", 3,
                        Arrays.asList(1, 1, 1)},
                new Object[]{"(" + select + " WHERE id=2)", 1, Collections.emptyList()},
                new Object[]{"((" + select + " WHERE id=1) UNION ALL (" + select + " WHERE id=2)) ORDER BY id", 2,
                        Arrays.asList(1)});
    }
    @ParameterizedTest
    @MethodSource("queries")
    void filtersEveryParenthesizedQueryBranchExactlyOnce(String sql, int calls, List<Integer> expected)
            throws Exception {
        List<String> ids = new ArrayList<>();
        DataPermissionInterceptor interceptor = new DataPermissionInterceptor((DataPermissionHandler) (where, id) -> {
            ids.add(id);
            return restrict(where);
        });
        assertThat(rows(interceptor.parserSingle(sql, "mapper.query"))).containsExactlyElementsOf(expected);
        assertThat(ids).hasSize(calls).containsOnly("mapper.query");
    }
    @Test
    void nullHandlerAndNullResultPreserveTheOriginalQuery() throws Exception {
        String sql = "((SELECT id FROM zz_legacy_permission_invoice WHERE id=2))";
        assertThat(rows(new DataPermissionInterceptor().parserSingle(sql, "none"))).containsExactly(2);
        DataPermissionInterceptor interceptor = new DataPermissionInterceptor(
                (DataPermissionHandler) (where, id) -> null);
        assertThat(rows(interceptor.parserSingle(sql, "none"))).containsExactly(2);
    }
    @Test
    void keepsTheSetWhereExtensionHook() throws Exception {
        AtomicInteger calls = new AtomicInteger();
        DataPermissionInterceptor interceptor = new DataPermissionInterceptor(
                (DataPermissionHandler) (where, id) -> restrict(where)) {

            @Override
            protected void setWhere(PlainSelect select, String id) {
                calls.incrementAndGet();
                super.setWhere(select, id);
            }
        };
        assertThat(
                rows(
                        interceptor.parserSingle(
                                "(SELECT id FROM zz_legacy_permission_invoice) UNION ALL (SELECT id FROM zz_legacy_permission_invoice)",
                                "query")))
                .containsExactly(1, 1);
        assertThat(calls).hasValue(2);
    }
    @Test
    void keepsLegacyCallbacksAtTheOuterQueryBoundary() throws Exception {
        AtomicInteger calls = new AtomicInteger();
        DataPermissionInterceptor interceptor = new DataPermissionInterceptor((DataPermissionHandler) (where, id) -> {
            calls.incrementAndGet();
            return restrict(where);
        });
        assertThat(
                rows(
                        interceptor.parserSingle(
                                "(SELECT id FROM (SELECT id,dept_id FROM zz_legacy_permission_invoice) nested)",
                                "query")))
                .containsExactly(1);
        assertThat(calls).hasValue(1);
    }
    @Test
    void multiTableHandlerStillProcessesParenthesizedUnionBranches() throws Exception {
        DataPermissionInterceptor interceptor = new DataPermissionInterceptor(
                (MultiDataPermissionHandler) (table, where, id) -> restrict(null));
        assertThat(
                rows(
                        interceptor.parserSingle(
                                "(SELECT id FROM zz_legacy_permission_invoice) UNION ALL (SELECT id FROM zz_legacy_permission_invoice)",
                                "query")))
                .containsExactly(1, 1);
    }
    @Test
    void mapperQueriesUseTheLegacyHandlerAndMappedStatementId() throws Exception {
        List<String> ids = new ArrayList<>();
        DataPermissionInterceptor interceptor = new DataPermissionInterceptor((DataPermissionHandler) (where, id) -> {
            ids.add(id);
            return restrict(where);
        });
        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setEnvironment(
                new Environment("legacy", new JdbcTransactionFactory(),
                        new UnpooledDataSource("org.h2.Driver", url, "sa", "")));
        MybatisPlusInterceptor plugin = new MybatisPlusInterceptor();
        plugin.addInnerInterceptor(interceptor);
        configuration.addInterceptor(plugin);
        configuration.addMapper(LegacyMapper.class);
        try (SqlSession session = new MybatisSqlSessionFactoryBuilder().build(configuration).openSession()) {
            assertThat(session.getMapper(LegacyMapper.class).select()).containsExactly(1);
        }
        assertThat(ids).containsExactly(LegacyMapper.class.getName() + ".select");
    }
    interface LegacyMapper {

        @Select("(SELECT id FROM zz_legacy_permission_invoice ORDER BY id)")
        List<Integer> select();
    }
    private static Expression restrict(Expression where) {
        try {
            return CCJSqlParserUtil.parseCondExpression((where == null ? "" : "(" + where + ") AND ") + "dept_id=1");
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
    private List<Integer> rows(String sql) throws Exception {
        List<Integer> result = new ArrayList<>();
        try (Statement s = connection.createStatement(); ResultSet rs = s.executeQuery(sql)) {
            while (rs.next())
                result.add(rs.getInt(1));
        }
        return result;
    }
}
