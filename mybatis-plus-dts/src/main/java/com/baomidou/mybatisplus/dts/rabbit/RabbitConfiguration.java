package com.baomidou.mybatisplus.dts.rabbit;

import com.baomidou.mybatisplus.dts.DtsConstants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
    protected RabbitTemplate rabbitTemplate;
    @Autowired
    protected RabbitAdmin rabbitAdmin;

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @PostConstruct
    protected void init() {
        // make rabbit template to support transactions
        rabbitTemplate.setChannelTransacted(true);

        // define deadletter exchange and queue
        rabbitAdmin.declareExchange(new DirectExchange(DtsConstants.DL_EXCHANGE, true, false));
        rabbitAdmin.declareQueue(new Queue(DtsConstants.DL_QUEUE, true, false, false, null));
        rabbitAdmin.declareBinding(new Binding(DtsConstants.DL_QUEUE, Binding.DestinationType.QUEUE, DtsConstants.DL_EXCHANGE, DtsConstants.DL_ROUTING_KEY, null));

        // define simple exchange, queue with deadletter support and binding
        rabbitAdmin.declareExchange(new TopicExchange(DtsConstants.EXCHANGE, true, false));
        Map<String, Object> args = new HashMap<>(2);
        args.put("x-dead-letter-exchange", DtsConstants.DL_EXCHANGE);
        args.put("x-dead-letter-routing-key", DtsConstants.DL_ROUTING_KEY);
        rabbitAdmin.declareQueue(new Queue(DtsConstants.QUEUE, true, false, true, args));

        // declare binding
        rabbitAdmin.declareBinding(new Binding(DtsConstants.QUEUE, Binding.DestinationType.QUEUE, DtsConstants.EXCHANGE, DtsConstants.ROUTING_KEY, null));
    }
}
