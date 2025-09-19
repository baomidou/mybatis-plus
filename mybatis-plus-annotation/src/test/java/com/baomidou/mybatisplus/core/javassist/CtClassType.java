package com.baomidou.mybatisplus.core.javassist;


import com.baomidou.mybatisplus.base.Overwrite;
import com.baomidou.mybatisplus.base.OverwriteFile;

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
