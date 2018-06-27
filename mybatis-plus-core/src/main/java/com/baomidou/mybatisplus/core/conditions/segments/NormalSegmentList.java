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
package com.baomidou.mybatisplus.core.conditions.segments;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.ISqlSegment;
import com.baomidou.mybatisplus.core.enums.SqlKeyword;

/**
 * <p>
 * 普通片段
 * </p>
 *
 * @author miemie
 * @since 2018-06-27
 */
public class NormalSegmentList extends ArrayList<ISqlSegment> implements ISqlSegment {

    private static final long serialVersionUID = -1991374407733611565L;
    /**
     * 最后一个值
     */
    private ISqlSegment lastValue = null;
    private boolean executeNot = true;

    @Override
    public boolean addAll(Collection<? extends ISqlSegment> c) {
        List<ISqlSegment> list = new ArrayList<>(c);
        ISqlSegment sqlSegment = list.get(0);
        if (list.size() == 1) {
            /**
             * 只有 and() 以及 or() 以及 not() 会进入
             */
            if (!MatchSegment.NOT.match(sqlSegment)) {
                //不是 not
                if (isEmpty()) {
                    //sqlSegment是 and 或者 or 并且在第一位,不继续执行
                    return false;
                }
                boolean matchLastAnd = MatchSegment.AND.match(lastValue);
                boolean matchLastOr = MatchSegment.OR.match(lastValue);
                if (matchLastAnd || matchLastOr) {
                    //上次最后一个值是 and 或者 or
                    if (matchLastAnd && MatchSegment.AND.match(sqlSegment)) {
                        return false;
                    } else if (matchLastOr && MatchSegment.OR.match(sqlSegment)) {
                        return false;
                    } else {
                        //和上次的不一样
                        removeLast();
                    }
                }
            } else {
                executeNot = false;
                return false;
            }
        } else {
            if (!executeNot) {
                list.add(1, SqlKeyword.NOT);
                executeNot = true;
            }
            if (!MatchSegment.AND_OR.match(lastValue) && !isEmpty()) {
                add(SqlKeyword.AND);
            }
        }
        //后置处理
        this.flushLastValue(list);
        return super.addAll(list);
    }

    private void flushLastValue(List<? extends ISqlSegment> list) {
        lastValue = list.get(list.size() - 1);
    }

    private void removeLast() {
        remove(size() - 1);
    }

    @Override
    public String getSqlSegment() {
        if (MatchSegment.AND_OR.match(lastValue)) {
            removeLast();
        }
        return this.stream().map(ISqlSegment::getSqlSegment).collect(Collectors.joining(" "));
    }
}
