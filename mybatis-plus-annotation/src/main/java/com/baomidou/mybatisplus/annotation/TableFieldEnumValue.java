package com.baomidou.mybatisplus.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * 支持普通枚举类字段, 只用在enum类的字段上
 *
 * 当实体类的属性是普通枚举，且是其中一个字段，使用该注解来标注枚举类里的那个属性对应字段
 *
 * class Student{
 *     private Integer id;
 *     private String name;
 *     private GradeEnum grade;
 * }
 *
 * public enum GradeEnum{
 *     PRIMARY(1,"小学"),
 *     SECONDORY("2", "中学"),
 *     HIGH(3, "高中");
 *
 *      @TableFieldEnumValue
 *      private final int code;
 *      private final String descp;
 *
 * }
 * </p>
 *
 * @author yuxiaobin
 * @date 2018/8/30
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface TableFieldEnumValue {

}
