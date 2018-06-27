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

import static com.baomidou.mybatisplus.core.toolkit.sql.segments.MatchSegment.AND;
import static com.baomidou.mybatisplus.core.toolkit.sql.segments.MatchSegment.AND_OR;
import static com.baomidou.mybatisplus.core.toolkit.sql.segments.MatchSegment.NOT;
import static com.baomidou.mybatisplus.core.toolkit.sql.segments.MatchSegment.OR;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.ISqlSegment;
import com.baomidou.mybatisplus.core.enums.SqlKeyword;

/**
 * @author miemie
 * @since 2018-06-27
 */
public class NormalSegment extends ArrayList<ISqlSegment> implements ISqlSegment {

    private static final long serialVersionUID = -1991374407733611565L;
    /**
     * 最后一个值
     */
    private ISqlSegment lastValue = null;

    @Override
    public boolean addAll(Collection<? extends ISqlSegment> c) {
        List<ISqlSegment> list = new ArrayList<>(c);
        ISqlSegment sqlSegment = list.get(0);
        if (list.size() == 1) {
            /**
             * 只有 and() 以及 or() 以及 not() 会进入
             */
            if (!MatchSegment.match(NOT, sqlSegment)) {
                //不是 not
                if (isEmpty()) {
                    //sqlSegment是 and 或者 or 并且在第一位,不继续执行
                    return false;
                }
                boolean matchLastAnd = MatchSegment.match(AND, lastValue);
                boolean matchLastOr = MatchSegment.match(OR, lastValue);
                if (matchLastAnd || matchLastOr) {
                    //上次最后一个值是 and 或者 or
                    if (matchLastAnd && MatchSegment.match(AND, sqlSegment)) {
                        return false;
                    } else if (matchLastOr && MatchSegment.match(OR, sqlSegment)) {
                        return false;
                    } else {
                        //和上次的不一样
                        removeLast();
                    }
                }
            }
        } else if (!MatchSegment.match(AND_OR, lastValue) && !isEmpty()) {
            add(SqlKeyword.AND);
        }
        //后置处理
        this.flushLastValue(list);
        return super.addAll(list);
    }

    private void flushLastValue(List<? extends ISqlSegment> list) {
        lastValue = list.get(list.size() - 1);
    }

    private void removeLast() {//todo
        remove(size() - 1);
    }

    @Override
    public String getSqlSegment() {
        return this.stream().map(ISqlSegment::getSqlSegment).collect(Collectors.joining(" "));
    }
}
