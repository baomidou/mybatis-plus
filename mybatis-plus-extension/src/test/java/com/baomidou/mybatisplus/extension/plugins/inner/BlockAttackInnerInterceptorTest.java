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
    void update() {
        check("update user set name = null", "null where");
        check("update user set name = null where 1=1", "1=1");
        check("update user set name = null where 1<>2", "1<>2");
        check("update user set name = null where 1!=2", "1!=2");
//        check("update user set name = null where 1=1 and 2=2", "1=1 and 2=2");
    }

    @Test
    void delete() {
        check("delete from user", "null where");
        check("delete from user where 1=1", "1=1");
        check("delete from user where 1<>2", "1<>2");
        check("delete from user where 1!=2", "1!=2");
//        check("delete from user where 1=1 and 2=2", "1=1 and 2=2");
    }

    void check(String sql, String as) {
        Exception e = null;
        try {
            interceptor.parserSingle(sql, null);
        } catch (Exception x) {
            e = x;
        }
        assertThat(e).as(as).isNotNull();
        assertThat(e).as(as).isInstanceOf(MybatisPlusException.class);
    }
}
