package com.baomidou.mybatisplus.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DuplicationValidation {

    String value() default "";

    String[] conditions() default {};

}
