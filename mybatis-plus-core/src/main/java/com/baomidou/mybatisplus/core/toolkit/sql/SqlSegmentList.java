/*
 * Copyright (c) 2011-2020, hubin (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.core.toolkit.sql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import com.baomidou.mybatisplus.core.conditions.ISqlSegment;
import com.baomidou.mybatisplus.core.enums.SqlKeyword;

/**
 * <p>
 * ISqlSegment List
 * </p>
 *
 * @author miemie
 * @since 2018-06-26
 */
public class SqlSegmentList extends ArrayList<ISqlSegment> {

    private static final long serialVersionUID = 8205969915086959490L;
    /**
     * 开启优化 and
     */
    private boolean automaticAnd = true;
    /**
     * 最后一个值
     */
    private ISqlSegment lastValue = null;
    /**
     * 是否启动过 order by
     */
    private boolean didOrderBy = false;

    public SqlSegmentList() {
    }

    public SqlSegmentList(boolean automaticAnd) {
        this.automaticAnd = automaticAnd;
    }

    @Override
    public boolean addAll(Collection<? extends ISqlSegment> c) {
        if (automaticAnd) {
            ArrayList<? extends ISqlSegment> list = new ArrayList<>(c);
            ISqlSegment sqlSegment = list.get(0);
            //只有一个元素
            if (list.size() == 1) {
                /**
                 * 只有 and() 以及 or() 以及 not() 会进入
                 */
                if (!match(PredicateStrategy.NOT, sqlSegment)) {
                    //不是 not
                    if (isEmpty()) {
                        //sqlSegment是 and 或者 or 并且在第一位,不继续执行
                        return false;
                    }
                    boolean matchLastAnd = match(PredicateStrategy.AND, lastValue);
                    boolean matchLastOr = match(PredicateStrategy.OR, lastValue);
                    if (matchLastAnd || matchLastOr) {
                        //上次最后一个值是 and 或者 or
                        if (matchLastAnd && match(PredicateStrategy.AND, sqlSegment)) {
                            return false;
                        } else if (matchLastOr && match(PredicateStrategy.OR, sqlSegment)) {
                            return false;
                        } else {
                            //和上次的不一样
                            removeLast();
                        }
                    }
                }
            } else if (!match(PredicateStrategy.AND_OR, lastValue) && !isEmpty()) {
                //多个元素
                if (match(PredicateStrategy.ORDER_BY, sqlSegment)) {
                    //处理 order by
                    if (!didOrderBy) {
                        didOrderBy = true;
                    } else {
                        list.remove(0);
                        super.add(() -> ",");
                    }
                } else {
                    add(SqlKeyword.AND);
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

    private boolean match(PredicateStrategy predicateStrategy, ISqlSegment value) {
        return predicateStrategy.getPredicate().test(value);
    }

    private void removeLast() {//todo
        remove(size() - 1);
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
