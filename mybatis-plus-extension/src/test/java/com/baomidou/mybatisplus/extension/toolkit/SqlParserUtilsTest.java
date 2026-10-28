package com.baomidou.mybatisplus.extension.toolkit;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("unit")
public class SqlParserUtilsTest {

    @Test
    void shouldRemoveWrapperSymbolsFromSqlIdentifiers() {
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
