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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.baomidou.mybatisplus.entity.Column;
import com.baomidou.mybatisplus.entity.Columns;
import com.baomidou.mybatisplus.enums.SqlLike;
import com.baomidou.mybatisplus.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.toolkit.ArrayUtils;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.toolkit.MapUtils;
import com.baomidou.mybatisplus.toolkit.SerializationUtils;
import com.baomidou.mybatisplus.toolkit.SqlUtils;
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
     * 占位符
     */
    private static final String PLACE_HOLDER = "{%s}";

    private static final String MYBATIS_PLUS_TOKEN = "#{%s.paramNameValuePairs.%s}";

    private static final String MP_GENERAL_PARAMNAME = "MPGENVAL";

    private static final String DEFAULT_PARAM_ALIAS = "ew";
    /**
     * 实现了TSQL语法的SQL实体
     */
    protected final SqlPlus sql = new SqlPlus();
    private final Map<String, Object> paramNameValuePairs = new HashMap<>();
    private final AtomicInteger paramNameSeq = new AtomicInteger(0);
    protected String paramAlias = null;
    /**
     * SQL 查询字段内容，例如：id,name,age
     */
    protected String sqlSelect = null;
    /**
     * 自定义是否输出sql为 WHERE OR AND OR OR
     */
    protected Boolean isWhere;
    /**
     * 拼接WHERE后应该是AND还是ORnull
     */
    protected String AND_OR = "AND";

    /**
     * <p>
     * 兼容EntityWrapper
     * </p>
     *
     * @return
     */
    public T getEntity() {
        return null;
    }

    /**
     * 查看where构造是否为空
     *
     * @return
     */
    public boolean isEmptyOfWhere() {
        return sql.isEmptyOfWhere();
    }

    /**
     * 查看where构造是否不为空
     *
     * @return
     */
    public boolean isNotEmptyOfWhere() {
        return !isEmptyOfWhere();
    }

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
     * <p>
     * SQL 片段 (子类实现)
     * </p>
     */
    public abstract String getSqlSegment();

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Wrapper<T>:");
        String sqlSegment = getSqlSegment();
        sb.append(replacePlaceholder(sqlSegment));
        Object entity = getEntity();
        if (entity != null) {
            sb.append("\n");
            sb.append("entity=").append(entity.toString());
        }
        return sb.toString();
    }

    /**
     * <p>
     * 替换占位符
     * </p>
     *
     * @param sqlSegment
     * @return
     */
    private String replacePlaceholder(String sqlSegment) {
        if (StringUtils.isEmpty(sqlSegment)) {
            return StringUtils.EMPTY;
        }
        return sqlSegment.replaceAll("#\\{" + getParamAlias() + ".paramNameValuePairs.MPGENVAL[0-9]+}", "\\?");
    }

    /**
     * <p>
     * 原生占位符sql
     * </p>
     *
     * @return
     */
    public String originalSql() {
        return replacePlaceholder(getSqlSegment());
    }

    /**
     * <p>
     * SQL中WHERE关键字跟的条件语句
     * </p>
     * <p>
     * eg: ew.where("name='zhangsan'").where(id!=null, "id={0}", id);
     * <p>
     * 输出:<br>
     * 如果id=123:  WHERE (NAME='zhangsan' AND id=123)<br>
     * 如果id=null: WHERE (NAME='zhangsan')
     * </p>
     *
     * @param condition 拼接的前置条件
     * @param sqlWhere  where语句
     * @param params    参数集
     * @return this
     */
    public Wrapper<T> where(boolean condition, String sqlWhere, Object... params) {
        if (condition) {
            sql.WHERE(formatSql(sqlWhere, params));
        }
        return this;
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
    public Wrapper<T> where(String sqlWhere, Object... params) {
        return where(true, sqlWhere, params);
    }

    /**
     * <p>
     * 等同于SQL的"field=value"表达式
     * </p>
     *
     * @param condition 拼接的前置条件
     * @param column
     * @param params
     * @return
     */
    public Wrapper<T> eq(boolean condition, String column, Object params) {
        if (condition) {
            sql.WHERE(formatSql(String.format("%s = {0}", column), params));
        }
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
        return eq(true, column, params);
    }

    /**
     * <p>
     * 等同于SQL的"field <> value"表达式
     * </p>
     *
     * @param condition 拼接的前置条件
     * @param column
     * @param params
     * @return
     */
    public Wrapper<T> ne(boolean condition, String column, Object params) {
        if (condition) {
            sql.WHERE(formatSql(String.format("%s <> {0}", column), params));
        }
        return this;
    }

    /**
     * <p>
     * 等同于SQL的"field <> value"表达式
     * </p>
     *
     * @param column
     * @param params
     * @return
     */
    public Wrapper<T> ne(String column, Object params) {
        return ne(true, column, params);

    }

    /**
     * <p>
     * 等同于SQL的"field=value"表达式
     * </p>
     *
     * @param condition 拼接的前置条件
     * @param params
     * @return
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Wrapper<T> allEq(boolean condition, Map<String, Object> params) {
        if (condition && MapUtils.isNotEmpty(params)) {
            Iterator iterator = params.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Object> entry = (Map.Entry<String, Object>) iterator.next();
                Object value = entry.getValue();
                if (StringUtils.checkValNotNull(value)) {
                    sql.WHERE(formatSql(String.format("%s = {0}", entry.getKey()), entry.getValue()));
                }

            }

        }
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
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Wrapper<T> allEq(Map<String, Object> params) {
        return allEq(true, params);
    }

    /**
     * <p>
     * 等同于SQL的"field>value"表达式
     * </p>
     *
     * @param condition 拼接的前置条件
     * @param column
     * @param params
     * @return
     */
    public Wrapper<T> gt(boolean condition, String column, Object params) {
        if (condition) {
            sql.WHERE(formatSql(String.format("%s > {0}", column), params));
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
        return gt(true, column, params);
    }

    /**
     * <p>
     * 等同于SQL的"field>=value"表达式
     * </p>
     *
     * @param condition 拼接的前置条件
     * @param column
     * @param params
     * @return
     */
    public Wrapper<T> ge(boolean condition, String column, Object params) {
        if (condition) {
            sql.WHERE(formatSql(String.format("%s >= {0}", column), params));
        }
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
        return ge(true, column, params);
    }

    /**
     * <p>
     * 等同于SQL的"field<value"表达式
     * </p>
     *
     * @param condition 拼接的前置条件
     * @param column
     * @param params
     * @return
     */
    public Wrapper<T> lt(boolean condition, String column, Object params) {
        if (condition) {
            sql.WHERE(formatSql(String.format("%s < {0}", column), params));
        }
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
        return lt(true, column, params);
    }

    /**
     * <p>
     * 等同于SQL的"field<=value"表达式
     * </p>
     *
     * @param condition 拼接的前置条件
     * @param column
     * @param params
     * @return
     */
    public Wrapper<T> le(boolean condition, String column, Object params) {
        if (condition) {
            sql.WHERE(formatSql(String.format("%s <= {0}", column), params));
        }
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
        return le(true, column, params);
    }

    /**
     * <p>
     * AND 连接后续条件
     * </p>
     *
     * @param condition 拼接的前置条件
     * @param sqlAnd    and条件语句
     * @param params    参数集
     * @return this
     */
    public Wrapper<T> and(boolean condition, String sqlAnd, Object... params) {
        if (condition) {
            sql.AND().WHERE(formatSql(sqlAnd, params));
        }
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
    public Wrapper<T> and(String sqlAnd, Object... params) {
        return and(true, sqlAnd, params);
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
     * @param condition 拼接的前置条件
     * @param sqlAnd    AND 条件语句
     * @param params    参数值
     * @return this
     */
    public Wrapper<T> andNew(boolean condition, String sqlAnd, Object... params) {
        if (condition) {
            sql.AND_NEW().WHERE(formatSql(sqlAnd, params));
        }
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
     * @return this
     */
    public Wrapper<T> andNew() {
        sql.AND_NEW();
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
    public Wrapper<T> andNew(String sqlAnd, Object... params) {
        return andNew(true, sqlAnd, params);
    }

    /**
     * <p>
     * 使用AND连接并换行
     * </p>
     * <p>
     *
     * @return this
     */
    public Wrapper<T> and() {
        sql.AND();
        return this;
    }

    /**
     * <p>
     * 使用OR连接并换行
     * </p>
     *
     * @return this
     */
    public Wrapper<T> or() {
        sql.OR();
        return this;
    }

    /**
     * <p>
     * 添加OR条件
     * </p>
     *
     * @param condition 拼接的前置条件
     * @param sqlOr     or 条件语句
     * @param params    参数集
     * @return this
     */
    public Wrapper<T> or(boolean condition, String sqlOr, Object... params) {
        if (condition) {
            if (StringUtils.isEmpty(sql.toString())) {
                AND_OR = "OR";
            }
            sql.OR().WHERE(formatSql(sqlOr, params));
        }
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
    public Wrapper<T> or(String sqlOr, Object... params) {
        return or(true, sqlOr, params);
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
     * @return this
     */
    public Wrapper<T> orNew() {
        sql.OR_NEW();
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
     * @param condition 拼接的前置条件
     * @param sqlOr     AND 条件语句
     * @param params    参数值
     * @return this
     */
    public Wrapper<T> orNew(boolean condition, String sqlOr, Object... params) {
        if (condition) {
            if (StringUtils.isEmpty(sql.toString())) {
                AND_OR = "OR";
            }
            sql.OR_NEW().WHERE(formatSql(sqlOr, params));
        }
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
    public Wrapper<T> orNew(String sqlOr, Object... params) {
        return orNew(true, sqlOr, params);
    }

    /**
     * <p>
     * SQL中groupBy关键字跟的条件语句
     * </p>
     * <p>
     * eg: ew.where("name='zhangsan'").groupBy("id,name")
     * </p>
     *
     * @param condition 拼接的前置条件
     * @param columns   SQL 中的 Group by 语句，无需输入 Group By 关键字
     * @return this
     */
    public Wrapper<T> groupBy(boolean condition, String columns) {
        if (condition) {
            sql.GROUP_BY(columns);
        }
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
    public Wrapper<T> groupBy(String columns) {
        return groupBy(true, columns);
    }

    /**
     * <p>
     * SQL中having关键字跟的条件语句
     * </p>
     * <p>
     * eg: ew.groupBy("id,name").having("id={0}",22).and("password is not null")
     * </p>
     *
     * @param condition 拼接的前置条件
     * @param sqlHaving having关键字后面跟随的语句
     * @param params    参数集
     * @return EntityWrapper<T>
     */
    public Wrapper<T> having(boolean condition, String sqlHaving, Object... params) {
        if (condition) {
            sql.HAVING(formatSql(sqlHaving, params));
        }
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
     * @return EntityWrapper<T>
     */
    public Wrapper<T> having(String sqlHaving, Object... params) {
        return having(true, sqlHaving, params);
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
     * @param condition 拼接的前置条件
     * @param columns   SQL 中的 order by 语句，无需输入 Order By 关键字
     * @return this
     */
    public Wrapper<T> orderBy(boolean condition, String columns) {
        if (condition) {
            sql.ORDER_BY(columns);
        }
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
    public Wrapper<T> orderBy(String columns) {
        return orderBy(true, columns);
    }

    /**
     * <p>
     * SQL中orderby关键字跟的条件语句，可根据变更动态排序
     * </p>
     *
     * @param condition 拼接的前置条件
     * @param columns   SQL 中的 order by 语句，无需输入 Order By 关键字
     * @param isAsc     是否为升序
     * @return this
     */
    public Wrapper<T> orderBy(boolean condition, String columns, boolean isAsc) {
        if (condition && StringUtils.isNotEmpty(columns)) {
            sql.ORDER_BY(columns + (isAsc ? " ASC" : " DESC"));
        }
        return this;
    }

    /**
     * <p>
     * SQL中orderby关键字跟的条件语句，可根据变更动态排序
     * </p>
     *
     * @param condition 拼接的前置条件
     * @param columns   SQL 中的 order by 语句，无需输入 Order By 关键字
     * @param isAsc     是否为升序
     * @return this
     */
    public Wrapper<T> orderBy(boolean condition, Collection<String> columns, boolean isAsc) {
        if (condition && CollectionUtils.isNotEmpty(columns)) {
            for (String column : columns) {
                orderBy(condition, column, isAsc);
            }
        }
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
    public Wrapper<T> orderBy(String columns, boolean isAsc) {
        return orderBy(true, columns, isAsc);
    }

    /**
     * <p>
     * 批量根据ASC排序
     * </p>
     *
     * @param columns 需要排序的集合
     * @return this
     */
    public Wrapper<T> orderAsc(Collection<String> columns) {
        return orderBy(true, columns, true);
    }

    /**
     * <p>
     * 批量根据DESC排序
     * </p>
     *
     * @param columns 需要排序的集合
     * @return this
     */
    public Wrapper<T> orderDesc(Collection<String> columns) {
        return orderBy(true, columns, false);
    }

    /**
     * <p>
     * LIKE条件语句，value中无需前后%
     * </p>
     *
     * @param condition 拼接的前置条件
     * @param column    字段名称
     * @param value     匹配值
     * @return this
     */
    public Wrapper<T> like(boolean condition, String column, String value) {
        if (condition) {
            handerLike(column, value, SqlLike.DEFAULT, false);
        }
        return this;
    }

    /**
     * <p>
     * LIKE条件语句，value中无需前后%
     * </p>
     *
     * @param column 字段名称
     * @param value  匹配值
     * @return this
     */
    public Wrapper<T> like(String column, String value) {
        return like(true, column, value);
    }

    /**
     * <p>
     * NOT LIKE条件语句，value中无需前后%
     * </p>
     *
     * @param condition 拼接的前置条件
     * @param column    字段名称
     * @param value     匹配值
     * @return this
     */
    public Wrapper<T> notLike(boolean condition, String column, String value) {
        if (condition) {
            handerLike(column, value, SqlLike.DEFAULT, true);
        }
        return this;
    }

    /**
     * <p>
     * NOT LIKE条件语句，value中无需前后%
     * </p>
     *
     * @param column 字段名称
     * @param value  匹配值
     * @return this
     */
    public Wrapper<T> notLike(String column, String value) {
        return notLike(true, column, value);
    }

    /**
     * <p>
     * 处理LIKE操作
     * </p>
     *
     * @param column 字段名称
     * @param value  like匹配值
     * @param isNot  是否为NOT LIKE操作
     */
    private void handerLike(String column, String value, SqlLike type, boolean isNot) {
        if (StringUtils.isNotEmpty(column) && StringUtils.isNotEmpty(value)) {
            StringBuilder inSql = new StringBuilder();
            inSql.append(column);
            if (isNot) {
                inSql.append(" NOT");
            }
            inSql.append(" LIKE {0}");
            sql.WHERE(formatSql(inSql.toString(), SqlUtils.concatLike(value, type)));
        }
    }

    /**
     * <p>
     * LIKE条件语句，value中无需前后%
     * </p>
     *
     * @param condition 拼接的前置条件
     * @param column    字段名称
     * @param value     匹配值
     * @param type
     * @return this
     */
    public Wrapper<T> like(boolean condition, String column, String value, SqlLike type) {
        if (condition) {
            handerLike(column, value, type, false);
        }
        return this;
    }

    /**
     * <p>
     * LIKE条件语句，value中无需前后%
     * </p>
     *
     * @param column 字段名称
     * @param value  匹配值
     * @param type
     * @return this
     */
    public Wrapper<T> like(String column, String value, SqlLike type) {
        return like(true, column, value, type);
    }

    /**
     * <p>
     * NOT LIKE条件语句，value中无需前后%
     * </p>
     *
     * @param condition 拼接的前置条件
     * @param column    字段名称
     * @param value     匹配值
     * @param type
     * @return this
     */
    public Wrapper<T> notLike(boolean condition, String column, String value, SqlLike type) {
        if (condition) {
            handerLike(column, value, type, true);
        }
        return this;
    }

    /**
     * <p>
     * NOT LIKE条件语句，value中无需前后%
     * </p>
     *
     * @param column 字段名称
     * @param value  匹配值
     * @param type
     * @return this
     */
    public Wrapper<T> notLike(String column, String value, SqlLike type) {
        return notLike(true, column, value, type);
    }

    /**
     * <p>
     * is not null 条件
     * </p>
     *
     * @param condition 拼接的前置条件
     * @param columns   字段名称。多个字段以逗号分隔。
     * @return this
     */
    public Wrapper<T> isNotNull(boolean condition, String columns) {
        if (condition) {
            sql.IS_NOT_NULL(columns);
        }
        return this;
    }

    /**
     * <p>
     * is not null 条件
     * </p>
     *
     * @param columns 字段名称。多个字段以逗号分隔。
     * @return this
     */
    public Wrapper<T> isNotNull(String columns) {
        return isNotNull(true, columns);
    }

    /**
     * <p>
     * is null 条件
     * </p>
     *
     * @param condition 拼接的前置条件
     * @param columns   字段名称。多个字段以逗号分隔。
     * @return this
     */
    public Wrapper<T> isNull(boolean condition, String columns) {
        if (condition) {
            sql.IS_NULL(columns);
        }
        return this;
    }

    /**
     * <p>
     * is null 条件
     * </p>
     *
     * @param columns 字段名称。多个字段以逗号分隔。
     * @return this
     */
    public Wrapper<T> isNull(String columns) {
        return isNull(true, columns);
    }

    /**
     * <p>
     * EXISTS 条件语句，目前适配mysql及oracle
     * </p>
     *
     * @param condition 拼接的前置条件
     * @param value     匹配值
     * @return this
     */
    public Wrapper<T> exists(boolean condition, String value) {
        if (condition) {
            sql.EXISTS(value);
        }
        return this;
    }

    /**
     * <p>
     * EXISTS 条件语句，目前适配mysql及oracle
     * </p>
     *
     * @param value 匹配值
     * @return this
     */
    public Wrapper<T> exists(String value) {
        return exists(true, value);
    }

    /**
     * <p>
     * NOT EXISTS条件语句
     * </p>
     *
     * @param condition 拼接的前置条件
     * @param value     匹配值
     * @return this
     */
    public Wrapper<T> notExists(boolean condition, String value) {
        if (condition) {
            sql.NOT_EXISTS(value);
        }
        return this;
    }

    /**
     * <p>
     * NOT EXISTS条件语句
     * </p>
     *
     * @param value 匹配值
     * @return this
     */
    public Wrapper<T> notExists(String value) {
        return notExists(true, value);
    }

    /**
     * <p>
     * IN 条件语句，目前适配mysql及oracle
     * </p>
     *
     * @param condition 拼接的前置条件
     * @param column    字段名称
     * @param value     逗号拼接的字符串
     * @return this
     */
    public Wrapper<T> in(boolean condition, String column, String value) {
        if (condition && StringUtils.isNotEmpty(value)) {
            in(column, StringUtils.splitWorker(value, ",", -1, false));
        }
        return this;
    }

    /**
     * <p>
     * IN 条件语句，目前适配mysql及oracle
     * </p>
     *
     * @param column 字段名称
     * @param value  逗号拼接的字符串
     * @return this
     */
    public Wrapper<T> in(String column, String value) {
        return in(true, column, value);
    }

    /**
     * <p>
     * NOT IN条件语句
     * </p>
     *
     * @param condition 拼接的前置条件
     * @param column    字段名称
     * @param value     逗号拼接的字符串
     * @return this
     */
    public Wrapper<T> notIn(boolean condition, String column, String value) {
        if (condition && StringUtils.isNotEmpty(value)) {
            notIn(column, StringUtils.splitWorker(value, ",", -1, false));
        }
        return this;
    }

    /**
     * <p>
     * NOT IN条件语句
     * </p>
     *
     * @param column 字段名称
     * @param value  逗号拼接的字符串
     * @return this
     */
    public Wrapper<T> notIn(String column, String value) {
        return notIn(true, column, value);
    }

    /**
     * <p>
     * IN 条件语句，目前适配mysql及oracle
     * </p>
     *
     * @param condition 拼接的前置条件
     * @param column    字段名称
     * @param value     匹配值 集合
     * @return this
     */
    public Wrapper<T> in(boolean condition, String column, Collection<?> value) {
        if (condition && CollectionUtils.isNotEmpty(value)) {
            sql.WHERE(formatSql(inExpression(column, value, false), value.toArray()));
        }
        return this;
    }

    /**
     * <p>
     * IN 条件语句，目前适配mysql及oracle
     * </p>
     *
     * @param column 字段名称
     * @param value  匹配值 集合
     * @return this
     */
    public Wrapper<T> in(String column, Collection<?> value) {
        return in(true, column, value);
    }

    /**
     * <p>
     * NOT IN 条件语句，目前适配mysql及oracle
     * </p>
     *
     * @param condition 拼接的前置条件
     * @param column    字段名称
     * @param value     匹配值 集合
     * @return this
     */
    public Wrapper<T> notIn(boolean condition, String column, Collection<?> value) {
        if (condition && CollectionUtils.isNotEmpty(value)) {
            sql.WHERE(formatSql(inExpression(column, value, true), value.toArray()));
        }
        return this;
    }

    /**
     * <p>
     * NOT IN 条件语句，目前适配mysql及oracle
     * </p>
     *
     * @param column 字段名称
     * @param value  匹配值 集合
     * @return this
     */
    public Wrapper<T> notIn(String column, Collection<?> value) {
        return notIn(true, column, value);
    }

    /**
     * <p>
     * IN 条件语句，目前适配mysql及oracle
     * </p>
     *
     * @param condition 拼接的前置条件
     * @param column    字段名称
     * @param value     匹配值 object数组
     * @return this
     */
    public Wrapper<T> in(boolean condition, String column, Object[] value) {
        if (condition && ArrayUtils.isNotEmpty(value)) {
            sql.WHERE(formatSql(inExpression(column, Arrays.asList(value), false), value));
        }
        return this;
    }

    /**
     * <p>
     * IN 条件语句，目前适配mysql及oracle
     * </p>
     *
     * @param column 字段名称
     * @param value  匹配值 object数组
     * @return this
     */
    public Wrapper<T> in(String column, Object[] value) {
        return in(true, column, value);
    }

    /**
     * <p>
     * NOT IN 条件语句，目前适配mysql及oracle
     * </p>
     *
     * @param condition 拼接的前置条件
     * @param column    字段名称
     * @param value     匹配值 object数组
     * @return this
     */
    public Wrapper<T> notIn(boolean condition, String column, Object... value) {
        if (condition && ArrayUtils.isNotEmpty(value)) {
            sql.WHERE(formatSql(inExpression(column, Arrays.asList(value), true), value));
        }
        return this;
    }

    /**
     * <p>
     * NOT IN 条件语句，目前适配mysql及oracle
     * </p>
     *
     * @param column 字段名称
     * @param value  匹配值 object数组
     * @return this
     */
    public Wrapper<T> notIn(String column, Object... value) {
        return notIn(true, column, value);
    }

    /**
     * <p>
     * 获取in表达式
     * </p>
     *
     * @param column 字段名称
     * @param value  集合
     * @param isNot  是否为NOT IN操作
     */
    private String inExpression(String column, Collection<?> value, boolean isNot) {
        if (StringUtils.isNotEmpty(column) && CollectionUtils.isNotEmpty(value)) {
            StringBuilder inSql = new StringBuilder();
            inSql.append(column);
            if (isNot) {
                inSql.append(" NOT");
            }
            inSql.append(" IN ");
            inSql.append("(");
            int size = value.size();
            for (int i = 0; i < size; i++) {
                inSql.append(String.format(PLACE_HOLDER, i));
                if (i + 1 < size) {
                    inSql.append(",");
                }
            }
            inSql.append(")");
            return inSql.toString();
        }
        return null;
    }

    /**
     * <p>
     * betwwee 条件语句
     * </p>
     *
     * @param condition 拼接的前置条件
     * @param column    字段名称
     * @param val1
     * @param val2
     * @return this
     */
    public Wrapper<T> between(boolean condition, String column, Object val1, Object val2) {
        if (condition) {
            sql.WHERE(formatSql(String.format("%s BETWEEN {0} AND {1}", column), val1, val2));
        }
        return this;
    }

    /**
     * <p>
     * betwwee 条件语句
     * </p>
     *
     * @param column 字段名称
     * @param val1
     * @param val2
     * @return this
     */
    public Wrapper<T> between(String column, Object val1, Object val2) {
        return between(true, column, val1, val2);
    }

    /**
     * <p>
     * NOT betwwee 条件语句
     * </p>
     *
     * @param condition 拼接的前置条件
     * @param column    字段名称
     * @param val1
     * @param val2
     * @return this
     */
    public Wrapper<T> notBetween(boolean condition, String column, Object val1, Object val2) {
        if (condition) {
            sql.WHERE(formatSql(String.format("%s NOT BETWEEN {0} AND {1}", column), val1, val2));
        }
        return this;
    }

    /**
     * <p>
     * NOT betwwee 条件语句
     * </p>
     *
     * @param column 字段名称
     * @param val1
     * @param val2
     * @return this
     */
    public Wrapper<T> notBetween(String column, Object val1, Object val2) {
        return notBetween(true, column, val1, val2);

    }

    /**
     * <p>
     * 为了兼容之前的版本,可使用where()或and()替代
     * </p>
     *
     * @param sqlWhere where sql部分
     * @param params   参数集
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
     * @param need     是否需要添加该条件
     * @param sqlWhere 条件语句
     * @param params   参数集
     * @return this
     */
    public Wrapper<T> addFilterIfNeed(boolean need, String sqlWhere, Object... params) {
        return need ? where(sqlWhere, params) : this;
    }

    /**
     * <p>
     * 格式化SQL
     * </p>
     *
     * @param sqlStr SQL语句部分
     * @param params 参数集
     * @return this
     */
    protected String formatSql(String sqlStr, Object... params) {
        return formatSqlIfNeed(true, sqlStr, params);
    }

    /**
     * <p>
     * 根据需要格式化SQL<BR>
     * <BR>
     * Format SQL for methods: EntityWrapper<T>.where/and/or...("name={0}", value);
     * ALL the {<b>i</b>} will be replaced with #{MPGENVAL<b>i</b>}<BR>
     * <BR>
     * ew.where("sample_name=<b>{0}</b>", "haha").and("sample_age &gt;<b>{0}</b>
     * and sample_age&lt;<b>{1}</b>", 18, 30) <b>TO</b>
     * sample_name=<b>#{MPGENVAL1}</b> and sample_age&gt;#<b>{MPGENVAL2}</b> and
     * sample_age&lt;<b>#{MPGENVAL3}</b><BR>
     * </p>
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
        // #200
        if (ArrayUtils.isNotEmpty(params)) {
            for (int i = 0; i < params.length; ++i) {
                String genParamName = MP_GENERAL_PARAMNAME + paramNameSeq.incrementAndGet();
                sqlStr = sqlStr.replace(String.format(PLACE_HOLDER, i),
                    String.format(MYBATIS_PLUS_TOKEN, getParamAlias(), genParamName));
                paramNameValuePairs.put(genParamName, params[i]);
            }
        }
        return sqlStr;
    }

    /**
     * <p>
     * 自定义是否输出sql开头为 `WHERE` OR `AND` OR `OR`
     * </p>
     *
     * @param bool
     * @return this
     */
    public Wrapper<T> isWhere(Boolean bool) {
        this.isWhere = bool;
        return this;
    }

    /**
     * <p>
     * 手动把sql拼接到最后(有sql注入的风险,请谨慎使用)
     * </p>
     *
     * @param limit
     * @return this
     */
    public Wrapper<T> last(String limit) {
        sql.LAST(limit);
        return this;
    }

    /**
     * Fix issue 200.
     *
     * @return
     * @since 2.0.3
     */
    public Map<String, Object> getParamNameValuePairs() {
        return paramNameValuePairs;
    }

    public String getParamAlias() {
        return StringUtils.isEmpty(paramAlias) ? DEFAULT_PARAM_ALIAS : paramAlias;
    }

    /**
     * <p>
     * 参数别名设置，初始化时优先设置该值、重复设置异常
     * </p>
     *
     * @param paramAlias 参数别名
     * @return
     */
    public Wrapper<T> setParamAlias(String paramAlias) {
        if (StringUtils.isNotEmpty(getSqlSegment())) {
            throw new MybatisPlusException("Error: Please call this method when initializing!");
        }
        if (StringUtils.isNotEmpty(this.paramAlias)) {
            throw new MybatisPlusException("Error: Please do not call the method repeatedly!");
        }
        this.paramAlias = paramAlias;
        return this;
    }

    /**
     * <p>
     * 克隆本身 fixed github issues/241
     * </p>
     */
    public Wrapper<T> clone() {
        return SerializationUtils.clone(this);
    }
}
