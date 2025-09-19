package com.baomidou.mybatisplus.code.sub.javassist;

import com.baomidou.mybatisplus.code.Overwrite;
import com.baomidou.mybatisplus.code.OverwriteFile;

/**
 * @author miemie
 * @since 2025/9/19
 */
public class ClassPool extends OverwriteFile {

    public ClassPool() {
        addStep(i -> i
            .source("""
                return javassist.util.proxy.DefineClassHelper.toClass(neighbor,
                """)
            .target("""
                return org.apache.ibatis.javassist.util.proxy.DefineClassHelper.toClass(neighbor,
                """)
            .operate(Overwrite.Operate.COVERAGE)
        );
        addStep(i -> i
            .source("""
                return javassist.util.proxy.DefineClassHelper.toClass(lookup,
                """)
            .target("""
                return org.apache.ibatis.javassist.util.proxy.DefineClassHelper.toClass(lookup,
                """)
            .operate(Overwrite.Operate.COVERAGE)
        );
        addStep(i -> i
            .source("""
                return javassist.util.proxy.DefineClassHelper.toClass(ct.getName(),
                """)
            .target("""
                return org.apache.ibatis.javassist.util.proxy.DefineClassHelper.toClass(ct.getName(),
                """)
            .operate(Overwrite.Operate.COVERAGE)
        );
    }
}
