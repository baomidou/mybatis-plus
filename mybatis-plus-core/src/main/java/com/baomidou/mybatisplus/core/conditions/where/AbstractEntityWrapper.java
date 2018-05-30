package com.baomidou.mybatisplus.core.conditions.where;

import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlUtils;

/**
 * @author ming
 * @since 2018/5/30
 */
public abstract class AbstractEntityWrapper<T, R, This extends AbstractEntityWrapper<T, R, This>> extends AbstractWrapper<T, R, This> {

    /**
     * 数据库表映射实体类
     */
    protected T entity = null;
    /**
     * SQL 查询字段内容，例如：id,name,age
     */
    protected String sqlSelect = null;

    @Override
    public T getEntity() {
        return entity;
    }

    public This setEntity(T entity) {
        this.entity = entity;
        return typedThis();
    }

    public String getSqlSelect() {
        return StringUtils.isEmpty(sqlSelect) ? null : SqlUtils.stripSqlInjection(sqlSelect);
    }

    public This setSqlSelect(String sqlSelect) {
        if (StringUtils.isNotEmpty(sqlSelect)) {
            this.sqlSelect = sqlSelect;
        }
        return typedThis();
    }
}
