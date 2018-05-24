package com.baomidou.mybatisplus.core.test.query.struct;

import static java.util.stream.Collectors.toList;

import java.util.ArrayDeque;
import java.util.Deque;

import com.baomidou.mybatisplus.core.test.query.ISqlSegment;


public class SqlStatement implements ISqlQueue<ISqlSegment>, ISqlSegment {

    private Deque<ISqlSegment> deque;

    public SqlStatement() {
        this.deque = new ArrayDeque<>();
    }

    @Override
    public void enqueue(ISqlSegment sqlSegment) {
        deque.add(sqlSegment);
    }

    @Override
    public String sqlSegment() {
        return String.join(" ", deque.stream()
            .map(ISqlSegment::sqlSegment)
            .collect(toList()));
    }
}
