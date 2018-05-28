package com.baomidou.mybatisplus.core.test.query;

import static com.baomidou.mybatisplus.core.test.query.Keywords.AND;
import static com.baomidou.mybatisplus.core.test.query.Keywords.BETWEEN;
import static com.baomidou.mybatisplus.core.test.query.Keywords.EQ;
import static com.baomidou.mybatisplus.core.test.query.Keywords.EXISTS;
import static com.baomidou.mybatisplus.core.test.query.Keywords.GE;
import static com.baomidou.mybatisplus.core.test.query.Keywords.GROUP_BY;
import static com.baomidou.mybatisplus.core.test.query.Keywords.GT;
import static com.baomidou.mybatisplus.core.test.query.Keywords.HAVING;
import static com.baomidou.mybatisplus.core.test.query.Keywords.IN;
import static com.baomidou.mybatisplus.core.test.query.Keywords.IS_NOT_NULL;
import static com.baomidou.mybatisplus.core.test.query.Keywords.IS_NULL;
import static com.baomidou.mybatisplus.core.test.query.Keywords.LE;
import static com.baomidou.mybatisplus.core.test.query.Keywords.LIKE;
import static com.baomidou.mybatisplus.core.test.query.Keywords.LT;
import static com.baomidou.mybatisplus.core.test.query.Keywords.NE;
import static com.baomidou.mybatisplus.core.test.query.Keywords.NOT;
import static com.baomidou.mybatisplus.core.test.query.Keywords.OR;
import static com.baomidou.mybatisplus.core.test.query.Keywords.ORDER_BY;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * <p>
 * 查询条件封装
 * </p>
 *
 * @author hubin
 * @since 2017-05-26
 */
public class Condition implements ISqlSegment {

    List<ISqlSegment> expression = new ArrayList<>();

    public Condition apply(String condition) {
        expression.add(() -> condition);
        return this;
    }

    public Condition and(String condition) {
        return this.addCondition(condition, AND);
    }

    public Condition and(Function<Condition, Condition> condition) {
        return addNestedCondition(condition, AND);
    }

    public Condition or(String condition) {
        return this.addCondition(condition, OR);
    }

    public Condition or(Function<Condition, Condition> condition) {
        return addNestedCondition(condition, OR);
    }

    public Condition in(String condition) {
        return this.addNestedCondition(condition, IN);
    }

    public Condition notIn(String condition) {
        return this.not().addNestedCondition(condition, IN);
    }

    /**
     * LIKE '%值%'
     */
    public Condition like(String condition) {
        expression.add(LIKE);
        expression.add(() -> "'%");
        expression.add(() -> condition);
        expression.add(() -> "%'");
        return this;
    }

    /**
     * LIKE '%值'
     */
    public Condition likeLeft(String condition) {
        expression.add(LIKE);
        expression.add(() -> "'%");
        expression.add(() -> condition);
        expression.add(() -> "'");
        return this;
    }

    /**
     * LIKE '值%'
     */
    public Condition likeRight(String condition) {
        expression.add(LIKE);
        expression.add(() -> "'");
        expression.add(() -> condition);
        expression.add(() -> "%'");
        return this;
    }

    /**
     * 等于 =
     */
    public Condition eq(String condition) {
        return this.addCondition(condition, EQ);
    }

    /**
     * 不等于 <>
     */
    public Condition ne(String condition) {
        return this.addCondition(condition, NE);
    }

    /**
     * 大于 >
     */
    public Condition gt(String condition) {
        return this.addCondition(condition, GT);
    }

    /**
     * 大于等于 >=
     */
    public Condition ge(String condition) {
        return this.addCondition(condition, GE);
    }

    /**
     * 小于 <
     */
    public Condition lt(String condition) {
        return this.addCondition(condition, LT);
    }

    /**
     * 小于等于 <=
     */
    public Condition le(String condition) {
        return this.addCondition(condition, LE);
    }

    /**
     * 字段 IS NULL
     */
    public Condition isNull(String condition) {
        expression.add(() -> condition);
        expression.add(IS_NULL);
        return this;
    }

    /**
     * 字段 IS NOT NULL
     */
    public Condition isNotNull(String condition) {
        expression.add(() -> condition);
        expression.add(IS_NOT_NULL);
        return this;
    }

    /**
     * 分组：GROUP BY 字段, ...
     */
    public Condition groupBy(String condition) {
        return this.addCondition(condition, GROUP_BY);
    }

    /**
     * 排序：ORDER BY 字段, ...
     */
    public Condition orderBy(String condition) {
        return this.addCondition(condition, ORDER_BY);
    }

    /**
     * HAVING 关键词
     */
    public Condition having() {
        expression.add(HAVING);
        return this;
    }

    /**
     * exists ( sql 语句 )
     */
    public Condition exists(String condition) {
        return this.addNestedCondition(condition, EXISTS);
    }

    /**
     * BETWEEN 值1 AND 值2
     */
    public Condition between(String condition, Object val1, Object val2) {
        expression.add(() -> condition);
        expression.add(BETWEEN);
        expression.add(() -> "val1");
        expression.add(AND);
        expression.add(() -> "val2");
        return this;
    }

    /**
     * LAST 拼接在 SQL 末尾
     */
    public Condition last(String condition) {
        expression.add(() -> condition);
        return this;
    }

    /**
     * NOT 关键词
     */
    protected Condition not() {
        expression.add(NOT);
        return this;
    }

    /**
     * <p>
     * 普通查询条件
     * </p>
     *
     * @param condition 查询条件值
     * @param keyword   SQL 关键词
     * @return
     */
    protected Condition addCondition(String condition, Keywords keyword) {
        expression.add(keyword);
        expression.add(() -> condition);
        return this;
    }

    /**
     * <p>
     * 嵌套查询条件
     * </p>
     *
     * @param condition 查询条件值
     * @param keyword   SQL 关键词
     * @return
     */
    protected Condition addNestedCondition(String condition, Keywords keyword) {
        expression.add(keyword);
        expression.add(() -> "(");
        expression.add(() -> condition);
        expression.add(() -> ")");
        return this;
    }

    /**
     * <p>
     * 多重嵌套查询条件
     * </p>
     *
     * @param condition 查询条件值
     * @param keyword   SQL 关键词
     * @return
     */
    protected Condition addNestedCondition(Function<Condition, Condition> condition, Keywords keyword) {
        expression.add(keyword);
        expression.add(() -> "(");
        expression.add(condition.apply(new Condition()));
        expression.add(() -> ")");
        return this;
    }

    @Override
    public String sqlSegment() {
        return String.join(" ", expression.stream()
            .map(ISqlSegment::sqlSegment)
            .collect(Collectors.toList()));
    }
}
