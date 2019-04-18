package com.baomidou.mybatisplus.rmt.config;

import com.baomidou.mybatisplus.rmt.mq.RabbitConfiguration;
import com.baomidou.mybatisplus.rmt.mq.RabbitQueueConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;


/**
 * <p>
 * 可靠消息事务自动配置
 * </p>
 *
 * @author jobob
 * @since 2019-04-18
 */
@Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
@Target(value = { java.lang.annotation.ElementType.TYPE })
@Documented
@Import({RabbitConfiguration.class, RabbitQueueConfiguration.class})
public @interface EnableRmtRabbit {

}
