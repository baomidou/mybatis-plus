package com.baomidou.mybatisplus.core.test.LambdaTest;

import java.util.function.Function;

/**
 * @author ming
 * @Date 2018/5/10
 */
public class LambdaEntityWrapper<T> extends Wrappers<LambdaEntityWrapper<T>, Function<T, ?>, T> {

    @Override
    public T getEntity() {
        return null;
    }

    @Override
    public LambdaEntityWrapper<T> where(boolean condition, String sqlWhere, Object... params) {
        return null;
    }

    @Override
    public LambdaEntityWrapper<T> eq(boolean condition, Function<T, ?> trFunction, Object params) {
        return null;
    }

    @Override
    public String getSqlSegment() {
        return null;
    }
}
