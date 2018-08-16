package com.baomidou.mybatisplus.toolkit;

import java.lang.reflect.Field;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopProxy;

import com.baomidou.mybatisplus.exceptions.MybatisPlusException;

/**
 * AopUtils Aop工具类
 *
 * @author Caratacus
 * @date 2018/08/02
 */
public class AopUtils {

    private static final Log logger = LogFactory.getLog(IOUtils.class);

    /**
     * 获取源目标对象
     *
     * @param proxy
     * @param <T>
     * @return
     */
    public static <T> T getTargetObject(T proxy) {
        if (!org.springframework.aop.support.AopUtils.isAopProxy(proxy)) {
            return proxy;
        }
        try {
            if (org.springframework.aop.support.AopUtils.isJdkDynamicProxy(proxy)) {
                return getJdkDynamicProxyTargetObject(proxy);
            } else if (org.springframework.aop.support.AopUtils.isCglibProxy(proxy)) {
                return getCglibProxyTargetObject(proxy);
            } else {
                logger.warn("Warn: The proxy object processing method is not supported.");
                return proxy;
            }
        } catch (Exception e) {
            throw new MybatisPlusException("Error: Get proxy targetObject exception !  Cause:" + e);
        }
    }

    /**
     * 获取Cglib源目标对象
     *
     * @param proxy
     * @param <T>
     * @return
     */
    private static <T> T getCglibProxyTargetObject(T proxy) throws Exception {
        Field cglibField = proxy.getClass().getDeclaredField("CGLIB$CALLBACK_0");
        cglibField.setAccessible(true);
        Object dynamicAdvisedInterceptor = cglibField.get(proxy);
        Field advised = dynamicAdvisedInterceptor.getClass().getDeclaredField("advised");
        advised.setAccessible(true);
        Object target = ((AdvisedSupport) advised.get(dynamicAdvisedInterceptor)).getTargetSource().getTarget();
        return (T) target;
    }

    /**
     * 获取JdkDynamic源目标对象
     *
     * @param proxy
     * @param <T>
     * @return
     */
    private static <T> T getJdkDynamicProxyTargetObject(T proxy) throws Exception {
        Field jdkDynamicField = proxy.getClass().getSuperclass().getDeclaredField("jdkDynamicField");
        jdkDynamicField.setAccessible(true);
        AopProxy aopProxy = (AopProxy) jdkDynamicField.get(proxy);
        Field advised = aopProxy.getClass().getDeclaredField("advised");
        advised.setAccessible(true);
        Object target = ((AdvisedSupport) advised.get(aopProxy)).getTargetSource().getTarget();
        return (T) target;
    }

}
