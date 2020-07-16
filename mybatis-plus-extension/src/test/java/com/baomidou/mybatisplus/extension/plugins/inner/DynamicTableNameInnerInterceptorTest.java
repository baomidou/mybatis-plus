package com.baomidou.mybatisplus.extension.plugins.inner;

import com.baomidou.mybatisplus.extension.plugins.handler.TableNameHandler;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 动态表名兰及诶器测试
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
        interceptor.setTableNameHandlerMap(newTableNameHandlerMap());
        // 表名相互包含
        @Language("SQL")
        String origin = "SELECT * FROM t_user, t_user_role", replaced = "SELECT * FROM t_user_r, t_user_role";
        assertEquals(replaced, interceptor.doIt(origin));
        // 表名在末尾
        origin = "SELECT * FROM t_user";
        replaced = "SELECT * FROM t_user_r";
        assertEquals(replaced, interceptor.doIt(origin));
        // 表名前后有注释
        origin = "SELECT * FROM /**/t_user/* t_user */";
        replaced = "SELECT * FROM /**/t_user_r/* t_user */";
        assertEquals(replaced, interceptor.doIt(origin));
        // 值中带有表名
        origin = "SELECT * FROM t_user where name = 't_user'";
        replaced = "SELECT * FROM t_user_r where name = 't_user'";
        assertEquals(replaced, interceptor.doIt(origin));
    }

    @NotNull
    private Map<String, TableNameHandler> newTableNameHandlerMap() {
        Map<String, TableNameHandler> map = new HashMap<>();
        map.put("t_user", (sql, name) -> "t_user_r");
        return map;
    }

}
