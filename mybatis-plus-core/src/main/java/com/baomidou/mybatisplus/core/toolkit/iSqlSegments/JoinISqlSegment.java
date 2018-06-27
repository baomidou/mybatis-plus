package com.baomidou.mybatisplus.core.toolkit.iSqlSegments;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import com.baomidou.mybatisplus.core.conditions.ISqlSegment;
import com.baomidou.mybatisplus.core.enums.SqlKeyword;

/**
 * @author miemie
 * @since 2018-06-27
 */
public class JoinISqlSegment implements ISqlSegment {

    private static final long serialVersionUID = 8401728865419013555L;

    private NormalISqlSegment normalISqlSegment = new NormalISqlSegment();
    private GroupByISqlSegment groupByISqlSegment = new GroupByISqlSegment();
    private OrderByISqlSegment orderByISqlSegment = new OrderByISqlSegment();

    public void add(ISqlSegment... iSqlSegments) {
        List<ISqlSegment> list = Arrays.asList(iSqlSegments);
        ISqlSegment sqlSegment = list.get(0);
        if (match(PredicateStrategy.ORDER_BY, sqlSegment)) {
            orderByISqlSegment.addAll(list);
        } else if (match(PredicateStrategy.GROUP_BY, sqlSegment)) {
            groupByISqlSegment.addAll(list);
        } else {
            normalISqlSegment.addAll(list);
        }
    }

    private boolean match(PredicateStrategy predicateStrategy, ISqlSegment value) {
        return predicateStrategy.getPredicate().test(value);
    }

    @Override
    public String getSqlSegment() {
        return normalISqlSegment.getSqlSegment() + groupByISqlSegment.getSqlSegment() + orderByISqlSegment.getSqlSegment();
    }

    /**
     * 验证策略
     */
    private enum PredicateStrategy {
        GROUP_BY(i -> i == SqlKeyword.GROUP_BY),
        ORDER_BY(i -> i == SqlKeyword.ORDER_BY),
        NOT(i -> i == SqlKeyword.NOT),
        AND(i -> i == SqlKeyword.AND),
        OR(i -> i == SqlKeyword.OR),
        AND_OR(i -> i == SqlKeyword.AND || i == SqlKeyword.OR);

        private Predicate<ISqlSegment> predicate;

        PredicateStrategy(Predicate<ISqlSegment> predicate) {
            this.predicate = predicate;
        }

        public Predicate<ISqlSegment> getPredicate() {
            return predicate;
        }
    }
}
