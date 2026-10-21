package com.baomidou.mybatisplus.test;

import com.baomidou.mybatisplus.extension.toolkit.SqlParserUtils;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SqlParserUtilsTest {

    @Test
    void testRemoveWrapperSymbol() {
        //用SQLServer的人喜欢写这种
        assertThat(SqlParserUtils.removeWrapperSymbol("[Demo]")).isEqualTo("Demo");
        assertThat(SqlParserUtils.removeWrapperSymbol("Demo")).isEqualTo("Demo");
        //mysql比较常见
        assertThat(SqlParserUtils.removeWrapperSymbol("`Demo`")).isEqualTo("Demo");
        //用关键字表的
        assertThat(SqlParserUtils.removeWrapperSymbol("\"Demo\"")).isEqualTo("Demo");
        //这种少
        assertThat(SqlParserUtils.removeWrapperSymbol("<Demo>")).isEqualTo("Demo");
    }

}
