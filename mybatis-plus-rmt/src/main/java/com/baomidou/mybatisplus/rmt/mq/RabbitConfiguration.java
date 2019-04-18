package com.baomidou.mybatisplus.rmt.mq;

import com.baomidou.mybatisplus.rmt.coordinator.IRmtCoordinator;
import com.baomidou.mybatisplus.rmt.coordinator.RedisRmtCoordinator;
import com.baomidou.mybatisplus.rmt.parser.IRmtParser;
import com.baomidou.mybatisplus.rmt.parser.JacksonRmtParser;
import com.baomidou.mybatisplus.rmt.sender.RabbitRmtSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.RabbitConnectionFactoryBean;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Rabbit MQ 连接工厂配置
 *
 * @author jobob
 * @since 2019-04-18
 */
@Slf4j
@EnableRabbit
@Configuration
public class RabbitConfiguration {
    @Value("${spring.rabbitmq.host}")
    String host;
    @Value("${spring.rabbitmq.port}")
    int port;
    @Value("${spring.rabbitmq.username}")
    String username;
    @Value("${spring.rabbitmq.password}")
    String password;
    @Value("${spring.rabbitmq.virtual.host}")
    String virtualHost;
    @Value("${spring.rabbitmq.cache.channel.size}")
    int cacheSize;

    /**
     * 创建RabbitMQ连接工厂
     *
     * @param
     * @return CachingConnectionFactory
     * @throws Exception 异常
     */
    @Bean
    public CachingConnectionFactory rabbitConnectionFactory() throws Exception {
        log.debug("custom rabbitmq connection factory");
        RabbitConnectionFactoryBean factory = new RabbitConnectionFactoryBean();
        factory.setHost(host);
        factory.setPort(port);
        factory.setUsername(username);
        factory.setPassword(password);
        factory.setVirtualHost(virtualHost);
        factory.setConnectionTimeout(60000);
        factory.setAutomaticRecoveryEnabled(true);
        factory.afterPropertiesSet();
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(factory.getObject());
        connectionFactory.setPublisherReturns(true);
        connectionFactory.setPublisherConfirms(true);
        connectionFactory.setChannelCacheSize(cacheSize);
        return connectionFactory;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        log.debug("custom rabbitmq Listener factory: {}", connectionFactory);
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setConcurrentConsumers(3);
        factory.setMaxConcurrentConsumers(10);
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        return factory;
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

    @Autowired
    private IRmtCoordinator rmtCoordinator;

    @Autowired
    private IRmtParser rmtParser;

    boolean returnFlag = false;

    @Bean
    public RabbitTemplate customRabbitTemplate(ConnectionFactory connectionFactory) {
        log.debug("==> custom rabbitTemplate, connectionFactory:" + connectionFactory);
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(rmtParser.getMessageConverter());
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (returnFlag) {
                log.error("mq发送错误，无对应的的交换机,confirm回掉,ack={},correlationData={} cause={}",
                        ack, correlationData, cause);
            }

            log.debug("confirm回调，ack={} correlationData={} cause={}", ack, correlationData, cause);
            String msgId = correlationData.getId();

            /** 只要消息能投入正确的消息队列，并持久化，就返回ack为true*/
            if (ack) {
                log.debug("消息已正确投递到队列, correlationData:{}", correlationData);
                rmtCoordinator.setSuccess(msgId);
            } else {
                log.error("消息投递至交换机失败,业务号:{}，原因:{}", correlationData.getId(), cause);
            }

        });

        //消息发送到RabbitMQ交换器，但无相应Exchange时的回调
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            String messageId = message.getMessageProperties().getMessageId();
            log.error("return回调，没有找到任何匹配的队列！message id:{},replyCode{},replyText:{},"
                    + "exchange:{},routingKey{}", messageId, replyCode, replyText, exchange, routingKey);
            returnFlag = true;
        });

        // rabbitTemplate.waitForConfirms(RmtConstants.TIME_GAP);
        return rabbitTemplate;
    }
}

