package com.baomidou.mybatisplus.test.h2;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.handlers.AnnotationHandler;
import com.baomidou.mybatisplus.test.h2.fillperformance.annotation.InsertUpdateFill;
import com.baomidou.mybatisplus.test.h2.fillperformance.model.PerformanceModel;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.UnknownTypeHandler;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;


public class H2AnnotationHandler implements AnnotationHandler {

    /**
     * 结合{@link PerformanceModel#getA()}简单实现{@link com.baomidou.mybatisplus.test.h2.fillperformance.annotation.InsertUpdateFill} 的获取过程
     *
     * @param field           字段
     * @param annotationClass 要获取的注解class
     * @param <T>
     * @return
     */
    @Override
    public <T extends Annotation> T getAnnotation(Field field, Class<T> annotationClass) {

        T annotation = field.getAnnotation(annotationClass);
        if (annotationClass != TableField.class) {
            return annotation;
        }

        Annotation insertUpdateFillAnno = field.getAnnotation(InsertUpdateFill.class);
        if (insertUpdateFillAnno == null) {
            return annotation;
        }

        /*
         如果是要获取TableField场景，尝试判断是否存在InsertUpdateFill，存在则假定存在@TableField(fill = FieldFill.INSERT_UPDATE)的配置，
         实际应用场景，应采取更加通用的方式，例如Spring的AnnotationUtils等
         */
        if (annotation != null) {
            TableField finalAnnotation = (TableField) annotation;
            annotation = (T) new TableField() {
                @Override
                public Class<? extends Annotation> annotationType() {
                    return finalAnnotation.annotationType();
                }

                @Override
                public String value() {
                    return finalAnnotation.value();
                }

                @Override
                public boolean exist() {
                    return finalAnnotation.exist();
                }

                @Override
                public String condition() {
                    return finalAnnotation.value();
                }

                @Override
                public String update() {
                    return finalAnnotation.update();
                }

                @Override
                public FieldStrategy insertStrategy() {
                    return finalAnnotation.insertStrategy();
                }

                @Override
                public FieldStrategy updateStrategy() {
                    return finalAnnotation.updateStrategy();
                }

                @Override
                public FieldStrategy whereStrategy() {
                    return finalAnnotation.whereStrategy();
                }

                @Override
                public FieldFill fill() {
                    return FieldFill.INSERT_UPDATE;
                }

                @Override
                public boolean select() {
                    return finalAnnotation.select();
                }

                @Override
                public boolean keepGlobalFormat() {
                    return finalAnnotation.keepGlobalFormat();
                }

                @Override
                public String property() {
                    return finalAnnotation.value();
                }

                @Override
                public JdbcType jdbcType() {
                    return finalAnnotation.jdbcType();
                }

                @Override
                public Class<? extends TypeHandler> typeHandler() {
                    return finalAnnotation.typeHandler();
                }

                @Override
                public boolean javaType() {
                    return finalAnnotation.javaType();
                }

                @Override
                public String numericScale() {
                    return finalAnnotation.value();
                }
            };
        } else {
            annotation = (T) new TableField() {
                @Override
                public Class<? extends Annotation> annotationType() {
                    return TableField.class;
                }

                @Override
                public String value() {
                    return "";
                }

                @Override
                public boolean exist() {
                    return true;
                }

                @Override
                public String condition() {
                    return "";
                }

                @Override
                public String update() {
                    return "";
                }

                @Override
                public FieldStrategy insertStrategy() {
                    return FieldStrategy.DEFAULT;
                }

                @Override
                public FieldStrategy updateStrategy() {
                    return FieldStrategy.DEFAULT;
                }

                @Override
                public FieldStrategy whereStrategy() {
                    return FieldStrategy.DEFAULT;
                }

                @Override
                public FieldFill fill() {
                    return FieldFill.INSERT_UPDATE;
                }

                @Override
                public boolean select() {
                    return true;
                }

                @Override
                public boolean keepGlobalFormat() {
                    return false;
                }

                @Override
                public String property() {
                    return "";
                }

                @Override
                public JdbcType jdbcType() {
                    return JdbcType.UNDEFINED;
                }

                @Override
                public Class<? extends TypeHandler> typeHandler() {
                    return UnknownTypeHandler.class;
                }

                @Override
                public boolean javaType() {
                    return false;
                }

                @Override
                public String numericScale() {
                    return "";
                }
            };
        }
        return annotation;
    }

    @Override
    public <T extends Annotation> boolean isAnnotationPresent(Field field, Class<T> annotationClass) {
        return getAnnotation(field, annotationClass) != null;
    }
}
