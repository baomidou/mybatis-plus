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

import java.util.Objects;

import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.TableInfoHelper;

/**
 * <p>
 * 条件构造抽象类
 * </p>
 *
 * @author hubin
 * @since 2018-05-25
 */
@SuppressWarnings("all")
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
     * 查询条件为空(包含entity)
     */
    public boolean isEmptyOfWhere() {
        return isEmptyOfNormal() && isEmptyOfEntity();
    }

    /**
     * 查询条件不为空(包含entity)
     */
    public boolean nonEmptyOfWhere() {
        return !isEmptyOfWhere();
    }

    /**
     * 查询条件为空(不包含entity)
     */
    public boolean isEmptyOfNormal() {
        return CollectionUtils.isEmpty(getExpression().getNormal());
    }

    /**
     * 查询条件为空(不包含entity)
     */
    public boolean nonEmptyOfNormal() {
        return !isEmptyOfNormal();
    }

    /**
     * 深层实体判断属性
     *
     * @return true 不为空
     */
    public boolean nonEmptyOfEntity() {
        T entity = getEntity();
        if (entity == null) {
            return false;
        }
        TableInfo tableInfo = TableInfoHelper.getTableInfo(entity.getClass());
        if (tableInfo == null) {
            return false;
        }
        if (StringUtils.isNotEmpty(tableInfo.getKeyProperty())) {
            // 有主键
            return Objects.nonNull(ReflectionKit.getMethodValue(entity, tableInfo.getKeyProperty())) &&
                tableInfo.getFieldList().stream().anyMatch(e -> fieldStrategyMatch(entity, e));
        } else {
            // 无主键
            return tableInfo.getFieldList().stream().anyMatch(e -> fieldStrategyMatch(entity, e));
        }
    }

    /**
     * 根据实体FieldStrategy属性来决定判断逻辑
     */
    private boolean fieldStrategyMatch(T entity, TableFieldInfo e) {
        switch (e.getFieldStrategy()) {
            case NOT_NULL:
                return Objects.nonNull(ReflectionKit.getMethodValue(entity, e.getProperty()));
            case IGNORED:
                return true;
            case NOT_EMPTY:
                return StringUtils.checkValNotNull(ReflectionKit.getMethodValue(entity, e.getProperty()));
            default:
                return Objects.nonNull(ReflectionKit.getMethodValue(entity, e.getProperty()));
        }
    }

    /**
     * 深层实体判断属性
     *
     * @return true 为空
     */
    public boolean isEmptyOfEntity() {
        return !nonEmptyOfEntity();
    }
}
