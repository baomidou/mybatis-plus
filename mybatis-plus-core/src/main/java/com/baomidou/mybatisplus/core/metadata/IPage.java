/*
 * Copyright (c) 2011-2021, baomidou (jobob@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baomidou.mybatisplus.core.metadata;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringPool;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

/**
 * 分页 Page 对象接口
 *
 * @author hubin
 * @since 2018-06-09
 */
public interface IPage<T> extends Serializable {

    /**
     * 获取排序信息，排序的字段和正反序
     *
     * @return 排序信息
     */
    List<OrderItem> orders();

    /**
     * KEY/VALUE 条件
     *
     * @return ignore
     * @deprecated 3.4.0 @2020-06-30
     */
    @Deprecated
    default Map<Object, Object> condition() {
        return null;
    }

    /**
     * 自动优化 COUNT SQL【 默认：true 】
     *
     * @return true 是 / false 否
     */
    default boolean optimizeCountSql() {
        return true;
    }

    /**
     * 进行 count 查询 【 默认: true 】
     *
     * @return true 是 / false 否
     */
    default boolean isSearchCount() {
        return true;
    }

    /**
     * 计算当前分页偏移量
     */
    default long offset() {
        long current = getCurrent();
        if (current <= 1L) {
            return 0L;
        }
        return (current - 1) * getSize();
    }

    /**
     * 最大每页分页数限制,优先级高于分页插件内的 maxLimit
     *
     * @since 3.4.0 @2020-07-17
     */
    default Long maxLimit() {
        return null;
    }

    /**
     * 当前分页总页数
     */
    default long getPages() {
        if (getSize() == 0) {
            return 0L;
        }
        long pages = getTotal() / getSize();
        if (getTotal() % getSize() != 0) {
            pages++;
        }
        return pages;
    }

    /**
     * 内部什么也不干
     * <p>只是为了 json 反序列化时不报错</p>
     */
    default IPage<T> setPages(long pages) {
        // to do nothing
        return this;
    }

    /**
     * 设置是否命中count缓存
     *
     * @param hit 是否命中
     * @since 3.3.1
     * @deprecated 3.4.0 @2020-06-30 缓存遵循mybatis的一或二缓
     */
    @Deprecated
    default void hitCount(boolean hit) {

    }

    /**
     * 是否命中count缓存
     *
     * @return 是否命中count缓存
     * @since 3.3.1
     * @deprecated 3.4.0 @2020-06-30 缓存遵循mybatis的一或二缓
     */
    @Deprecated
    default boolean isHitCount() {
        return false;
    }

    /**
     * 分页记录列表
     *
     * @return 分页对象记录列表
     */
    List<T> getRecords();

    /**
     * 设置分页记录列表
     */
    IPage<T> setRecords(List<T> records);

    /**
     * 当前满足条件总行数
     *
     * @return 总条数
     */
    long getTotal();

    /**
     * 设置当前满足条件总行数
     */
    IPage<T> setTotal(long total);

    /**
     * 获取每页显示条数
     *
     * @return 每页显示条数
     */
    long getSize();

    /**
     * 设置每页显示条数
     */
    IPage<T> setSize(long size);

    /**
     * 当前页
     *
     * @return 当前页
     */
    long getCurrent();

    /**
     * 设置当前页
     */
    IPage<T> setCurrent(long current);

    /**
     * IPage 的泛型转换
     *
     * @param mapper 转换函数
     * @param <R>    转换后的泛型
     * @return 转换泛型后的 IPage
     */
    @SuppressWarnings("unchecked")
    default <R> IPage<R> convert(Function<? super T, ? extends R> mapper) {
        List<R> collect = this.getRecords().stream().map(mapper).collect(toList());
        return ((IPage<R>) this).setRecords(collect);
    }

    /**
     * 老分页插件不支持
     * <p>
     * MappedStatement 的 id
     *
     * @return id
     * @since 3.4.0 @2020-06-19
     */
    default String countId() {
        return null;
    }

    /**
     * 生成缓存key值
     *
     * @return 缓存key值
     * @since 3.3.2
     * @deprecated 3.4.0 @2020-06-30
     */
    @Deprecated
    default String cacheKey() {
        StringBuilder key = new StringBuilder();
        key.append(offset()).append(StringPool.COLON).append(getSize());
        List<OrderItem> orders = orders();
        if (CollectionUtils.isNotEmpty(orders)) {
            for (OrderItem item : orders) {
                key.append(StringPool.COLON).append(item.getColumn()).append(StringPool.COLON).append(item.isAsc());
            }
        }
        return key.toString();
    }

}
