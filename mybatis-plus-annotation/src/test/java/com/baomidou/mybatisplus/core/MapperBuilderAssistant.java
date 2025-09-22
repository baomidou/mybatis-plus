package com.baomidou.mybatisplus.core;


import com.baomidou.mybatisplus.base.Overwrite;
import com.baomidou.mybatisplus.base.OverwriteFile;

/**
 * {@link org.apache.ibatis.builder.MapperBuilderAssistant}
 *
 * @author miemie
 * @since 2025/9/18
 */
public class MapperBuilderAssistant extends OverwriteFile {

    public MapperBuilderAssistant() {
        addStep(i -> i
            .source("""
                import org.apache.ibatis.type.TypeHandler;
                """)
            .target("""
                import com.baomidou.mybatisplus.core.handlers.IJsonTypeHandler;
                import com.baomidou.mybatisplus.core.toolkit.MybatisUtils;
                """)
        );
        addStep(i -> i
            .source("""
                Class<?> javaTypeClass = resolveResultJavaType(resultType, property, javaType);
                TypeHandler<?> typeHandlerInstance = resolveTypeHandler(javaTypeClass, typeHandler);
                """)
            .target("""
                Class<?> javaTypeClass = resolveResultJavaType(resultType, property, javaType);
                TypeHandler<?> typeHandlerInstance = null;
                if (typeHandler != null) {
                    if (IJsonTypeHandler.class.isAssignableFrom(typeHandler)) {
                        try {
                            Field field = resultType.getDeclaredField(property);
                            typeHandlerInstance = MybatisUtils.newJsonTypeHandler(typeHandler, javaTypeClass, field);
                        } catch (NoSuchFieldException e) {
                            //ignore 降级兼容处理
                            typeHandlerInstance = resolveTypeHandler(javaTypeClass, typeHandler);
                        }
                    } else {
                        typeHandlerInstance = resolveTypeHandler(javaTypeClass, typeHandler);
                    }
                }
                """)
            .operate(Overwrite.Operate.COVERAGE)
        );
    }
}
