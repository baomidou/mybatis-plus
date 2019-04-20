package com.baomidou.mybatisplus.dts.config;

import com.baomidou.mybatisplus.dts.parser.JacksonRmtParser;
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
public class DtsAutoConfiguration {

    @Bean
    public JacksonRmtParser rmtParser() {
        return new JacksonRmtParser();
    }

    @Bean
    public RabbitRmtSender rmtSender() {
        return new RabbitRmtSender();
    }

}
