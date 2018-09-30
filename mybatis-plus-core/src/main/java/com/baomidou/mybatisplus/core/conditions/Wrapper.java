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
package com.baomidou.mybatisplus.core.conditions;

import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.core.toolkit.TableInfoHelper;

import java.util.Objects;

/**
 * <p>
 * 条件构造抽象类
 * </p>
 *
 * @author hubin
 * @since 2018-05-25
 */
@SuppressWarnings("serial")
public abstract class Wrapper<T> implements ISqlSegment {

    /**
     * <p>
     * 实体对象（子类实现）
     * </p>
     *
     * @return 泛型 T
     */
    public abstract T getEntity();

    /**
     * 查询条件 SQL 片段（子类实现）
     */
    public String getSqlSelect() {
        return null;
    }

    /**
     * 更新 SQL 片段（子类实现）
     */
    public String getSqlSet() {
        return null;
    }

    /**
     * 获取 MergeSegments
     */
    public abstract MergeSegments getExpression();

    /**
     * 查询条件为空
     */
    public boolean isEmptyOfWhere() {
        return CollectionUtils.isEmpty(getExpression().getNormal()) && !nonEntityNull();
    }

    /**
     * 深层实体判断属性
     *
     * @return true 不为空
     */
    private boolean nonEntityNull() {
        T entity = getEntity();
        return Objects.nonNull(getEntity()) && TableInfoHelper.getTableInfo(entity.getClass()).getFieldList().stream()
            .anyMatch(e -> Objects.nonNull(ReflectionKit.getMethodValue(entity, e.getProperty())));
    }

    /**
     * 查询条件不为空
     */
    public boolean notEmptyOfWhere() {
        return !isEmptyOfWhere();
    }
}

