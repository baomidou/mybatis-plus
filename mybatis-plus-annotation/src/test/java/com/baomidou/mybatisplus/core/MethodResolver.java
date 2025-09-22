package com.baomidou.mybatisplus.core;


import com.baomidou.mybatisplus.base.Overwrite;
import com.baomidou.mybatisplus.base.OverwriteFile;

/**
 * {@link org.apache.ibatis.builder.annotation.MethodResolver}
 *
 * @author miemie
 * @since 2025/9/18
 */
public class MethodResolver extends OverwriteFile {

    public MethodResolver() {
        addStep(i -> i
            .source("""
                private final MapperAnnotationBuilder annotationBuilder;
                """)
            .target("""
                protected final MapperAnnotationBuilder annotationBuilder;
                """)
            .operate(Overwrite.Operate.COVERAGE)
        );
    }
}
