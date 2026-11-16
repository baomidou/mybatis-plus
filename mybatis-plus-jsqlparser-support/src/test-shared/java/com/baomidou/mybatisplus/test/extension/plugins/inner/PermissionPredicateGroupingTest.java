package com.baomidou.mybatisplus.test.extension.plugins.inner;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.MybatisSqlSessionFactoryBuilder;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.MultiDataPermissionHandler;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.DataPermissionInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.baomidou.mybatisplus.jsqlparser.enums.ExpressionAppendMode;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
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
import org.junit.jupiter.params.provider.EnumSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("integration")
class PermissionPredicateGroupingTest {

    private UnpooledDataSource datasource;
    private Connection connection;

    @BeforeEach
    void setup() throws Exception {
        datasource = new UnpooledDataSource("org.h2.Driver", "jdbc:h2:mem:" + UUID.randomUUID(), "sa", "");
        connection = datasource.getConnection();
        execute("CREATE TABLE zz_permission_grouping(id INT PRIMARY KEY, tenant_id INT, dept_id INT, amount INT)");
        execute("INSERT INTO zz_permission_grouping VALUES(1,1,1,10),(2,1,2,20),(3,2,2,900),(4,1,9,40),(5,2,1,500)");
    }

    @AfterEach
    void close() throws Exception {
        connection.close();
    }

    @ParameterizedTest
    @EnumSource(ExpressionAppendMode.class)
    void selectKeepsOriginalPredicate(ExpressionAppendMode mode) throws Exception {
        String sql = permission(mode)
                .parserSingle("SELECT id FROM zz_permission_grouping WHERE tenant_id=1 AND id=1", "select");
        assertThat(rows(sql)).containsExactly(1);
    }

    @ParameterizedTest
    @EnumSource(ExpressionAppendMode.class)
    void updateKeepsOriginalPredicate(ExpressionAppendMode mode) throws Exception {
        String sql = permission(mode)
                .parserSingle("UPDATE zz_permission_grouping SET amount=77 WHERE tenant_id=1 AND id=1", "update");
        assertThat(execute(sql)).isEqualTo(1);
        assertThat(rows("SELECT amount FROM zz_permission_grouping ORDER BY id")).containsExactly(77, 20, 900, 40, 500);
    }

    @ParameterizedTest
    @EnumSource(ExpressionAppendMode.class)
    void deleteKeepsOriginalPredicate(ExpressionAppendMode mode) throws Exception {
        String sql = permission(mode)
                .parserSingle("DELETE FROM zz_permission_grouping WHERE tenant_id=1 AND id=1", "delete");
        assertThat(execute(sql)).isEqualTo(1);
        assertThat(rows("SELECT id FROM zz_permission_grouping ORDER BY id")).containsExactly(2, 3, 4, 5);
    }

    @ParameterizedTest
    @EnumSource(ExpressionAppendMode.class)
    void joinsKeepEachTablePermissionGrouped(ExpressionAppendMode mode) throws Exception {
        String sql = permission(mode).parserSingle(
                "SELECT b.id FROM zz_permission_grouping a INNER JOIN zz_permission_grouping b ON a.id=b.id WHERE a.id=1",
                "join");
        assertThat(rows(sql)).containsExactly(1);
    }

    @ParameterizedTest
    @EnumSource(ExpressionAppendMode.class)
    void multiplePermissionsRemainIndependentWithoutOriginalWhere(ExpressionAppendMode mode) throws Exception {
        String sql = permission(mode).parserSingle(
                "SELECT a.id FROM zz_permission_grouping a, zz_permission_grouping b ORDER BY a.id", "commaJoin");
        assertThat(rows(sql)).containsExactly(1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 5, 5, 5, 5);
    }

    @ParameterizedTest
    @EnumSource(ExpressionAppendMode.class)
    void dataPermissionCannotOverrideTenantPlugin(ExpressionAppendMode mode) {
        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setEnvironment(new Environment("permissions", new JdbcTransactionFactory(), datasource));
        MybatisPlusInterceptor plugins = new MybatisPlusInterceptor();
        plugins.addInnerInterceptor(new TenantLineInnerInterceptor(new TenantLineHandler() {

            @Override
            public Expression getTenantId() {
                return new LongValue(1);
            }
        }));
        plugins.addInnerInterceptor(permission(mode));
        configuration.addInterceptor(plugins);
        configuration.addMapper(PermissionMapper.class);
        try (SqlSession session = new MybatisSqlSessionFactoryBuilder().build(configuration).openSession()) {
            assertThat(session.getMapper(PermissionMapper.class).ids()).containsExactly(2);
        }
    }

    @Test
    void nullAndAlreadyGroupedPermissionsPreserveQueryBehavior() throws Exception {
        DataPermissionInterceptor none = new DataPermissionInterceptor((MultiDataPermissionHandler) (t, w, id) -> null);
        assertThat(rows(none.parserSingle("SELECT id FROM zz_permission_grouping WHERE id=4", "none")))
                .containsExactly(4);
        DataPermissionInterceptor grouped = new DataPermissionInterceptor(
                (MultiDataPermissionHandler) (t, w, id) -> expression("(dept_id=1 OR dept_id=2)"));
        assertThat(rows(grouped.parserSingle("SELECT id FROM zz_permission_grouping WHERE id=1", "grouped")))
                .containsExactly(1);
    }

    @ParameterizedTest
    @EnumSource(ExpressionAppendMode.class)
    void xorPermissionKeepsItsGrouping(ExpressionAppendMode mode) {
        DataPermissionInterceptor interceptor = new DataPermissionInterceptor(
                (MultiDataPermissionHandler) (t, w, id) -> expression("flag_a=1 XOR flag_b=1"));
        interceptor.setExpressionAppendMode(mode);
        String rewritten = interceptor.parserSingle("UPDATE records SET amount=1 WHERE id=7", "xor");
        assertThat(rewritten).contains("(flag_a = 1 XOR flag_b = 1)");
    }

    private static DataPermissionInterceptor permission(ExpressionAppendMode mode) {
        DataPermissionInterceptor interceptor = new DataPermissionInterceptor(
                (MultiDataPermissionHandler) (t, w, id) -> {
                    String prefix = t.getAlias() == null ? "" : t.getAlias().getName() + ".";
                    return expression(prefix + "dept_id=1 OR " + prefix + "dept_id=2");
                });
        interceptor.setExpressionAppendMode(mode);
        return interceptor;
    }

    private static Expression expression(String sql) {
        try {
            return CCJSqlParserUtil.parseCondExpression(sql);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private int execute(String sql) throws Exception {
        try (Statement statement = connection.createStatement()) {
            return statement.executeUpdate(sql);
        }
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

    interface PermissionMapper {

        @Select("SELECT id FROM zz_permission_grouping WHERE dept_id=2 ORDER BY id")
        List<Integer> ids();
    }
}
