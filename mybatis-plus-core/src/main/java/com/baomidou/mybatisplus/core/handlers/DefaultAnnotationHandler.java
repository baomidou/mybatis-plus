package com.baomidou.mybatisplus.core.handlers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * @author 唐振超
 */
public class DefaultAnnotationHandler implements AnnotationHandler {
    @Override
    public <T extends Annotation> T getAnnotation(Class<?> beanClass, Class<T> annotationClass) {
        return beanClass.getAnnotation(annotationClass);
    }

    @Override
    public <T extends Annotation> boolean isAnnotationPresent(Class<?> beanClass, Class<T> annotationClass) {
        return beanClass.isAnnotationPresent(annotationClass);
    }

    @Override
    public <T extends Annotation> T getAnnotation(Field field, Class<T> annotationClass) {
        return field.getAnnotation(annotationClass);
    }

    @Override
    public <T extends Annotation> boolean isAnnotationPresent(Field field, Class<T> annotationClass) {
        return field.isAnnotationPresent(annotationClass);
    }
}
