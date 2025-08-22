package com.baomidou.mybatisplus.core.handlers;

import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * 过滤处理器
 * <p>
 * 通过传入的过滤条件（{@link Predicate}）筛选查询结果，
 * 仅对满足条件的结果调用消费函数进行处理
 * <p>
 * 适用于需要对查询结果做条件过滤再处理的场景
 * 避免无用数据进入后续处理逻辑，提高效率
 *
 * @param <T> 查询结果的数据类型
 * @author AprilWind
 */
public class FilterResultHandler<T> implements ResultHandler<T> {

    /**
     * 过滤条件，用于判断是否处理该条结果
     */
    private final Predicate<T> filter;

    /**
     * 处理满足过滤条件的结果的消费函数
     */
    private final Consumer<T> consumer;

    /**
     * 构造过滤处理器
     *
     * @param filter   过滤条件
     * @param consumer 满足过滤条件时的处理函数
     */
    public FilterResultHandler(Predicate<T> filter, Consumer<T> consumer) {
        this.filter = filter;
        this.consumer = consumer;
    }

    /**
     * 查询结果回调，先用过滤条件判断是否处理
     * 满足条件则调用消费函数，否则忽略该条结果
     *
     * @param context 查询结果上下文，包含单条结果
     */
    @Override
    public void handleResult(ResultContext<? extends T> context) {
        T result = context.getResultObject();
        if (filter.test(result)) {
            consumer.accept(result);
        }
    }
}
