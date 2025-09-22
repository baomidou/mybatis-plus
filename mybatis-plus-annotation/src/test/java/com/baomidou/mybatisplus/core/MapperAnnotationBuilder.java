package com.baomidou.mybatisplus.core;

import com.baomidou.mybatisplus.base.Overwrite;
import com.baomidou.mybatisplus.base.OverwriteFile;

/**
 * {@link org.apache.ibatis.builder.annotation.MapperAnnotationBuilder}
 *
 * @author miemie
 * @since 2025/9/22
 */
public class MapperAnnotationBuilder extends OverwriteFile {

    public MapperAnnotationBuilder() {
        addStep(i -> i
            .source("""
                import java.util.stream.Stream;
                """)
            .target("""
                import com.baomidou.mybatisplus.core.metadata.IPage;
                import com.baomidou.mybatisplus.core.plugins.IgnoreStrategy;
                import com.baomidou.mybatisplus.core.plugins.InterceptorIgnoreHelper;
                import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
                """)
        );
        addStep(i -> i
            .source("""
                for (Method method : type.getMethods()) {
                """)
            .target("""
                IgnoreStrategy ignoreStrategy = InterceptorIgnoreHelper.initSqlParserInfoCache(type);
                for (Method method : type.getMethods()) {
                    InterceptorIgnoreHelper.initSqlParserInfoCache(ignoreStrategy, type.getName(), method);
                """)
            .operate(Overwrite.Operate.COVERAGE)
        );
        addStep(i -> i
            .source("""
                    }
                    configuration.parsePendingMethods(false);
                }
                """)
            .target("""
                        try {
                            if (GlobalConfigUtils.isSupperMapperChildren(configuration, type)) {
                                parserInjector();
                            }
                        } catch (IncompleteElementException e) {
                            // https://github.com/baomidou/mybatis-plus/issues/3038
                            configuration.addIncompleteMethod(new InjectorResolver(this));
                        }
                    }
                    configuration.parsePendingMethods(false);
                }

                public void parserInjector() {
                    GlobalConfigUtils.getSqlInjector(configuration).inspectInject(assistant, type);
                }

                public static class InjectorResolver extends MethodResolver {

                    public InjectorResolver(MapperAnnotationBuilder annotationBuilder) {
                        super(annotationBuilder, null);
                    }

                    @Override
                    public void resolve() {
                        annotationBuilder.parserInjector();
                    }
                }
                """)
            .operate(Overwrite.Operate.COVERAGE)
        );
        addStep(i -> i
            .source("""
                } else if (Optional.class.equals(rawType)) {
                    Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                    Type returnTypeParameter = actualTypeArguments[0];
                    if (returnTypeParameter instanceof Class<?>) {
                        returnType = (Class<?>) returnTypeParameter;
                    }
                }
                """)
            .target("""
                else if (IPage.class.isAssignableFrom(rawType)) {
                    Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                    Type returnTypeParameter = actualTypeArguments[0];
                    if (returnTypeParameter instanceof Class<?>) {
                        returnType = (Class<?>) returnTypeParameter;
                    } else if (returnTypeParameter instanceof ParameterizedType) {
                        returnType = (Class<?>) ((ParameterizedType) returnTypeParameter).getRawType();
                    }
                }
                """)
        );
    }
}
