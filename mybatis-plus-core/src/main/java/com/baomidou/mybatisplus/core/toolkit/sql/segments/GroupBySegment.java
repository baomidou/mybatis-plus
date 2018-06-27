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

import static com.baomidou.mybatisplus.core.enums.SqlKeyword.GROUP_BY;
import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.ISqlSegment;

/**
 * @author miemie
 * @since 2018-06-27
 */
public class GroupBySegment extends ArrayList<ISqlSegment> implements ISqlSegment {

    private static final long serialVersionUID = -4135938724727477310L;

    @Override
    public boolean addAll(Collection<? extends ISqlSegment> c) {
        List<ISqlSegment> list = new ArrayList<>(c);
        list.remove(0);
        return super.addAll(list);
    }

    @Override
    public String getSqlSegment() {
        if (isEmpty()) {
            return "";
        }
        return this.stream().map(ISqlSegment::getSqlSegment).collect(joining(",",
            " " + GROUP_BY.getSqlSegment() + " ", ""));
    }
}
