/**
 * Copyright (c) 2011-2014, hubin (jobob@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.baomidou.mybatisplus.enums.FieldStrategy;

/**
 * <p>
 * 表字段标识
 * </p>
 * 
 * @author hubin sjy
 * @Date 2016-09-09
 */
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
	 * 支持：@TableField(el = "role, typeHandler=com.baomidou.xx.typehandler.PhoneTypeHandler")
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
	 * 字段验证
	 * </p>
	 * <p>
	 * 默认 非 null 判断
	 * </p>
	 */
	FieldStrategy validate() default FieldStrategy.NOT_NULL;

}
