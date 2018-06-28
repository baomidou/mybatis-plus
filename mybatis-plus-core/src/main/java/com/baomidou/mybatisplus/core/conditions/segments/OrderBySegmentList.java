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

import static com.baomidou.mybatisplus.core.enums.SqlKeyword.ORDER_BY;
import static java.util.stream.Collectors.joining;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.ISqlSegment;

/**
 * <p>
 * Order By SQL 片段
 * </p>
 *
 * @author miemie
 * @since 2018-06-27
 */
@SuppressWarnings("serial")
public class OrderBySegmentList extends AbstractISegmentList {

    @Override
    protected boolean transformList(List<ISqlSegment> list, ISqlSegment firstSegment) {
        list.remove(0);
        if (!isEmpty()) {
            super.add(() -> ",");
        }
        return true;
    }

    @Override
    public String getSqlSegment() {
        if (isEmpty()) {
            return "";
        }
        return this.stream().map(ISqlSegment::getSqlSegment).collect(joining(" ",
            " " + ORDER_BY.getSqlSegment() + " ", ""));
    }
}
