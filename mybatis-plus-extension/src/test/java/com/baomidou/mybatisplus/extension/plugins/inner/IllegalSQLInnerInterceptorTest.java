package com.baomidou.mybatisplus.extension.plugins.inner;

import org.junit.jupiter.api.Test;

/**
 * @author miemie
 * @since 2022-04-11
 */
class IllegalSQLInnerInterceptorTest {
    private final IllegalSQLInnerInterceptor interceptor = new IllegalSQLInnerInterceptor();

    @Test
    void test() {
        interceptor.parserSingle("SELECT COUNT(*) AS total FROM t_user WHERE (client_id = ?)", null);
    }
}
