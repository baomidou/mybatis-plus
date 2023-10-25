package com.baomidou.mybatisplus.test.h2.issues.aop;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author nieqiurong
 */
@Aspect
@EnableAspectJAutoProxy
public class AopConfig1 {

    private static final Logger LOGGER = LoggerFactory.getLogger(AopConfig1.class);

    @Pointcut("execution(* com.baomidou.mybatisplus.test.h2.issues.aop.mapper..*.*(..))")
    public void point() {
    }

    @Before("point()")
    public void before() {
        LOGGER.info("before ...");
    }

    @After("point()")
    public void after() {
        LOGGER.info("After ...");
    }

}
