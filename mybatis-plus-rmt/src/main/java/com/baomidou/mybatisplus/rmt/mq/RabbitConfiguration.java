package com.baomidou.mybatisplus.rmt.mq;

import com.baomidou.mybatisplus.rmt.RmtConstants;
import com.baomidou.mybatisplus.rmt.coordinator.RedisRmtCoordinator;
import com.baomidou.mybatisplus.rmt.parser.JacksonRmtParser;
import com.baomidou.mybatisplus.rmt.sender.RabbitRmtSender;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.transaction.RabbitTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * Rabbit MQ 可靠消息配置
 *
 * @author jobob
 * @since 2019-04-19
 */
@Configuration
@ConditionalOnClass(EnableRabbit.class)
public class RabbitConfiguration {
    @Autowired
    protected PlatformTransactionManager transactionManager;
    @Autowired
    protected RabbitTemplate rabbitTemplate;
    @Autowired
    protected RabbitAdmin rabbitAdmin;

    @Bean
    public RabbitTransactionManager rabbitTransactionManager(ConnectionFactory connectionFactory) {
        return new RabbitTransactionManager(connectionFactory);
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @PostConstruct
    protected void init() {
        /**
         * make rabbit template to support transactions
         */
        rabbitTemplate.setChannelTransacted(true);

        /**
         * init queue
         */
        // define deadletter exchange and queue
        rabbitAdmin.declareExchange(new DirectExchange(RmtConstants.DL_EXCHANGE, true, false));
        rabbitAdmin.declareQueue(new Queue(RmtConstants.DL_QUEUE, true, false, false, null));
        rabbitAdmin.declareBinding(new Binding(RmtConstants.DL_QUEUE, Binding.DestinationType.QUEUE, RmtConstants.DL_EXCHANGE, RmtConstants.DL_ROUTING_KEY, null));

        // define simple exchange, queue with deadletter support and binding
        rabbitAdmin.declareExchange(new TopicExchange(RmtConstants.EXCHANGE, true, false));
        Map<String, Object> args = new HashMap<>(2);
        args.put("x-dead-letter-exchange", RmtConstants.DL_EXCHANGE);
        args.put("x-dead-letter-routing-key", RmtConstants.DL_ROUTING_KEY);
        rabbitAdmin.declareQueue(new Queue(RmtConstants.QUEUE, true, false, true, args));

        // declare binding
        rabbitAdmin.declareBinding(new Binding(RmtConstants.QUEUE, Binding.DestinationType.QUEUE, RmtConstants.EXCHANGE, RmtConstants.ROUTING_KEY, null));
    }

    /**
     * <p>
     * 配置可靠消息事务发送者
     * </p>
     *
     * @return
     */
    @Bean
    public RabbitTransactionalAspect rabbitTransactionalAspect() {
        return new RabbitTransactionalAspect();
    }

    @Bean
    public RedisRmtCoordinator redisRmtCoordinator() {
        return new RedisRmtCoordinator();
    }

    @Bean
    public JacksonRmtParser rmtParser() {
        return new JacksonRmtParser();
    }

    @Bean
    public RabbitRmtSender rmtSender() {
        return new RabbitRmtSender();
    }
}
