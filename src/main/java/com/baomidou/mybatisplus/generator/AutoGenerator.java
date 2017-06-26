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
package com.baomidou.mybatisplus.generator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import com.baomidou.mybatisplus.generator.config.ConstVal;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.FileOutConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.TemplateConfig;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.config.po.TableField;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.toolkit.StringUtils;

/**
 * 生成文件
 *
 * @author YangHu, tangguo
 * @since 2016-08-30
 */
public class AutoGenerator {

    private static final Log logger = LogFactory.getLog(AutoGenerator.class);

    protected ConfigBuilder config;
    protected InjectionConfig injectionConfig;
    /**
     * 数据源配置
     */
    private DataSourceConfig dataSource;
    /**
     * 数据库表配置
     */
    private StrategyConfig strategy;
    /**
     * 包 相关配置
     */
    private PackageConfig packageInfo;
    /**
     * 模板 相关配置
     */
    private TemplateConfig template;
    /**
     * 全局 相关配置
     */
    private GlobalConfig globalConfig;
    /**
     * velocity引擎
     */
    private VelocityEngine engine;

    /**
     * 生成代码
     */
    public void execute() {
        logger.debug("==========================准备生成文件...==========================");
        // 初始化配置
        initConfig();
        // 创建输出文件路径
        mkdirs(config.getPathInfo());
        // 获取上下文
        Map<String, VelocityContext> ctxData = analyzeData(config);
        // 循环生成文件
        for (Map.Entry<String, VelocityContext> ctx : ctxData.entrySet()) {
            batchOutput(ctx.getKey(), ctx.getValue());
        }
        // 打开输出目录
        if (config.getGlobalConfig().isOpen()) {
            try {
                String osName = System.getProperty("os.name");
                if (osName != null) {
                    if (osName.contains("Mac")) {
                        Runtime.getRuntime().exec("open " + config.getGlobalConfig().getOutputDir());
                    } else if (osName.contains("Windows")) {
                        Runtime.getRuntime().exec("cmd /c start " + config.getGlobalConfig().getOutputDir());
                    } else {
                        logger.debug("文件输出目录:" + config.getGlobalConfig().getOutputDir());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        logger.debug("==========================文件生成完成！！！==========================");
    }

    /**
     * <p>
     * 开放表信息、预留子类重写
     * </p>
     *
     * @param config 配置信息
     * @return
     */
    protected List<TableInfo> getAllTableInfoList(ConfigBuilder config) {
        return config.getTableInfoList();
    }

    /**
     * <p>
     * 分析数据
     * </p>
     *
     * @param config 总配置信息
     * @return 解析数据结果集
     */
    private Map<String, VelocityContext> analyzeData(ConfigBuilder config) {
        List<TableInfo> tableList = this.getAllTableInfoList(config);
        Map<String, String> packageInfo = config.getPackageInfo();
        Map<String, VelocityContext> ctxData = new HashMap<>();
        String superEntityClass = getSuperClassName(config.getSuperEntityClass());
        String superMapperClass = getSuperClassName(config.getSuperMapperClass());
        String superServiceClass = getSuperClassName(config.getSuperServiceClass());
        String superServiceImplClass = getSuperClassName(config.getSuperServiceImplClass());
        String superControllerClass = getSuperClassName(config.getSuperControllerClass());
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        VelocityContext ctx;
        for (TableInfo tableInfo : tableList) {
            ctx = new VelocityContext();
            if (null != injectionConfig) {
                /**
                 * 注入自定义配置
                 */
                injectionConfig.initMap();
                ctx.put("cfg", injectionConfig.getMap());
            }
            /* ---------- 添加导入包 ---------- */
            if (config.getGlobalConfig().isActiveRecord()) {
                // 开启 ActiveRecord 模式
                tableInfo.setImportPackages("com.baomidou.mybatisplus.activerecord.Model");
            }
            if (tableInfo.isConvert()) {
                // 表注解
                tableInfo.setImportPackages("com.baomidou.mybatisplus.annotations.TableName");
            }
            if (tableInfo.isLogicDelete(config.getStrategyConfig().getLogicDeleteFieldName())) {
                // 逻辑删除注解
                tableInfo.setImportPackages("com.baomidou.mybatisplus.annotations.TableLogic");
            }
            if (StringUtils.isNotEmpty(config.getSuperEntityClass())) {
                // 父实体
                tableInfo.setImportPackages(config.getSuperEntityClass());
            } else {
                tableInfo.setImportPackages("java.io.Serializable");
            }
            
            
            // Boolean类型is前缀处理

            if ( config.getStrategyConfig().isEntityBooleanColumnRemoveIsPrefix() ) {
                for ( TableField field : tableInfo.getFields() ) {
                    if ( field.getPropertyType().equalsIgnoreCase( "boolean" ) ) {
                        // 如果是Lombok模式,那么boolean类型使用基本类型,而不是Boolean
                        // 否则Boolean生成的Get方法是getXXX而不是isXXX,因为Boolean默认值是:null,boolean默认值是:false
                        if ( config.getStrategyConfig().isEntityLombokModel() ) {
                            field.setColumnType( DbColumnType.BASIC_BOOLEAN );
                        }
                        if ( field.getPropertyName().indexOf( "is" ) != -1 ) {
                            field.setPropertyName(
                                    config.getStrategyConfig(),
                                    StringUtils.removePrefixAfterPrefixToLower( field.getPropertyName(), 2 )
                            );
            
            String primaryKeyTypeString = null;
            // Controller基本CRUD
            if ( config.getStrategyConfig().isControllerBasicMethod() ) {
                for ( TableField field : tableInfo.getFields() ) {
                    if ( field.isKeyFlag() ) {
                        primaryKeyTypeString = field.getPropertyType();
                        break;
                    }
                }
                ctx.put( "primaryKeyTypeString", primaryKeyTypeString );
                ctx.put( "serviceVariableName", StringUtils.firstCharToLower( tableInfo.getServiceName() ) );
                ctx.put( "entityVariableName", StringUtils.firstCharToLower( tableInfo.getEntityName() ) );
            }

           
            // RequestMapping 连字符风格 user-info
            if (config.getStrategyConfig().isControllerMappingHyphenStyle()) {
                ctx.put("controllerMappingHyphenStyle", config.getStrategyConfig().isControllerMappingHyphenStyle());
                ctx.put("controllerMappingHyphen", StringUtils.camelToHyphen(tableInfo.getEntityPath()));
            }
            ctx.put("restControllerStyle", config.getStrategyConfig().isRestControllerStyle());
            ctx.put("package", packageInfo);
            ctx.put("author", config.getGlobalConfig().getAuthor());
            ctx.put("logicDeleteFieldName", config.getStrategyConfig().getLogicDeleteFieldName());
            ctx.put("activeRecord", config.getGlobalConfig().isActiveRecord());
            ctx.put("date", date);
            ctx.put("table", tableInfo);
            ctx.put("enableCache", config.getGlobalConfig().isEnableCache());
            ctx.put("baseResultMap", config.getGlobalConfig().isBaseResultMap());
            ctx.put("baseColumnList", config.getGlobalConfig().isBaseColumnList());
            ctx.put("entity", tableInfo.getEntityName());
            ctx.put("entityColumnConstant", config.getStrategyConfig().isEntityColumnConstant());
            ctx.put("entityBuilderModel", config.getStrategyConfig().isEntityBuilderModel());
            ctx.put("entityLombokModel", config.getStrategyConfig().isEntityLombokModel());
            ctx.put("entityBooleanColumnRemoveIsPrefix", config.getStrategyConfig().isEntityBooleanColumnRemoveIsPrefix());
            ctx.put("superEntityClass", superEntityClass);
            ctx.put("superMapperClassPackage", config.getSuperMapperClass());
            ctx.put("superMapperClass", superMapperClass);
            ctx.put("superServiceClassPackage", config.getSuperServiceClass());
            ctx.put("superServiceClass", superServiceClass);
            ctx.put("superServiceImplClassPackage", config.getSuperServiceImplClass());
            ctx.put("superServiceImplClass", superServiceImplClass);
            ctx.put("superControllerClassPackage", config.getSuperControllerClass());
            ctx.put("superControllerClass", superControllerClass);
            ctxData.put(tableInfo.getEntityName(), ctx);
        }
        return ctxData;
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
        if (StringUtils.isEmpty(classPath))
            return null;
        return classPath.substring(classPath.lastIndexOf(".") + 1);
    }

    /**
     * <p>
     * 处理输出目录
     * </p>
     *
     * @param pathInfo 路径信息
     */
    private void mkdirs(Map<String, String> pathInfo) {
        for (Map.Entry<String, String> entry : pathInfo.entrySet()) {
            File dir = new File(entry.getValue());
            if (!dir.exists()) {
                boolean result = dir.mkdirs();
                if (result) {
                    logger.debug("创建目录： [" + entry.getValue() + "]");
                }
            }
        }
    }

    /**
     * <p>
     * 合成上下文与模板
     * </p>
     *
     * @param context vm上下文
     */
    private void batchOutput(String entityName, VelocityContext context) {
        try {
            TableInfo tableInfo = (TableInfo) context.get("table");
            Map<String, String> pathInfo = config.getPathInfo();
            String entityFile = String.format((pathInfo.get(ConstVal.ENTITY_PATH) + ConstVal.ENTITY_NAME), entityName);
            String mapperFile = String.format((pathInfo.get(ConstVal.MAPPER_PATH) + File.separator + tableInfo.getMapperName() + ConstVal.JAVA_SUFFIX), entityName);
            String xmlFile = String.format((pathInfo.get(ConstVal.XML_PATH) + File.separator + tableInfo.getXmlName() + ConstVal.XML_SUFFIX), entityName);
            String serviceFile = String.format((pathInfo.get(ConstVal.SERIVCE_PATH) + File.separator + tableInfo.getServiceName() + ConstVal.JAVA_SUFFIX), entityName);
            String implFile = String.format((pathInfo.get(ConstVal.SERVICEIMPL_PATH) + File.separator + tableInfo.getServiceImplName() + ConstVal.JAVA_SUFFIX), entityName);
            String controllerFile = String.format((pathInfo.get(ConstVal.CONTROLLER_PATH) + File.separator + tableInfo.getControllerName() + ConstVal.JAVA_SUFFIX), entityName);

            TemplateConfig template = config.getTemplate();

            // 根据override标识来判断是否需要创建文件
            if (isCreate(entityFile)) {
                vmToFile(context, template.getEntity(), entityFile);
            }
            if (isCreate(mapperFile)) {
                vmToFile(context, template.getMapper(), mapperFile);
            }
            if (isCreate(xmlFile)) {
                vmToFile(context, template.getXml(), xmlFile);
            }
            if (isCreate(serviceFile)) {
                vmToFile(context, template.getService(), serviceFile);
            }
            if (isCreate(implFile)) {
                vmToFile(context, template.getServiceImpl(), implFile);
            }
            if (isCreate(controllerFile)) {
                vmToFile(context, template.getController(), controllerFile);
            }
            if (injectionConfig != null) {
                /**
                 * 输出自定义文件内容
                 */
                List<FileOutConfig> focList = injectionConfig.getFileOutConfigList();
                if (CollectionUtils.isNotEmpty(focList)) {
                    for (FileOutConfig foc : focList) {
                        vmToFile(context, foc.getTemplatePath(), foc.outputFile(tableInfo));
                    }
                }
            }

        } catch (IOException e) {
            logger.error("无法创建文件，请检查配置信息！", e);
        }
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
        VelocityEngine velocity = getVelocityEngine();
        Template template = velocity.getTemplate(templatePath, ConstVal.UTF8);
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

    /**
     * 设置模版引擎，主要指向获取模版路径
     */
    private VelocityEngine getVelocityEngine() {
        if (engine == null) {
            Properties p = new Properties();
            p.setProperty(ConstVal.VM_LOADPATH_KEY, ConstVal.VM_LOADPATH_VALUE);
            p.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, "");
            p.setProperty(Velocity.ENCODING_DEFAULT, ConstVal.UTF8);
            p.setProperty(Velocity.INPUT_ENCODING, ConstVal.UTF8);
            p.setProperty(Velocity.OUTPUT_ENCODING, ConstVal.UTF8);
            p.setProperty("file.resource.loader.unicode", "true");
            engine = new VelocityEngine(p);
        }
        return engine;
    }

    /**
     * 检测文件是否存在
     *
     * @return 是否
     */
    private boolean isCreate(String filePath) {
        File file = new File(filePath);
        return !file.exists() || config.getGlobalConfig().isFileOverride();
    }


    // ==================================  相关配置  ==================================
    /**
     * 初始化配置
     */
    protected void initConfig() {
        if (null == config) {
            config = new ConfigBuilder(packageInfo, dataSource, strategy, template, globalConfig);
            if (null != injectionConfig) {
                injectionConfig.setConfig(config);
            }
        }
    }

    public DataSourceConfig getDataSource() {
        return dataSource;
    }

    public AutoGenerator setDataSource(DataSourceConfig dataSource) {
        this.dataSource = dataSource;
        return this;
    }

    public StrategyConfig getStrategy() {
        return strategy;
    }

    public AutoGenerator setStrategy(StrategyConfig strategy) {
        this.strategy = strategy;
        return this;
    }

    public PackageConfig getPackageInfo() {
        return packageInfo;
    }

    public AutoGenerator setPackageInfo(PackageConfig packageInfo) {
        this.packageInfo = packageInfo;
        return this;
    }

    public TemplateConfig getTemplate() {
        return template;
    }

    public AutoGenerator setTemplate(TemplateConfig template) {
        this.template = template;
        return this;
    }

    public ConfigBuilder getConfig() {
        return config;
    }

    public AutoGenerator setConfig(ConfigBuilder config) {
        this.config = config;
        return this;
    }

    public GlobalConfig getGlobalConfig() {
        return globalConfig;
    }

    public AutoGenerator setGlobalConfig(GlobalConfig globalConfig) {
        this.globalConfig = globalConfig;
        return this;
    }

    public InjectionConfig getCfg() {
        return injectionConfig;
    }

    public AutoGenerator setCfg(InjectionConfig injectionConfig) {
        this.injectionConfig = injectionConfig;
        return this;
    }
}
