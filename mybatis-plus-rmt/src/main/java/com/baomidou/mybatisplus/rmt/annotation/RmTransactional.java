package com.baomidou.mybatisplus.rmt.annotation;

import com.baomidou.mybatisplus.rmt.RmtConstants;

import java.lang.annotation.*;

/**
 * <p>
 * 可靠消息事务 reliable message transactional
 * </p>
 *
 * @author jobob
 * @since 2019-04-17
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
@Documented
public @interface RmTransactional {

    /**
     * 业务值
     */
    String value() default "";

    /**
     * 交换器
     */
    String exchange() default RmtConstants.EXCHANGE;

    /**
     * 路由 KEY
     */
    String routingKey() default RmtConstants.KEY;
}
