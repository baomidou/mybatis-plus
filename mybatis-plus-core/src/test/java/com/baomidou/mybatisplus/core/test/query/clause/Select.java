package com.baomidou.mybatisplus.core.test.query.clause;

import static java.lang.String.format;

import com.baomidou.mybatisplus.core.test.query.ISqlSegment;


public class Select implements ISqlSegment {

    private String[] columns;

    public Select(String[] columns) {
        this.columns = columns;
    }

    @Override
    public String sqlSegment() {
        return format("SELECT %s", String.join(", ", (CharSequence[]) columns));
    }
}
