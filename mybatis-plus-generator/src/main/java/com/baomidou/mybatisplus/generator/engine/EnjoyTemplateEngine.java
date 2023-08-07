/*
 * Copyright (c) 2011-2023, baomidou (jobob@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baomidou.mybatisplus.generator.engine;

import com.baomidou.mybatisplus.generator.config.ConstVal;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.jfinal.template.Engine;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;

/**
 * enjoy 模板引擎实现文件输出
 *
 * @author flyinke
 * @since 2022-06-16
 */
public class EnjoyTemplateEngine extends AbstractTemplateEngine {

    private Engine engine;

    @Override
    public @NotNull AbstractTemplateEngine init(@NotNull ConfigBuilder configBuilder) {
        engine = Engine.createIfAbsent("mybatis-plus-generator", e -> {
            e.setToClassPathSourceFactory();
        });
        return this;
    }

    @Override
    public void writer(@NotNull Map<String, Object> objectMap, @NotNull String templatePath, @NotNull File outputFile) throws Exception {
        String str = engine.getTemplate(templatePath).renderToString(objectMap);
        try (FileOutputStream fos = new FileOutputStream(outputFile);
             OutputStreamWriter ow = new OutputStreamWriter(fos, ConstVal.UTF8);
             BufferedWriter writer = new BufferedWriter(ow)) {
            writer.append(str);
        }
        LOGGER.debug("模板:" + templatePath + ";  文件:" + outputFile);
    }

    @Override
    public @NotNull String templateFilePath(@NotNull String filePath) {
        final String dotVm = ".ej";
        return filePath.endsWith(dotVm) ? filePath : filePath + dotVm;
    }
}

