package com.baomidou.mybatisplus.test.h2.issues.aop;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author nieqiurong
 */
@Aspect
public class AopConfig2 {

    private static final Logger LOGGER = LoggerFactory.getLogger(AopConfig2.class);

    @Pointcut("execution(* com.baomidou.mybatisplus.test.h2.issues.aop.mapper..*.*(..))")
    public void point() {
    }

    @Before("point()")
    public void before() {
        LOGGER.info("before ...");
    }

}
