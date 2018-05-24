package com.baomidou.mybatisplus.core.test.query.predicate;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;

import com.baomidou.mybatisplus.core.test.query.ISqlSegment;
import com.baomidou.mybatisplus.core.test.query.clause.Join;


public class Table implements ISqlSegment {

    List<ISqlSegment> stringables = new ArrayList<>();

    /**
     * INNER JOIN table
     *
     * @param table table to join
     * @return this
     */
    public Table innerJoin(String table) {
        stringables.add(new Join(table, Join.JoinType.INNER));
        return this;
    }

    /**
     * CROSS JOIN table
     *
     * @param table table to join
     * @return this
     */
    public Table crossJoin(String table) {
        stringables.add(new Join(table, Join.JoinType.CROSS));
        return this;
    }

    /**
     * JOIN table
     *
     * @param table table to join
     * @return this
     */
    public Table join(String table) {
        stringables.add(new Join(table, Join.JoinType.DEFAULT));
        return this;
    }

    /**
     * LEFT OUTER JOIN table
     *
     * @param table table to join
     * @return this
     */
    public Table leftOuterJoin(String table) {
        stringables.add(new Join(table, Join.JoinType.LEFT_OUTER));
        return this;
    }

    /**
     * RIGHT OUTER JOIN table
     *
     * @param table table to join
     * @return this
     */
    public Table rightOuterJoin(String table) {
        stringables.add(new Join(table, Join.JoinType.RIGHT_OUTER));
        return this;
    }

    /**
     * FULL OUTER JOIN table
     *
     * @param table table to join
     * @return this
     */
    public Table fullOuterJoin(String table) {
        stringables.add(new Join(table, Join.JoinType.FULL_OUTER));
        return this;
    }

    /**
     * ON expression
     *
     * @param expression expression on which to join
     * @return this
     */
    public Table on(String expression) {
        stringables.add(new On(expression));
        return this;
    }

    @Override
    public String sqlSegment() {
        return String.join(" ", stringables
            .stream()
            .map(ISqlSegment::sqlSegment)
            .collect(toList()));
    }
}
