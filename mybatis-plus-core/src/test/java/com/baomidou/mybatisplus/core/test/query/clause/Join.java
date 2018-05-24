package com.baomidou.mybatisplus.core.test.query.clause;

import static java.lang.String.format;

import com.baomidou.mybatisplus.core.test.query.ISqlSegment;


public class Join implements ISqlSegment {

    public enum JoinType {

        DEFAULT,
        INNER,
        LEFT_OUTER,
        RIGHT_OUTER,
        FULL_OUTER,
        CROSS;

        protected String toSQLString() {

            if (this == DEFAULT) {
                return "JOIN";
            } else {
                return format("%s %s", this.toString(), "JOIN");
            }
        }
    }

    private String table;
    private JoinType joinType;

    public Join(String table, JoinType type) {
        this.table = table;
        this.joinType = type;
    }

    @Override
    public String sqlSegment() {
        return format("%s %s", joinType.toSQLString(), table);
    }
}
