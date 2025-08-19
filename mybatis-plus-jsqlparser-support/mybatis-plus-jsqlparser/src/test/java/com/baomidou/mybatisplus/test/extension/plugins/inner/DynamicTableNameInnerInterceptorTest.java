package com.baomidou.mybatisplus.test.extension.plugins.inner;

import com.baomidou.mybatisplus.extension.plugins.handler.TableNameHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 动态表名内部拦截器测试
 *
 * @author miemie, hcl
 * @since 2020-07-16
 */
class DynamicTableNameInnerInterceptorTest {

    /**
     * 测试 SQL 中的动态表名替换
     */
    @Test
    @SuppressWarnings({"SqlDialectInspection", "SqlNoDataSourceInspection"})
    void doIt() {
        DynamicTableNameInnerInterceptor interceptor = new DynamicTableNameInnerInterceptor();
        TableNameHandler testHandler = (sql, tableName) -> tableName + "_r";
        interceptor.setTableNameHandler(testHandler);

        // 表名相互包含
        String origin = "SELECT * FROM t_user, t_user_role";
        assertEquals("SELECT * FROM t_user_r, t_user_role_r", interceptor.changeTable(origin, testHandler));

        // 表名在末尾
        origin = "SELECT * FROM t_user";
        assertEquals("SELECT * FROM t_user_r", interceptor.changeTable(origin, testHandler));

        // 表名前后有注释
        origin = "SELECT * FROM /**/t_user/* t_user */";
        assertEquals("SELECT * FROM /**/t_user_r/* t_user */", interceptor.changeTable(origin, testHandler));

        // 值中带有表名
        origin = "SELECT * FROM t_user WHERE name = 't_user'";
        assertEquals("SELECT * FROM t_user_r WHERE name = 't_user'", interceptor.changeTable(origin, testHandler));

        // 别名被声明要替换
        origin = "SELECT t_user.* FROM t_user_real t_user";
        assertEquals("SELECT t_user.* FROM t_user_real_r t_user", interceptor.changeTable(origin, testHandler));

        // 别名被声明要替换
        origin = "SELECT t.* FROM t_user_real t left join entity e on e.id = t.id";
        assertEquals("SELECT t.* FROM t_user_real_r t left join entity_r e on e.id = t.id", interceptor.changeTable(origin, testHandler));
    }
}
