package com.baomidou.mybatisplus.core.toolkit;

import static com.baomidou.mybatisplus.core.enums.SqlKeyword.AND;
import static com.baomidou.mybatisplus.core.enums.SqlKeyword.GROUP_BY;
import static com.baomidou.mybatisplus.core.enums.SqlKeyword.NOT;
import static com.baomidou.mybatisplus.core.enums.SqlKeyword.OR;
import static com.baomidou.mybatisplus.core.enums.SqlKeyword.ORDER_BY;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import com.baomidou.mybatisplus.core.conditions.ISqlSegment;

/**
 * @author miemie
 * @since 2018-06-26
 */
public class ISqlSegmentList extends ArrayList<ISqlSegment> {

    private static final long serialVersionUID = 8205969915086959490L;
    //验证策略 只验证 group by
    private final Predicate<ISqlSegment> predicateGroupBy = i -> i == GROUP_BY;
    //验证策略 只验证 order by
    private final Predicate<ISqlSegment> predicateOrderBy = i -> i == ORDER_BY;
    //验证策略 只验证 and
    private final Predicate<ISqlSegment> predicateNot = i -> i == NOT;
    //验证策略 只验证 and
    private final Predicate<ISqlSegment> predicateAnd = i -> i == AND;
    //验证策略 只验证 or
    private final Predicate<ISqlSegment> predicateOr = i -> i == OR;
    //验证策略 全部验证 and 和 or
    private final Predicate<ISqlSegment> predicateAll = i -> i == AND || i == OR;
    //开启优化 and
    private boolean automaticAnd = true;
    //最后一个值
    private ISqlSegment lastValue = null;

    public ISqlSegmentList() {
    }

    public ISqlSegmentList(boolean automaticAnd) {
        this.automaticAnd = automaticAnd;
    }

    @Override
    public boolean addAll(Collection<? extends ISqlSegment> c) {
        if (automaticAnd) {
            ArrayList<? extends ISqlSegment> list = new ArrayList<>(c);
            if (list.size() == 1) {//只有一个元素
                /**
                 * 只有 and() 以及 or() 以及 not() 会进入
                 */
                ISqlSegment sqlSegment = list.get(0);
                if (!match(predicateNot, sqlSegment)) {//不是 not
                    if (isEmpty()) { //sqlSegment是 and 或者 or 并且在第一位,不继续执行
                        return false;
                    }
                    boolean matchLastAnd = match(predicateAnd, lastValue);
                    boolean matchLastOr = match(predicateOr, lastValue);
                    if (matchLastAnd || matchLastOr) {//上次最后一个值是 and 或者 or
                        if (matchLastAnd && match(predicateAnd, sqlSegment)) {
                            return false;
                        } else if (matchLastOr && match(predicateOr, sqlSegment)) {
                            return false;
                        } else {//和上次的不一样
                            removeLast();
                        }
                    }
                }
            } else {//多个元素
                if (!match(predicateAll, lastValue) && !isEmpty()) {
                    add(AND);
                }
            }
            //后置处理
            this.flushLastValue(list);
            return super.addAll(list);
        } else {
            return super.addAll(c);
        }
    }

    private void flushLastValue(List<? extends ISqlSegment> list) {
        lastValue = list.get(list.size() - 1);
    }

    private boolean match(Predicate<ISqlSegment> predicate, ISqlSegment value) {
        return predicate.test(value);
    }

    private void removeLast() {//todo
        remove(size() - 1);
    }
}
