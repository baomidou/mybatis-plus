package com.baomidou.mybatisplus.core.toolkit.sql;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link StringEscape} 是 MySQL 那套字符串转义：{@code \0 ' " \ \n \r Ctrl-Z} 六个字符必须换成反斜杠形式， 其余原样保留。仓库里唯一的调用点是已废弃的
 * {@code StringUtils.quotaMark}，所以它此前没有任何直接用例—— 转义表整个是靠 MySQL 文档"抄"来的，改动只能靠这份清单兜住。
 */
class StringEscapeTest {

    @Test
    void textWithoutSpecialCharactersIsReturnedUntouched() {
        String plain = "abc";
        String escaped = StringEscape.escapeRawString(plain);
        assertThat(escaped).isEqualTo("abc");
        assertThat(escaped).as("不需要转义时不复制字符串").isSameAs(plain);
    }

    @Test
    void quotesAndBackslashArePrefixedWithBackslash() {
        assertThat(StringEscape.escapeRawString("a'b")).isEqualTo("a\\'b");
        assertThat(StringEscape.escapeRawString("a\"b")).isEqualTo("a\\\"b");
        assertThat(StringEscape.escapeRawString("a\\b")).isEqualTo("a\\\\b");
    }

    @Test
    void controlCharactersBecomeTwoCharacterEscapes() {
        assertThat(StringEscape.escapeRawString("a\nb")).isEqualTo("a\\nb");
        assertThat(StringEscape.escapeRawString("a\rb")).isEqualTo("a\\rb");
        assertThat(StringEscape.escapeRawString("a\0b")).isEqualTo("a\\0b");
        assertThat(StringEscape.escapeRawString("a\032b")).isEqualTo("a\\Zb");
    }

    @Test
    void mixedTextEscapesEverySpecialCharacter() {
        assertThat(StringEscape.escapeRawString("it's a \"test\"\n\\")).isEqualTo("it\\'s a \\\"test\\\"\\n\\\\");
    }

    @Test
    void escapeStringWrapsTheResultInSingleQuotes() {
        assertThat(StringEscape.escapeString("abc")).isEqualTo("'abc'");
        assertThat(StringEscape.escapeString("a\\b")).isEqualTo("'a\\\\b'");
        assertThat(StringEscape.escapeString("a\nb")).isEqualTo("'a\\nb'");
    }

    @Test
    void escapeStringStripsAnExistingOuterQuotePair() {
        assertThat(StringEscape.escapeString("'abc'")).isEqualTo("'abc'");
        assertThat(StringEscape.escapeString("'a'b'")).isEqualTo("'a\\'b'");
    }

    @Test
    void loneQuotesAreNotTreatedAsAWrappingPair() {
        assertThat(StringEscape.escapeString("'")).isEqualTo("'\\''");
        assertThat(StringEscape.escapeString("''")).isEqualTo("'\\'\\''");
    }
}
