package com.baomidou.mybatisplus.dts.rabbit.config;

import com.baomidou.mybatisplus.dts.rabbit.coordinator.RedisRmtCoordinator;
import com.baomidou.mybatisplus.dts.rabbit.mq.RabbitTransactionalAspect;
import com.baomidou.mybatisplus.dts.rabbit.parser.JacksonRmtParser;
import com.baomidou.mybatisplus.dts.rabbit.sender.RabbitRmtSender;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Rabbit MQ 可靠消息配置
 *
 * @author jobob
 * @since 2019-04-19
 */
@Configuration
public class RmtAutoConfiguration {

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

    /**
     * 配置可靠消息事务发送者
     */
    @Bean
    public RabbitTransactionalAspect rabbitTransactionalAspect() {
        return new RabbitTransactionalAspect();
    }

}
