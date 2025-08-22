package com.baomidou.mybatisplus.core.handlers;

import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * 批量结果处理器
 *
 * <p>将查询结果按指定批量大小缓存，
 * 达到批量大小时调用批量消费函数进行批量处理，避免一次性加载全部数据导致内存压力过大
 *
 * <p>适用于导出Excel、批量写库、定时上报等需要批量操作场景，
 * 能有效降低内存使用并提升处理性能
 *
 * <p><b>使用须知：</b><br>
 * 1. 构造时传入非空的批量消费函数和合法的批量大小（>0）<br>
 * 2. 结果回调时自动缓存并批量触发消费函数<br>
 * 3. 查询完成后务必调用 {@link #flush()}，处理剩余不足批量的数据，避免数据遗漏
 *
 * @param <T> 查询结果类型
 * @author AprilWind
 */
public class BatchResultHandler<T> implements ResultHandler<T> {

    /**
     * 批量数据消费函数
     *
     * <p>注意：长时间的批量处理操作（如写Excel、写数据库或调用远程接口）可能导致数据库连接超时或断开，
     * 以及MyBatis查询线程阻塞，影响系统性能和稳定性。
     *
     * <p>因此推荐：
     * <ul>
     *   <li>结合合适的批量大小和限速策略，避免瞬时过载</li>
     *   <li>采用异步写入机制，将批量处理操作与MyBatis查询线程解耦，减少阻塞</li>
     *   <li>合理拆分查询任务，避免长时间持有数据库连接</li>
     * </ul>
     *
     * <p>这样能有效保障系统的高性能和稳定运行
     */
    private final Consumer<List<T>> batchConsumer;

    /**
     * 批量大小，必须大于0
     */
    private final int batchSize;

    /**
     * 当前缓存数据
     */
    private final List<T> buffer;

    /**
     * 构造批量结果处理器
     *
     * @param batchConsumer 批量消费函数，不能为null
     * @param batchSize     批量大小，必须大于0
     */
    public BatchResultHandler(Consumer<List<T>> batchConsumer, int batchSize) {
        this.batchConsumer = batchConsumer;
        this.batchSize = batchSize;
        this.buffer = new ArrayList<>(batchSize);
    }

    /**
     * MyBatis 查询结果回调，缓存单条结果，
     * 达到批量大小时触发批量消费函数处理缓存数据
     *
     * @param context 查询结果上下文，包含单条结果
     */
    @Override
    public void handleResult(ResultContext<? extends T> context) {
        buffer.add(context.getResultObject());
        if (buffer.size() >= batchSize) {
            flush();
        }
    }

    /**
     * 刷新缓存，触发批量消费函数处理剩余数据，
     * 通常查询结束后调用，确保所有数据均被处理
     */
    public void flush() {
        if (!buffer.isEmpty()) {
            batchConsumer.accept(new ArrayList<>(buffer));
            buffer.clear();
        }
    }
}
