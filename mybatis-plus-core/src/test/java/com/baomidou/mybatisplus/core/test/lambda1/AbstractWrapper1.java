package com.baomidou.mybatisplus.core.test.lambda1;

import com.baomidou.mybatisplus.core.conditions.SqlPlus;
import com.baomidou.mybatisplus.core.enums.SqlLike;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.metadata.Column;
import com.baomidou.mybatisplus.core.metadata.Columns;
import com.baomidou.mybatisplus.core.toolkit.ArrayUtils;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.MapUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author ming
 * @Date 2018/5/11
 */
@SuppressWarnings("unchecked")
public abstract class AbstractWrapper1<This, T, R> implements Wrapper1<T> {

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

    private T entity;

    public T getEntity() {
        return entity;
    }

    @SuppressWarnings("unchecked")
    public This setEntity(T t) {
        entity = t;
        return (This) this;
    }

    abstract String getColumn(R r);

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

    @SuppressWarnings("unchecked")
    This setSqlSelect(String sqlSelect) {
        if (StringUtils.isNotEmpty(sqlSelect)) {
            this.sqlSelect = sqlSelect;
        }
        return (This) this;
    }

    @SuppressWarnings("unchecked")
    This setSqlSelect(String... columns) {
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
        return (This) this;
    }

    /**
     * <p>
     * 使用对象封装的setsqlselect
     * </p>
     *
     * @param column 字段
     * @return
     */
    This setSqlSelect(Column... column) {
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
        return (This) this;
    }

    /**
     * <p>
     * 使用对象封装的setsqlselect
     * </p>
     *
     * @param columns 字段
     * @return
     */
    This setSqlSelect(Columns columns) {
        Column[] columnArray = columns.getColumns();
        if (ArrayUtils.isNotEmpty(columnArray)) {
            setSqlSelect(columnArray);
        }
        return (This) this;
    }

    /**
     * SQL SET 字段
     */
    public String getSqlSet() {
        // to do nothing
        return null;
    }

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
     * @param r  where语句
     * @param params    参数集
     * @return this
     */
    This where(boolean condition, R r, Object... params) {
        if (condition) {
            sql.WHERE(formatSql(getColumn(r), params));
        }
        return (This) this;
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
     * @param r where语句
     * @param params   参数集
     * @return this
     */
    This where(R r, Object... params) {
        return where(true, r, params);
    }

    /**
     * <p>
     * 等同于SQL的"field=value"表达式
     * </p>
     *
     * @param condition 拼接的前置条件
     * @param r
     * @param params
     * @return
     */
    This eq(boolean condition, R r, Object params) {
        if (condition) {
            sql.WHERE(formatSql(String.format("%s = {0}", getColumn(r)), params));
        }
        return (This) this;
    }

    /**
     * <p>
     * 等同于SQL的"field=value"表达式
     * </p>
     *
     * @param r
     * @param params
     * @return
     */
    This eq(R r, Object params) {
        return eq(true, r, params);
    }

    /**
     * <p>
     * 等同于SQL的"field <> value"表达式
     * </p>
     *
     * @param condition 拼接的前置条件
     * @param r
     * @param params
     * @return
     */
    This ne(boolean condition, R r, Object params) {
        if (condition) {
            sql.WHERE(formatSql(String.format("%s <> {0}", getColumn(r)), params));
        }
        return (This) this;
    }

    /**
     * <p>
     * 等同于SQL的"field <> value"表达式
     * </p>
     *
     * @param r
     * @param params
     * @return
     */
    This ne(R r, Object params) {
        return ne(true, r, params);

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
    This allEq(boolean condition, Map<String, Object> params) {
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
        return (This) this;
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
    This allEq(Map<String, Object> params) {
        return allEq(true, params);
    }

    /**
     * <p>
     * 等同于SQL的"field>value"表达式
     * </p>
     *
     * @param condition 拼接的前置条件
     * @param r
     * @param params
     * @return
     */
    This gt(boolean condition, R r, Object params) {
        if (condition) {
            sql.WHERE(formatSql(String.format("%s > {0}", getColumn(r)), params));
        }
        return (This) this;
    }

    /**
     * <p>
     * 等同于SQL的"field>value"表达式
     * </p>
     *
     * @param r
     * @param params
     * @return
     */
    This gt(R r, Object params) {
        return gt(true, r, params);
    }

    /**
     * <p>
     * 等同于SQL的"field>=value"表达式
     * </p>
     *
     * @param condition 拼接的前置条件
     * @param r
     * @param params
     * @return
     */
    This ge(boolean condition, R r, Object params) {
        if (condition) {
            sql.WHERE(formatSql(String.format("%s >= {0}", r), params));
        }
        return (This) this;
    }

    /**
     * <p>
     * 等同于SQL的"field>=value"表达式
     * </p>
     *
     * @param r
     * @param params
     * @return
     */
    This ge(R r, Object params) {
        return ge(true, r, params);
    }

    /**
     * <p>
     * 等同于SQL的"field<value"表达式
     * </p>
     *
     * @param condition 拼接的前置条件
     * @param r
     * @param params
     * @return
     */
    This lt(boolean condition, R r, Object params) {
        if (condition) {
            sql.WHERE(formatSql(String.format("%s < {0}", r), params));
        }
        return (This) this;
    }

    /**
     * <p>
     * 等同于SQL的"field<value"表达式
     * </p>
     *
     * @param r
     * @param params
     * @return
     */
    This lt(R r, Object params) {
        return lt(true, r, params);
    }

    /**
     * <p>
     * 等同于SQL的"field<=value"表达式
     * </p>
     *
     * @param condition 拼接的前置条件
     * @param r
     * @param params
     * @return
     */
    This le(boolean condition, R r, Object params) {
        if (condition) {
            sql.WHERE(formatSql(String.format("%s <= {0}", r), params));
        }
        return (This) this;
    }

    /**
     * <p>
     * 等同于SQL的"field<=value"表达式
     * </p>
     *
     * @param r
     * @param params
     * @return
     */
    This le(R r, Object params) {
        return le(true, r, params);
    }

    /**
     * <p>
     * AND 连接后续条件
     * </p>
     *
     * @param condition 拼接的前置条件
     * @param r    and条件语句
     * @param params    参数集
     * @return this
     */
    This and(boolean condition, R r, Object... params) {
        if (condition) {
            sql.AND().WHERE(formatSql(getColumn(r), params));
        }
        return (This) this;
    }

    /**
     * <p>
     * AND 连接后续条件
     * </p>
     *
     * @param r and条件语句
     * @param params 参数集
     * @return this
     */
    This and(R r, Object... params) {
        return and(true, r, params);
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
     * @param r    AND 条件语句
     * @param params    参数值
     * @return this
     */
    This andNew(boolean condition, R r, Object... params) {
        if (condition) {
            sql.AND_NEW().WHERE(formatSql(getColumn(r), params));
        }
        return (This) this;
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
    This andNew() {
        sql.AND_NEW();
        return (This) this;
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
     * @param r AND 条件语句
     * @param params 参数值
     * @return this
     */
    This andNew(R r, Object... params) {
        return andNew(true, r, params);
    }

    /**
     * <p>
     * 使用AND连接并换行
     * </p>
     *
     * @return this
     */
    This and() {
        sql.AND();
        return (This) this;
    }

    /**
     * <p>
     * 左嵌套方法
     * </p>
     *
     * @return this
     */
    This leftNest() {
        leftNest(1);
        return (This) this;
    }

    /**
     * <p>
     * 右嵌套方法
     * </p>
     *
     * @return this
     */
    This rightNest() {
        rightNest(1);
        return (This) this;
    }

    /**
     * <p>
     * 左嵌套方法,一般认为填补mp一个条件中多重嵌套解决方案
     * </p>
     *
     * @param num 嵌套数量
     * @return this
     */
    This leftNest(int num) {
        nest("(", num);
        return (This) this;
    }

    /**
     * <p>
     * 右嵌套方法,一般认为填补mp一个条件中多重嵌套解决方案
     * </p>
     *
     * @param num 嵌套数量
     * @return this
     */
    This rightNest(int num) {
        nest(")", num);
        return (This) this;
    }

    /**
     * <p>
     * 嵌套方法,一般认为填补mp一个条件中多重嵌套解决方案
     * </p>
     *
     * @param nest 嵌套字符串 "(" or ")"
     * @param num  嵌套数量
     * @return this
     */
    This nest(String nest, int num) {
        if (num >= 1) {
            StringBuilder builder = new StringBuilder(num);
            for (int i = 0; i < num; i++) {
                builder.append(nest);
            }
            sql.SUPP(builder.toString());
        }
        return (This) this;
    }

    /**
     * <p>
     * 使用OR连接并换行
     * </p>
     *
     * @return this
     */
    This or() {
        sql.OR();
        return (This) this;
    }

    /**
     * <p>
     * 添加OR条件
     * </p>
     *
     * @param condition 拼接的前置条件
     * @param r     or 条件语句
     * @param params    参数集
     * @return this
     */
    This or(boolean condition, R r, Object... params) {
        if (condition) {
            if (StringUtils.isEmpty(sql.toString())) {
                AND_OR = "OR";
            }
            sql.OR().WHERE(formatSql(getColumn(r), params));
        }
        return (This) this;
    }

    /**
     * <p>
     * 添加OR条件
     * </p>
     *
     * @param r  or 条件语句
     * @param params 参数集
     * @return this
     */
    This or(R r, Object... params) {
        return or(true, r, params);
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
    This orNew() {
        sql.OR_NEW();
        return (This) this;
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
     * @param r     AND 条件语句
     * @param params    参数值
     * @return this
     */
    This orNew(boolean condition, R r, Object... params) {
        if (condition) {
            if (StringUtils.isEmpty(sql.toString())) {
                AND_OR = "OR";
            }
            sql.OR_NEW().WHERE(formatSql(getColumn(r), params));
        }
        return (This) this;
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
     * @param r  AND 条件语句
     * @param params 参数值
     * @return this
     */
    This orNew(R r, Object... params) {
        return orNew(true, r, params);
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
     * @param r   SQL 中的 Group by 语句，无需输入 Group By 关键字
     * @return this
     */
    This groupBy(boolean condition, R r) {
        if (condition) {
            sql.GROUP_BY(getColumn(r));
        }
        return (This) this;
    }

    /**
     * <p>
     * SQL中groupBy关键字跟的条件语句
     * </p>
     * <p>
     * eg: ew.where("name='zhangsan'").groupBy("id,name")
     * </p>
     *
     * @param r SQL 中的 Group by 语句，无需输入 Group By 关键字
     * @return this
     */
    This groupBy(R r) {
        return groupBy(true, r);
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
    This having(boolean condition, String sqlHaving, Object... params) {
        if (condition) {
            sql.HAVING(formatSql(sqlHaving, params));
        }
        return (This) this;
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
    This having(String sqlHaving, Object... params) {
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
     * @param r   SQL 中的 order by 语句，无需输入 Order By 关键字
     * @return this
     */
    This orderBy(boolean condition, R r) {
        if (condition) {
            sql.ORDER_BY(getColumn(r));
        }
        return (This) this;
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
     * @param r SQL 中的 order by 语句，无需输入 Order By 关键字
     * @return this
     */
    This orderBy(R r) {
        return orderBy(true, r);
    }

    /**
     * <p>
     * SQL中orderby关键字跟的条件语句，可根据变更动态排序
     * </p>
     *
     * @param condition 拼接的前置条件
     * @param r   SQL 中的 order by 语句，无需输入 Order By 关键字
     * @param isAsc     是否为升序
     * @return this
     */
    This orderBy(boolean condition, R r, boolean isAsc) {
        if (condition && StringUtils.isNotEmpty(getColumn(r))) {
            sql.ORDER_BY(getColumn(r) + (isAsc ? " ASC" : " DESC"));
        }
        return (This) this;
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
    This orderBy(boolean condition, Collection<R> columns, boolean isAsc) {
        if (condition && CollectionUtils.isNotEmpty(columns)) {
            for (R r : columns) {
                orderBy(condition, r, isAsc);
            }
        }
        return (This) this;
    }

    /**
     * <p>
     * SQL中orderby关键字跟的条件语句，可根据变更动态排序
     * </p>
     *
     * @param r SQL 中的 order by 语句，无需输入 Order By 关键字
     * @param isAsc   是否为升序
     * @return this
     */
    This orderBy(R r, boolean isAsc) {
        return orderBy(true, r, isAsc);
    }

    /**
     * <p>
     * 批量根据ASC排序
     * </p>
     *
     * @param columns 需要排序的集合
     * @return this
     */
    This orderAsc(Collection<R> columns) {
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
    This orderDesc(Collection<R> columns) {
        return orderBy(true, columns, false);
    }

    /**
     * <p>
     * LIKE条件语句，value中无需前后%
     * </p>
     *
     * @param condition 拼接的前置条件
     * @param r    字段名称
     * @param value     匹配值
     * @return this
     */
    This like(boolean condition, R r, String value) {
        if (condition) {
            handlerLike(r, value, SqlLike.DEFAULT, false);
        }
        return (This) this;
    }

    /**
     * <p>
     * LIKE条件语句，value中无需前后%
     * </p>
     *
     * @param r 字段名称
     * @param value  匹配值
     * @return this
     */
    This like(R r, String value) {
        return like(true, r, value);
    }

    /**
     * <p>
     * NOT LIKE条件语句，value中无需前后%
     * </p>
     *
     * @param condition 拼接的前置条件
     * @param r    字段名称
     * @param value     匹配值
     * @return this
     */
    This notLike(boolean condition, R r, String value) {
        if (condition) {
            handlerLike(r, value, SqlLike.DEFAULT, true);
        }
        return (This) this;
    }

    /**
     * <p>
     * NOT LIKE条件语句，value中无需前后%
     * </p>
     *
     * @param r 字段名称
     * @param value  匹配值
     * @return this
     */
    This notLike(R r, String value) {
        return notLike(true, r, value);
    }

    /**
     * <p>
     * 处理LIKE操作
     * </p>
     *
     * @param r 字段名称
     * @param value  like匹配值
     * @param isNot  是否为NOT LIKE操作
     */
    private void handlerLike(R r, String value, SqlLike type, boolean isNot) {
        if (StringUtils.isNotEmpty(getColumn(r)) && StringUtils.isNotEmpty(value)) {
            StringBuilder inSql = new StringBuilder();
            inSql.append(getColumn(r));
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
     * @param r    字段名称
     * @param value     匹配值
     * @param type
     * @return this
     */
    This like(boolean condition, R r, String value, SqlLike type) {
        if (condition) {
            handlerLike(r, value, type, false);
        }
        return (This) this;
    }

    /**
     * <p>
     * LIKE条件语句，value中无需前后%
     * </p>
     *
     * @param r 字段名称
     * @param value  匹配值
     * @param type
     * @return this
     */
    This like(R r, String value, SqlLike type) {
        return like(true, r, value, type);
    }

    /**
     * <p>
     * NOT LIKE条件语句，value中无需前后%
     * </p>
     *
     * @param condition 拼接的前置条件
     * @param r    字段名称
     * @param value     匹配值
     * @param type
     * @return this
     */
    This notLike(boolean condition, R r, String value, SqlLike type) {
        if (condition) {
            handlerLike(r, value, type, true);
        }
        return (This) this;
    }

    /**
     * <p>
     * NOT LIKE条件语句，value中无需前后%
     * </p>
     *
     * @param r 字段名称
     * @param value  匹配值
     * @param type
     * @return this
     */
    This notLike(R r, String value, SqlLike type) {
        return notLike(true, r, value, type);
    }

    /**
     * <p>
     * is not null 条件
     * </p>
     *
     * @param condition 拼接的前置条件
     * @param r   字段名称。多个字段以逗号分隔。
     * @return this
     */
    This isNotNull(boolean condition, R r) {
        if (condition) {
            sql.IS_NOT_NULL(getColumn(r));
        }
        return (This) this;
    }

    /**
     * <p>
     * is not null 条件
     * </p>
     *
     * @param r 字段名称。多个字段以逗号分隔。
     * @return this
     */
    This isNotNull(R r) {
        return isNotNull(true, r);
    }

    /**
     * <p>
     * is null 条件
     * </p>
     *
     * @param condition 拼接的前置条件
     * @param r   字段名称。多个字段以逗号分隔。
     * @return this
     */
    This isNull(boolean condition, R r) {
        if (condition) {
            sql.IS_NULL(getColumn(r));
        }
        return (This) this;
    }

    /**
     * <p>
     * is null 条件
     * </p>
     *
     * @param r 字段名称。多个字段以逗号分隔。
     * @return this
     */
    This isNull(R r) {
        return isNull(true, r);
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
    This exists(boolean condition, String value) {
        if (condition) {
            sql.EXISTS(value);
        }
        return (This) this;
    }

    /**
     * <p>
     * EXISTS 条件语句，目前适配mysql及oracle
     * </p>
     *
     * @param value 匹配值
     * @return this
     */
    This exists(String value) {
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
    This notExists(boolean condition, String value) {
        if (condition) {
            sql.NOT_EXISTS(value);
        }
        return (This) this;
    }

    /**
     * <p>
     * NOT EXISTS条件语句
     * </p>
     *
     * @param value 匹配值
     * @return this
     */
    This notExists(String value) {
        return notExists(true, value);
    }

    /**
     * <p>
     * IN 条件语句，目前适配mysql及oracle
     * </p>
     *
     * @param condition 拼接的前置条件
     * @param r    字段名称
     * @param value     逗号拼接的字符串
     * @return this
     */
    This in(boolean condition, R r, String value) {
        if (condition && StringUtils.isNotEmpty(value)) {
            in(r, StringUtils.splitWorker(value, ",", -1, false));
        }
        return (This) this;
    }

    /**
     * <p>
     * IN 条件语句，目前适配mysql及oracle
     * </p>
     *
     * @param r 字段名称
     * @param value  逗号拼接的字符串
     * @return this
     */
    This in(R r, String value) {
        return in(true, r, value);
    }

    /**
     * <p>
     * NOT IN条件语句
     * </p>
     *
     * @param condition 拼接的前置条件
     * @param r    字段名称
     * @param value     逗号拼接的字符串
     * @return this
     */
    This notIn(boolean condition, R r, String value) {
        if (condition && StringUtils.isNotEmpty(value)) {
            notIn(r, StringUtils.splitWorker(value, ",", -1, false));
        }
        return (This) this;
    }

    /**
     * <p>
     * NOT IN条件语句
     * </p>
     *
     * @param r 字段名称
     * @param value  逗号拼接的字符串
     * @return this
     */
    This notIn(R r, String value) {
        return notIn(true, r, value);
    }

    /**
     * <p>
     * IN 条件语句，目前适配mysql及oracle
     * </p>
     *
     * @param condition 拼接的前置条件
     * @param r    字段名称
     * @param value     匹配值 集合
     * @return this
     */
    This in(boolean condition, R r, Collection<?> value) {
        if (condition && CollectionUtils.isNotEmpty(value)) {
            sql.WHERE(formatSql(inExpression(r, value, false), value.toArray()));
        }
        return (This) this;
    }

    /**
     * <p>
     * IN 条件语句，目前适配mysql及oracle
     * </p>
     *
     * @param r 字段名称
     * @param value  匹配值 集合
     * @return this
     */
    This in(R r, Collection<?> value) {
        return in(true, r, value);
    }

    /**
     * <p>
     * NOT IN 条件语句，目前适配mysql及oracle
     * </p>
     *
     * @param condition 拼接的前置条件
     * @param r    字段名称
     * @param value     匹配值 集合
     * @return this
     */
    This notIn(boolean condition, R r, Collection<?> value) {
        if (condition && CollectionUtils.isNotEmpty(value)) {
            sql.WHERE(formatSql(inExpression(r, value, true), value.toArray()));
        }
        return (This) this;
    }

    /**
     * <p>
     * NOT IN 条件语句，目前适配mysql及oracle
     * </p>
     *
     * @param r 字段名称
     * @param value  匹配值 集合
     * @return this
     */
    This notIn(R r, Collection<?> value) {
        return notIn(true, r, value);
    }

    /**
     * <p>
     * IN 条件语句，目前适配mysql及oracle
     * </p>
     *
     * @param condition 拼接的前置条件
     * @param r    字段名称
     * @param value     匹配值 object数组
     * @return this
     */
    This in(boolean condition, R r, Object[] value) {
        if (condition && ArrayUtils.isNotEmpty(value)) {
            sql.WHERE(formatSql(inExpression(r, Arrays.asList(value), false), value));
        }
        return (This) this;
    }

    /**
     * <p>
     * IN 条件语句，目前适配mysql及oracle
     * </p>
     *
     * @param r 字段名称
     * @param value  匹配值 object数组
     * @return this
     */
    This in(R r, Object[] value) {
        return in(true, r, value);
    }

    /**
     * <p>
     * NOT IN 条件语句，目前适配mysql及oracle
     * </p>
     *
     * @param condition 拼接的前置条件
     * @param r    字段名称
     * @param value     匹配值 object数组
     * @return this
     */
    This notIn(boolean condition, R r, Object... value) {
        if (condition && ArrayUtils.isNotEmpty(value)) {
            sql.WHERE(formatSql(inExpression(r, Arrays.asList(value), true), value));
        }
        return (This) this;
    }

    /**
     * <p>
     * NOT IN 条件语句，目前适配mysql及oracle
     * </p>
     *
     * @param r 字段名称
     * @param value  匹配值 object数组
     * @return this
     */
    This notIn(R r, Object... value) {
        return notIn(true, r, value);
    }

    /**
     * <p>
     * 获取in表达式
     * </p>
     *
     * @param r 字段名称
     * @param value  集合
     * @param isNot  是否为NOT IN操作
     */
    private String inExpression(R r, Collection<?> value, boolean isNot) {
        if (StringUtils.isNotEmpty(getColumn(r)) && CollectionUtils.isNotEmpty(value)) {
            StringBuilder inSql = new StringBuilder();
            inSql.append(getColumn(r));
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
     * @param r    字段名称
     * @param val1
     * @param val2
     * @return this
     */
    This between(boolean condition, R r, Object val1, Object val2) {
        if (condition) {
            sql.WHERE(formatSql(String.format("%s BETWEEN {0} AND {1}", getColumn(r)), val1, val2));
        }
        return (This) this;
    }

    /**
     * <p>
     * betwwee 条件语句
     * </p>
     *
     * @param r 字段名称
     * @param val1
     * @param val2
     * @return this
     */
    This between(R r, Object val1, Object val2) {
        return between(true, r, val1, val2);
    }

    /**
     * <p>
     * NOT betwwee 条件语句
     * </p>
     *
     * @param condition 拼接的前置条件
     * @param r    字段名称
     * @param val1
     * @param val2
     * @return this
     */
    This notBetween(boolean condition, R r, Object val1, Object val2) {
        if (condition) {
            sql.WHERE(formatSql(String.format("%s NOT BETWEEN {0} AND {1}", getColumn(r)), val1, val2));
        }
        return (This) this;
    }

    /**
     * <p>
     * NOT betwwee 条件语句
     * </p>
     *
     * @param r 字段名称
     * @param val1
     * @param val2
     * @return this
     */
    This notBetween(R r, Object val1, Object val2) {
        return notBetween(true, r, val1, val2);

    }

    /**
     * <p>
     * 为了兼容之前的版本,可使用where()或and()替代
     * </p>
     *
     * @param r where sql部分
     * @param params   参数集
     * @return this
     */
    This addFilter(R r, Object... params) {
        return and(r, params);
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
     * @param r 条件语句
     * @param params   参数集
     * @return this
     */
    This addFilterIfNeed(boolean need, R r, Object... params) {
        return need ? where(r, params) : (This)this;
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
    This isWhere(Boolean bool) {
        this.isWhere = bool;
        return (This) this;
    }

    /**
     * <p>
     * 手动把sql拼接到最后(有sql注入的风险,请谨慎使用)
     * </p>
     *
     * @param limit
     * @return this
     */
    This last(String limit) {
        sql.LAST(limit);
        return (This) this;
    }

    /**
     * Fix issue 200.
     *
     * @return
     * @since 2.0.3
     */
    @Override
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
    This setParamAlias(String paramAlias) {
        if (StringUtils.isNotEmpty(getSqlSegment())) {
            throw new MybatisPlusException("Error: Please call this method when initializing!");
        }
        if (StringUtils.isNotEmpty(this.paramAlias)) {
            throw new MybatisPlusException("Error: Please do not call the method repeatedly!");
        }
        this.paramAlias = paramAlias;
        return (This) this;
    }

    //TODO: 3.0
    public boolean isEmptyWrapper() {
        return checkWrapperEmpty();
    }

    //TODO: 3.0 提供protect方法，让子类可覆盖
    protected boolean checkWrapperEmpty() {
        return sql.isEmptyOfWhere() && sqlSelect == null;
    }
}
