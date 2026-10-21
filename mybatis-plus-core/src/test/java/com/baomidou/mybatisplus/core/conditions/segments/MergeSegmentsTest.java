package com.baomidou.mybatisplus.core.conditions.segments;

import com.baomidou.mybatisplus.core.conditions.ISqlSegment;
import com.baomidou.mybatisplus.core.enums.SqlKeyword;
import com.baomidou.mybatisplus.core.enums.WrapperKeyword;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link MergeSegments} 是 wrapper 拼 WHERE 的装配台：条件按 {@code [列, 操作符, 占位符]} 三段进来， {@code NormalSegmentList} 负责补 AND、吃掉悬空的
 * and/or、把上一步 {@code not()} 落到下一个条件上； {@code MergeSegments.add} 再按首个关键字把 group by / having / order by 分流到各自的列表。
 * 这套规则此前只有 h2 集成测试间接经过，没有一条直接用例。下面钉的是现状行为， 包括 {@code having()} 单独使用时整条被丢掉这个坑（等 issue 立项后再把断言改成期望行为）。
 */
class MergeSegmentsTest {

    @Test
    void conditionsAreJoinedByAnImplicitAnd() {
        MergeSegments segments = new MergeSegments();
        segments.add(seg("name"), SqlKeyword.EQ, seg("v1"));
        segments.add(seg("age"), SqlKeyword.EQ, seg("v2"));
        assertThat(segments.getSqlSegment()).isEqualTo("(name = v1 AND age = v2)");
        assertThat(segments.getNormal()).hasSize(7);
    }

    @Test
    void explicitOrSuppressesTheImplicitAnd() {
        MergeSegments segments = new MergeSegments();
        segments.add(seg("name"), SqlKeyword.EQ, seg("v1"));
        segments.add(SqlKeyword.OR);
        segments.add(seg("age"), SqlKeyword.EQ, seg("v2"));
        assertThat(segments.getSqlSegment()).isEqualTo("(name = v1 OR age = v2)");
        assertThat(segments.getNormal()).hasSize(7);
    }

    @Test
    void conjunctionWithoutPrecedingConditionIsDropped() {
        MergeSegments segments = new MergeSegments();
        segments.add(SqlKeyword.AND);
        segments.add(SqlKeyword.OR);
        assertThat(segments.getNormal()).isEmpty();
        assertThat(segments.getSqlSegment()).isEmpty();
    }

    @Test
    void switchingThePendingConjunctionReplacesIt() {
        MergeSegments segments = new MergeSegments();
        segments.add(seg("name"), SqlKeyword.EQ, seg("v1"));
        segments.add(SqlKeyword.OR);
        segments.add(SqlKeyword.AND);
        assertThat(segments.getNormal()).hasSize(4);
        segments.add(seg("age"), SqlKeyword.EQ, seg("v2"));
        assertThat(segments.getSqlSegment()).isEqualTo("(name = v1 AND age = v2)");
    }

    @Test
    void repeatedConjunctionIsKeptOnlyOnce() {
        MergeSegments segments = new MergeSegments();
        segments.add(seg("name"), SqlKeyword.EQ, seg("v1"));
        segments.add(SqlKeyword.AND);
        segments.add(SqlKeyword.AND);
        segments.add(seg("age"), SqlKeyword.EQ, seg("v2"));
        assertThat(segments.getSqlSegment()).isEqualTo("(name = v1 AND age = v2)");
        assertThat(segments.getNormal()).hasSize(7);
    }

    @Test
    void trailingConjunctionIsTrimmedWhenRendering() {
        MergeSegments segments = new MergeSegments();
        segments.add(seg("name"), SqlKeyword.EQ, seg("v1"));
        segments.add(SqlKeyword.AND);
        assertThat(segments.getNormal()).hasSize(4);
        assertThat(segments.getSqlSegment()).isEqualTo("(name = v1)");
    }

    @Test
    void pendingNotIsMaterializedIntoTheNextCondition() {
        MergeSegments segments = new MergeSegments();
        segments.add(SqlKeyword.NOT);
        segments.add(seg("name"), SqlKeyword.EQ, seg("v1"));
        assertThat(segments.getSqlSegment()).isEqualTo("(NOT name = v1)");
        assertThat(segments.getNormal()).hasSize(4);
    }

    @Test
    void consecutiveNotIsMaterializedOnlyOnce() {
        MergeSegments segments = new MergeSegments();
        segments.add(SqlKeyword.NOT);
        segments.add(SqlKeyword.NOT);
        segments.add(seg("name"), SqlKeyword.EQ, seg("v1"));
        assertThat(segments.getSqlSegment()).isEqualTo("(NOT name = v1)");
        assertThat(segments.getNormal()).hasSize(4);
    }

    @Test
    void notAfterAConditionStillGetsAnAndInFront() {
        MergeSegments segments = new MergeSegments();
        segments.add(seg("name"), SqlKeyword.EQ, seg("v1"));
        segments.add(SqlKeyword.NOT);
        segments.add(seg("age"), SqlKeyword.EQ, seg("v2"));
        assertThat(segments.getSqlSegment()).isEqualTo("(name = v1 AND NOT age = v2)");
        assertThat(segments.getNormal()).hasSize(8);
    }

    @Test
    void applyMarkerKeepsOnlyItsRawText() {
        MergeSegments segments = new MergeSegments();
        segments.add(WrapperKeyword.APPLY, seg("1 = 1"));
        segments.add(seg("name"), SqlKeyword.EQ, seg("v1"));
        assertThat(segments.getNormal()).hasSize(5);
        assertThat(segments.getSqlSegment()).isEqualTo("(1 = 1 AND name = v1)");
    }

    @Test
    void clauseKeywordsRouteIntoTheirOwnListsAndRenderAfterTheWhere() {
        MergeSegments segments = new MergeSegments();
        segments.add(seg("name"), SqlKeyword.EQ, seg("v1"));
        segments.add(SqlKeyword.ORDER_BY, seg("age"), SqlKeyword.ASC);
        segments.add(SqlKeyword.GROUP_BY, seg("dept"));
        segments.add(SqlKeyword.HAVING, seg("cnt"), SqlKeyword.GT, seg("1"));
        assertThat(segments.getNormal()).hasSize(3);
        assertThat(segments.getOrderBy()).hasSize(1);
        assertThat(segments.getGroupBy()).hasSize(1);
        assertThat(segments.getHaving()).hasSize(3);
        assertThat(segments.getSqlSegment()).isEqualTo("(name = v1) GROUP BY dept HAVING cnt > 1 ORDER BY age ASC");
    }

    @Test
    void repeatedClauseKeywordsAppendWithComma() {
        MergeSegments segments = new MergeSegments();
        segments.add(SqlKeyword.ORDER_BY, seg("age"), SqlKeyword.ASC);
        segments.add(SqlKeyword.ORDER_BY, seg("name"), SqlKeyword.DESC);
        segments.add(SqlKeyword.GROUP_BY, seg("dept"));
        segments.add(SqlKeyword.GROUP_BY, seg("name"));
        assertThat(segments.getSqlSegment()).isEqualTo(" GROUP BY dept,name ORDER BY age ASC,name DESC");
    }

    @Test
    void havingWithoutGroupByOrOrderByIsSilentlyDropped() {
        MergeSegments segments = new MergeSegments();
        segments.add(SqlKeyword.HAVING, seg("cnt"), SqlKeyword.GT, seg("1"));
        assertThat(segments.getHaving()).hasSize(3);
        assertThat(segments.getSqlSegment()).isEmpty();
        segments.add(SqlKeyword.GROUP_BY, seg("dept"));
        assertThat(segments.getSqlSegment()).isEqualTo(" GROUP BY dept HAVING cnt > 1");
    }

    @Test
    void cachedTextFollowsTheParamAliasRewrite() {
        MergeSegments segments = new MergeSegments();
        segments.add(seg("id"), SqlKeyword.EQ, seg("#{ew.paramNameValuePairs.MPGENVAL1}"));
        assertThat(segments.getSqlSegment()).isEqualTo("(id = #{ew.paramNameValuePairs.MPGENVAL1})");
        segments.changeParamAlias("ew", "param1");
        assertThat(segments.getSqlSegment()).isEqualTo("(id = #{param1.paramNameValuePairs.MPGENVAL1})");
        assertThat(segments.getNormal().getSqlSegment()).isEqualTo("(id = #{param1.paramNameValuePairs.MPGENVAL1})");
    }

    @Test
    void clearEmptiesEveryListAndRestartsCaching() {
        MergeSegments segments = new MergeSegments();
        segments.add(seg("name"), SqlKeyword.EQ, seg("v1"));
        segments.add(SqlKeyword.GROUP_BY, seg("dept"));
        segments.getSqlSegment();
        segments.clear();
        assertThat(segments.getNormal()).isEmpty();
        assertThat(segments.getGroupBy()).isEmpty();
        assertThat(segments.getSqlSegment()).isEmpty();
        segments.add(seg("age"), SqlKeyword.EQ, seg("v2"));
        assertThat(segments.getSqlSegment()).isEqualTo("(age = v2)");
    }

    private static ISqlSegment seg(String text) {
        return () -> text;
    }
}
