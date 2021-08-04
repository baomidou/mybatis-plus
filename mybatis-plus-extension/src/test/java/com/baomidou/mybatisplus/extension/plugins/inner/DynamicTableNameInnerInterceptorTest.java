package com.baomidou.mybatisplus.extension.plugins.inner;

import com.baomidou.mybatisplus.extension.plugins.handler.TableNameHandler;
import com.baomidou.mybatisplus.extension.plugins.provider.TableNameHandlerProvider;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

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
        interceptor.setTableNameHandlerProvider(newTableNameHandlerProvider());
        // 表名相互包含
        @Language("SQL")
        String origin = "SELECT * FROM t_user, t_user_role", replaced = "SELECT * FROM t_user_r, t_user_role";
        assertEquals(replaced, interceptor.changeTable(origin));
        // 表名在末尾
        origin = "SELECT * FROM t_user";
        replaced = "SELECT * FROM t_user_r";
        assertEquals(replaced, interceptor.changeTable(origin));
        // 表名前后有注释
        origin = "SELECT * FROM /**/t_user/* t_user */";
        replaced = "SELECT * FROM /**/t_user_r/* t_user */";
        assertEquals(replaced, interceptor.changeTable(origin));
        // 值中带有表名
        origin = "SELECT * FROM t_user WHERE name = 't_user'";
        replaced = "SELECT * FROM t_user_r WHERE name = 't_user'";
        assertEquals(replaced, interceptor.changeTable(origin));
        // 别名被声明要替换
        origin = "SELECT t_user.* FROM t_user_real t_user";
        assertEquals(origin, interceptor.changeTable(origin));

        DynamicTableNameInnerInterceptor sameInterceptor = new DynamicTableNameInnerInterceptor();
        sameInterceptor.setTableNameHandlerProvider(newSameTableNameHandlerProvider());
        // 表名相互包含
        @Language("SQL")
        String originSql = "SELECT * FROM t_user, t_user_role", replacedSql = "SELECT * FROM some_str.t_user, some_str.t_user_role";
        assertEquals(replacedSql, sameInterceptor.changeTable(originSql));
        // 表名在末尾
        originSql = "SELECT * FROM t_user";
        replacedSql = "SELECT * FROM some_str.t_user";
        assertEquals(replacedSql, sameInterceptor.changeTable(originSql));
        // 表名前后有注释
        originSql = "SELECT * FROM /**/t_user/* t_user */";
        replacedSql = "SELECT * FROM /**/some_str.t_user/* t_user */";
        assertEquals(replacedSql, sameInterceptor.changeTable(originSql));
        // 值中带有表名
        originSql = "SELECT * FROM t_user WHERE name = 't_user'";
        replacedSql = "SELECT * FROM some_str.t_user WHERE name = 't_user'";
        assertEquals(replacedSql, sameInterceptor.changeTable(originSql));
        // 别名被声明要替换
        originSql = "SELECT t_user.* FROM t_user_real t_user";
        replacedSql = "SELECT t_user.* FROM some_str.t_user_real t_user";
        assertEquals(replacedSql, sameInterceptor.changeTable(originSql));

    }


    /**
     * 获取表名处理器的提供者
     * 不同表名不同处理器
     * @return TableNameHandlerProvider
     */
    private static TableNameHandlerProvider newTableNameHandlerProvider() {
        return new TableNameHandlerProvider() {
            private Map<String, TableNameHandler> tableNameHandlerMap;

            private void assertMap() {
                if (tableNameHandlerMap == null || tableNameHandlerMap.isEmpty()) {
                    tableNameHandlerMap = newTableNameHandlerMap();
                }
            }

            @Override
            public TableNameHandler get(String value) {
                assertMap();
                return tableNameHandlerMap.get(value);
            }
        };
    }


    /**
     * 获取表名处理器的提供者
     * 所有表名相同处理方式
     * @return TableNameHandlerProvider
     */
    private static TableNameHandlerProvider newSameTableNameHandlerProvider() {
        return value -> {
            String schemaNameOrSomeFixedModifier = "some_str.";
            return (sql, name) -> schemaNameOrSomeFixedModifier + value;
        };
    }

    /**
     * 替换以下表名：
     * t_user -> t_user_r
     *
     * @return 表名处理表
     */
    @NotNull
    private static Map<String, TableNameHandler> newTableNameHandlerMap() {
        Map<String, TableNameHandler> map = new HashMap<>();
        map.put("t_user", (sql, name) -> "t_user_r");
        return map;
    }

}
