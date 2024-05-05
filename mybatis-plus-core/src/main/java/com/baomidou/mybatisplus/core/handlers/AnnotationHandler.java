/*
 * Copyright (c) 2011-2024, baomidou (jobob@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baomidou.mybatisplus.core.handlers;

import com.baomidou.mybatisplus.core.toolkit.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author 唐振超
 * @since 2023-02-25
 */
public interface AnnotationHandler {

    /**
     * 从类上获取注解
     *
     * @param beanClass       类的class
     * @param annotationClass 要获取的注解class
     * @param <T>             具体注解
     * @return 注解
     */
    default <T extends Annotation> T getAnnotation(Class<?> beanClass, Class<T> annotationClass) {
        return AnnotationUtils.findFirstAnnotation(annotationClass, beanClass);
    }

    /**
     * 判断类上是否存在注解
     *
     * @param beanClass       类的class
     * @param annotationClass 要获取的注解class
     * @param <T>             具体注解
     * @return 是否包含该注解
     */
    default <T extends Annotation> boolean isAnnotationPresent(Class<?> beanClass, Class<T> annotationClass) {
        return AnnotationUtils.findFirstAnnotation(annotationClass, beanClass) != null;
    }

    /**
     * 从字段上获取注解
     *
     * @param field           字段
     * @param annotationClass 要获取的注解class
     * @param <T>             具体注解
     * @return 注解
     */
    default <T extends Annotation> T getAnnotation(Field field, Class<T> annotationClass) {
        return AnnotationUtils.findFirstAnnotation(annotationClass, field);
    }

    /**
     * 判断字段上是否存在注解
     *
     * @param field           字段
     * @param annotationClass 要获取的注解class
     * @param <T>             具体注解
     * @return 是否包含该注解
     */
    default <T extends Annotation> boolean isAnnotationPresent(Field field, Class<T> annotationClass) {
        return AnnotationUtils.findFirstAnnotation(annotationClass, field) != null;
    }

    /**
     * 从方法上获取注解
     *
     * @param method          方法
     * @param annotationClass 要获取的注解class
     * @param <T>             具体注解
     * @return 注解
     */
    default <T extends Annotation> T getAnnotation(Method method, Class<T> annotationClass) {
        return AnnotationUtils.findFirstAnnotation(annotationClass, method);
    }

    /**
     * 判断方法上是否存在注解
     *
     * @param method           方法
     * @param annotationClass 要获取的注解class
     * @param <T>             具体注解
     * @return 是否包含该注解
     */
    default <T extends Annotation> boolean isAnnotationPresent(Method method, Class<T> annotationClass) {
        return AnnotationUtils.findFirstAnnotation(annotationClass, method) != null;
    }
}
