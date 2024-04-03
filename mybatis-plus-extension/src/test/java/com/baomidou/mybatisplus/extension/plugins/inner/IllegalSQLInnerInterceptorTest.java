package com.baomidou.mybatisplus.extension.plugins.inner;

import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author miemie
 * @since 2022-04-11
 */
class IllegalSQLInnerInterceptorTest {

    private final IllegalSQLInnerInterceptor interceptor = new IllegalSQLInnerInterceptor();

    @Test
    void test() {
        Assertions.assertDoesNotThrow(() -> interceptor.parserSingle("SELECT COUNT(*) AS total FROM t_user WHERE (client_id = ?)", null));
        Assertions.assertThrows(MybatisPlusException.class, () -> interceptor.parserSingle("select * from t_user", null));
        Assertions.assertThrows(MybatisPlusException.class, () -> interceptor.parserSingle("update t_user set age = 18", null));
        Assertions.assertThrows(MybatisPlusException.class, () -> interceptor.parserSingle("delete from t_user set age = 18", null));
        Assertions.assertThrows(MybatisPlusException.class, () -> interceptor.parserSingle("select * from t_user where age != 1", null));
        Assertions.assertThrows(MybatisPlusException.class, () -> interceptor.parserSingle("select * from t_user where age = 1 or name = 'test'", null));
    }

}
