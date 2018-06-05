package com.baomidou.mybatisplus.mapper;

import com.baomidou.mybatisplus.core.metadata.Column;
import com.baomidou.mybatisplus.core.metadata.Columns;
import com.baomidou.mybatisplus.core.toolkit.ArrayUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlUtils;

/**
 * 请尽快修改该类的引用 mp 3.1 将删除该类
 */
@Deprecated
@SuppressWarnings("serial")
public class EntityWrapper<T> extends Wrapper<T> {

    /**
     * 数据库表映射实体类
     */
    protected T entity = null;
    /**
     * SQL 查询字段内容，例如：id,name,age
     */
    protected String sqlSelect = null;

    public EntityWrapper() {
        /* 注意，传入查询参数 */
    }

    public EntityWrapper(T entity) {
        this.entity = entity;
    }

    public EntityWrapper(T entity, String sqlSelect) {
        this.entity = entity;
        this.sqlSelect = sqlSelect;
    }

    @Override
    public T getEntity() {
        return entity;
    }

    public void setEntity(T entity) {
        this.entity = entity;
    }

    @Override
    public String getSqlSelect() {
        return StringUtils.isEmpty(sqlSelect) ? null : SqlUtils.stripSqlInjection(sqlSelect);
    }

    public Wrapper<T> setSqlSelect(String sqlSelect) {
        if (StringUtils.isNotEmpty(sqlSelect)) {
            this.sqlSelect = sqlSelect;
        }
        return this;
    }

    /**
     * <p>
     * 使用字符串数组封装sqlSelect，便于在不需要指定 AS 的情况下通过实体类自动生成的列静态字段快速组装 sqlSelect，<br/>
     * 减少手动录入的错误率
     * </p>
     *
     * @param columns 字段
     * @return
     */
    public Wrapper<T> setSqlSelect(String... columns) {
        StringBuilder builder = new StringBuilder();
        for (String column : columns) {
            if (StringUtils.isNotEmpty(column)) {
                if (builder.length() > 0) {
                    builder.append(",");
                }
                builder.append(column);
            }
        }
        this.sqlSelect = builder.toString();
        return this;
    }

    /**
     * <p>
     * 使用对象封装的setsqlselect
     * </p>
     *
     * @param column 字段
     * @return
     */
    public Wrapper<T> setSqlSelect(Column... column) {
        if (ArrayUtils.isNotEmpty(column)) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < column.length; i++) {
                if (column[i] != null) {
                    String col = column[i].getColumn();
                    String as = column[i].getAs();
                    if (StringUtils.isEmpty(col)) {
                        continue;
                    }
                    builder.append(col).append(as);
                    if (i < column.length - 1) {
                        builder.append(",");
                    }
                }
            }
            this.sqlSelect = builder.toString();
        }
        return this;
    }

    /**
     * <p>
     * 使用对象封装的setsqlselect
     * </p>
     *
     * @param columns 字段
     * @return
     */
    public Wrapper<T> setSqlSelect(Columns columns) {
        Column[] columnArray = columns.getColumns();
        if (ArrayUtils.isNotEmpty(columnArray)) {
            setSqlSelect(columnArray);
        }
        return this;
    }

    /**
     * SQL 片段
     */
    @Override
    public String getSqlSegment() {
        /*
         * 无条件
         */
        String sqlWhere = sql.toString();
        if (StringUtils.isEmpty(sqlWhere)) {
            return null;
        }

        /*
         * 根据当前实体判断是否需要将WHERE替换成 AND 增加实体不为空但所有属性为空的情况
         */
        return isWhere != null ? (isWhere ? sqlWhere : sqlWhere.replaceFirst("WHERE", AND_OR)) : sqlWhere.replaceFirst("WHERE", AND_OR);
    }

}
