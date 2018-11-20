package com.baomidou.mybatisplus.core.toolkit;

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
public final class Wrappers {
    /**
     * 空的 EmptyWrapper
     */
    private static final QueryWrapper emptyWrapper = new EmptyWrapper<>();

    private Wrappers() {
        // ignore
    }

    /**
     * 获取 QueryWrapper<T>
     *
     * @param <T> 实体类泛型
     * @return QueryWrapper<T>
     */
    public static <T> QueryWrapper<T> query() {
        return new QueryWrapper<>();
    }

    /**
     * 获取 QueryWrapper<T>
     *
     * @param entity 实体类
     * @param <T>    实体类泛型
     * @return QueryWrapper<T>
     */
    public static <T> QueryWrapper<T> query(T entity) {
        return new QueryWrapper<>(entity);
    }

    /**
     * 获取 LambdaQueryWrapper<T>
     *
     * @param <T> 实体类泛型
     * @return LambdaQueryWrapper<T>
     */
    public static <T> LambdaQueryWrapper<T> lambdaQuery() {
        return new LambdaQueryWrapper<>();
    }

    /**
     * 获取 LambdaQueryWrapper<T>
     *
     * @param entity 实体类
     * @param <T>    实体类泛型
     * @return LambdaQueryWrapper<T>
     */
    public static <T> LambdaQueryWrapper<T> lambdaQuery(T entity) {
        return new LambdaQueryWrapper<>(entity);
    }

    /**
     * 获取 UpdateWrapper<T>
     *
     * @param <T> 实体类泛型
     * @return UpdateWrapper<T>
     */
    public static <T> UpdateWrapper<T> update() {
        return new UpdateWrapper<>();
    }

    /**
     * 获取 UpdateWrapper<T>
     *
     * @param entity 实体类
     * @param <T>    实体类泛型
     * @return UpdateWrapper<T>
     */
    public static <T> UpdateWrapper<T> update(T entity) {
        return new UpdateWrapper<>(entity);
    }

    /**
     * 获取 LambdaUpdateWrapper<T>
     *
     * @param <T> 实体类泛型
     * @return LambdaUpdateWrapper<T>
     */
    public static <T> LambdaUpdateWrapper<T> lambdaUpdate() {
        return new LambdaUpdateWrapper<>();
    }

    /**
     * 获取 LambdaUpdateWrapper<T>
     *
     * @param entity 实体类
     * @param <T>    实体类泛型
     * @return LambdaUpdateWrapper<T>
     */
    public static <T> LambdaUpdateWrapper<T> lambdaUpdate(T entity) {
        return new LambdaUpdateWrapper<>(entity);
    }

    /**
     * 获取 EmptyWrapper<T>
     *
     * @param <T> 任意泛型
     * @return EmptyWrapper<T>
     */
    @SuppressWarnings("unchecked")
    public static <T> QueryWrapper<T> emptyWrapper() {
        return (QueryWrapper<T>) emptyWrapper;
    }
}
