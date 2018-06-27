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
package com.baomidou.mybatisplus.core.toolkit.sql.segments;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import com.baomidou.mybatisplus.core.conditions.ISqlSegment;
import com.baomidou.mybatisplus.core.enums.SqlKeyword;

/**
 * @author miemie
 * @since 2018-06-27
 */
public class JoinSegment implements ISqlSegment {

    private static final long serialVersionUID = 8401728865419013555L;

    private NormalSegment normalSegment = new NormalSegment();
    private GroupBySegment groupBySegment = new GroupBySegment();
    private OrderBySegment orderBySegment = new OrderBySegment();

    public void add(ISqlSegment... iSqlSegments) {
        List<ISqlSegment> list = Arrays.asList(iSqlSegments);
        ISqlSegment sqlSegment = list.get(0);
        if (match(PredicateStrategy.ORDER_BY, sqlSegment)) {
            orderBySegment.addAll(list);
        } else if (match(PredicateStrategy.GROUP_BY, sqlSegment)) {
            groupBySegment.addAll(list);
        } else {
            normalSegment.addAll(list);
        }
    }

    private boolean match(PredicateStrategy predicateStrategy, ISqlSegment value) {
        return predicateStrategy.getPredicate().test(value);
    }

    @Override
    public String getSqlSegment() {
        return normalSegment.getSqlSegment() + groupBySegment.getSqlSegment() + orderBySegment.getSqlSegment();
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
