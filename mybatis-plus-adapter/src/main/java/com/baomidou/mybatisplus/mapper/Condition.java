package com.baomidou.mybatisplus.mapper;

import com.baomidou.mybatisplus.core.conditions.EntityWrapper;
import com.baomidou.mybatisplus.core.conditions.Wrapper;

/**
 * 请尽快修改该类的引用 mp 3.1 将删除该类
 */
@Deprecated
public abstract class Condition {

    /**
     * 构建一个Empty条件构造 避免传递参数使用null
     */
    public static final Wrapper EMPTY = Wrapper.getInstance();

    /**
     * 构造一个空的Wrapper<T></>
     *
     * @param <T>
     * @return
     */
    public static <T> Wrapper<T> empty() {
        return (Wrapper<T>) EMPTY;
    }

    /**
     * 构造一个EntityWrapper<T></>
     *
     * @param <T>
     * @return
     */
    public static <T> EntityWrapper<T> entityWrapper() {
        return entityWrapper(null);
    }

    /**
     * 构造一个EntityWrapper<T></>
     *
     * @param <T>
     * @return
     */
    public static <T> EntityWrapper<T> entityWrapper(T entity) {
        return new EntityWrapper<>(entity);
    }

}
