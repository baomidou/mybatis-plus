package com.baomidou.mybatisplus.core.enums;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link SqlKeyword} 的每个值都会被直接拼进最终 SQL：{@code and}/{@code or}/{@code not} 的衔接、 {@code between} 的写法、比较符全靠这串文本。本类把 24
 * 个关键字的渲染文本钉成整表断言， 顺手钉住 {@link WrapperKeyword#APPLY} 那个"只用来辨识、不参与拼接"的空文本约定； 改文本或者新增一个没登记的值都会红。
 */
class SqlKeywordTest {

    private static final Map<SqlKeyword, String> KEYWORD_TEXT = Map.ofEntries(
            Map.entry(SqlKeyword.AND, "AND"), Map.entry(SqlKeyword.OR, "OR"), Map.entry(SqlKeyword.NOT, "NOT"),
            Map.entry(SqlKeyword.IN, "IN"), Map.entry(SqlKeyword.NOT_IN, "NOT IN"), Map.entry(SqlKeyword.LIKE, "LIKE"),
            Map.entry(SqlKeyword.NOT_LIKE, "NOT LIKE"), Map.entry(SqlKeyword.EQ, "="), Map.entry(SqlKeyword.NE, "<>"),
            Map.entry(SqlKeyword.GT, ">"), Map.entry(SqlKeyword.GE, ">="), Map.entry(SqlKeyword.LT, "<"),
            Map.entry(SqlKeyword.LE, "<="), Map.entry(SqlKeyword.IS_NULL, "IS NULL"),
            Map.entry(SqlKeyword.IS_NOT_NULL, "IS NOT NULL"), Map.entry(SqlKeyword.GROUP_BY, "GROUP BY"),
            Map.entry(SqlKeyword.HAVING, "HAVING"), Map.entry(SqlKeyword.ORDER_BY, "ORDER BY"),
            Map.entry(SqlKeyword.EXISTS, "EXISTS"), Map.entry(SqlKeyword.NOT_EXISTS, "NOT EXISTS"),
            Map.entry(SqlKeyword.BETWEEN, "BETWEEN"), Map.entry(SqlKeyword.NOT_BETWEEN, "NOT BETWEEN"),
            Map.entry(SqlKeyword.ASC, "ASC"), Map.entry(SqlKeyword.DESC, "DESC"));

    @Test
    void everyKeywordRendersItsRegisteredText() {
        assertThat(renderedText()).isEqualTo(KEYWORD_TEXT);
    }

    @Test
    void applyMarkerCarriesNoText() {
        assertThat(WrapperKeyword.values()).containsExactly(WrapperKeyword.APPLY);
        assertThat(WrapperKeyword.APPLY.getSqlSegment()).isNull();
    }

    private static Map<SqlKeyword, String> renderedText() {
        Map<SqlKeyword, String> rendered = new EnumMap<>(SqlKeyword.class);
        Arrays.stream(SqlKeyword.values()).forEach(keyword -> rendered.put(keyword, keyword.getSqlSegment()));
        return rendered;
    }
}
