/**
 * Copyright (c) 2011-2014, hubin (jobob@qq.com).
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
package com.baomidou.mybatisplus.mapper;

import com.baomidou.mybatisplus.toolkit.StringUtils;

/**
 * 查询条件
 *
 * @author hubin
 * @Date 2016-08-18
 */
public class Condition<T> {
    /**
     * 根据实体类查询
     */
    private T whereEntity = null;
    /**
     * 需要查询的参数
     */
    private String sqlSelect = null;
    /**
     * 附加Sql片段,实现自定义查询
     */
    private String sqlSegment = null;
    /**
     * <p>
     * SQL 排序 ORDER BY 字段，例如： id DESC（根据id倒序查询）
     * </p>
     * <p>
     * DESC 表示按倒序排序(即：从大到小排序)<br>
     * ASC 表示按正序排序(即：从小到大排序)
     * </p>
     */
    private String orderByField = null;
    /**
     * 是否为升序 ASC（ 默认： true ）
     */
    private boolean isAsc = true;
    /**
     * 封装EntityWrapper
     */
    private EntityWrapper<T> entityWrapper = null;

    public T getWhereEntity() {
        return whereEntity;
    }

    public void setWhereEntity(T whereEntity) {
        this.whereEntity = whereEntity;
    }

    public String getSqlSelect() {
        return sqlSelect;
    }

    public void setSqlSelect(String sqlSelect) {
        if (StringUtils.isNotEmpty(sqlSelect)) {
            this.sqlSelect = sqlSelect;
        }
    }

    public String getSqlSegment() {
        return sqlSegment;
    }

    public void setSqlSegment(String sqlSegment) {
        if (StringUtils.isNotEmpty(sqlSegment)) {
            this.sqlSegment = sqlSegment;
        }
    }

    public String getOrderByField() {
        return orderByField;
    }

    public void setOrderByField(String orderByField) {
        if (StringUtils.isNotEmpty(orderByField)) {
            this.orderByField = orderByField;
        }
    }

    public boolean isAsc() {
        return isAsc;
    }

    public void setAsc(boolean asc) {
        isAsc = asc;
    }

    public EntityWrapper<T> getEntityWrapper() {
        return new EntityWrapper<T>(whereEntity, sqlSelect, convertSqlSegment(sqlSegment, orderByField, isAsc));
    }

    public void setEntityWrapper(EntityWrapper<T> entityWrapper) {
        this.entityWrapper = entityWrapper;
    }

    /**
     * 转换 SQL 片段 + 排序
     */
    private String convertSqlSegment(String sqlSegment, String orderByField, boolean isAsc) {
        StringBuffer segment = new StringBuffer();
        if (StringUtils.isNotEmpty(sqlSegment)) {
            segment.append(sqlSegment);
        } else {
            segment.append(" 1=1 ");
        }
        if (StringUtils.isNotEmpty(orderByField)) {
            segment.append(" ORDER BY ").append(orderByField);
            if (!isAsc) {
                segment.append(" DESC");
            }
        }
        return segment.toString();
    }

}
