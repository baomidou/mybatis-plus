package com.baomidou.mybatisplus.code.sub.javassist;

import com.baomidou.mybatisplus.code.Overwrite;
import com.baomidou.mybatisplus.code.OverwriteFile;

/**
 * @author miemie
 * @since 2025/9/19
 */
public class CtClassType extends OverwriteFile {

    public CtClassType() {
        addStep(i -> i
            .source("""
                return javassist.bytecode.annotation.AnnotationImpl.make(
                """)
            .target("""
                return org.apache.ibatis.javassist.bytecode.annotation.AnnotationImpl.make(
                """)
            .operate(Overwrite.Operate.COVERAGE)
        );
    }
}
