package com.baomidou.mybatisplus.core.plugins;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author miemie
 * @since 2020-08-02
 */
class InterceptorIgnoreHelperTest {

    @BeforeEach
    void beforeEach() {
        init(Xx.class);
        init(Pp.class);
        init(Gg.class);
    }

    @Test
    void m1() {
        checkTenantLine(Xx.class, "gg", true);
        checkTenantLine(Xx.class, "hh", false);
        checkTenantLine(Xx.class, "mm", true);

        checkTenantLine(Pp.class, "pp", false);
        checkTenantLine(Pp.class, "dd", true);
        checkTenantLine(Pp.class, "mj", false);

        checkTenantLine(Gg.class, "uu", true);

        checkOthers(Xx.class, "gg", "loli", false);
        checkOthers(Xx.class, "gg", "mn", false);
        checkOthers(Xx.class, "hh", "loli", true);
        checkOthers(Xx.class, "hh", "mn", true);

        checkOthers(Pp.class, "pp", "loli", true);
        checkOthers(Pp.class, "dd", "loli", false);

        // 不存在的
        checkOthers(Pp.class, "mj", "xxxxx", false);
    }

    private void init(Class<?> clazz) {
        InterceptorIgnoreHelper.InterceptorIgnoreCache cache = InterceptorIgnoreHelper.initSqlParserInfoCache(clazz);
        for (Method method : clazz.getMethods()) {
            InterceptorIgnoreHelper.initSqlParserInfoCache(cache, clazz.getName(), method);
        }
    }

    private void checkTenantLine(Class<?> clazz, String method, boolean mustTrue) {
        boolean result = InterceptorIgnoreHelper.willIgnoreTenantLine(clazz.getName().concat(".").concat(method));
        if (mustTrue) {
            assertThat(result).isTrue();
        } else {
            assertThat(result).isFalse();
        }
    }

    private void checkOthers(Class<?> clazz, String method, String key, boolean mustTrue) {
        boolean result = InterceptorIgnoreHelper.willIgnoreOthersByKey(clazz.getName().concat(".").concat(method), key);
        if (mustTrue) {
            assertThat(result).isTrue();
        } else {
            assertThat(result).isFalse();
        }
    }

    @InterceptorIgnore(tenantLine = "on", others = {"loli@1", "mn@1"})
    interface Xx {

        @InterceptorIgnore(others = {"loli@0", "mn@0"})
        void gg();

        @InterceptorIgnore(tenantLine = "off")
        void hh();

        @InterceptorIgnore(illegalSql = "off")
        void mm();

        void ds();
    }

    interface Pp {

        @InterceptorIgnore(tenantLine = "0", others = "loli@1")
        void pp();

        @InterceptorIgnore(tenantLine = "1")
        void dd();

        @InterceptorIgnore
        void mj();
    }

    @InterceptorIgnore(tenantLine = "1")
    interface Gg {

        void uu();
    }
}
