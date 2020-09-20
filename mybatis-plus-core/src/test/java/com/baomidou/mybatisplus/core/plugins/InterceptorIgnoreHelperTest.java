package com.baomidou.mybatisplus.core.plugins;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author miemie
 * @since 2020-08-02
 */
class InterceptorIgnoreHelperTest {

    @Test
    void m1() {
        InterceptorIgnoreHelper.InterceptorIgnoreCache cache = InterceptorIgnoreHelper.initSqlParserInfoCache(Xx.class);
        for (Method method : Xx.class.getMethods()) {
            InterceptorIgnoreHelper.initSqlParserInfoCache(cache, Xx.class.getName(), method);
        }
        boolean b = InterceptorIgnoreHelper.willIgnoreTenantLine(
            "com.baomidou.mybatisplus.core.plugins.InterceptorIgnoreHelperTest$Xx.gg");
        assertThat(b).isTrue();

        b = InterceptorIgnoreHelper.willIgnoreTenantLine(
            "com.baomidou.mybatisplus.core.plugins.InterceptorIgnoreHelperTest$Xx.hh");
        assertThat(b).isFalse();

        b = InterceptorIgnoreHelper.willIgnoreTenantLine(
            "com.baomidou.mybatisplus.core.plugins.InterceptorIgnoreHelperTest$Xx.mm");
        assertThat(b).isTrue();
    }

    @InterceptorIgnore(tenantLine = "on")
    interface Xx {

        void gg();

        @InterceptorIgnore(tenantLine = "off")
        void hh();

        @InterceptorIgnore(illegalSql = "off")
        void mm();
    }
}
