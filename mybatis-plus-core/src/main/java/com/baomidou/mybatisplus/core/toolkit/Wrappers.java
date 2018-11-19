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

    private static final QueryWrapper emptyWrapper = new EmptyWrapper<>();

    /**
     * 获取QueryWrapper
     *
     * @param <T> 实体类泛型
     * @return QueryWrapper
     */
    public static <T> QueryWrapper<T> query() {
        return new QueryWrapper<>();
    }

    /**
     * 获取UpdateWrapper
     *
     * @param <T> 实体类泛型
     * @return UpdateWrapper
     */
    public static <T> UpdateWrapper<T> update() {
        return new UpdateWrapper<>();
    }

    /**
     * 获取LambdaQueryWrapper
     *
     * @param entity 实体类
     * @param <T>    实体类泛型
     * @return LambdaQueryWrapper
     */
    public static <T> LambdaQueryWrapper<T> query(T entity) {
        return new LambdaQueryWrapper<>(entity);
    }

    /**
     * 获取LambdaUpdateWrapper
     *
     * @param entity 实体类
     * @param <T>    实体类泛型
     * @return LambdaUpdateWrapper
     */
    public static <T> LambdaUpdateWrapper<T> update(T entity) {
        return new UpdateWrapper<>(entity).lambda();
    }

    /**
     * 获取 EmptyWrapper
     *
     * @param <T> 任意泛型
     * @return EmptyWrapper
     */
    @SuppressWarnings("unchecked")
    public static <T> QueryWrapper<T> emptyWrapper() {
        return (QueryWrapper<T>) emptyWrapper;
    }
}
