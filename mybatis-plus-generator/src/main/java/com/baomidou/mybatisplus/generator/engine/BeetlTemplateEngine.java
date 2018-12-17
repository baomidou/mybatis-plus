package com.baomidou.mybatisplus.generator.engine;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.ClasspathResourceLoader;

import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;

/**
 * <p>
 * Beetl 模板引擎实现文件输出
 * </p>
 *
 * @author yandixuan
 * @since 2018-12-16
 */
public class BeetlTemplateEngine extends AbstractTemplateEngine {

    private GroupTemplate groupTemplate;

    @Override
    public AbstractTemplateEngine init(ConfigBuilder configBuilder) {
        super.init(configBuilder);
        try {
            Configuration cfg = Configuration.defaultConfiguration();
            groupTemplate = new GroupTemplate(new ClasspathResourceLoader("/"), cfg);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return this;
    }

    @Override
    public void writer(Map<String, Object> objectMap, String templatePath, String outputFile) throws Exception {
        Template template = groupTemplate.getTemplate(templatePath);
        FileOutputStream fileOutputStream = new FileOutputStream(new File(outputFile));
        template.binding(objectMap);
        template.renderTo(fileOutputStream);
        fileOutputStream.close();
        logger.debug("模板:" + templatePath + ";  文件:" + outputFile);
    }

    @Override
    public String templateFilePath(String filePath) {
        return filePath + ".btl";
    }
}
