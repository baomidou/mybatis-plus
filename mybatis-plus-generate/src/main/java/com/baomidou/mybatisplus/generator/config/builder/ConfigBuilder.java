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
package com.baomidou.mybatisplus.generator.config.builder;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.ConstVal;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.IDbQuery;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.TemplateConfig;
import com.baomidou.mybatisplus.generator.config.po.TableField;
import com.baomidou.mybatisplus.generator.config.po.TableFill;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.DbType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.toolkit.StringUtils;

/**
 * <p>
 * 配置汇总 传递给文件生成工具
 * </p>
 *
 * @author YangHu, tangguo, hubin
 * @since 2016-08-30
 */
public class ConfigBuilder {

    /**
     * 模板路径配置信息
     */
    private final TemplateConfig template;
    /**
     * 数据库配置
     */
    private final DataSourceConfig dataSourceConfig;
    /**
     * SQL连接
     */
    private Connection connection;
    /**
     * SQL语句类型
     */
    private IDbQuery dbQuery;
    private String superEntityClass;
    private String superMapperClass;
    /**
     * service超类定义
     */
    private String superServiceClass;
    private String superServiceImplClass;
    private String superControllerClass;
    /**
     * 数据库表信息
     */
    private List<TableInfo> tableInfoList;
    /**
     * 包配置详情
     */
    private Map<String, String> packageInfo;
    /**
     * 路径配置信息
     */
    private Map<String, String> pathInfo;
    /**
     * 策略配置
     */
    private StrategyConfig strategyConfig;
    /**
     * 全局配置信息
     */
    private GlobalConfig globalConfig;
    /**
     * 注入配置信息
     */
    private InjectionConfig injectionConfig;


    /**
     * <p>
     * 在构造器中处理配置
     * </p>
     *
     * @param packageConfig    包配置
     * @param dataSourceConfig 数据源配置
     * @param strategyConfig   表配置
     * @param template         模板配置
     * @param globalConfig     全局配置
     */
    public ConfigBuilder(PackageConfig packageConfig, DataSourceConfig dataSourceConfig, StrategyConfig strategyConfig,
                         TemplateConfig template, GlobalConfig globalConfig) {
        // 全局配置
        if (null == globalConfig) {
            this.globalConfig = new GlobalConfig();
        } else {
            this.globalConfig = globalConfig;
        }
        // 模板配置
        if (null == template) {
            this.template = new TemplateConfig();
        } else {
            this.template = template;
        }
        // 包配置
        if (null == packageConfig) {
            handlerPackage(this.template, this.globalConfig.getOutputDir(), new PackageConfig());
        } else {
            handlerPackage(this.template, this.globalConfig.getOutputDir(), packageConfig);
        }
        this.dataSourceConfig = dataSourceConfig;
        handlerDataSource(dataSourceConfig);
        // 策略配置
        if (null == strategyConfig) {
            this.strategyConfig = new StrategyConfig();
        } else {
            this.strategyConfig = strategyConfig;
        }
        handlerStrategy(this.strategyConfig);
    }

    // ************************ 曝露方法 BEGIN*****************************

    /**
     * <p>
     * 所有包配置信息
     * </p>
     *
     * @return 包配置
     */
    public Map<String, String> getPackageInfo() {
        return packageInfo;
    }


    /**
     * <p>
     * 所有路径配置
     * </p>
     *
     * @return 路径配置
     */
    public Map<String, String> getPathInfo() {
        return pathInfo;
    }


    public String getSuperEntityClass() {
        return superEntityClass;
    }


    public String getSuperMapperClass() {
        return superMapperClass;
    }


    /**
     * <p>
     * 获取超类定义
     * </p>
     *
     * @return 完整超类名称
     */
    public String getSuperServiceClass() {
        return superServiceClass;
    }


    public String getSuperServiceImplClass() {
        return superServiceImplClass;
    }


    public String getSuperControllerClass() {
        return superControllerClass;
    }


    /**
     * <p>
     * 表信息
     * </p>
     *
     * @return 所有表信息
     */
    public List<TableInfo> getTableInfoList() {
        return tableInfoList;
    }

    public ConfigBuilder setTableInfoList(List<TableInfo> tableInfoList) {
        this.tableInfoList = tableInfoList;
        return this;
    }


    /**
     * <p>
     * 模板路径配置信息
     * </p>
     *
     * @return 所以模板路径配置信息
     */
    public TemplateConfig getTemplate() {
        return template == null ? new TemplateConfig() : template;
    }

    // ****************************** 曝露方法 END**********************************

    /**
     * <p>
     * 处理包配置
     * </p>
     *
     * @param template  TemplateConfig
     * @param outputDir
     * @param config    PackageConfig
     */
    private void handlerPackage(TemplateConfig template, String outputDir, PackageConfig config) {
        packageInfo = new HashMap<>();
        packageInfo.put(ConstVal.MODULENAME, config.getModuleName());
        packageInfo.put(ConstVal.ENTITY, joinPackage(config.getParent(), config.getEntity()));
        packageInfo.put(ConstVal.MAPPER, joinPackage(config.getParent(), config.getMapper()));
        packageInfo.put(ConstVal.XML, joinPackage(config.getParent(), config.getXml()));
        packageInfo.put(ConstVal.SERIVCE, joinPackage(config.getParent(), config.getService()));
        packageInfo.put(ConstVal.SERVICEIMPL, joinPackage(config.getParent(), config.getServiceImpl()));
        packageInfo.put(ConstVal.CONTROLLER, joinPackage(config.getParent(), config.getController()));

        // 生成路径信息
        pathInfo = new HashMap<>();
        if (StringUtils.isNotEmpty(template.getEntity(getGlobalConfig().isKotlin()))) {
            pathInfo.put(ConstVal.ENTITY_PATH, joinPath(outputDir, packageInfo.get(ConstVal.ENTITY)));
        }
        if (StringUtils.isNotEmpty(template.getMapper())) {
            pathInfo.put(ConstVal.MAPPER_PATH, joinPath(outputDir, packageInfo.get(ConstVal.MAPPER)));
        }
        if (StringUtils.isNotEmpty(template.getXml())) {
            pathInfo.put(ConstVal.XML_PATH, joinPath(outputDir, packageInfo.get(ConstVal.XML)));
        }
        if (StringUtils.isNotEmpty(template.getService())) {
            pathInfo.put(ConstVal.SERIVCE_PATH, joinPath(outputDir, packageInfo.get(ConstVal.SERIVCE)));
        }
        if (StringUtils.isNotEmpty(template.getServiceImpl())) {
            pathInfo.put(ConstVal.SERVICEIMPL_PATH, joinPath(outputDir, packageInfo.get(ConstVal.SERVICEIMPL)));
        }
        if (StringUtils.isNotEmpty(template.getController())) {
            pathInfo.put(ConstVal.CONTROLLER_PATH, joinPath(outputDir, packageInfo.get(ConstVal.CONTROLLER)));
        }
    }


    /**
     * <p>
     * 处理数据源配置
     * </p>
     *
     * @param config DataSourceConfig
     */
    private void handlerDataSource(DataSourceConfig config) {
        connection = config.getConn();
        dbQuery = config.getDbQuery();
    }


    /**
     * <p>
     * 处理数据库表 加载数据库表、列、注释相关数据集
     * </p>
     *
     * @param config StrategyConfig
     */
    private void handlerStrategy(StrategyConfig config) {
        processTypes(config);
        tableInfoList = getTablesInfo(config);
    }


    /**
     * <p>
     * 处理superClassName,IdClassType,IdStrategy配置
     * </p>
     *
     * @param config 策略配置
     */
    private void processTypes(StrategyConfig config) {
        if (StringUtils.isEmpty(config.getSuperServiceClass())) {
            superServiceClass = ConstVal.SUPERD_SERVICE_CLASS;
        } else {
            superServiceClass = config.getSuperServiceClass();
        }
        if (StringUtils.isEmpty(config.getSuperServiceImplClass())) {
            superServiceImplClass = ConstVal.SUPERD_SERVICEIMPL_CLASS;
        } else {
            superServiceImplClass = config.getSuperServiceImplClass();
        }
        if (StringUtils.isEmpty(config.getSuperMapperClass())) {
            superMapperClass = ConstVal.SUPERD_MAPPER_CLASS;
        } else {
            superMapperClass = config.getSuperMapperClass();
        }
        superEntityClass = config.getSuperEntityClass();
        superControllerClass = config.getSuperControllerClass();
    }


    /**
     * <p>
     * 处理表对应的类名称
     * </P>
     *
     * @param tableList 表名称
     * @param strategy  命名策略
     * @param config    策略配置项
     * @return 补充完整信息后的表
     */
    private List<TableInfo> processTable(List<TableInfo> tableList, NamingStrategy strategy, StrategyConfig config) {
        String[] tablePrefix = config.getTablePrefix();
        String[] fieldPrefix = config.getFieldPrefix();
        for (TableInfo tableInfo : tableList) {
            tableInfo.setEntityName(strategyConfig, NamingStrategy.capitalFirst(processName(tableInfo.getName(), strategy, tablePrefix)));
            if (StringUtils.isNotEmpty(globalConfig.getMapperName())) {
                tableInfo.setMapperName(String.format(globalConfig.getMapperName(), tableInfo.getEntityName()));
            } else {
                tableInfo.setMapperName(tableInfo.getEntityName() + ConstVal.MAPPER);
            }
            if (StringUtils.isNotEmpty(globalConfig.getXmlName())) {
                tableInfo.setXmlName(String.format(globalConfig.getXmlName(), tableInfo.getEntityName()));
            } else {
                tableInfo.setXmlName(tableInfo.getEntityName() + ConstVal.MAPPER);
            }
            if (StringUtils.isNotEmpty(globalConfig.getServiceName())) {
                tableInfo.setServiceName(String.format(globalConfig.getServiceName(), tableInfo.getEntityName()));
            } else {
                tableInfo.setServiceName("I" + tableInfo.getEntityName() + ConstVal.SERIVCE);
            }
            if (StringUtils.isNotEmpty(globalConfig.getServiceImplName())) {
                tableInfo.setServiceImplName(String.format(globalConfig.getServiceImplName(), tableInfo.getEntityName()));
            } else {
                tableInfo.setServiceImplName(tableInfo.getEntityName() + ConstVal.SERVICEIMPL);
            }
            if (StringUtils.isNotEmpty(globalConfig.getControllerName())) {
                tableInfo.setControllerName(String.format(globalConfig.getControllerName(), tableInfo.getEntityName()));
            } else {
                tableInfo.setControllerName(tableInfo.getEntityName() + ConstVal.CONTROLLER);
            }
            //强制开启字段注解
            checkTableIdTableFieldAnnotation(config, tableInfo, fieldPrefix);
        }
        return tableList;
    }


    /**
     * <p>
     * 检查是否有
     * {@link com.baomidou.mybatisplus.annotations.TableId}
     * {@link com.baomidou.mybatisplus.annotations.TableField}
     * 注解
     * </p>
     *
     * @param config
     * @param tableInfo
     * @param fieldPrefix
     */
    private void checkTableIdTableFieldAnnotation(StrategyConfig config, TableInfo tableInfo, String[] fieldPrefix) {
        boolean importTableFieldAnnotaion = false;
        boolean importTableIdAnnotaion = false;
        if (config.isEntityTableFieldAnnotationEnable()) {
            for (TableField tf : tableInfo.getFields()) {
                tf.setConvert(true);
                importTableFieldAnnotaion = true;
                importTableIdAnnotaion = true;
            }
        } else if (fieldPrefix != null && fieldPrefix.length != 0) {
            for (TableField tf : tableInfo.getFields()) {
                if (NamingStrategy.isPrefixContained(tf.getName(), fieldPrefix)) {
                    if (tf.isKeyFlag()) {
                        importTableIdAnnotaion = true;
                    }
                    tf.setConvert(true);
                    importTableFieldAnnotaion = true;
                }
            }
        }
        if (importTableFieldAnnotaion) {
            tableInfo.getImportPackages().add(com.baomidou.mybatisplus.annotations.TableField.class.getCanonicalName());
        }
        if (importTableIdAnnotaion) {
            tableInfo.getImportPackages().add(com.baomidou.mybatisplus.annotations.TableId.class.getCanonicalName());
        }
        if (globalConfig.getIdType() != null) {
            if (!importTableIdAnnotaion) {
                tableInfo.getImportPackages().add(com.baomidou.mybatisplus.annotations.TableId.class.getCanonicalName());
            }
            tableInfo.getImportPackages().add(com.baomidou.mybatisplus.enums.IdType.class.getCanonicalName());
        }
    }


    /**
     * <p>
     * 获取所有的数据库表信息
     * </p>
     */
    private List<TableInfo> getTablesInfo(StrategyConfig config) {
        boolean isInclude = (null != config.getInclude() && config.getInclude().length > 0);
        boolean isExclude = (null != config.getExclude() && config.getExclude().length > 0);
        if (isInclude && isExclude) {
            throw new RuntimeException("<strategy> 标签中 <include> 与 <exclude> 只能配置一项！");
        }
        //所有的表信息
        List<TableInfo> tableList = new ArrayList<>();

        //需要反向生成或排除的表信息
        List<TableInfo> includeTableList = new ArrayList<>();
        List<TableInfo> excludeTableList = new ArrayList<>();

        //不存在的表名
        Set<String> notExistTables = new HashSet<>();
        PreparedStatement preparedStatement = null;
        try {
            String tablesSql = dbQuery.tablesSql();
            if (DbType.POSTGRE_SQL == dbQuery.dbType()) {
                tablesSql = String.format(tablesSql, dataSourceConfig.getSchemaname());
            }
            //oracle数据库表太多，出现最大游标错误
            else if (DbType.ORACLE == dbQuery.dbType()) {
                if (isInclude) {
                    StringBuilder sb = new StringBuilder(tablesSql);
                    sb.append(" WHERE ").append(dbQuery.tableName()).append(" IN (");
                    for (String tbname : config.getInclude()) {
                        sb.append("'").append(tbname.toUpperCase()).append("',");
                    }
                    sb.replace(sb.length() - 1, sb.length(), ")");
                    tablesSql = sb.toString();
                } else if (isExclude) {
                    StringBuilder sb = new StringBuilder(tablesSql);
                    sb.append(" WHERE ").append(dbQuery.tableName()).append(" NOT IN (");
                    for (String tbname : config.getExclude()) {
                        sb.append("'").append(tbname.toUpperCase()).append("',");
                    }
                    sb.replace(sb.length() - 1, sb.length(), ")");
                    tablesSql = sb.toString();
                }
            }
            preparedStatement = connection.prepareStatement(tablesSql);
            ResultSet results = preparedStatement.executeQuery();
            TableInfo tableInfo;
            while (results.next()) {
                String tableName = results.getString(dbQuery.tableName());
                if (StringUtils.isNotEmpty(tableName)) {
                    String tableComment = results.getString(dbQuery.tableComment());
                    if (config.isSkipView() && "VIEW".equals(tableComment)) {
                        // 跳过视图
                        continue;
                    }
                    tableInfo = new TableInfo();
                    tableInfo.setName(tableName);
                    tableInfo.setComment(tableComment);
                    if (isInclude) {
                        for (String includeTab : config.getInclude()) {
                            if (includeTab.equalsIgnoreCase(tableName)) {
                                includeTableList.add(tableInfo);
                            } else {
                                notExistTables.add(includeTab);
                            }
                        }
                    } else if (isExclude) {
                        for (String excludeTab : config.getExclude()) {
                            if (excludeTab.equalsIgnoreCase(tableName)) {
                                excludeTableList.add(tableInfo);
                            } else {
                                notExistTables.add(excludeTab);
                            }
                        }
                    }
                    tableList.add(tableInfo);
                } else {
                    System.err.println("当前数据库为空！！！");
                }
            }
            // 将已经存在的表移除，获取配置中数据库不存在的表
            for (TableInfo tabInfo : tableList) {
                notExistTables.remove(tabInfo.getName());
            }

            if (notExistTables.size() > 0) {
                System.err.println("表 " + notExistTables + " 在数据库中不存在！！！");
            }

            // 需要反向生成的表信息
            if (isExclude) {
                tableList.removeAll(excludeTableList);
                includeTableList = tableList;
            }
            if (!isInclude && !isExclude) {
                includeTableList = tableList;
            }
            /**
             * 性能优化，只处理需执行表字段 github issues/219
             */
            for (TableInfo ti : includeTableList) {
                this.convertTableFields(ti, config.getColumnNaming());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // 释放资源
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return processTable(includeTableList, config.getNaming(), config);
    }


    /**
     * <p>
     * 将字段信息与表信息关联
     * </p>
     *
     * @param tableInfo 表信息
     * @param strategy  命名策略
     * @return
     */
    private TableInfo convertTableFields(TableInfo tableInfo, NamingStrategy strategy) {
        boolean haveId = false;
        List<TableField> fieldList = new ArrayList<>();
        List<TableField> commonFieldList = new ArrayList<>();
        try {
            String tableFieldsSql = dbQuery.tableFieldsSql();
            if (DbType.POSTGRE_SQL == dbQuery.dbType()) {
                tableFieldsSql = String.format(tableFieldsSql, dataSourceConfig.getSchemaname(), tableInfo.getName());
            } else {
                tableFieldsSql = String.format(tableFieldsSql, tableInfo.getName());
            }
            PreparedStatement preparedStatement = connection.prepareStatement(tableFieldsSql);
            ResultSet results = preparedStatement.executeQuery();
            while (results.next()) {
                TableField field = new TableField();
                String key = results.getString(dbQuery.fieldKey());
                // 避免多重主键设置，目前只取第一个找到ID，并放到list中的索引为0的位置
                boolean isId = StringUtils.isNotEmpty(key) && key.toUpperCase().equals("PRI");
                // 处理ID
                if (isId && !haveId) {
                    field.setKeyFlag(true);
                    if (dbQuery.isKeyIdentity(results)) {
                        field.setKeyIdentityFlag(true);
                    }
                    haveId = true;
                } else {
                    field.setKeyFlag(false);
                }
                // 自定义字段查询
                String[] fcs = dbQuery.fieldCustom();
                if (null != fcs) {
                    Map<String, Object> customMap = new HashMap<>();
                    for (String fc : fcs) {
                        customMap.put(fc, results.getObject(fc));
                    }
                    field.setCustomMap(customMap);
                }
                // 处理其它信息
                field.setName(results.getString(dbQuery.fieldName()));
                field.setType(results.getString(dbQuery.fieldType()));
                field.setPropertyName(strategyConfig, processName(field.getName(), strategy));
                field.setColumnType(dataSourceConfig.getTypeConvert().processTypeConvert(field.getType()));
                field.setComment(results.getString(dbQuery.fieldComment()));
                if (strategyConfig.includeSuperEntityColumns(field.getName())) {
                    // 跳过公共字段
                    commonFieldList.add(field);
                    continue;
                }
                // 填充逻辑判断
                List<TableFill> tableFillList = this.getStrategyConfig().getTableFillList();
                if (null != tableFillList) {
                    for (TableFill tableFill : tableFillList) {
                        if (tableFill.getFieldName().equals(field.getName())) {
                            field.setFill(tableFill.getFieldFill().name());
                            break;
                        }
                    }
                }
                fieldList.add(field);
            }
        } catch (SQLException e) {
            System.err.println("SQL Exception：" + e.getMessage());
        }
        tableInfo.setFields(fieldList);
        tableInfo.setCommonFields(commonFieldList);
        return tableInfo;
    }


    /**
     * <p>
     * 连接路径字符串
     * </p>
     *
     * @param parentDir   路径常量字符串
     * @param packageName 包名
     * @return 连接后的路径
     */
    private String joinPath(String parentDir, String packageName) {
        if (StringUtils.isEmpty(parentDir)) {
            parentDir = System.getProperty(ConstVal.JAVA_TMPDIR);
        }
        if (!StringUtils.endsWith(parentDir, File.separator)) {
            parentDir += File.separator;
        }
        packageName = packageName.replaceAll("\\.", "\\" + File.separator);
        return parentDir + packageName;
    }


    /**
     * <p>
     * 连接父子包名
     * </p>
     *
     * @param parent     父包名
     * @param subPackage 子包名
     * @return 连接后的包名
     */
    private String joinPackage(String parent, String subPackage) {
        if (StringUtils.isEmpty(parent)) {
            return subPackage;
        }
        return parent + "." + subPackage;
    }


    /**
     * <p>
     * 处理字段名称
     * </p>
     *
     * @return 根据策略返回处理后的名称
     */
    private String processName(String name, NamingStrategy strategy) {
        return processName(name, strategy, this.strategyConfig.getFieldPrefix());
    }


    /**
     * <p>
     * 处理表/字段名称
     * </p>
     *
     * @param name
     * @param strategy
     * @param prefix
     * @return 根据策略返回处理后的名称
     */
    private String processName(String name, NamingStrategy strategy, String[] prefix) {
        boolean removePrefix = false;
        if (prefix != null && prefix.length >= 1) {
            removePrefix = true;
        }
        String propertyName;
        if (removePrefix) {
            if (strategy == NamingStrategy.underline_to_camel) {
                // 删除前缀、下划线转驼峰
                propertyName = NamingStrategy.removePrefixAndCamel(name, prefix);
            } else {
                // 删除前缀
                propertyName = NamingStrategy.removePrefix(name, prefix);
            }
        } else if (strategy == NamingStrategy.underline_to_camel) {
            // 下划线转驼峰
            propertyName = NamingStrategy.underlineToCamel(name);
        } else {
            // 不处理
            propertyName = name;
        }
        return propertyName;
    }


    public StrategyConfig getStrategyConfig() {
        return strategyConfig;
    }


    public ConfigBuilder setStrategyConfig(StrategyConfig strategyConfig) {
        this.strategyConfig = strategyConfig;
        return this;
    }


    public GlobalConfig getGlobalConfig() {
        return globalConfig;
    }


    public ConfigBuilder setGlobalConfig(GlobalConfig globalConfig) {
        this.globalConfig = globalConfig;
        return this;
    }


    public InjectionConfig getInjectionConfig() {
        return injectionConfig;
    }


    public ConfigBuilder setInjectionConfig(InjectionConfig injectionConfig) {
        this.injectionConfig = injectionConfig;
        return this;
    }
}
