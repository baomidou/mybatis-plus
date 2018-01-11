package com.baomidou.mybatisplus.generator.engine;

import com.baomidou.mybatisplus.generator.config.ConstVal;
import com.baomidou.mybatisplus.generator.config.FileOutConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.toolkit.StringUtils;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author nieqiurong 2018/1/11.
 */
public class FreemarkerTemplateEngine extends AbstractTemplateEngine {
    
    private Configuration configuration;
    
    @Override
    public AbstractTemplateEngine batchOutput() {
        try {
            configuration = new Configuration(Configuration.VERSION_2_3_0);
            configuration.setDefaultEncoding(ConstVal.UTF8);
            configuration.setClassForTemplateLoading(FreemarkerTemplateEngine.class, "/");
            List<TableInfo> tableInfoList = this.getConfigBuilder().getTableInfoList();
            for (TableInfo tableInfo : tableInfoList) {
                Map<String,Object> objectMap = this.getVelocityContextInfo(tableInfo);
                String entityName = tableInfo.getEntityName();
                Map<String, String> pathInfo = this.getConfigBuilder().getPathInfo();
                String entityFile = String.format((pathInfo.get(ConstVal.ENTITY_PATH) + File.separator + "%s" + this.suffixJavaOrKt()), entityName);
                String mapperFile = String.format((pathInfo.get(ConstVal.MAPPER_PATH) + File.separator + tableInfo.getMapperName() + this.suffixJavaOrKt()), entityName);
                String xmlFile = String.format((pathInfo.get(ConstVal.XML_PATH) + File.separator + tableInfo.getXmlName() + ConstVal.XML_SUFFIX), entityName);
                String serviceFile = String.format((pathInfo.get(ConstVal.SERIVCE_PATH) + File.separator + tableInfo.getServiceName() + this.suffixJavaOrKt()), entityName);
                String implFile = String.format((pathInfo.get(ConstVal.SERVICEIMPL_PATH) + File.separator + tableInfo.getServiceImplName() + this.suffixJavaOrKt()), entityName);
                String controllerFile = String.format((pathInfo.get(ConstVal.CONTROLLER_PATH) + File.separator + tableInfo.getControllerName() + this.suffixJavaOrKt()), entityName);
                if (isCreate(entityFile)) {
                    if(this.getConfigBuilder().getGlobalConfig().isKotlin()){
                        this.writer(configuration.getTemplate(templateFilePath(ConstVal.TEMPLATE_ENTITY_KT)),objectMap,entityFile);
                    }else{
                        this.writer(configuration.getTemplate(templateFilePath(ConstVal.TEMPLATE_ENTITY_JAVA)),objectMap,entityFile);
                    }
                }
                if (isCreate(mapperFile)) {
                    this.writer(configuration.getTemplate(templateFilePath(ConstVal.TEMPLATE_MAPPER)),objectMap,mapperFile);
                }
                if (isCreate(xmlFile)) {
                    this.writer(configuration.getTemplate(templateFilePath(ConstVal.TEMPLATE_XML)),objectMap,xmlFile);
                }
                if (isCreate(serviceFile)) {
                    this.writer(configuration.getTemplate(templateFilePath(ConstVal.TEMPLATE_SERVICE)),objectMap,serviceFile);
                }
                if (isCreate(implFile)) {
                    this.writer(configuration.getTemplate(templateFilePath(ConstVal.TEMPLATE_SERVICEIMPL)),objectMap,implFile);
                }
                if (isCreate(controllerFile)) {
                    this.writer(configuration.getTemplate(templateFilePath(ConstVal.TEMPLATE_CONTROLLER)),objectMap,controllerFile);
                }
                if (this.getConfigBuilder().getInjectionConfig() != null) {
                    List<FileOutConfig> focList = this.getConfigBuilder().getInjectionConfig().getFileOutConfigList();
                    if (CollectionUtils.isNotEmpty(focList)) {
                        for (FileOutConfig foc : focList) {
                            if (isCreate(foc.outputFile(tableInfo))) {
                                this.writer(configuration.getTemplate(foc.getTemplatePath()),objectMap,foc.outputFile(tableInfo));
                            }
                        }
                    }
                }
            
            }
        } catch (IOException e) {
            logger.error("无法创建文件，请检查配置信息！", e);
        } catch (TemplateException e) {
            e.printStackTrace();
        }
        return this;
    }
    
    /**
     * 获取模板引擎渲染上下文信息
     * @param tableInfo 表信息对象
     * @return
     */
    public Map<String,Object> getVelocityContextInfo(TableInfo tableInfo) {
        Map<String,Object>  objectMap = new HashMap<>();
        ConfigBuilder config = this.getConfigBuilder();
        if (config.getStrategyConfig().isControllerMappingHyphenStyle()) {
            objectMap.put("controllerMappingHyphenStyle", config.getStrategyConfig().isControllerMappingHyphenStyle());
            objectMap.put("controllerMappingHyphen", StringUtils.camelToHyphen(tableInfo.getEntityPath()));
        }
        objectMap.put("restControllerStyle", config.getStrategyConfig().isRestControllerStyle());
        objectMap.put("package", config.getPackageInfo());
        GlobalConfig globalConfig = config.getGlobalConfig();
        objectMap.put("author", globalConfig.getAuthor());
        objectMap.put("idType", globalConfig.getIdType() == null ? null : globalConfig.getIdType().toString());
        objectMap.put("logicDeleteFieldName", config.getStrategyConfig().getLogicDeleteFieldName());
        objectMap.put("versionFieldName", config.getStrategyConfig().getVersionFieldName());
        objectMap.put("activeRecord", globalConfig.isActiveRecord());
        objectMap.put("kotlin", globalConfig.isKotlin());
        objectMap.put("date", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        objectMap.put("table", tableInfo);
        objectMap.put("enableCache", globalConfig.isEnableCache());
        objectMap.put("baseResultMap", globalConfig.isBaseResultMap());
        objectMap.put("baseColumnList", globalConfig.isBaseColumnList());
        objectMap.put("entity", tableInfo.getEntityName());
        objectMap.put("entityColumnConstant", config.getStrategyConfig().isEntityColumnConstant());
        objectMap.put("entityBuilderModel", config.getStrategyConfig().isEntityBuilderModel());
        objectMap.put("entityLombokModel", config.getStrategyConfig().isEntityLombokModel());
        objectMap.put("entityBooleanColumnRemoveIsPrefix", config.getStrategyConfig().isEntityBooleanColumnRemoveIsPrefix());
        objectMap.put("superEntityClass", this.getSuperClassName(config.getSuperEntityClass()));
        objectMap.put("superMapperClassPackage", config.getSuperMapperClass());
        objectMap.put("superMapperClass", this.getSuperClassName(config.getSuperMapperClass()));
        objectMap.put("superServiceClassPackage", config.getSuperServiceClass());
        objectMap.put("superServiceClass", this.getSuperClassName(config.getSuperServiceClass()));
        objectMap.put("superServiceImplClassPackage", config.getSuperServiceImplClass());
        objectMap.put("superServiceImplClass", this.getSuperClassName(config.getSuperServiceImplClass()));
        objectMap.put("superControllerClassPackage", config.getSuperControllerClass());
        objectMap.put("superControllerClass", this.getSuperClassName(config.getSuperControllerClass()));
        return objectMap;
    }
    
    
    /**
     * 获取类名
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
     * 文件写出
     * @param template
     * @param dataModel
     * @param fileName
     * @throws IOException
     * @throws TemplateException
     */
    private void writer(Template template, Object dataModel, String fileName) throws IOException, TemplateException {
        File file = new File(fileName);
        Writer writer = new OutputStreamWriter(new FileOutputStream(file), ConstVal.UTF8);
        template.process(dataModel, writer);
    }
    
    /**
     * <p>
     * 模板真实文件路径加上.ftl
     * </p>
     * @param filePath 文件路径
     * @return
     */
    protected String templateFilePath(String filePath) {
        StringBuilder fp = new StringBuilder();
        fp.append(filePath).append(".ftl");
        return fp.toString();
    }
}
