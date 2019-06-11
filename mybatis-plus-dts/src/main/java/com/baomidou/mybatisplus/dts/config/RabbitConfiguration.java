/*
 * Copyright (c) 2011-2020, baomidou (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.dts.config;

import com.baomidou.mybatisplus.dts.DtsConstants;
import com.baomidou.mybatisplus.dts.listener.RabbitRmtListener;
import com.baomidou.mybatisplus.dts.sender.RabbitRmtSender;
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
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
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
    public RabbitRmtSender rmtSender() {
        return new RabbitRmtSender();
    }

    @Bean
    public RabbitRmtListener rabbitRmtListener() {
        return new RabbitRmtListener();
    }

    @Bean
    @ConditionalOnMissingBean
    public RabbitTransactionManager rabbitTransactionManager(ConnectionFactory connectionFactory) {
        return new RabbitTransactionManager(connectionFactory);
    }

    @Bean
    @ConditionalOnMissingBean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @PostConstruct
    protected void init() {
        // make rmt template to support transactions
        rabbitTemplate.setChannelTransacted(true);

        // define deadletter exchange and queue
        rabbitAdmin.declareExchange(new DirectExchange(DtsConstants.RABBIT_DEADLETTER_EXCHANGE, true, false));
        rabbitAdmin.declareQueue(new Queue(DtsConstants.RABBIT_DEADLETTER_QUEUE, true, false, false, null));
        rabbitAdmin.declareBinding(new Binding(DtsConstants.RABBIT_DEADLETTER_QUEUE, Binding.DestinationType.QUEUE,
            DtsConstants.RABBIT_DEADLETTER_EXCHANGE, DtsConstants.RABBIT_DEADLETTER_ROUTINGKEY, null));

        // define simple exchange, queue with deadletter support and binding
        rabbitAdmin.declareExchange(new TopicExchange(DtsConstants.RABBIT_EXCHANGE, true, false));
        Map<String, Object> args = new HashMap<>(2);
        args.put("x-dead-letter-exchange", DtsConstants.RABBIT_DEADLETTER_EXCHANGE);
        args.put("x-dead-letter-routing-key", DtsConstants.RABBIT_DEADLETTER_ROUTINGKEY);
        rabbitAdmin.declareQueue(new Queue(DtsConstants.RABBIT_QUEUE, true, false, true, args));

        // declare binding
        rabbitAdmin.declareBinding(new Binding(DtsConstants.RABBIT_QUEUE, Binding.DestinationType.QUEUE, DtsConstants.RABBIT_EXCHANGE,
            DtsConstants.RABBIT_ROUTINGKEY, null));
    }
}
