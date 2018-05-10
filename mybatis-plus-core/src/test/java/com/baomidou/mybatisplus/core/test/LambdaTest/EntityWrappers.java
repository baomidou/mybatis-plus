package com.baomidou.mybatisplus.core.test.LambdaTest;

/**
 * @author ming
 * @Date 2018/5/10
 * @T 实体类
 */
public class EntityWrappers<T> extends Wrappers<EntityWrappers<T>, String, T> {

    private T entity;

    @Override
    public T getEntity() {
        return entity;
    }

    @Override
    public EntityWrappers<T> where(boolean condition, String sqlWhere, Object... params) {
        return null;
    }

    @Override
    public EntityWrappers<T> eq(boolean condition, String s, Object params) {
        return null;
    }

    @Override
    public String getSqlSegment() {
        return null;
    }
}
