package com.baomidou.mybatisplus.test;

import com.baomidou.mybatisplus.extension.toolkit.SqlParserUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SqlParserUtilsTest {

    @Test
    void testRemoveWrapperSymbol() {
        //用SQLServer的人喜欢写这种
        Assertions.assertEquals("Demo", SqlParserUtils.removeWrapperSymbol("[Demo]"));
        Assertions.assertEquals("Demo", SqlParserUtils.removeWrapperSymbol("Demo"));
        //mysql比较常见
        Assertions.assertEquals("Demo", SqlParserUtils.removeWrapperSymbol("`Demo`"));
        //用关键字表的
        Assertions.assertEquals("Demo", SqlParserUtils.removeWrapperSymbol("\"Demo\""));
        //这种少
        Assertions.assertEquals("Demo", SqlParserUtils.removeWrapperSymbol("<Demo>"));
    }

}
