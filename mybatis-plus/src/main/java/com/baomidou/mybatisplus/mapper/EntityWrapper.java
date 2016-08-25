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

import java.io.Serializable;
import java.text.MessageFormat;

import com.baomidou.mybatisplus.toolkit.StringUtils;

/**
 * <p>
 * Entity 对象封装操作类，定义T-SQL语法
 * </p>
 *
 * @author hubin , yanghu , Dyang
 * @Date 2016-03-15
 */
@SuppressWarnings("serial")
public class EntityWrapper<T> implements Serializable {

    /**
     * 数据库表映射实体类
     */
    protected T entity = null;

    /**
     * SQL 查询字段内容，例如：id,name,age
     */
    protected String sqlSelect = null;

    /**
     * 实现了TSQL语法的SQL实体
     */
    protected TSqlPlus sql = new TSqlPlus();

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

    public T getEntity() {
        return entity;
    }

    public void setEntity(T entity) {
        this.entity = entity;
    }

    public String getSqlSelect() {
        if (StringUtils.isEmpty(sqlSelect)) {
            return null;
        }
        return stripSqlInjection(sqlSelect);
    }

    public void setSqlSelect(String sqlSelect) {
        if (StringUtils.isNotEmpty(sqlSelect)) {
            this.sqlSelect = sqlSelect;
        }
    }

    /**
     * SQL 片段
     */
    public String getSqlSegment() {
        /*
         * 无条件
		 */
        String sqlWhere = sql.toString();
        if (StringUtils.isEmpty(sqlWhere)) {
            return null;
        }

        // 根据当前实体判断是否需要将WHERE替换成AND
        sqlWhere = (null != entity) ? sqlWhere.replaceFirst("WHERE", "AND") : sqlWhere;

		/*
         * 使用防SQL注入处理后返回
		 */
        return stripSqlInjection(sqlWhere);
    }

    /**
     * <p>
     * SQL中WHERE关键字跟的条件语句
     * </p>
     * <p>
     * eg: ew.where("name='zhangsan'").where("id={0}","123");
     * <p>
     * 输出: WHERE (NAME='zhangsan' AND id=123)
     * </p>
     *
     * @param sqlWhere where语句
     * @param params   参数集
     * @return this
     */
    public EntityWrapper<T> where(String sqlWhere, Object... params) {
        sql.WHERE(formatSql(sqlWhere, params));
        return this;
    }

    /**
     * <p>
     * AND 连接后续条件
     * </p>
     *
     * @param sqlAnd and条件语句
     * @param params 参数集
     * @return this
     */
    public EntityWrapper<T> and(String sqlAnd, Object... params) {
        sql.AND().WHERE(formatSql(sqlAnd, params));
        return this;
    }

    /**
     * <p>
     * 使用AND连接并换行
     * </p>
     * <p>
     * eg: ew.where("name='zhangsan'").and("id=11").andNew("statu=1"); 输出： WHERE
     * (name='zhangsan' AND id=11) AND (statu=1)
     * </p>
     *
     * @param sqlAnd AND 条件语句
     * @param params 参数值
     * @return this
     */
    public EntityWrapper<T> andNew(String sqlAnd, Object... params) {
        sql.AND_NEW().WHERE(formatSql(sqlAnd, params));
        return this;
    }

    /**
     * <p>
     * 添加OR条件
     * </p>
     *
     * @param sqlOr  or 条件语句
     * @param params 参数集
     * @return this
     */
    public EntityWrapper<T> or(String sqlOr, Object... params) {
        sql.OR().WHERE(formatSql(sqlOr, params));
        return this;
    }

    /**
     * <p>
     * 使用OR换行，并添加一个带()的新的条件
     * </p>
     * <p>
     * eg: ew.where("name='zhangsan'").and("id=11").orNew("statu=1"); 输出： WHERE
     * (name='zhangsan' AND id=11) OR (statu=1)
     * </p>
     *
     * @param sqlOr  AND 条件语句
     * @param params 参数值
     * @return this
     */
    public EntityWrapper<T> orNew(String sqlOr, Object... params) {
        sql.OR_NEW().WHERE(formatSql(sqlOr, params));
        return this;
    }

    /**
     * <p>
     * SQL中groupBy关键字跟的条件语句
     * </p>
     * <p>
     * eg: ew.where("name='zhangsan'").groupBy("id,name")
     * </p>
     *
     * @param columns SQL 中的 Group by 语句，无需输入 Group By 关键字
     * @return this
     */
    public EntityWrapper<T> groupBy(String columns) {
        sql.GROUP_BY(columns);
        return this;
    }

    /**
     * <p>
     * SQL中having关键字跟的条件语句
     * </p>
     * <p>
     * eg: ew.groupBy("id,name").having("id={0}",22).and("password is not null")
     * </p>
     *
     * @param sqlHaving having关键字后面跟随的语句
     * @param params    参数集
     * @return EntityWrapper
     */
    public EntityWrapper<T> having(String sqlHaving, Object... params) {
        sql.HAVING(formatSql(sqlHaving, params));
        return this;
    }

    /**
     * <p>
     * SQL中orderby关键字跟的条件语句
     * </p>
     * <p>
     * eg: ew.groupBy("id,name").having("id={0}",22).and("password is not null"
     * ).orderBy("id,name")
     * </p>
     *
     * @param columns SQL 中的 order by 语句，无需输入 Order By 关键字
     * @return this
     */
    public EntityWrapper<T> orderBy(String columns) {
        sql.ORDER_BY(columns);
        return this;
    }

    /**
     * <p>
     * SQL中orderby关键字跟的条件语句，可根据变更动态排序
     * </p>
     *
     * @param columns SQL 中的 order by 语句，无需输入 Order By 关键字
     * @param isAsc   是否为升序
     * @return this
     */
    public EntityWrapper<T> orderBy(String columns, boolean isAsc) {
        if (StringUtils.isNotEmpty(columns)) {
            sql.ORDER_BY(columns + (isAsc ? " ASC" : " DESC"));
        }
        return this;
    }

    /**
     * LIKE条件语句，value中无需前后% 目前适配mysql及oracle
     *
     * @param column 字段名称
     * @param value  匹配值
     * @return this
     */
    public EntityWrapper<T> like(String column, String value) {
        sql.LIKE(column, value);
        return this;
    }

    /**
     * NOT LIKE条件语句，value中无需前后% 目前适配mysql及oracle
     *
     * @param column 字段名称
     * @param value  匹配值
     * @return this
     */
    public EntityWrapper<T> notLike(String column, String value) {
        sql.NOT_LIKE(column, value);
        return this;
    }

    /**
     * is not null 条件
     *
     * @param columns 字段名称。多个字段以逗号分隔。
     * @return this
     */
    public EntityWrapper<T> isNotNull(String columns) {
        sql.IS_NOT_NULL(columns);
        return this;
    }

    /**
     * is not null 条件
     *
     * @param columns 字段名称。多个字段以逗号分隔。
     * @return this
     */
    public EntityWrapper<T> isNull(String columns) {
        sql.IS_NULL(columns);
        return this;
    }

    /**
     * 为了兼容之前的版本,可使用where()或and()替代
     *
     * @param sqlWhere where sql部分
     * @param params   参数集
     * @return this
     */
    public EntityWrapper<T> addFilter(String sqlWhere, Object... params) {
        return and(sqlWhere, params);
    }

    /**
     * <p>
     * 根据判断条件来添加条件语句部分 使用 andIf() 替代
     * </p>
     * <p>
     * eg: ew.filterIfNeed(false,"name='zhangsan'").where("name='zhangsan'")
     * .filterIfNeed(true,"id={0}",22)
     * <p>
     * 输出: WHERE (name='zhangsan' AND id=22)
     * </p>
     *
     * @param need     是否需要添加该条件
     * @param sqlWhere 条件语句
     * @param params   参数集
     * @return this
     */
    public EntityWrapper<T> addFilterIfNeed(boolean need, String sqlWhere, Object... params) {
        return need ? where(sqlWhere, params) : this;
    }

    /**
     * <p>
     * SQL注入内容剥离
     * </p>
     *
     * @param value 待处理内容
     * @return this
     */
    protected String stripSqlInjection(String value) {
        return value.replaceAll("('.+--)|(--)|(\\|)|(%7C)", "");
    }


    /**
     * 格式化SQL
     *
     * @param sqlStr SQL语句部分
     * @param params 参数集
     * @return this
     */
    protected String formatSql(String sqlStr, Object... params) {
        return formatSqlIfNeed(true, sqlStr, params);
    }

    /**
     * 根据需要格式化SQL
     *
     * @param need   是否需要格式化
     * @param sqlStr SQL语句部分
     * @param params 参数集
     * @return this
     */
    protected String formatSqlIfNeed(boolean need, String sqlStr, Object... params) {
        if (!need || StringUtils.isEmpty(sqlStr)) {
            return null;
        }
        if (null != params && params.length > 0) {
            sqlStr = MessageFormat.format(sqlStr, params);
        }
        return sqlStr;
    }

}
