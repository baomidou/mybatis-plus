package com.baomidou.mybatisplus.core.test.query;


public enum Keywords implements ISqlSegment {

    AND, OR;

    @Override
    public String sqlSegment() {
        return this.toString();
    }
}
