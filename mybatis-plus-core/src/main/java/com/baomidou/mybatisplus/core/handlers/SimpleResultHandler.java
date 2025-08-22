package com.baomidou.mybatisplus.core.handlers;

import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;

import java.util.function.Consumer;

/**
 * 单条处理处理器
 *
 * <p>适用于无需缓存或批量处理，直接对结果逐条消费的场景
 * 逻辑简单直接，实时处理每条数据
 *
 * @param <T> 查询结果的数据类型
 * @author AprilWind
 */
public class SimpleResultHandler<T> implements ResultHandler<T> {

    /**
     * 消费单条查询结果的函数
     */
    private final Consumer<T> consumer;

    /**
     * 构造 SimpleResultHandler 实例
     *
     * @param consumer 处理单条查询结果的消费函数，不能为空
     */
    public SimpleResultHandler(Consumer<T> consumer) {
        this.consumer = consumer;
    }

    /**
     * MyBatis 查询结果回调方法，
     * 每接收到一条查询结果即调用消费函数进行处理
     *
     * @param context 查询结果上下文，包含当前单条结果对象
     */
    @Override
    public void handleResult(ResultContext<? extends T> context) {
        T result = context.getResultObject();
        consumer.accept(result);
    }
}
