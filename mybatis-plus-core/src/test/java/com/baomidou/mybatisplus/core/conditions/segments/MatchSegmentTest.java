package com.baomidou.mybatisplus.core.conditions.segments;

import com.baomidou.mybatisplus.core.conditions.ISqlSegment;
import com.baomidou.mybatisplus.core.enums.SqlKeyword;
import com.baomidou.mybatisplus.core.enums.WrapperKeyword;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link MatchSegment} 是 wrapper 拼 SQL 时的分类开关：{@link MergeSegments} 靠它决定片段进哪个列表， {@code NormalSegmentList} 靠它决定要不要自动补
 * AND、上一个 {@code not()} 要不要落到本次条件上。 每个开关认哪些值是一条硬契约——新增一个 {@link SqlKeyword} 却忘了同步判定，运行期只会静默走 normal 分支，
 * 所以这里按整表相等钉死，多认或少认都会红。
 */
class MatchSegmentTest {

    private static final Map<MatchSegment, Set<ISqlSegment>> MATCHES = Map.ofEntries(
            Map.entry(MatchSegment.GROUP_BY, Set.<ISqlSegment>of(SqlKeyword.GROUP_BY)),
            Map.entry(MatchSegment.ORDER_BY, Set.<ISqlSegment>of(SqlKeyword.ORDER_BY)),
            Map.entry(MatchSegment.NOT, Set.<ISqlSegment>of(SqlKeyword.NOT)),
            Map.entry(MatchSegment.AND, Set.<ISqlSegment>of(SqlKeyword.AND)),
            Map.entry(MatchSegment.OR, Set.<ISqlSegment>of(SqlKeyword.OR)),
            Map.entry(MatchSegment.AND_OR, Set.<ISqlSegment>of(SqlKeyword.AND, SqlKeyword.OR)),
            Map.entry(MatchSegment.EXISTS, Set.<ISqlSegment>of(SqlKeyword.EXISTS)),
            Map.entry(MatchSegment.HAVING, Set.<ISqlSegment>of(SqlKeyword.HAVING)),
            Map.entry(MatchSegment.APPLY, Set.<ISqlSegment>of(WrapperKeyword.APPLY)));

    @Test
    void everyMatchSegmentRecognizesExactlyItsOwnKeywords() {
        List<ISqlSegment> universe = new ArrayList<>();
        universe.addAll(Arrays.asList(SqlKeyword.values()));
        universe.addAll(Arrays.asList(WrapperKeyword.values()));
        Map<MatchSegment, Set<ISqlSegment>> actual = new EnumMap<>(MatchSegment.class);
        for (MatchSegment segment : MatchSegment.values()) {
            Set<ISqlSegment> matched = new HashSet<>();
            universe.stream().filter(segment::match).forEach(matched::add);
            actual.put(segment, matched);
        }
        assertThat(actual).isEqualTo(MATCHES);
    }
}
