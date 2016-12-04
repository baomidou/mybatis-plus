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
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import com.baomidou.mybatisplus.enums.SQLlikeType;
import com.baomidou.mybatisplus.toolkit.MapUtils;
import com.baomidou.mybatisplus.toolkit.StringUtils;


/**
 * <p>
 * 条件构造抽象类，定义T-SQL语法
 * </p>
 *
 * @author hubin , yanghu , Dyang , Caratacus
 * @Date 2016-11-7
 */
@SuppressWarnings("serial")
public abstract class Wrapper<T> implements Serializable {

    /**
     * SQL 查询字段内容，例如：id,name,age
     */
    protected String sqlSelect = null;

    /**
     * 实现了TSQL语法的SQL实体
     */
    protected SqlPlus sql = new SqlPlus();

    /**
     * 兼容EntityWrapper
     *
     * @return
     */
    public T getEntity() {
        return null;
    }

    public String getSqlSelect() {
        if (StringUtils.isEmpty(sqlSelect)) {
            return null;
        }
        return stripSqlInjection(sqlSelect);
    }

    public Wrapper<T> setSqlSelect(String sqlSelect) {
        if (StringUtils.isNotEmpty(sqlSelect)) {
            this.sqlSelect = sqlSelect;
        }
        return this;
    }

    /**
     * SQL 片段 (子类实现)
     */
    public abstract String getSqlSegment();

    public String toString() {
        return getSqlSegment();
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
     * @param sqlWhere
     *            where语句
     * @param params
     *            参数集
     * @return this
     */
    public Wrapper<T> where(String sqlWhere, Object... params) {
        sql.WHERE(formatSql(sqlWhere, params));
        return this;
    }

    /**
     * <p>
     * 等同于SQL的"field=value"表达式
     * </p>
     *
     * @param column
     * @param params
     * @return
     */
    public Wrapper<T> eq(String column, Object params) {
        sql.WHERE(formatSql(String.format("%s = {0}", column), params));
        return this;
    }

    /**
     * <p>
     * 等同于SQL的"field=value"表达式
     * </p>
     *
     * @param params
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Wrapper<T> allEq(Map<String, Object> params) {
        if (MapUtils.isNotEmpty(params)) {
			Iterator iterator = params.entrySet().iterator();
            while (iterator.hasNext()) {
				Map.Entry<String,Object> entry = (Map.Entry<String,Object>) iterator.next();
                Object value = entry.getValue();
                if(StringUtils.checkValNotNull(value)){
                    sql.WHERE(formatSql(String.format("%s = {0}", entry.getKey()), entry.getValue()));
                }

            }

        }
        return this;
    }

    /**
     * <p>
     * 等同于SQL的"field>value"表达式
     * </p>
     *
     * @param column
     * @param params
     * @return
     */
    public Wrapper<T> gt(String column, Object params) {
        sql.WHERE(formatSql(String.format("%s > {0}", column), params));
        return this;
    }

    /**
     * <p>
     * 等同于SQL的"field>=value"表达式
     * </p>
     *
     * @param column
     * @param params
     * @return
     */
    public Wrapper<T> ge(String column, Object params) {
        sql.WHERE(formatSql(String.format("%s >= {0}", column), params));
        return this;
    }

    /**
     * <p>
     * 等同于SQL的"field<value"表达式
     * </p>
     *
     * @param column
     * @param params
     * @return
     */
    public Wrapper<T> lt(String column, Object params) {
        sql.WHERE(formatSql(String.format("%s < {0}", column), params));
        return this;
    }

    /**
     * <p>
     * 等同于SQL的"field<=value"表达式
     * </p>
     *
     * @param column
     * @param params
     * @return
     */
    public Wrapper<T> le(String column, Object params) {
        sql.WHERE(formatSql(String.format("%s <= {0}", column), params));
        return this;
    }

    /**
     * <p>
     * AND 连接后续条件
     * </p>
     *
     * @param sqlAnd
     *            and条件语句
     * @param params
     *            参数集
     * @return this
     */
    public Wrapper<T> and(String sqlAnd, Object... params) {
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
     * @param sqlAnd
     *            AND 条件语句
     * @param params
     *            参数值
     * @return this
     */
    public Wrapper<T> andNew(String sqlAnd, Object... params) {
        sql.AND_NEW().WHERE(formatSql(sqlAnd, params));
        return this;
    }

    /**
     * <p>
     * 添加OR条件
     * </p>
     *
     * @param sqlOr
     *            or 条件语句
     * @param params
     *            参数集
     * @return this
     */
    public Wrapper<T> or(String sqlOr, Object... params) {
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
     * @param sqlOr
     *            AND 条件语句
     * @param params
     *            参数值
     * @return this
     */
    public Wrapper<T> orNew(String sqlOr, Object... params) {
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
     * @param columns
     *            SQL 中的 Group by 语句，无需输入 Group By 关键字
     * @return this
     */
    public Wrapper<T> groupBy(String columns) {
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
     * @param sqlHaving
     *            having关键字后面跟随的语句
     * @param params
     *            参数集
     * @return EntityWrapper<T>
     */
    public Wrapper<T> having(String sqlHaving, Object... params) {
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
     * @param columns
     *            SQL 中的 order by 语句，无需输入 Order By 关键字
     * @return this
     */
    public Wrapper<T> orderBy(String columns) {
        sql.ORDER_BY(columns);
        return this;
    }

    /**
     * <p>
     * SQL中orderby关键字跟的条件语句，可根据变更动态排序
     * </p>
     *
     * @param columns
     *            SQL 中的 order by 语句，无需输入 Order By 关键字
     * @param isAsc
     *            是否为升序
     * @return this
     */
    public Wrapper<T> orderBy(String columns, boolean isAsc) {
        if (StringUtils.isNotEmpty(columns)) {
            sql.ORDER_BY(columns + (isAsc ? " ASC" : " DESC"));
        }
        return this;
    }

    /**
     * LIKE条件语句，value中无需前后%
     *
     * @param column
     *            字段名称
     * @param value
     *            匹配值
     * @return this
     */
    public Wrapper<T> like(String column, String value) {
        sql.LIKE(column, value,SQLlikeType.DEFAULT);
        return this;
    }

    /**
     * NOT LIKE条件语句，value中无需前后%
     *
     * @param column
     *            字段名称
     * @param value
     *            匹配值
     * @return this
     */
    public Wrapper<T> notLike(String column, String value) {
        sql.NOT_LIKE(column, value,SQLlikeType.DEFAULT);
        return this;
    }
    /**
     * LIKE条件语句，value中无需前后%
     *
     * @param column
     *            字段名称
     * @param value
     *            匹配值
     * @param type
     * @return this
     */
    public Wrapper<T> like(String column, String value, SQLlikeType type) {
        sql.LIKE(column, value,type);
        return this;
    }

    /**
     * NOT LIKE条件语句，value中无需前后%
     *
     * @param column
     *            字段名称
     * @param value
     *            匹配值
     * @param type
     * @return this
     */
    public Wrapper<T> notLike(String column, String value, SQLlikeType type) {
        sql.NOT_LIKE(column, value,type);
        return this;
    }

    /**
     * is not null 条件
     *
     * @param columns
     *            字段名称。多个字段以逗号分隔。
     * @return this
     */
    public Wrapper<T> isNotNull(String columns) {
        sql.IS_NOT_NULL(columns);
        return this;
    }

    /**
     * is not null 条件
     *
     * @param columns
     *            字段名称。多个字段以逗号分隔。
     * @return this
     */
    public Wrapper<T> isNull(String columns) {
        sql.IS_NULL(columns);
        return this;
    }

    /**
     * EXISTS 条件语句，目前适配mysql及oracle
     *
     * @param value
     *            匹配值
     * @return this
     */
    public Wrapper<T> exists(String value) {
        sql.EXISTS(value);
        return this;
    }

    /**
     * NOT EXISTS条件语句
     *
     * @param value
     *            匹配值
     * @return this
     */
    public Wrapper<T> notExists(String value) {
        sql.NOT_EXISTS(value);
        return this;
    }

    /**
     * IN 条件语句，目前适配mysql及oracle
     *
     * @param column
     *            字段名称
     * @param value
     *            逗号拼接的字符串
     * @return this
     */
    public Wrapper<T> in(String column, String value) {
        sql.IN(column, value);
        return this;
    }

    /**
     * NOT IN条件语句
     *
     * @param column
     *            字段名称
     * @param value
     *            逗号拼接的字符串
     * @return this
     */
    public Wrapper<T> notIn(String column, String value) {
        sql.NOT_IN(column, value);
        return this;
    }

    /**
     * IN 条件语句，目前适配mysql及oracle
     *
     * @param column
     *            字段名称
     * @param value
     *            匹配值 List集合
     * @return this
     */
    public Wrapper<T> in(String column, Collection<?> value) {
        sql.IN(column, value);
        return this;
    }

    /**
     * NOT IN 条件语句，目前适配mysql及oracle
     *
     * @param column
     *            字段名称
     * @param value
     *            匹配值 List集合
     * @return this
     */
    public Wrapper<T> notIn(String column, Collection<?> value) {
        sql.NOT_IN(column, value);
        return this;
    }

    /**
     * IN 条件语句，目前适配mysql及oracle
     *
     * @param column
     *            字段名称
     * @param value
     *            匹配值 object数组
     * @return this
     */
    public Wrapper<T> in(String column, Object[] value) {
        sql.IN(column, Arrays.asList(value));
        return this;
    }

    /**
     * NOT IN 条件语句，目前适配mysql及oracle
     *
     * @param column
     *            字段名称
     * @param value
     *            匹配值 object数组
     * @return this
     */
    public Wrapper<T> notIn(String column, Object... value) {
        sql.NOT_IN(column, Arrays.asList(value));
        return this;
    }

    /**
     * betwwee 条件语句
     *
     * @param column
     *            字段名称
     * @param val1
     * @param val2
     * @return this
     */
    public Wrapper<T> between(String column, String val1, String val2) {
        sql.BETWEEN_AND(column, val1, val2);
        return this;
    }

    /**
     * 为了兼容之前的版本,可使用where()或and()替代
     *
     * @param sqlWhere
     *            where sql部分
     * @param params
     *            参数集
     * @return this
     */
    public Wrapper<T> addFilter(String sqlWhere, Object... params) {
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
     * @param need
     *            是否需要添加该条件
     * @param sqlWhere
     *            条件语句
     * @param params
     *            参数集
     * @return this
     */
    public Wrapper<T> addFilterIfNeed(boolean need, String sqlWhere, Object... params) {
        return need ? where(sqlWhere, params) : this;
    }

    /**
     * <p>
     * SQL注入内容剥离
     * </p>
     *
     * @param value
     *            待处理内容
     * @return this
     */
    protected String stripSqlInjection(String value) {
        return value.replaceAll("('.+--)|(--)|(\\|)|(%7C)", "");
    }

    /**
     * <p>
     * 格式化SQL
     * </p>
     *
     * @param sqlStr
     *            SQL语句部分
     * @param params
     *            参数集
     * @return this
     */
    protected String formatSql(String sqlStr, Object... params) {
        return formatSqlIfNeed(true, sqlStr, params);
    }

    /**
     * <p>
     * 根据需要格式化SQL
     * </p>
     *
     * @param need
     *            是否需要格式化
     * @param sqlStr
     *            SQL语句部分
     * @param params
     *            参数集
     * @return this
     */
    protected String formatSqlIfNeed(boolean need, String sqlStr, Object... params) {
        if (!need || StringUtils.isEmpty(sqlStr)) {
            return null;
        }
        return StringUtils.sqlArgsFill(sqlStr, params);
    }

}
