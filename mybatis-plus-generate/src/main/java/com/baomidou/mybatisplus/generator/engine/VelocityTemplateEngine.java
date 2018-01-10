/**
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import com.baomidou.mybatisplus.generator.config.ConstVal;
import com.baomidou.mybatisplus.generator.config.FileOutConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.TemplateConfig;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.toolkit.StringUtils;

/**
 * <p>
 * Velocity 模板引擎实现文件输出
 * </p>
 *
 * @author hubin
 * @since 2018-01-10
 */
public class VelocityTemplateEngine extends AbstractTemplateEngine {

    private VelocityEngine velocityEngine;

    @Override
    public VelocityTemplateEngine init(ConfigBuilder configBuilder) {
        super.init(configBuilder);
        if (null == velocityEngine) {
            Properties p = new Properties();
            p.setProperty(ConstVal.VM_LOADPATH_KEY, ConstVal.VM_LOADPATH_VALUE);
            p.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, "");
            p.setProperty(Velocity.ENCODING_DEFAULT, ConstVal.UTF8);
            p.setProperty(Velocity.INPUT_ENCODING, ConstVal.UTF8);
            p.setProperty("file.resource.loader.unicode", "true");
            velocityEngine = new VelocityEngine(p);
        }
        return this;
    }

    @Override
    public VelocityTemplateEngine batchOutput() {
        try {
            List<TableInfo> tableInfoList = this.getConfigBuilder().getTableInfoList();
            for (TableInfo tableInfo : tableInfoList) {
                VelocityContext context = this.getVelocityContextInfo(tableInfo);
                String entityName = tableInfo.getEntityName();
                Map<String, String> pathInfo = this.getConfigBuilder().getPathInfo();
                String entityFile = String.format((pathInfo.get(ConstVal.ENTITY_PATH) + File.separator + "%s" + this.suffixJavaOrKt()), entityName);
                String mapperFile = String.format((pathInfo.get(ConstVal.MAPPER_PATH) + File.separator + tableInfo.getMapperName() + this.suffixJavaOrKt()), entityName);
                String xmlFile = String.format((pathInfo.get(ConstVal.XML_PATH) + File.separator + tableInfo.getXmlName() + ConstVal.XML_SUFFIX), entityName);
                String serviceFile = String.format((pathInfo.get(ConstVal.SERIVCE_PATH) + File.separator + tableInfo.getServiceName() + this.suffixJavaOrKt()), entityName);
                String implFile = String.format((pathInfo.get(ConstVal.SERVICEIMPL_PATH) + File.separator + tableInfo.getServiceImplName() + this.suffixJavaOrKt()), entityName);
                String controllerFile = String.format((pathInfo.get(ConstVal.CONTROLLER_PATH) + File.separator + tableInfo.getControllerName() + this.suffixJavaOrKt()), entityName);
                TemplateConfig template = this.getConfigBuilder().getTemplate();
                // 根据override标识来判断是否需要创建文件
                if (isCreate(entityFile)) {
                    this.vmToFile(context, this.templateFilePath(template.getEntity(this.getConfigBuilder()
                        .getGlobalConfig().isKotlin())), entityFile);
                }
                if (isCreate(mapperFile)) {
                    this.vmToFile(context, this.templateFilePath(template.getMapper()), mapperFile);
                }
                if (isCreate(xmlFile)) {
                    this.vmToFile(context, this.templateFilePath(template.getXml()), xmlFile);
                }
                if (isCreate(serviceFile)) {
                    this.vmToFile(context, this.templateFilePath(template.getService()), serviceFile);
                }
                if (isCreate(implFile)) {
                    this.vmToFile(context, this.templateFilePath(template.getServiceImpl()), implFile);
                }
                if (isCreate(controllerFile)) {
                    this.vmToFile(context, this.templateFilePath(template.getController()), controllerFile);
                }
                if (this.getConfigBuilder().getInjectionConfig() != null) {
                    /**
                     * 输出自定义文件内容
                     */
                    List<FileOutConfig> focList = this.getConfigBuilder().getInjectionConfig().getFileOutConfigList();
                    if (CollectionUtils.isNotEmpty(focList)) {
                        for (FileOutConfig foc : focList) {
                            // 判断自定义文件是否存在
                            if (isCreate(foc.outputFile(tableInfo))) {
                                this.vmToFile(context, foc.getTemplatePath(), foc.outputFile(tableInfo));
                            }
                        }
                    }
                }

            }
        } catch (IOException e) {
            logger.error("无法创建文件，请检查配置信息！", e);
        }
        return this;
    }

    /**
     * <p>
     * 模板真实文件路径加上 .vm
     * </p>
     *
     * @param filePath 文件路径
     * @return
     */
    protected String templateFilePath(String filePath) {
        StringBuilder fp = new StringBuilder();
        fp.append(filePath).append(".vm");
        return fp.toString();
    }

    /**
     * <p>
     * 获取模板引擎渲染上下文信息
     * </p>
     *
     * @param tableInfo 表信息对象
     * @return
     */
    public VelocityContext getVelocityContextInfo(TableInfo tableInfo) {
        VelocityContext ctx = new VelocityContext();
        ConfigBuilder config = this.getConfigBuilder();
        // RequestMapping 连字符风格 user-info
        if (config.getStrategyConfig().isControllerMappingHyphenStyle()) {
            ctx.put("controllerMappingHyphenStyle", config.getStrategyConfig().isControllerMappingHyphenStyle());
            ctx.put("controllerMappingHyphen", StringUtils.camelToHyphen(tableInfo.getEntityPath()));
        }
        ctx.put("restControllerStyle", config.getStrategyConfig().isRestControllerStyle());
        ctx.put("package", config.getPackageInfo());
        GlobalConfig globalConfig = config.getGlobalConfig();
        ctx.put("author", globalConfig.getAuthor());
        ctx.put("idType", globalConfig.getIdType() == null ? null : globalConfig.getIdType().toString());
        ctx.put("logicDeleteFieldName", config.getStrategyConfig().getLogicDeleteFieldName());
        ctx.put("versionFieldName", config.getStrategyConfig().getVersionFieldName());
        ctx.put("activeRecord", globalConfig.isActiveRecord());
        ctx.put("kotlin", globalConfig.isKotlin());
        ctx.put("date", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        ctx.put("table", tableInfo);
        ctx.put("enableCache", globalConfig.isEnableCache());
        ctx.put("baseResultMap", globalConfig.isBaseResultMap());
        ctx.put("baseColumnList", globalConfig.isBaseColumnList());
        ctx.put("entity", tableInfo.getEntityName());
        ctx.put("entityColumnConstant", config.getStrategyConfig().isEntityColumnConstant());
        ctx.put("entityBuilderModel", config.getStrategyConfig().isEntityBuilderModel());
        ctx.put("entityLombokModel", config.getStrategyConfig().isEntityLombokModel());
        ctx.put("entityBooleanColumnRemoveIsPrefix", config.getStrategyConfig().isEntityBooleanColumnRemoveIsPrefix());
        ctx.put("superEntityClass", this.getSuperClassName(config.getSuperEntityClass()));
        ctx.put("superMapperClassPackage", config.getSuperMapperClass());
        ctx.put("superMapperClass", this.getSuperClassName(config.getSuperMapperClass()));
        ctx.put("superServiceClassPackage", config.getSuperServiceClass());
        ctx.put("superServiceClass", this.getSuperClassName(config.getSuperServiceClass()));
        ctx.put("superServiceImplClassPackage", config.getSuperServiceImplClass());
        ctx.put("superServiceImplClass", this.getSuperClassName(config.getSuperServiceImplClass()));
        ctx.put("superControllerClassPackage", config.getSuperControllerClass());
        ctx.put("superControllerClass", this.getSuperClassName(config.getSuperControllerClass()));
        return ctx;
    }


    /**
     * <p>
     * 获取类名
     * </p>
     *
     * @param classPath
     * @return
     */
    private String getSuperClassName(String classPath) {
        if (StringUtils.isEmpty(classPath)) {
            return null;
        }
        return classPath.substring(classPath.lastIndexOf(".") + 1);
    }

    /**
     * <p>
     * 将模板转化成为文件
     * </p>
     *
     * @param context      内容对象
     * @param templatePath 模板文件
     * @param outputFile   文件生成的目录
     */
    private void vmToFile(VelocityContext context, String templatePath, String outputFile) throws IOException {
        if (StringUtils.isEmpty(templatePath)) {
            return;
        }
        Template template = velocityEngine.getTemplate(templatePath, ConstVal.UTF8);
        File file = new File(outputFile);
        if (!file.getParentFile().exists()) {
            // 如果文件所在的目录不存在，则创建目录
            if (!file.getParentFile().mkdirs()) {
                logger.debug("创建文件所在的目录失败!");
                return;
            }
        }
        FileOutputStream fos = new FileOutputStream(outputFile);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos, ConstVal.UTF8));
        template.merge(context, writer);
        writer.close();
        logger.debug("模板:" + templatePath + ";  文件:" + outputFile);
    }

}
