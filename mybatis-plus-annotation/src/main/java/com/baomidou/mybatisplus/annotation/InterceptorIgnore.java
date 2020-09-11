package com.baomidou.mybatisplus.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 内置插件的一些过滤规则
 * <p>
 * 支持注解在 mapper 上以及 mapper.method 上
 * 同时存在则 mapper.method 比 mapper 优先级高
 * <p>
 * 支持:
 * true 和 false , 1 和 0 , on 和 off
 * <p>
 * 各属性返回 true 表示不走插件(在配置了插件的情况下,不填则默认表示 false)
 *
 * @author miemie
 * @since 2020-07-31
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface InterceptorIgnore {

    /**
     * 行级租户 {@link com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor}
     */
    String tenantLine() default "";

    /**
     * 动态表名 {@link com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor}
     */
    String dynamicTableName() default "";

    /**
     * 攻击 SQL 阻断解析器,防止全表更新与删除 {@link com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor}
     */
    String blockAttack() default "";

    /**
     * 垃圾SQL拦截 {@link com.baomidou.mybatisplus.extension.plugins.inner.IllegalSQLInnerInterceptor}
     */
    String illegalSql() default "";

    /**
     * 是否忽视所有interceptor
     */
    boolean ignoreAll() default false;

    /**
     * 忽略interceptor集合
     */
    Class<?>[] ignores() default {};
}
