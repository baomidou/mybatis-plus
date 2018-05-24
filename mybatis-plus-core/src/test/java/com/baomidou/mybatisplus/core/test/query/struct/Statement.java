package com.baomidou.mybatisplus.core.test.query.struct;

import static java.util.stream.Collectors.toList;

import java.util.ArrayDeque;
import java.util.Deque;

import com.baomidou.mybatisplus.core.test.query.ISqlSegment;


public class Statement implements SimpleQueue<ISqlSegment>, ISqlSegment {

    private Deque<ISqlSegment> deque;

    public Statement() {
        this.deque = new ArrayDeque<>();
    }

    @Override
    public void enqueue(ISqlSegment stringable) {
        deque.add(stringable);
    }

    @Override
    public String sqlSegment() {
        return String.join(" ", deque.stream()
            .map(ISqlSegment::sqlSegment)
            .collect(toList()));
    }
}
