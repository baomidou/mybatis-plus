package com.baomidou.mybatisplus.core.toolkit;

import java.io.Serializable;

import com.baomidou.mybatisplus.core.conditions.query.EmptyWrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;

/**
 * <p>
 * Wrapper 条件构造
 * </p>
 *
 * @author Caratacus
 */
public class Wrappers implements Serializable {

    private static final QueryWrapper emptyWrapper = new EmptyWrapper<>();

    /**
     * 获取QueryWrapper
     *
     * @param <T>
     * @return
     */
    public static <T> QueryWrapper<T> query() {
        return new QueryWrapper<>();
    }

    /**
     * 获取UpdateWrapper
     *
     * @param <T>
     * @return
     */
    public static <T> UpdateWrapper<T> update() {
        return new UpdateWrapper<>();
    }

    /**
     * 获取LambdaQueryWrapper
     *
     * @param entity
     * @param <T>
     * @return
     */
    public static <T> LambdaQueryWrapper<T> query(T entity) {
        return new LambdaQueryWrapper(entity);
    }

    /**
     * 获取LambdaUpdateWrapper
     *
     * @param entity
     * @param <T>
     * @return
     */
    public static <T> LambdaUpdateWrapper<T> update(T entity) {
        return new UpdateWrapper<>(entity).lambda();
    }

    /**
     * 获取EmptyWrapper
     *
     * @param <T>
     * @return
     */
    public static <T> QueryWrapper<T> emptyWrapper() {
        return (QueryWrapper<T>) emptyWrapper;
    }
}
