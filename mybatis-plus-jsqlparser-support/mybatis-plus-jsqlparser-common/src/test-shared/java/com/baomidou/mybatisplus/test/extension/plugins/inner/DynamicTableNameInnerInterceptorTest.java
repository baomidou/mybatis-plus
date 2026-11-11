package com.baomidou.mybatisplus.test.extension.plugins.inner;

import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 动态表名内部拦截器测试
 *
 * @author miemie, hcl
 * @since 2020-07-16
 */
@Tag("integration")
class DynamicTableNameInnerInterceptorTest {

    @Test
    void preservesLiteralTextWhileRoutingTables() {
        DynamicTableNameInnerInterceptor interceptor = new DynamicTableNameInnerInterceptor((sql, name) -> name + "_r");
        assertThat(
                interceptor.changeTable("UPDATE audit_log SET message = 'moved from orders to archive' WHERE id = 1"))
                .isEqualTo("UPDATE audit_log_r SET message = 'moved from orders to archive' WHERE id = 1");
        assertThat(interceptor.changeTable("SELECT '-- diagnostic' AS note FROM audit_log"))
                .isEqualTo("SELECT '-- diagnostic' AS note FROM audit_log_r");
        assertThat(interceptor.changeTable("SELECT '/*' AS note FROM audit_log WHERE message = '*/'"))
                .isEqualTo("SELECT '/*' AS note FROM audit_log_r WHERE message = '*/'");
    }

    @Test
    void writesOriginalLiteralValueToTheRoutedTable() throws SQLException {
        DynamicTableNameInnerInterceptor interceptor = new DynamicTableNameInnerInterceptor(
                (sql, name) -> "quoted_audit".equals(name) ? "quoted_audit_2026" : name);
        try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:dynamic_table_quoted", "sa", "");
                Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE quoted_audit_2026 (id INT PRIMARY KEY, message VARCHAR(100))");
            statement.execute("INSERT INTO quoted_audit_2026 VALUES (1, 'initial')");
            assertThat(
                    statement.executeUpdate(
                            interceptor.changeTable(
                                    "UPDATE quoted_audit SET message = 'moved from quoted_audit to archive' WHERE id = 1")))
                    .isEqualTo(1);
            try (ResultSet result = statement.executeQuery("SELECT message FROM quoted_audit_2026 WHERE id = 1")) {
                assertThat(result.next()).isTrue();
                assertThat(result.getString(1)).isEqualTo("moved from quoted_audit to archive");
            }
            try (ResultSet result = statement.executeQuery(
                    interceptor
                            .changeTable("SELECT '-- diagnostic' AS note, message FROM quoted_audit WHERE id = 1"))) {
                assertThat(result.next()).isTrue();
                assertThat(result.getString(1)).isEqualTo("-- diagnostic");
                assertThat(result.getString(2)).isEqualTo("moved from quoted_audit to archive");
            }
        }
    }

    /**
     * 测试 SQL 中的动态表名替换
     */
    @Test
    @SuppressWarnings({"SqlDialectInspection", "SqlNoDataSourceInspection"})
    void doIt() {
        DynamicTableNameInnerInterceptor interceptor = new DynamicTableNameInnerInterceptor();
        interceptor.setTableNameHandler((sql, tableName) -> tableName + "_r");

        // 表名相互包含
        String origin = "SELECT * FROM t_user, t_user_role";
        assertThat(interceptor.changeTable(origin)).isEqualTo("SELECT * FROM t_user_r, t_user_role_r");

        // 表名在末尾
        origin = "SELECT * FROM t_user";
        assertThat(interceptor.changeTable(origin)).isEqualTo("SELECT * FROM t_user_r");

        // 表名前后有注释
        origin = "SELECT * FROM /**/t_user/* t_user */";
        assertThat(interceptor.changeTable(origin)).isEqualTo("SELECT * FROM /**/t_user_r/* t_user */");

        // 值中带有表名
        origin = "SELECT * FROM t_user WHERE name = 't_user'";
        assertThat(interceptor.changeTable(origin)).isEqualTo("SELECT * FROM t_user_r WHERE name = 't_user'");

        // 别名被声明要替换
        origin = "SELECT t_user.* FROM t_user_real t_user";
        assertThat(interceptor.changeTable(origin)).isEqualTo("SELECT t_user.* FROM t_user_real_r t_user");

        // 别名被声明要替换
        origin = "SELECT t.* FROM t_user_real t left join entity e on e.id = t.id";
        assertThat(interceptor.changeTable(origin))
                .isEqualTo("SELECT t.* FROM t_user_real_r t left join entity_r e on e.id = t.id");
    }
}
