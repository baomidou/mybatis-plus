package com.baomidou.mybatisplus.core;


import com.baomidou.mybatisplus.base.Overwrite;
import com.baomidou.mybatisplus.base.OverwriteFile;

/**
 * {@link org.apache.ibatis.type.TypeHandlerRegistry}
 *
 * @author miemie
 * @since 2025/9/18
 */
public class TypeHandlerRegistry extends OverwriteFile {

    public TypeHandlerRegistry() {
        addStep(i -> i
            .source("""
                import java.util.Map;
                """)
            .target("""
                import com.baomidou.mybatisplus.core.handlers.CompositeEnumTypeHandler;
                """)
        );
        addStep(i -> i
            .source("""
                private Class<? extends TypeHandler> defaultEnumTypeHandler = EnumTypeHandler.class;
                """)
            .target("""
                private Class<? extends TypeHandler> defaultEnumTypeHandler = CompositeEnumTypeHandler.class;
                """)
            .operate(Overwrite.Operate.COVERAGE)
        );
    }
}
