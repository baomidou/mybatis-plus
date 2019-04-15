/*
 * Copyright (c) 2011-2020, baomidou (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
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
 * 支持普通枚举类字段, 只用在enum类的字段上
 * <p>当实体类的属性是普通枚举，且是其中一个字段，使用该注解来标注枚举类里的那个属性对应字段</p>
 * <p>
 * 使用方式参考 com.baomidou.mybatisplus.test.h2.H2StudentMapperTest
 * <pre>
 * &#64;TableName("student")
 * class Student {
 *     private Integer id;
 *     private String name;
 *     private GradeEnum grade;//数据库grade字段类型为int
 * }
 *
 * public enum GradeEnum {
 *     PRIMARY(1,"小学"),
 *     SECONDORY("2", "中学"),
 *     HIGH(3, "高中");
 *
 *     &#64;EnumValue
 *     private final int code;
 *     private final String descp;
 * }
 * </pre>
 * </p>
 *
 * @author yuxiaobin
 * @date 2018/8/30
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface EnumValue {

}
