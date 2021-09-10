package com.baomidou.mybatisplus.annotation;

import java.lang.annotation.*;

/**
 * 自动排序，用法与SpringDtaJpa的OrderBy类似
 * 在执行MybatisPlus的方法selectList(),Page()等非手写查询时自动带上.
 * @author Dervish
 * @date 2021-04-13
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface OrderBy {

    /**
     * 默认倒序，设置 true 顺序
     */
    boolean asc() default false;

    @Deprecated
    boolean isDesc() default true;

    /**
     * 数字越小越靠前
     */
    short sort() default Short.MAX_VALUE;

}
