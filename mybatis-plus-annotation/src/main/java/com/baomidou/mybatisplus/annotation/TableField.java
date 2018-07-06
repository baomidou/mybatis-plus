/*
 * Copyright (c) 2011-2020, hubin (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * <p>
 * 表字段标识
 * </p>
 *
 * @author hubin sjy tantan
 * @since 2016-09-09
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface TableField {

    /**
     * <p>
     * 字段值（驼峰命名方式，该值可无）
     * </p>
     */
    String value() default "";

    /**
     * <p>
     * 当该Field为类对象时, 可使用#{对象.属性}来映射到数据表.
     * </p>
     * <p>
     * 支持：@TableField(el = "role, jdbcType=BIGINT)<br>
     * 支持：@TableField(el = "role, typeHandler=com.baomidou.springcloud.typehandler.PhoneTypeHandler")
     * </p>
     */
    String el() default "";

    /**
     * <p>
     * 是否为数据库表字段
     * </p>
     * <p>
     * 默认 true 存在，false 不存在
     * </p>
     */
    boolean exist() default true;

    /**
     * <p>
     * 字段 where 实体查询比较条件
     * </p>
     * <p>
     * 默认 `=` 等值
     * </p>
     */
    String condition() default "";

    /**
     * <p>
     * 字段 update set 部分注入, 该注解优于 el 注解使用
     * </p>
     * <p>
     * 例如：@TableField(.. , update="%s+1") 其中 %s 会填充为字段
     * 输出 SQL 为：update 表 set 字段=字段+1 where ...
     * </p>
     * <p>
     * 例如：@TableField(.. , update="now()") 使用数据库时间
     * 输出 SQL 为：update 表 set 字段=now() where ...
     * </p>
     */
    String update() default "";

    /**
     * <p>
     * 字段验证策略
     * </p>
     * <p>
     * 默认 非 null 判断
     * </p>
     */
    FieldStrategy strategy() default FieldStrategy.NOT_NULL;

    /**
     * <p>
     * 字段自动填充策略
     * </p>
     */
    FieldFill fill() default FieldFill.DEFAULT;
}
