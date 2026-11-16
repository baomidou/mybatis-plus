package com.baomidou.mybatisplus.test.h2.issues.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author nieqiurong
 */
@Aspect
@EnableAspectJAutoProxy
@Slf4j
public class AopConfig1 {

    @Pointcut("execution(* com.baomidou.mybatisplus.test.h2.issues.aop.mapper..*.*(..))")
    public void point() {
    }

    @Before("point()")
    public void before() {
        log.info("before ...");
    }

    @After("point()")
    public void after() {
        log.info("After ...");
    }

}
