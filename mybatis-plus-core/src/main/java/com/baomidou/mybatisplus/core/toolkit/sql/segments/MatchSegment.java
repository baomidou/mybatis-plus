package com.baomidou.mybatisplus.core.toolkit.sql.segments;

import java.util.function.Predicate;

import com.baomidou.mybatisplus.core.conditions.ISqlSegment;
import com.baomidou.mybatisplus.core.enums.SqlKeyword;

/**
 * @author miemie
 * @since 2018-06-27
 */
enum MatchSegment {
    GROUP_BY(i -> i == SqlKeyword.GROUP_BY),
    ORDER_BY(i -> i == SqlKeyword.ORDER_BY),
    NOT(i -> i == SqlKeyword.NOT),
    AND(i -> i == SqlKeyword.AND),
    OR(i -> i == SqlKeyword.OR),
    AND_OR(i -> i == SqlKeyword.AND || i == SqlKeyword.OR);

    private final Predicate<ISqlSegment> predicate;

    MatchSegment(Predicate<ISqlSegment> predicate) {
        this.predicate = predicate;
    }

    protected static boolean match(MatchSegment matchSegment, ISqlSegment segment) {
        return matchSegment.getPredicate().test(segment);
    }

    protected Predicate<ISqlSegment> getPredicate() {
        return predicate;
    }
}
