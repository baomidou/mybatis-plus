/*
 * Copyright (c) 2011-2020, baomidou (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.core.conditions.segments;

import com.baomidou.mybatisplus.core.conditions.ISqlSegment;
import com.baomidou.mybatisplus.core.enums.SqlKeyword;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 普通片段
 *
 * @author miemie
 * @since 2018-06-27
 */
@SuppressWarnings("serial")
public class NormalSegmentList extends AbstractISegmentList {

    /**
     * 是否处理了的上个 not
     */
    private boolean executeNot = true;

    NormalSegmentList() {
        this.flushLastValue = true;
    }

    @Override
    protected boolean transformList(List<ISqlSegment> list, ISqlSegment firstSegment, ISqlSegment lastSegment) {
        if (list.size() == 1) {
            /* 只有 and() 以及 or() 以及 not() 会进入 */
            if (!MatchSegment.NOT.match(firstSegment)) {
                //不是 not
                if (isEmpty()) {
                    //sqlSegment是 and 或者 or 并且在第一位,不继续执行
                    return false;
                }
                boolean matchLastAnd = MatchSegment.AND.match(lastValue);
                boolean matchLastOr = MatchSegment.OR.match(lastValue);
                if (matchLastAnd || matchLastOr) {
                    //上次最后一个值是 and 或者 or
                    if (matchLastAnd && MatchSegment.AND.match(firstSegment)) {
                        return false;
                    } else if (matchLastOr && MatchSegment.OR.match(firstSegment)) {
                        return false;
                    } else {
                        //和上次的不一样
                        removeAndFlushLast();
                    }
                }
            } else {
                executeNot = false;
                return false;
            }
        } else {
            if (!executeNot) {
                list.add(MatchSegment.EXISTS.match(firstSegment) ? 0 : 1, SqlKeyword.NOT);
                executeNot = true;
            }
            if (MatchSegment.APPLY.match(firstSegment)) {
                list.remove(0);
            }
            if (MatchSegment.BRACKET.match(firstSegment)) {
                list.remove(0);
            }
            if (!MatchSegment.AND_OR.match(lastValue) && !isEmpty()) {
                add(SqlKeyword.AND);
            }
        }
        return true;
    }

    @Override
    protected String childrenSqlSegment() {
        if (MatchSegment.AND_OR.match(lastValue)) {
            removeAndFlushLast();
        }
        final String str = this.stream().map(ISqlSegment::getSqlSegment).collect(Collectors.joining(SPACE));
        return (LEFT_BRACKET + str + RIGHT_BRACKET);
    }

    @Override
    public void clear() {
        super.clear();
        flushLastValue = true;
        executeNot = true;
    }
}
