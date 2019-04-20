package com.baomidou.mybatisplus.dts;

/**
 * <p>
 * 常量类
 * </p>
 *
 * @author hubin
 * @since 2019-04-20
 */
public interface DtsConstants {
    /**
     * 队列配置
     */
    String EXCHANGE = "rabbit-exchange";
    String QUEUE = "rabbit-queue";
    String ROUTING_KEY = "rabbit-routing-key";
    /**
     * 死信队列配置
     */
    String DL_EXCHANGE = "rabbit-dl-exchange";
    String DL_QUEUE = "rabbit-dl-queue";
    String DL_ROUTING_KEY = "rabbit-dl-routing-key";

}
