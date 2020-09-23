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
        checkEx("update user set name = null", "null where");
        checkEx("update user set name = null where 1=1", "1=1");
        checkEx("update user set name = null where 1<>2", "1<>2");
        checkEx("update user set name = null where 1!=2", "1!=2");
        checkEx("update user set name = null where 1=1 and 2=2", "1=1 and 2=2");
        checkEx("update user set name = null where 1=1 and 2=3 or 1=1", "1=1 and 2=3 or 1=1");
        checkEx("update user set name = null where 1=1 and (2=2)", "1=1 and (2=2)");
        checkEx("update user set name = null where (1=1 and 2=2)", "(1=1 and 2=2)");
        checkEx("update user set name = null where (1=1 and (2=3 or 3=3))", "(1=1 and (2=3 or 3=3))");

        checkNotEx("update user set name = null where 1=?", "1=?");
    }

    @Test
    void delete() {
        checkEx("delete from user", "null where");
        checkEx("delete from user where 1=1", "1=1");
        checkEx("delete from user where 1<>2", "1<>2");
        checkEx("delete from user where 1!=2", "1!=2");
        checkEx("delete from user where 1=1 and 2=2", "1=1 and 2=2");
        checkEx("delete from user where 1=1 and 2=3 or 1=1", "1=1 and 2=3 or 1=1");
    }

    void checkEx(String sql, String as) {
        Exception e = null;
        try {
            interceptor.parserSingle(sql, null);
        } catch (Exception x) {
            e = x;
        }
        assertThat(e).as(as).isNotNull();
        assertThat(e).as(as).isInstanceOf(MybatisPlusException.class);
    }

    void checkNotEx(String sql, String as) {
        Exception e = null;
        try {
            interceptor.parserSingle(sql, null);
        } catch (Exception x) {
            e = x;
        }
        assertThat(e).as(as).isNull();
    }
}
