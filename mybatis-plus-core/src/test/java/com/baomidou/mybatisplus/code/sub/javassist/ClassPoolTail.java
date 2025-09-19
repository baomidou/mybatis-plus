package com.baomidou.mybatisplus.code.sub.javassist;

import com.baomidou.mybatisplus.code.Overwrite;
import com.baomidou.mybatisplus.code.OverwriteFile;

/**
 * @author miemie
 * @since 2025/9/19
 */
public class ClassPoolTail extends OverwriteFile {

    public ClassPoolTail() {
        addStep(i -> i
            .source("""
                if (org.apache.ibatis.javassist.bytecode.ClassFile.MAJOR_VERSION < javassist.bytecode.ClassFile.JAVA_9)
                """)
            .target("""
                if (org.apache.ibatis.javassist.bytecode.ClassFile.MAJOR_VERSION < org.apache.ibatis.javassist.bytecode.ClassFile.JAVA_9)
                """)
            .operate(Overwrite.Operate.COVERAGE)
        );
    }
}
