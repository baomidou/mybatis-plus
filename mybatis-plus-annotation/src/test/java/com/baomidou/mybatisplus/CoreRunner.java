package com.baomidou.mybatisplus;

import com.baomidou.mybatisplus.base.OverwriteRunner;
import com.baomidou.mybatisplus.core.*;
import com.baomidou.mybatisplus.core.javassist.ClassPool;
import com.baomidou.mybatisplus.core.javassist.ClassPoolTail;
import com.baomidou.mybatisplus.core.javassist.CtClassType;

import java.util.List;

/**
 * @author miemie
 * @since 2025/9/19
 */
public class CoreRunner {

    public static void main(String[] args) throws Exception {
        new OverwriteRunner("mybatis-plus-core", "mybatis", "mybatis", List.of(
            new Configuration(),
            new DefaultParameterHandler(),
            new MapperMethod(),
            new MapperProxy(),
            new TypeHandlerRegistry(),
            new XMLConfigBuilder(),
            new XMLLanguageDriver(),
            // javassist
            new ClassPool(),
            new ClassPoolTail(),
            new CtClassType()
        )).setOnlyFile(true).run();
    }
}
