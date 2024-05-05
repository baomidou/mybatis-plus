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
package com.baomidou.mybatisplus.core.toolkit;

import lombok.experimental.UtilityClass;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * @author nieqiurong
 * @since 3.5.6
 */
@UtilityClass
public class AnnotationUtils {

    public <T extends Annotation> T findFirstAnnotation(Class<T> annotationClazz, Field field) {
        return getAnnotation(annotationClazz, new HashSet<>(), field.getDeclaredAnnotations());
    }

    public <T extends Annotation> T findFirstAnnotation(Class<T> annotationClazz, Class<?> clz) {
        Set<Class<? extends Annotation>> hashSet = new HashSet<>();
        T annotation = getAnnotation(annotationClazz, hashSet, clz.getDeclaredAnnotations());
        if (annotation != null) {
            return annotation;
        }
        Class<?> currentClass = clz.getSuperclass();
        while (currentClass != null) {
            annotation = getAnnotation(annotationClazz, hashSet, currentClass.getDeclaredAnnotations());
            if (annotation != null) {
                return annotation;
            }
            currentClass = currentClass.getSuperclass();
        }
        return null;
    }

    public <T extends Annotation> T findFirstAnnotation(Class<T> annotationClazz, Method method) {
        return getAnnotation(annotationClazz, new HashSet<>(), method.getDeclaredAnnotations());
    }

    @SuppressWarnings("unchecked")
    private <T extends Annotation> T getAnnotation(Class<T> annotationClazz, Set<Class<? extends Annotation>> annotationSet, Annotation... annotations) {
        for (Annotation annotation : annotations) {
            if (annotationSet.add(annotation.annotationType())) {
                if (annotationClazz.isAssignableFrom(annotation.annotationType())) {
                    return (T) annotation;
                }
                annotation = getAnnotation(annotationClazz, annotationSet, annotation.annotationType().getDeclaredAnnotations());
                if (annotation != null) {
                    return (T) annotation;
                }
            }
        }
        return null;
    }

}
