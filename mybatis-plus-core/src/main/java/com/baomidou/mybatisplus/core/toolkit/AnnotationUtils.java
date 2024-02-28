/*
 * Copyright (c) 2011-2023, baomidou (jobob@qq.com).
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

/**
 * @author nieqiurong
 * @since 3.5.6
 */
@UtilityClass
public class AnnotationUtils {

    private static final String javaPackage = Override.class.getPackage().getName();

    public <T extends Annotation> T findFirstAnnotation(Class<T> annotationClazz, Field field) {
        return getAnnotation(annotationClazz, field.getDeclaredAnnotations());
    }

    @SuppressWarnings("unchecked")
    private <T extends Annotation> T getAnnotation(Class<T> annotationClazz, Annotation... annotations) {
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().getPackage().getName().startsWith(javaPackage)) {
                continue;
            }
            if (annotationClazz.isAssignableFrom(annotation.annotationType())) {
                return (T) annotation;
            }
            annotation = getAnnotation(annotationClazz, annotation.annotationType().getDeclaredAnnotations());
            if (annotation != null) {
                return (T) annotation;
            }
        }
        return null;
    }

}
