package com.baomidou.mybatisplus.annotation;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.MissingFormatArgumentException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 这些常量会被 {@code @TableField(condition = ...)} 引用，core 拿它做<br>
 * {@code String.format(condition, 列名, 参数占位符)}，再把结果拼进动态 SQL 脚本片段。 因此占位符元数、百分号转义、以及“运算符要写成 XML 实体”都是必须钉住的公共契约。
 *
 * @author taoyu
 */
class SqlConditionTest {

    @Test
    void eachConditionTakesColumnAndParameter() {
        assertThat(format(SqlCondition.EQUAL)).isEqualTo("name=#{param}");
        assertThat(format(SqlCondition.LIKE)).isEqualTo("name LIKE CONCAT('%',#{param},'%')");
        assertThat(format(SqlCondition.ORACLE_LIKE)).isEqualTo("name LIKE CONCAT(CONCAT('%',#{param}),'%')");
        assertThat(format(SqlCondition.LIKE_LEFT)).isEqualTo("name LIKE CONCAT('%',#{param})");
        assertThat(format(SqlCondition.LIKE_RIGHT)).isEqualTo("name LIKE CONCAT(#{param},'%')");
    }

    @Test
    void likeFamilyDoublesTheWildcardSoFormatSurvives() {
        assertThat(SqlCondition.LIKE).as("字面百分号必须写成 %%，否则会被当成格式符").contains("%%");
        assertThat(format(SqlCondition.LIKE)).as("格式化后剩单个百分号").doesNotContain("%%");
        for (String condition : conditions()) {
            assertThat(format(condition)).as("拼出来的片段不该留下没吃到的占位符: %s", condition).doesNotContain("%s");
        }
    }

    @Test
    void everyConditionHasExactlyTwoPlaceholders() {
        for (String condition : conditions()) {
            assertThat(condition.split("%s", -1)).as("core 固定传两个参数: %s", condition).hasSize(3);
            assertThatThrownBy(() -> String.format(condition, "name")).as("少传一个参数必须立刻报错: %s", condition)
                    .isInstanceOf(MissingFormatArgumentException.class);
        }
    }

    @Test
    void notEqualKeepsTheXmlEntityForTheOperator() {
        assertThat(SqlCondition.NOT_EQUAL).as("写成 &lt;&gt; 不是笔误：这段串会进动态 SQL 脚本，裸 <> 会让 XML 解析失败")
                .isEqualTo("%s&lt;&gt;#{%s}");
        assertThat(SqlCondition.NOT_EQUAL).doesNotContain("<>");
        assertThat(format(SqlCondition.NOT_EQUAL)).as("格式化不会替我们解码实体，解码发生在 MyBatis 解析 XML 时")
                .isEqualTo("name&lt;&gt;#{param}");
    }

    private static List<String> conditions() {
        return List.of(
                SqlCondition.EQUAL, SqlCondition.NOT_EQUAL, SqlCondition.LIKE, SqlCondition.ORACLE_LIKE,
                SqlCondition.LIKE_LEFT, SqlCondition.LIKE_RIGHT);
    }

    private static String format(String condition) {
        return String.format(condition, "name", "param");
    }

}
