package com.baomidou.mybatisplus.rmt.mq;

import com.baomidou.mybatisplus.rmt.RmtConstants;
import com.baomidou.mybatisplus.rmt.listener.DeadLetterMessageListener;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * RabbitMQ交换机、队列的配置类.定义交换机、key、queue并做好绑定。
 * 同时定义每个队列的ttl，队列最大长度，Qos等等
 *
 * @author jobob
 * @since 2019-04-18
 */
@Configuration
public class RabbitQueueConfiguration {


    /**
     * 死信交换机
     */
    @Bean
    public DirectExchange dlxExchange() {
        return new DirectExchange(RmtConstants.DLX_EXCHANGE);
    }

    /**
     * 死信队列
     */
    @Bean
    public Queue dlxQueue() {
        return new Queue(RmtConstants.DLX_QUEUE,true,false,false);
    }

    /**
     * 通过死信路由key绑定死信交换机和死信队列
     */
    @Bean
    public Binding dlxBinding() {
        return BindingBuilder.bind(dlxQueue()).to(dlxExchange())
                .with(RmtConstants.DLX_ROUTING_KEY);
    }

    @Bean
    public DeadLetterMessageListener deadLetterMessageListener() {
        return new DeadLetterMessageListener();
    }

    /**
     *
     * 死信队列的监听
     *
     * @param connectionFactory RabbitMQ连接工厂
     * @param deadLetterMessageListener  死信队列监听
     * @return 监听容器对象
     */
    @Bean
    public SimpleMessageListenerContainer deadLetterListenerContainer(ConnectionFactory connectionFactory,
    		DeadLetterMessageListener deadLetterMessageListener) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setQueues(dlxQueue());
        container.setExposeListenerChannel(true);
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        container.setMessageListener(deadLetterMessageListener);
        // 设置消费者能处理消息的最大个数
        container.setPrefetchCount(100);
        return container;
    }
}
