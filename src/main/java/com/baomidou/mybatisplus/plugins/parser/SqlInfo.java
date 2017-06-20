package com.baomidou.mybatisplus.plugins.parser;

/**
 * Created by jobob on 17/6/20.
 */
public class SqlInfo {

    private String sql;// SQL 内容
    private boolean orderBy = true;// 是否排序

    public static SqlInfo newInstance() {
        return new SqlInfo();
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public boolean isOrderBy() {
        return orderBy;
    }

    public void setOrderBy(boolean orderBy) {
        this.orderBy = orderBy;
    }
}
