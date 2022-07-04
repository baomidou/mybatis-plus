package com.baomidou.mybatisplus.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Method映射XML语句
 * 抛弃原有映射关系重新映射
 * 若函数没有默认映射关系则直接绑定
 * 若函数已有默认映射关系需要{&#064;Param  overlay} 设置是否覆盖映射关系
 *
 * @author 16K
 * @author yixuan.zhang
 * @since 2022/07/03
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MappedStatementXML {

    /**
     * XML语句ID
     */
    String id();

    /**
     * 是否覆盖已有映射关系
     *
     * 值为true 覆盖
     * 值为false 不覆盖
     */
    boolean overlay() default false;

}
