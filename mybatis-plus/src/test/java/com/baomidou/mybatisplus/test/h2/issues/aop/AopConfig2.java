package com.baomidou.mybatisplus.test.h2.issues.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

/**
 * @author nieqiurong
 */
@Aspect
@Slf4j
public class AopConfig2 {

    @Pointcut("execution(* com.baomidou.mybatisplus.test.h2.issues.aop.mapper..*.*(..))")
    public void point() {
    }

    @Before("point()")
    public void before() {
        log.info("before ...");
    }

}
