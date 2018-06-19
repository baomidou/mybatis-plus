/*
 * Copyright (c) 2011-2020, hubin (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.generator.engine;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.generator.config.ConstVal;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;

/**
 * <p>
 * Velocity 模板引擎实现文件输出
 * </p>
 *
 * @author hubin
 * @since 2018-01-10
 */
public class VelocityTemplateEngine extends AbstractTemplateEngine {

    private static final String DOT_VM = ".vm";
    private VelocityEngine velocityEngine;

    @Override
    public VelocityTemplateEngine init(ConfigBuilder configBuilder) {
        super.init(configBuilder);
        if (null == velocityEngine) {
            Properties p = new Properties();
            p.setProperty(ConstVal.VM_LOAD_PATH_KEY, ConstVal.VM_LOAD_PATH_VALUE);
            p.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, "");
            p.setProperty(Velocity.ENCODING_DEFAULT, ConstVal.UTF8);
            p.setProperty(Velocity.INPUT_ENCODING, ConstVal.UTF8);
            p.setProperty("file.resource.loader.unicode", "true");
            velocityEngine = new VelocityEngine(p);
        }
        return this;
    }


    @Override
    public void writer(Map<String, Object> objectMap, String templatePath, String outputFile) throws Exception {
        if (StringUtils.isEmpty(templatePath)) {
            return;
        }
        Template template = velocityEngine.getTemplate(templatePath, ConstVal.UTF8);
        FileOutputStream fos = new FileOutputStream(outputFile);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos, ConstVal.UTF8));
        template.merge(new VelocityContext(objectMap), writer);
        writer.close();
        logger.debug("模板:" + templatePath + ";  文件:" + outputFile);
    }


    @Override
    public String templateFilePath(String filePath) {
        if (null == filePath || filePath.contains(DOT_VM)) {
            return filePath;
        }
        StringBuilder fp = new StringBuilder();
        fp.append(filePath).append(DOT_VM);
        return fp.toString();
    }
}
