/*
 * Copyright (c) 2011-2021, baomidou (jobob@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baomidou.mybatisplus.core.conditions;

import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;
import com.baomidou.mybatisplus.core.conditions.segments.NormalSegmentList;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.*;

import java.util.Objects;

/**
 * 条件构造抽象类
 *
 * @author hubin
 * @since 2018-05-25
 */
@SuppressWarnings("all")
public abstract class Wrapper<T> implements ISqlSegment {

    /**
     * 实体对象（子类实现）
     *
     * @return 泛型 T
     */
    public abstract T getEntity();

    public String getSqlSelect() {
        return null;
    }

    public String getSqlSet() {
        return null;
    }

    public String getSqlComment() {
        return null;
    }

    public String getSqlFirst() {
        return null;
    }

    /**
     * 获取 MergeSegments
     */
    public abstract MergeSegments getExpression();

    /**
     * 获取自定义SQL 简化自定义XML复杂情况
     * <p>
     * 使用方法: `select xxx from table` + ${ew.customSqlSegment}
     * <p>
     * 注意事项:
     * 1. 逻辑删除需要自己拼接条件 (之前自定义也同样)
     * 2. 不支持wrapper中附带实体的情况 (wrapper自带实体会更麻烦)
     * 3. 用法 ${ew.customSqlSegment} (不需要where标签包裹,切记!)
     * 4. ew是wrapper定义别名,不能使用其他的替换
     */
    public String getCustomSqlSegment() {
        MergeSegments expression = getExpression();
        if (Objects.nonNull(expression)) {
            NormalSegmentList normal = expression.getNormal();
            String sqlSegment = getSqlSegment();
            if (StringUtils.isNotBlank(sqlSegment)) {
                if (normal.isEmpty()) {
                    return sqlSegment;
                } else {
                    return Constants.WHERE + StringPool.SPACE + sqlSegment;
                }
            }
        }
        return StringPool.EMPTY;
    }

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
        if (tableInfo.getFieldList().stream().anyMatch(e -> fieldStrategyMatch(entity, e))) {
            return true;
        }
        return StringUtils.isNotBlank(tableInfo.getKeyProperty()) ? Objects.nonNull(ReflectionKit.getFieldValue(entity, tableInfo.getKeyProperty())) : false;
    }

    /**
     * 根据实体FieldStrategy属性来决定判断逻辑
     */
    private boolean fieldStrategyMatch(T entity, TableFieldInfo e) {
        switch (e.getWhereStrategy()) {
            case NOT_NULL:
                return Objects.nonNull(ReflectionKit.getFieldValue(entity, e.getProperty()));
            case IGNORED:
                return true;
            case NOT_EMPTY:
                return StringUtils.checkValNotNull(ReflectionKit.getFieldValue(entity, e.getProperty()));
            case NEVER:
                return false;
            default:
                return Objects.nonNull(ReflectionKit.getFieldValue(entity, e.getProperty()));
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

    /**
     * 获取格式化后的执行sql
     *
     * @return sql
     * @since 3.3.1
     */
    public String getTargetSql() {
        return getSqlSegment().replaceAll("#\\{.+?}", "?");
    }

    /**
     * 条件清空
     *
     * @since 3.3.1
     */
    abstract public void clear();
}
