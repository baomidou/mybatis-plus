package com.baomidou.mybatisplus.core.test.query;


/**
 * <p>
 * SQL 保留关键字枚举
 * </p>
 *
 * @author hubin
 * @since 2017-05-26
 */
public enum Keywords implements ISqlSegment {
    AND("AND"),
    OR("OR"),
    IN("IN"),
    NOT("NOT"),
    LIKE("LIKE"),
    EQ("="),
    NE("<>"),
    GT(">"),
    GE(">="),
    LT("<"),
    LE("<="),
    IS_NULL("IS NULL"),
    IS_NOT_NULL("IS NOT NULL"),
    GROUP_BY("GROUP BY"),
    HAVING("HAVING"),
    ORDER_BY("ORDER BY"),
    EXISTS("EXISTS"),
    BETWEEN("BETWEEN");

    private String condition;

    Keywords(final String condition) {
        this.condition = condition;
    }

    @Override
    public String sqlSegment() {
        return this.condition;
    }
}
