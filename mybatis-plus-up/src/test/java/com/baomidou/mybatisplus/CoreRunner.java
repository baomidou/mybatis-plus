package com.baomidou.mybatisplus;

import com.baomidou.mybatisplus.base.OverwriteRunner;
import com.baomidou.mybatisplus.core.*;

import java.nio.file.Path;
import java.util.List;

/**
 * @author miemie
 * @since 2025/9/19
 */
public class CoreRunner {

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            throw new IllegalArgumentException("请传入 MyBatis sources.jar 路径，或运行 :mybatis-plus-up:runCoreRunner");
        }
        new OverwriteRunner(
                        "mybatis-plus-core",
                        Path.of(args[0]),
                        List.of(
                                // new Configuration(),
                                // new DefaultParameterHandler(),
                                // new MapperAnnotationBuilder(),
                                // new MapperBuilderAssistant(),
                                new MapperMethod(), new MapperProxy()
                                // new MapperRegistry(),
                                // new MethodResolver(),
                                // new TypeHandlerRegistry(),
                                // new XMLConfigBuilder(),
                                // new XMLLanguageDriver()
                                ))
                .run();
    }
}
