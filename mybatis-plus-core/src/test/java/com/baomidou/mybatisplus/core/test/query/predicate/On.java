package com.baomidou.mybatisplus.core.test.query.predicate;

import com.baomidou.mybatisplus.core.test.query.ISqlSegment;


public class On implements ISqlSegment {

    private String criteria;

    public On(String criteria) {
        this.criteria = criteria;
    }

    @Override
    public String sqlSegment() {
        return String.format("ON %s", criteria);
    }
}
