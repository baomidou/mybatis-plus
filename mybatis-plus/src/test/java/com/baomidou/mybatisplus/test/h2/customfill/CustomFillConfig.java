package com.baomidou.mybatisplus.test.h2.customfill;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.handlers.AnnotationHandler;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.baomidou.mybatisplus.test.h2.customfill.annotation.InsertUpdateFill;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.EnumOrdinalTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.UnknownTypeHandler;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@MapperScan("com.baomidou.mybatisplus.test.h2.customfill.mapper")
public class CustomFillConfig {

    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean sqlSessionFactory = new MybatisSqlSessionFactoryBean();
        sqlSessionFactory.setDataSource(dataSource);
        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setJdbcTypeForNull(JdbcType.NULL);
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setDefaultExecutorType(ExecutorType.REUSE);
        configuration.setDefaultEnumTypeHandler(EnumOrdinalTypeHandler.class);
        sqlSessionFactory.setConfiguration(configuration);
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setMetaObjectHandler(metaObjectHandler());
        globalConfig.setAnnotationHandler(annotationHandler());
        sqlSessionFactory.setGlobalConfig(globalConfig);

        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        sqlSessionFactory.setPlugins(mybatisPlusInterceptor);

        return sqlSessionFactory.getObject();
    }

    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new MetaObjectHandler() {
            @Override
            public void insertFill(MetaObject metaObject) {
                Object object = metaObject.getOriginalObject();
                Class<?> clazz = object.getClass();

                Field[] declaredFields = clazz.getDeclaredFields();
                List<Field> fieldList = Arrays.stream(declaredFields)
                    .filter(field -> metaObject.hasSetter(field.getName()))
                    .filter(field -> {
                        AnnotationHandler annotationHandler = GlobalConfigUtils.getGlobalConfig(TableInfoHelper.getTableInfo(clazz).getConfiguration()).getAnnotationHandler();
                        return annotationHandler.isAnnotationPresent(field, TableField.class);
                    })
                    .collect(Collectors.toList());

                for (Field field : fieldList) {
                    // 此处没有做字段与列的自动转化
                    String columnName = field.getName();
                    setFieldValByName(columnName, "1234567890", metaObject);
                }
            }

            @Override
            public void updateFill(MetaObject metaObject) {

            }
        };
    }
    
    @Bean
    public AnnotationHandler annotationHandler() {
        return new AnnotationHandler() {

            /**
             * 结合{@link com.baomidou.mybatisplus.test.h2.customfill.model.TestModel#getA()}简单实现{@link InsertUpdateFill} 的获取过程
             *
             * @param field           字段
             * @param annotationClass 要获取的注解class
             * @param <T>
             * @return
             */
            @Override
            public <T extends Annotation > T getAnnotation(Field field, Class<T> annotationClass) {

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
        };
    }

}
