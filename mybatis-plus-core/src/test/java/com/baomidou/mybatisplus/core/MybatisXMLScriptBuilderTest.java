package com.baomidou.mybatisplus.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import static com.baomidou.mybatisplus.core.MybatisXMLScriptBuilder.replaceLeadingAndTrailingWhitespace;

public class MybatisXMLScriptBuilderTest {

    @Test
    void testReplaceLeadingAndTrailingWhitespace() {
        Assertions.assertEquals(" 1=1 ", replaceLeadingAndTrailingWhitespace("\n    1=1 \n \n\n"));
        Assertions.assertEquals(" 1=1 ", replaceLeadingAndTrailingWhitespace("\t\t1=1 \n \n\n"));
        Assertions.assertEquals(" 1=1 ", replaceLeadingAndTrailingWhitespace("    1=1 \n \n\n"));
        Assertions.assertEquals("1=1 ", replaceLeadingAndTrailingWhitespace("1=1 \n \n\n"));
        Assertions.assertEquals(" 1\n=1 ", replaceLeadingAndTrailingWhitespace("\n    1\n=1 \n \n\n"));
        Assertions.assertEquals("1\n=1 ", replaceLeadingAndTrailingWhitespace("1\n=1 \n \n\n"));
    }

}
