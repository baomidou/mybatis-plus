package com.baomidou.mybatisplus.extension.plugins.inner;

import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author miemie
 * @since 2020-08-18
 */
class BlockAttackInnerInterceptorTest {

    private final BlockAttackInnerInterceptor interceptor = new BlockAttackInnerInterceptor();

    @Test
    void test() {
        Exception e = null;
        try {
            interceptor.parserSingle("update set name = null", null);
        } catch (Exception x) {
            e = x;
        }
        assertThat(e).isNotNull();
        assertThat(e).isInstanceOf(MybatisPlusException.class);
    }
}
