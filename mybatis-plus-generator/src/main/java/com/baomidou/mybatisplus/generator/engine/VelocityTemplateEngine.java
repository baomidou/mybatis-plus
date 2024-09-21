/*
 * Copyright (c) 2011-2024, baomidou (jobob@qq.com).
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

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.generator.config.ConstVal;
import com.baomidou.mybatisplus.generator.config.TemplateLoadWay;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Map;
import java.util.Properties;

/**
 * Velocity 模板引擎实现文件输出
 *
 * @author hubin
 * @since 2018-01-10
 */
public class VelocityTemplateEngine extends AbstractTemplateEngine {
    private VelocityEngine velocityEngine;

    {
        try {
            Class.forName("org.apache.velocity.util.DuckType");
        } catch (ClassNotFoundException e) {
            // velocity1.x的生成格式错乱 https://github.com/baomidou/generator/issues/5
            LOGGER.warn("Velocity 1.x is outdated, please upgrade to 2.x or later.");
        }
    }

    @Override
    public @NotNull VelocityTemplateEngine init(@NotNull ConfigBuilder configBuilder) {
        if (null == velocityEngine) {
            Properties p = new Properties();
            p.setProperty(Velocity.ENCODING_DEFAULT, ConstVal.UTF8);
            p.setProperty(Velocity.INPUT_ENCODING, ConstVal.UTF8);
            if (configBuilder.getTemplateLoadWay().isFile()) {
                // 文件模板
                p.setProperty(ConstVal.VM_LOAD_PATH_KEY, ConstVal.VM_LOAD_PATH_VALUE);
                p.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, StringPool.EMPTY);
                p.setProperty("file.resource.loader.unicode", StringPool.TRUE);
            } else {
                // 文本模板
                p.setProperty(Velocity.RESOURCE_LOADER, TemplateLoadWay.STRING.getValue());
            }
            velocityEngine = new VelocityEngine(p);
        }
        return this;
    }

    @Override
    public String writer(@NotNull Map<String, Object> objectMap, @NotNull String templateName, @NotNull String templateString) throws Exception {
        StringWriter writer = new StringWriter();
        velocityEngine.evaluate(new VelocityContext(objectMap), writer, templateName, templateString);
        return writer.toString();
    }

    @Override
    public void writer(@NotNull Map<String, Object> objectMap, @NotNull String templatePath, @NotNull File outputFile) throws Exception {
        Template template = velocityEngine.getTemplate(templatePath, ConstVal.UTF8);
        try (FileOutputStream fos = new FileOutputStream(outputFile);
             OutputStreamWriter ow = new OutputStreamWriter(fos, ConstVal.UTF8);
             BufferedWriter writer = new BufferedWriter(ow)) {
            template.merge(new VelocityContext(objectMap), writer);
        }
        LOGGER.debug("模板:{};  文件:{}", templatePath, outputFile);
    }


    @Override
    public @NotNull String templateFilePath(@NotNull String filePath) {
        final String dotVm = ".vm";
        return filePath.endsWith(dotVm) ? filePath : filePath + dotVm;
    }
}
