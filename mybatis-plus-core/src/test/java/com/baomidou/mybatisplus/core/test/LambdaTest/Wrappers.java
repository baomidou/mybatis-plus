package com.baomidou.mybatisplus.core.test.LambdaTest;

/**
 * @author ming
 * @Date 2018/5/10
 * @This 自己的实现类
 * @R 入参类型
 */
public abstract class Wrappers<This, R, T> {

    abstract T getEntity();

    abstract This where(boolean condition, String sqlWhere, Object... params);

    This eq(R r, Object params) {
        return eq(true, r, params);
    }

    abstract This eq(boolean condition, R r, Object params);

    abstract String getSqlSegment();
}
