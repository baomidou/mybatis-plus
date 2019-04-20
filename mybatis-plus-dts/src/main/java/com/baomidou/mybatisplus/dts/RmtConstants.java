package com.baomidou.mybatisplus.dts;

/**
 * <p>
 * 常量类
 * </p>
 *
 * @author hubin
 * @since 2019-04-18
 */
public interface RmtConstants {
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


    /**
     * 默认 KEY
     */
    String KEY = "rabbit.key";
    /**
     * 消息重发计数
     */
    String MQ_RESEND_COUNTER = "mq.resend.counter";

    /**
     * 消息最大重发次数
     */
    long MAX_RETRY_COUNT = 3;

    /**
     * 分隔符
     */
    String DB_SPLIT = ",";

    /**
     * 缓存超时时间,超时进行重发
     */
    long TIME_GAP = 2000;

    /**
     * 处于ready状态消息
     */
    Object MQ_MSG_READY = "mq.msg.ready";

    /**
     * 处于prepare状态消息
     */
    Object MQ_MSG_PREPARE = "mq.msg.prepare";


    String MQ_PRODUCER_RETRY_KEY = "mq.producer.retry.key";
    String MQ_CONSUMER_RETRY_COUNT_KEY = "mq.consumer.retry.count.key";

    /**
     * 发送端重试乘数(ms)
     */
    int MUTIPLIER_TIME = 500;
    /**
     * 发送端最大重试时时间（s）
     */
    int MAX_RETRY_TIME = 10;
    /**
     * 消费端最大重试次数
     */
    int MAX_CONSUMER_COUNT = 5;
    /**
     * 递增时的基本常量
     */
    int BASE_NUM = 2;
    /**
     * 空的字符串
     */
    String BLANK_STR = "";


}
