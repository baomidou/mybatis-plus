package com.baomidou.mybatisplus.core;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static com.baomidou.mybatisplus.core.MybatisXMLScriptBuilder.replaceLeadingAndTrailingWhitespace;
import static org.assertj.core.api.Assertions.assertThat;

@Tag("unit")
public class MybatisXMLScriptBuilderTest {

    @Test
    void shouldStripLeadingAndTrailingWhitespaceFromSql() {
        assertThat(replaceLeadingAndTrailingWhitespace("\n    1=1 \n \n\n")).isEqualTo(" 1=1 ");
        assertThat(replaceLeadingAndTrailingWhitespace("\t\t1=1 \n \n\n")).isEqualTo(" 1=1 ");
        assertThat(replaceLeadingAndTrailingWhitespace("    1=1 \n \n\n")).isEqualTo(" 1=1 ");
        assertThat(replaceLeadingAndTrailingWhitespace("1=1 \n \n\n")).isEqualTo("1=1 ");
        assertThat(replaceLeadingAndTrailingWhitespace("\n    1\n=1 \n \n\n")).isEqualTo(" 1\n=1 ");
        assertThat(replaceLeadingAndTrailingWhitespace("1\n=1 \n \n\n")).isEqualTo("1\n=1 ");
    }

}
