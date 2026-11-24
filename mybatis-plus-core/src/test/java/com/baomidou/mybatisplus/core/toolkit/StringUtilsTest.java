package com.baomidou.mybatisplus.core.toolkit;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author HCL <br>
 *         Create at 2018/9/17
 */

@Tag("unit")
class StringUtilsTest {

    @Test
    void camelToUnderlineTest() {
        String s = "userId";
        assertThat(StringUtils.camelToUnderline(s)).isEqualTo("user_id");
        assertThat(StringUtils.camelToUnderline("A")).isEqualTo("a");
    }

    @Test
    void isCapitalModeTest() {
        assertThat(StringUtils.isCapitalMode("test")).isFalse();
        assertThat(StringUtils.isCapitalMode("Test")).isFalse();
        assertThat(StringUtils.isCapitalMode("teSt")).isFalse();
        assertThat(StringUtils.isCapitalMode("TEST")).isTrue();
    }

    /**
     * 取列名
     */
    @Test
    void getTargetColumn() {
        assertThat(StringUtils.getTargetColumn("order")).isEqualTo("order");
        assertThat(StringUtils.getTargetColumn("`order`")).isEqualTo("order");
        assertThat(StringUtils.getTargetColumn("'order'")).isEqualTo("order");
    }

    /**
     * 测试equals方法
     */
    @Test
    void equal() {
        assertThat(StringUtils.equals(null, null)).isTrue();
        assertThat(StringUtils.equals(null, "abc")).isFalse();
        assertThat(StringUtils.equals("abc", null)).isFalse();
        assertThat(StringUtils.equals("abc", "abc")).isTrue();
        assertThat(StringUtils.equals("abc", "ABC")).isFalse();
    }

    /**
     * 以下三例自聚合模块 {@code test.toolkit.StringUtilsTest} 并入：同一个 StringUtils 不该有两处测试。 原副本用 {@code Assert.isTrue}
     * 当断言，等于拿另一个待测工具类验证结果，这里换成 AssertJ 直接比对。
     */
    @Test
    void shouldTreatNullEmptyAndWhitespaceAsBlank() {
        assertThat(StringUtils.isBlank("")).isTrue();
        assertThat(StringUtils.isBlank(null)).isTrue();
        assertThat(StringUtils.isBlank("   ")).isTrue();
    }

    @Test
    void shouldStripInjectionPayload() {
        String originalStr = "(select*from(select sleep(if(length(database())=13,3,0))union/**/select+1)a)";
        assertThat(StringUtils.sqlInjectionReplaceBlank(originalStr))
                .isEqualTo("(selectfrom(selectsleep(if(length(database())13,3,0))union//select1)a)");
    }

    @Test
    void shouldStripEveryInjectionCharacter() {
        assertThat(StringUtils.sqlInjectionReplaceBlank("'\"<>&*+=#-;    \n\t")).isEmpty();
    }
}
