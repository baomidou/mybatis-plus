package com.baomidou.mybatisplus.test.extension.plugins.inner;

import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 常量恒等式绕过全表更新/删除校验的回归测试
 *
 * @author hubin
 * @since 3.5.18
 */
class BlockAttackInnerInterceptorTautologyTest {

    private final BlockAttackInnerInterceptor interceptor = new BlockAttackInnerInterceptor();

    @Test
    void xor() {
        checkEx("update records set status = 2 where 1 xor 0");
        checkEx("update records set status = 2 where 1 XOR 0");
        checkEx("update records set status = 2 where 0 xor 1");
        checkEx("update records set status = 2 where 1 xor 1 = 0");
        checkEx("update records set status = 2 where 1 = 1 xor 1 = 0");
        checkEx("update records set status = 2 where 1 = 1 and 1 xor 0");
        checkEx("delete from records where 1 xor 0");
        checkEx("delete from records where (1 xor 0) xor 0");
        checkNotEx("delete from records where (1 xor 0) xor 1");
    }

    @Test
    void not() {
        checkEx("update records set status = 2 where not 1 = 2");
        checkEx("update records set status = 2 where not (1 <> 1)");
        checkEx("delete from records where not (1 = 2)");
        checkNotEx("update records set status = 2 where not 1 = 1");
        checkNotEx("update records set status = 2 where not (1 <> 2)");
    }

    @Test
    void comparison() {
        checkEx("update records set status = 2 where 1 <= 1");
        checkEx("update records set status = 2 where 2 > 1");
        checkEx("update records set status = 2 where 1.0 = 1");
        checkEx("update records set status = 2 where 'a' = 'a'");
        checkEx("delete from records where 1 = 1");
        checkNotEx("update records set status = 2 where 1 = 2");
        checkNotEx("update records set status = 2 where 1 > 2");
        checkNotEx("update records set status = 2 where 'a' = 'b'");
    }

    @Test
    void constant() {
        checkEx("update records set status = 2 where 1");
        checkEx("update records set status = 2 where 1 and 1 = 1");
        checkEx("delete from records where 1");
        checkNotEx("update records set status = 2 where 0");
    }

    @Test
    void normalWhere() {
        checkNotEx("update records set status = 2 where id = 1");
        checkNotEx("update records set status = 2 where id = ?");
        checkNotEx("update records set status = 2 where status = 0");
        checkNotEx("update records set status = 2 where status > 0 and name = 'x'");
        checkNotEx("update records set status = 2 where status = 1 xor archived = 1");
        checkNotEx("update records set status = 2 where id in (1, 2)");
        checkNotEx("update records set status = 2 where status is null");
        checkNotEx("update records set status = 2 where name like 'a%'");
        checkNotEx("delete from records where id = 1");
    }

    void checkEx(String sql) {
        assertThatThrownBy(() -> interceptor.parserMulti(sql, null)).as(sql).isInstanceOf(MybatisPlusException.class);
    }

    void checkNotEx(String sql) {
        assertThat(interceptor.parserMulti(sql, null)).as(sql).isNotBlank();
    }
}
