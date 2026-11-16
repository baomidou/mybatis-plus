package com.baomidou.mybatisplus.test.extension.plugins.inner;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.MybatisSqlSessionFactoryBuilder;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.MultiDataPermissionHandler;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.BaseMultiTableInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DataPermissionInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.schema.Column;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("integration")
class SelectExpressionIsolationTest {

    private UnpooledDataSource datasource;
    private Connection connection;

    @BeforeEach
    void setup() throws Exception {
        datasource = new UnpooledDataSource("org.h2.Driver", "jdbc:h2:mem:" + UUID.randomUUID(), "sa", "");
        connection = datasource.getConnection();
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE zz_expression_anchor(id INT,tenant_id INT)");
            statement.execute("INSERT INTO zz_expression_anchor VALUES(1,1),(2,2)");
            statement.execute("CREATE TABLE zz_expression_secret(secret INT,tenant_id INT)");
            statement.execute("INSERT INTO zz_expression_secret VALUES(10,1),(900,2)");
        }
    }

    @AfterEach
    void close() throws Exception {
        connection.close();
    }

    static Stream<Arguments> projections() {
        String subquery = "(SELECT MAX(secret) FROM zz_expression_secret)";
        return Stream
                .of(
                        subquery, subquery + " + 0", "CASE WHEN id=1 THEN " + subquery + " ELSE 0 END",
                        "CASE WHEN " + subquery + " > 50 THEN 900 ELSE 10 END", "CAST(" + subquery + " AS INTEGER)",
                        "COALESCE(" + subquery + " + 0,0)", "ABS(CASE WHEN id=1 THEN " + subquery + " ELSE 0 END)",
                        "-(-" + subquery + ")",
                        "CASE WHEN EXISTS(SELECT 1 FROM zz_expression_secret WHERE secret>50) THEN 900 ELSE 10 END")
                .flatMap(expression -> Stream.of(Arguments.of(expression, false), Arguments.of(expression, true)));
    }

    @ParameterizedTest
    @MethodSource("projections")
    void nestedProjectionOnlyReadsAllowedRows(String projection, boolean permission) throws Exception {
        BaseMultiTableInnerInterceptor interceptor = permission ? permission() : tenant();
        String rewritten = interceptor
                .parserSingle("SELECT " + projection + " FROM zz_expression_anchor WHERE id=1", "projectionMapper");
        assertThat(rows(rewritten)).containsExactly(10);
    }

    @Test
    void mapperExecutionFiltersWrappedSubquery() {
        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setEnvironment(new Environment("projection", new JdbcTransactionFactory(), datasource));
        MybatisPlusInterceptor plugins = new MybatisPlusInterceptor();
        plugins.addInnerInterceptor(tenant());
        configuration.addInterceptor(plugins);
        configuration.addMapper(ProjectionMapper.class);
        try (SqlSession session = new MybatisSqlSessionFactoryBuilder().build(configuration).openSession()) {
            assertThat(session.getMapper(ProjectionMapper.class).value()).isEqualTo(10);
        }
    }

    @Test
    void visitsEachSelectOnceAndPreservesIgnoredTables() throws Exception {
        AtomicInteger calls = new AtomicInteger();
        TenantLineInnerInterceptor interceptor = new TenantLineInnerInterceptor(new TenantLineHandler() {

            @Override
            public Expression getTenantId() {
                calls.incrementAndGet();
                return new LongValue(1);
            }
        });
        String sql = "SELECT ABS((SELECT MAX(secret) FROM zz_expression_secret) + 0) FROM zz_expression_anchor WHERE id=1";
        assertThat(rows(interceptor.parserSingle(sql, null))).containsExactly(10);
        assertThat(calls).hasValue(2);
        interceptor.setTenantLineHandler(new TenantLineHandler() {

            @Override
            public Expression getTenantId() {
                return new LongValue(1);
            }

            @Override
            public boolean ignoreTable(String tableName) {
                return "zz_expression_secret".equals(tableName);
            }
        });
        assertThat(rows(interceptor.parserSingle(sql, null))).containsExactly(900);
    }

    private static TenantLineInnerInterceptor tenant() {
        return new TenantLineInnerInterceptor(new TenantLineHandler() {

            @Override
            public Expression getTenantId() {
                return new LongValue(1);
            }
        });
    }

    private static DataPermissionInterceptor permission() {
        return new DataPermissionInterceptor((MultiDataPermissionHandler) (table, where, id) -> {
            assertThat(id).isEqualTo("projectionMapper");
            return new EqualsTo(new Column(table, "tenant_id"), new LongValue(1));
        });
    }

    private List<Integer> rows(String sql) throws Exception {
        List<Integer> result = new ArrayList<>();
        try (Statement statement = connection.createStatement(); ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                result.add(rs.getInt(1));
            }
        }
        return result;
    }

    interface ProjectionMapper {

        @Select("SELECT CASE WHEN id=1 THEN (SELECT MAX(secret) FROM zz_expression_secret) ELSE 0 END FROM zz_expression_anchor WHERE id=1")
        int value();
    }
}
