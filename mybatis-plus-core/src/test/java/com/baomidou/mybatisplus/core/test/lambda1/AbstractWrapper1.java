package com.baomidou.mybatisplus.core.test.lambda1;

/**
 * @author ming
 * @Date 2018/5/11
 */
public abstract class AbstractWrapper1<This, T, R> implements Wrapper1<T> {

    private T entity;

    public T getEntity() {
        return entity;
    }

    @SuppressWarnings("unchecked")
    public This setEntity(T t) {
        entity = t;
        return (This) this;
    }

    abstract String getColumn(R r);

    @SuppressWarnings("unchecked")
    This where(boolean condition, String sqlWhere, Object... params) {
        //todo 一通操作组装成sql
        return (This) this;
    }

    This eq(R r, Object params) {
        return eq(true, r, params);
    }

    @SuppressWarnings("unchecked")
    This eq(boolean condition, R r, Object params) {
        //todo 一通操作组装成sql
        if (condition) {
            // todo 咔咔咔
            getColumn(r);//todo 这是获取到的字段名 user_id
        }
        return (This) this;
    }
}
