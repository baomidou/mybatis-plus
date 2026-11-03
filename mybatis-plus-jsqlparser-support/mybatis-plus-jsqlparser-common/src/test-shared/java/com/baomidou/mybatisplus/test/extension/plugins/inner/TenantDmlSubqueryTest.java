package com.baomidou.mybatisplus.test.extension.plugins.inner;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.MybatisSqlSessionFactoryBuilder;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.sql.*;
import java.util.*;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("integration")
class TenantDmlSubqueryTest {

    private Connection connection;
    private String url;
    private final TenantLineInnerInterceptor tenant = interceptor(null);

    @BeforeEach
    void setup() throws Exception {
        url = "jdbc:h2:mem:" + UUID.randomUUID();
        connection = DriverManager.getConnection(url, "sa", "");
        execute("CREATE TABLE zz_tenant_dml_accounts(id INT,tenant_id INT,balance INT)");
        execute("INSERT INTO zz_tenant_dml_accounts VALUES(1,1,20),(2,2,900)");
        execute("CREATE TABLE zz_tenant_dml_secrets(account_id INT,tenant_id INT,secret INT)");
        execute("INSERT INTO zz_tenant_dml_secrets VALUES(99,1,10),(1,2,900)");
    }
    @AfterEach
    void close() throws Exception {
        connection.close();
    }

    static Stream<Object[]> predicates() {
        String source = "zz_tenant_dml_secrets";
        List<Object[]> cases = Arrays.asList(
                new Object[]{"a.id IN (SELECT account_id FROM " + source + ")", 0},
                new Object[]{"a.id NOT IN (SELECT account_id FROM " + source + ")", 1},
                new Object[]{"EXISTS (SELECT 1 FROM " + source + " s WHERE s.account_id=a.id)", 0},
                new Object[]{"NOT EXISTS (SELECT 1 FROM " + source + " s WHERE s.account_id=a.id)", 1},
                new Object[]{"a.id = (SELECT MIN(account_id) FROM " + source + ")", 0},
                new Object[]{"CASE WHEN EXISTS (SELECT 1 FROM " + source
                        + " s WHERE s.account_id=a.id) THEN 1 ELSE 0 END = 1", 0},
                new Object[]{"COALESCE((SELECT MIN(account_id) FROM " + source + "),0) = a.id", 0},
                new Object[]{"(SELECT MIN(account_id) FROM " + source + ") + 0 = a.id", 0});
        return cases.stream().flatMap(c -> Stream.of(new Object[]{false, c[0], c[1]}, new Object[]{true, c[0], c[1]}));
    }

    @ParameterizedTest
    @MethodSource("predicates")
    void filtersSubqueriesBeforeDecidingWhichRowsToChange(boolean delete, String predicate, int expected)
            throws Exception {
        String sql = (delete
                ? "DELETE FROM zz_tenant_dml_accounts a"
                : "UPDATE zz_tenant_dml_accounts a SET balance=77") + " WHERE a.id=1 AND (" + predicate + ")";
        assertThat(execute(tenant.parserSingle(sql, null))).isEqualTo(expected);
        assertThat(value("SELECT balance FROM zz_tenant_dml_accounts WHERE id=2")).isEqualTo(900);
    }

    @ParameterizedTest
    @ValueSource(strings = {"(SELECT MAX(secret) FROM zz_tenant_dml_secrets)",
            "(SELECT MAX(secret) FROM zz_tenant_dml_secrets) + 0",
            "CASE WHEN id=1 THEN (SELECT MAX(secret) FROM zz_tenant_dml_secrets) ELSE 0 END",
            "CAST((SELECT MAX(secret) FROM zz_tenant_dml_secrets) AS INTEGER)",
            "COALESCE((SELECT MAX(secret) FROM zz_tenant_dml_secrets) + 0,0)"})
    void filtersSubqueriesInSetExpressionWrappers(String expression) throws Exception {
        assertThat(
                execute(
                        tenant.parserSingle(
                                "UPDATE zz_tenant_dml_accounts SET balance=" + expression + " WHERE id=1", null)))
                .isEqualTo(1);
        assertThat(value("SELECT balance FROM zz_tenant_dml_accounts WHERE id=1")).isEqualTo(10);
    }

    @Test
    void retainsAllowedWrites() throws Exception {
        execute("UPDATE zz_tenant_dml_secrets SET account_id=1 WHERE tenant_id=1");
        assertThat(
                execute(
                        tenant.parserSingle(
                                "UPDATE zz_tenant_dml_accounts SET balance=77 WHERE id IN (SELECT account_id FROM zz_tenant_dml_secrets)",
                                null)))
                .isEqualTo(1);
    }

    @Test
    void preservesIgnoredSourceAndTargetSemantics() throws Exception {
        String sql = "UPDATE zz_tenant_dml_accounts SET balance=77 WHERE id IN (SELECT account_id FROM zz_tenant_dml_secrets)";
        assertThat(execute(interceptor("zz_tenant_dml_secrets").parserSingle(sql, null))).isEqualTo(1);
        assertThat(interceptor("zz_tenant_dml_accounts").parserSingle(sql, null)).doesNotContain("tenant_id");
    }

    @Test
    void appliesTheFixThroughTheMapperPrepareHook() throws Exception {
        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setEnvironment(
                new Environment("tenant", new JdbcTransactionFactory(),
                        new UnpooledDataSource("org.h2.Driver", url, "sa", "")));
        MybatisPlusInterceptor plugin = new MybatisPlusInterceptor();
        plugin.addInnerInterceptor(tenant);
        configuration.addInterceptor(plugin);
        configuration.addMapper(DmlMapper.class);
        try (SqlSession session = new MybatisSqlSessionFactoryBuilder().build(configuration).openSession(true)) {
            assertThat(session.getMapper(DmlMapper.class).update()).isZero();
        }
    }

    interface DmlMapper {

        @Update("UPDATE zz_tenant_dml_accounts SET balance=77 WHERE id IN (SELECT account_id FROM zz_tenant_dml_secrets)")
        int update();
    }
    private static TenantLineInnerInterceptor interceptor(String ignored) {
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
    private int execute(String sql) throws Exception {
        try (Statement s = connection.createStatement()) {
            return s.executeUpdate(sql);
        }
    }
    private int value(String sql) throws Exception {
        try (Statement s = connection.createStatement(); ResultSet rs = s.executeQuery(sql)) {
            rs.next();
            return rs.getInt(1);
        }
    }
}
