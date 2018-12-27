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

import com.baomidou.mybatisplus.core.conditions.ISqlSegment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * <p>
 * SQL 片段集合 处理的抽象类
 * </p>
 *
 * @author miemie
 * @since 2018-06-27
 */
@SuppressWarnings("serial")
public abstract class AbstractISegmentList extends ArrayList<ISqlSegment> implements ISqlSegment {

    /**
     * 最后一个值
     */
    protected ISqlSegment lastValue = null;
    /**
     * 刷新 lastValue
     */
    protected boolean flushLastValue = false;

    @Override
    public boolean addAll(Collection<? extends ISqlSegment> c) {
        List<ISqlSegment> list = new ArrayList<>(c);
        boolean goon = transformList(list, list.get(0));
        if (goon) {
            if (flushLastValue) {
                this.flushLastValue(list);
            }
            return super.addAll(list);
        }
        return false;
    }

    /**
     * <p>
     * 在其中对值进行判断以及更改 list 的内部元素
     * </P>
     *
     * @param list         传入进来的 ISqlSegment 集合
     * @param firstSegment ISqlSegment 集合里第一个值
     * @return true 是否继续向下执行; false 不再向下执行
     */
    protected abstract boolean transformList(List<ISqlSegment> list, ISqlSegment firstSegment);

    /**
     * 刷新属性 lastValue
     */
    protected void flushLastValue(List<? extends ISqlSegment> list) {
        lastValue = list.get(list.size() - 1);
    }

    /**
     * 删除元素里最后一个值
     */
    protected void removeAndFlushLast() {
        remove(size() - 1);
        flushLastValue(this);
    }
}
