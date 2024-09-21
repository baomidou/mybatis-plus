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

import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.ClasspathResourceLoader;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Beetl 模板引擎实现文件输出
 *
 * @author yandixuan
 * @since 2018-12-16
 */
public class BeetlTemplateEngine extends AbstractTemplateEngine {

    private static Method method;

    static {
        try {
            method = GroupTemplate.class.getDeclaredMethod("getTemplate", Object.class);
        } catch (NoSuchMethodException e) {
            try {
                //3.2.x 方法签名修改成了object,其他低版本为string
                method = GroupTemplate.class.getDeclaredMethod("getTemplate", String.class);
            } catch (NoSuchMethodException exception) {
                throw new RuntimeException(exception);
            }
        }
    }

    private GroupTemplate groupTemplate;

    @Override
    public @NotNull AbstractTemplateEngine init(@NotNull ConfigBuilder configBuilder) {
        try {
            Configuration cfg = Configuration.defaultConfiguration();
            groupTemplate = new GroupTemplate(new ClasspathResourceLoader("/"), cfg);
        } catch (IOException e) {
            LOGGER.error("初始化模板引擎失败:", e);
            throw new RuntimeException(e);
        }
        return this;
    }

    @Override
    public String writer(@NotNull Map<String, Object> objectMap, @NotNull String templateName, @NotNull String templateString) throws Exception {
        Template template = groupTemplate.getTemplate(templateString);
        template.binding(objectMap);
        return template.render();
    }

    @Override
    public void writer(@NotNull Map<String, Object> objectMap, @NotNull String templatePath, @NotNull File outputFile) throws Exception {
        Template template = (Template) method.invoke(groupTemplate, templatePath);
        try (FileOutputStream fileOutputStream = new FileOutputStream(outputFile)) {
            template.binding(objectMap);
            template.renderTo(fileOutputStream);
        }
        LOGGER.debug("模板:{};  文件:{}", templatePath, outputFile);
    }

    @Override
    public @NotNull String templateFilePath(@NotNull String filePath) {
        return filePath + ".btl";
    }
}
