package com.baomidou.mybatisplus.test.extension.plugins.inner;

import com.baomidou.mybatisplus.extension.plugins.handler.MultiDataPermissionHandler;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.*;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.sql.*;
import java.util.*;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("integration")
class InnerJoinUsingIsolationTest {

    private Connection connection;
    @BeforeEach
    void setup() throws Exception {
        connection = DriverManager.getConnection("jdbc:h2:mem:" + UUID.randomUUID(), "sa", "");
        try (Statement s = connection.createStatement()) {
            for (String table : Arrays.asList("a", "b", "c"))
                s.execute("CREATE TABLE zz_using_" + table + "(id INT,tenant_id INT,amount INT,link_id INT)");
            s.execute("INSERT INTO zz_using_a VALUES(1,1,10,1),(2,1,20,2),(3,2,900,1)");
            s.execute("INSERT INTO zz_using_b VALUES(11,1,10,1),(12,2,900,1),(13,1,30,3)");
            s.execute("INSERT INTO zz_using_c VALUES(21,1,100,1),(22,2,999,1),(23,1,300,3)");
        }
    }
    @AfterEach
    void close() throws Exception {
        connection.close();
    }
    static Stream<Object[]> joins() {
        String a = "zz_using_a a", b = "zz_using_b b", c = "zz_using_c c";
        List<Object[]> queries = Arrays
                .asList(
                        new Object[]{"SELECT b.amount FROM " + a + " INNER JOIN " + b + " USING(link_id) WHERE a.id=1",
                                Arrays.asList(10)},
                        new Object[]{"SELECT b.amount FROM " + a + " JOIN " + b + " USING(link_id) WHERE a.id=1",
                                Arrays.asList(10)},
                        new Object[]{"SELECT b.amount FROM " + a + " INNER JOIN " + b
                                + " USING(link_id,tenant_id) WHERE a.id=1", Arrays.asList(10)},
                        new Object[]{"SELECT c.amount FROM " + a + " INNER JOIN " + b + " USING(link_id) INNER JOIN "
                                + c + " USING(link_id) WHERE a.id=1", Arrays.asList(100)},
                        new Object[]{"SELECT c.amount FROM " + a + " INNER JOIN " + b + " USING(link_id) INNER JOIN "
                                + c + " ON b.link_id=c.link_id WHERE a.id=1", Arrays.asList(100)},
                        new Object[]{"SELECT COALESCE(c.amount,-1) FROM " + a + " INNER JOIN " + b
                                + " USING(link_id) LEFT JOIN " + c + " ON b.link_id=c.link_id AND c.id=23 WHERE a.id=1",
                                Arrays.asList(-1)},
                        new Object[]{
                                "SELECT COALESCE(b.amount,-1) FROM " + a + " INNER JOIN " + b
                                        + " USING(link_id) RIGHT JOIN " + c + " ON b.link_id=c.link_id ORDER BY c.id",
                                Arrays.asList(10, -1)},
                        new Object[]{"SELECT c.amount FROM (" + a + " INNER JOIN " + b + " USING(link_id)) INNER JOIN "
                                + c + " ON b.link_id=c.link_id WHERE a.id=1", Arrays.asList(100)},
                        new Object[]{"SELECT c.amount FROM " + a + " LEFT JOIN (" + b + " INNER JOIN " + c
                                + " USING(link_id)) ON a.link_id=b.link_id WHERE a.id=1", Arrays.asList(100)},
                        new Object[]{
                                "SELECT c.amount FROM " + a + " RIGHT JOIN (" + b + " INNER JOIN " + c
                                        + " USING(link_id)) ON a.link_id=b.link_id ORDER BY c.id",
                                Arrays.asList(100, 300)},
                        new Object[]{"SELECT b.amount FROM " + a + " INNER JOIN " + b
                                + " ON a.link_id=b.link_id WHERE a.id=1", Arrays.asList(10)});
        return queries.stream()
                .flatMap(q -> Stream.of(new Object[]{false, q[0], q[1]}, new Object[]{true, q[0], q[1]}));
    }
    @ParameterizedTest
    @MethodSource("joins")
    void filtersAllUsingInputsAcrossJoinChains(boolean permission, String sql, List<Integer> expected)
            throws Exception {
        assertThat(rows(interceptor(permission, null).parserSingle(sql, "mapper.query")))
                .containsExactlyElementsOf(expected);
    }
    @Test
    void preservesMergedColumnsAndStarProjectionShape() throws Exception {
        String sql = interceptor(false, null)
                .parserSingle("SELECT * FROM zz_using_a a INNER JOIN zz_using_b b USING(link_id) WHERE a.id=1", null);
        assertThat(sql).contains("USING").doesNotContain(" ON ");
        try (Statement s = connection.createStatement(); ResultSet rs = s.executeQuery(sql)) {
            assertThat(rs.getMetaData().getColumnCount()).isEqualTo(7);
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt("link_id")).isEqualTo(1);
            assertThat(rs.next()).isFalse();
        }
        // H2 rejects an unqualified USING column even in the original SQL.
        // Preserve its SQL shape here; execute SELECT * above to verify merged metadata.
        assertThat(
                interceptor(false, null).parserSingle(
                        "SELECT link_id FROM zz_using_a a INNER JOIN zz_using_b b USING(link_id) WHERE a.id=1", null))
                .startsWith("SELECT link_id FROM").contains("USING (link_id)");
    }
    @Test
    void honorsIgnoredTables() throws Exception {
        assertThat(
                rows(
                        interceptor(false, "zz_using_a").parserSingle(
                                "SELECT b.amount FROM zz_using_a a INNER JOIN zz_using_b b USING(link_id) WHERE a.id=3",
                                null)))
                .containsExactly(10);
    }
    @Test
    void permissionCallbacksRunOncePerUnfilteredTable() throws Exception {
        List<String> tables = new ArrayList<>();
        DataPermissionInterceptor interceptor = new DataPermissionInterceptor(
                (MultiDataPermissionHandler) (table, where, id) -> {
                    tables.add(table.getName());
                    return condition(table.getAlias().getName() + ".tenant_id=1");
                });
        assertThat(
                rows(
                        interceptor.parserSingle(
                                "SELECT c.amount FROM zz_using_a a INNER JOIN zz_using_b b USING(link_id) INNER JOIN zz_using_c c ON b.link_id=c.link_id WHERE a.id=1",
                                "mapper.query")))
                .containsExactly(100);
        assertThat(tables).containsExactlyInAnyOrder("zz_using_a", "zz_using_b", "zz_using_c");
    }
    private static BaseMultiTableInnerInterceptor interceptor(boolean permission, String ignored) {
        if (permission)
            return new DataPermissionInterceptor((MultiDataPermissionHandler) (table, where,
                    id) -> condition(table.getAlias().getName() + ".tenant_id=1"));
        return new TenantLineInnerInterceptor(new TenantLineHandler() {

            @Override
            public Expression getTenantId() {
                return new LongValue(1);
            }
            @Override
            public boolean ignoreTable(String table) {
                return table.equals(ignored);
            }
        });
    }
    private static Expression condition(String sql) {
        try {
            return CCJSqlParserUtil.parseCondExpression(sql);
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
