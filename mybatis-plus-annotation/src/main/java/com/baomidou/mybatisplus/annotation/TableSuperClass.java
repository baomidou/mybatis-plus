package com.baomidou.mybatisplus.annotation;

import java.lang.annotation.*;

/**
 * @author by keray
 * date:2019/8/9 23:15
 * 标识该类的字段属性是被子类继承
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TableSuperClass {

}
