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
package com.baomidou.mybatisplus.generator.config;

import java.util.List;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.generator.config.po.TableFill;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

/**
 * <p>
 * 策略配置项
 * </p>
 *
 * @author YangHu, tangguo, hubin
 * @since 2016/8/30
 */
public class StrategyConfig {

    /**
     * 表名、字段名、是否使用下划线命名（默认 false）
     */
    private boolean dbColumnUnderline = false;

    /**
     * 是否大写命名
     */
    private boolean isCapitalMode = false;

    /**
     * 是否跳过视图
     */
    private boolean skipView = false;

    /**
     * 数据库表映射到实体的命名策略
     */
    private NamingStrategy naming = NamingStrategy.nochange;
    /**
     * 数据库表字段映射到实体的命名策略<br/>
     * 未指定按照 naming 执行
     */
    private NamingStrategy columnNaming = null;

    /**
     * 表前缀
     */
    private String[] tablePrefix;

    /**
     * 字段前缀
     */
    private String[] fieldPrefix;

    /**
     * 自定义继承的Entity类全称，带包名
     */
    private String superEntityClass;

    /**
     * 自定义基础的Entity类，公共字段
     */
    private String[] superEntityColumns;

    /**
     * 自定义继承的Mapper类全称，带包名
     */
    private String superMapperClass = ConstVal.SUPER_MAPPER_CLASS;

    /**
     * 自定义继承的Service类全称，带包名
     */
    private String superServiceClass = ConstVal.SUPER_SERVICE_CLASS;

    /**
     * 自定义继承的ServiceImpl类全称，带包名
     */
    private String superServiceImplClass = ConstVal.SUPER_SERVICE_IMPL_CLASS;

    /**
     * 自定义继承的Controller类全称，带包名
     */
    private String superControllerClass;

    /**
     * 需要包含的表名，允许正则表达式（与exclude二选一配置）
     */
    private String[] include = null;

    /**
     * 需要排除的表名，允许正则表达式
     */
    private String[] exclude = null;
    /**
     * 【实体】是否生成字段常量（默认 false）<br>
     * -----------------------------------<br>
     * public static final String ID = "test_id";
     */
    private boolean entityColumnConstant = false;

    /**
     * 【实体】是否为构建者模型（默认 false）<br>
     * -----------------------------------<br>
     * public User setName(String name) { this.name = name; return this; }
     */
    private boolean entityBuilderModel = false;

    /**
     * 【实体】是否为lombok模型（默认 false）<br>
     * <a href="https://projectlombok.org/">document</a>
     */
    private boolean entityLombokModel = false;

    /**
     * Boolean类型字段是否移除is前缀（默认 false）<br>
     * 比如 : 数据库字段名称 : 'is_xxx',类型为 : tinyint. 在映射实体的时候则会去掉is,在实体类中映射最终结果为 xxx
     */
    private boolean entityBooleanColumnRemoveIsPrefix = false;
    /**
     * 生成 <code>@RestController</code> 控制器
     * <pre>
     *      <code>@Controller</code> -> <code>@RestController</code>
     * </pre>
     */
    private boolean restControllerStyle = false;
    /**
     * 驼峰转连字符
     * <pre>
     *      <code>@RequestMapping("/managerUserActionHistory")</code> -> <code>@RequestMapping("/manager-user-action-history")</code>
     * </pre>
     */
    private boolean controllerMappingHyphenStyle = false;

    /**
     * 是否生成实体时，生成字段注解
     */
    private boolean entityTableFieldAnnotationEnable = false;
    /**
     * 乐观锁属性名称
     */
    private String versionFieldName;

    /**
     * 逻辑删除属性名称
     */
    private String logicDeleteFieldName;

    /**
     * 表填充字段
     */
    private List<TableFill> tableFillList = null;

    public boolean isDbColumnUnderline() {
        return dbColumnUnderline;
    }

    public StrategyConfig setDbColumnUnderline(boolean dbColumnUnderline) {
        this.dbColumnUnderline = dbColumnUnderline;
        return this;
    }

    /**
     * <p>
     * 大写命名、字段符合大写字母数字下划线命名
     * </p>
     *
     * @param word 待判断字符串
     * @return
     */
    public boolean isCapitalModeNaming(String word) {
        return isCapitalMode && StringUtils.isCapitalMode(word);
    }

    /**
     * <p>
     * 表名称包含指定前缀
     * </p>
     *
     * @param tableName 表名称
     * @return
     */
    public boolean containsTablePrefix(String tableName) {
        if (null != tableName) {
            String[] tps = getTablePrefix();
            if (null != tps) {
                for (String tp : tps) {
                    if (tableName.contains(tp)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isCapitalMode() {
        return isCapitalMode;
    }

    public StrategyConfig setCapitalMode(boolean isCapitalMode) {
        this.isCapitalMode = isCapitalMode;
        return this;
    }

    public boolean isSkipView() {
        return skipView;
    }

    public StrategyConfig setSkipView(boolean skipView) {
        this.skipView = skipView;
        return this;
    }

    public NamingStrategy getNaming() {
        return naming;
    }

    public StrategyConfig setNaming(NamingStrategy naming) {
        this.naming = naming;
        return this;
    }

    public NamingStrategy getColumnNaming() {
        if (null == columnNaming) {
            // 未指定以 naming 策略为准
            return naming;
        }
        return columnNaming;
    }

    public StrategyConfig setColumnNaming(NamingStrategy columnNaming) {
        this.columnNaming = columnNaming;
        return this;
    }

    public String[] getTablePrefix() {
        return tablePrefix;
    }

    public StrategyConfig setTablePrefix(String... tablePrefix) {
        this.tablePrefix = tablePrefix;
        return this;
    }

    public String getSuperEntityClass() {
        return superEntityClass;
    }

    public StrategyConfig setSuperEntityClass(String superEntityClass) {
        this.superEntityClass = superEntityClass;
        return this;
    }

    public boolean includeSuperEntityColumns(String fieldName) {
        if (null != superEntityColumns) {
            for (String column : superEntityColumns) {
                if (column.equals(fieldName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public String[] getSuperEntityColumns() {
        return superEntityColumns;
    }

    public StrategyConfig setSuperEntityColumns(String... superEntityColumns) {
        this.superEntityColumns = superEntityColumns;
        return this;
    }

    public String getSuperMapperClass() {
        return superMapperClass;
    }

    public StrategyConfig setSuperMapperClass(String superMapperClass) {
        this.superMapperClass = superMapperClass;
        return this;
    }

    public String getSuperServiceClass() {
        return superServiceClass;
    }

    public StrategyConfig setSuperServiceClass(String superServiceClass) {
        this.superServiceClass = superServiceClass;
        return this;
    }

    public String getSuperServiceImplClass() {
        return superServiceImplClass;
    }

    public StrategyConfig setSuperServiceImplClass(String superServiceImplClass) {
        this.superServiceImplClass = superServiceImplClass;
        return this;
    }

    public String getSuperControllerClass() {
        return superControllerClass;
    }

    public StrategyConfig setSuperControllerClass(String superControllerClass) {
        this.superControllerClass = superControllerClass;
        return this;
    }

    public String[] getInclude() {
        return include;
    }

    public StrategyConfig setInclude(String... include) {
        this.include = include;
        return this;
    }

    public String[] getExclude() {
        return exclude;
    }

    public StrategyConfig setExclude(String... exclude) {
        this.exclude = exclude;
        return this;
    }

    public boolean isEntityColumnConstant() {
        return entityColumnConstant;
    }

    public StrategyConfig setEntityColumnConstant(boolean entityColumnConstant) {
        this.entityColumnConstant = entityColumnConstant;
        return this;
    }

    public boolean isEntityBuilderModel() {
        return entityBuilderModel;
    }

    public StrategyConfig setEntityBuilderModel(boolean entityBuilderModel) {
        this.entityBuilderModel = entityBuilderModel;
        return this;
    }

    public boolean isEntityLombokModel() {
        return entityLombokModel;
    }

    public StrategyConfig setEntityLombokModel(boolean entityLombokModel) {
        this.entityLombokModel = entityLombokModel;
        return this;
    }

    public boolean isEntityBooleanColumnRemoveIsPrefix() {
        return entityBooleanColumnRemoveIsPrefix;
    }

    public StrategyConfig setEntityBooleanColumnRemoveIsPrefix(boolean entityBooleanColumnRemoveIsPrefix) {
        this.entityBooleanColumnRemoveIsPrefix = entityBooleanColumnRemoveIsPrefix;
        return this;
    }

    public boolean isRestControllerStyle() {
        return restControllerStyle;
    }

    public StrategyConfig setRestControllerStyle(boolean restControllerStyle) {
        this.restControllerStyle = restControllerStyle;
        return this;
    }

    public boolean isControllerMappingHyphenStyle() {
        return controllerMappingHyphenStyle;
    }

    public StrategyConfig setControllerMappingHyphenStyle(boolean controllerMappingHyphenStyle) {
        this.controllerMappingHyphenStyle = controllerMappingHyphenStyle;
        return this;
    }

    public String getLogicDeleteFieldName() {
        return logicDeleteFieldName;
    }

    /**
     * 设置逻辑删除字段
     *
     * @param logicDeleteFieldName 数据库字段
     * @return
     */
    public StrategyConfig setLogicDeleteFieldName(String logicDeleteFieldName) {
        this.logicDeleteFieldName = logicDeleteFieldName;
        return this;
    }

    public String getVersionFieldName() {
        return versionFieldName;
    }

    /**
     * 设置乐观锁字段
     *
     * @param versionFieldName 数据库字段
     * @return
     */
    public StrategyConfig setVersionFieldName(String versionFieldName) {
        this.versionFieldName = versionFieldName;
        return this;
    }

    public List<TableFill> getTableFillList() {
        return tableFillList;
    }

    public StrategyConfig setTableFillList(List<TableFill> tableFillList) {
        this.tableFillList = tableFillList;
        return this;
    }

    public String[] getFieldPrefix() {
        return fieldPrefix;
    }

    public StrategyConfig setFieldPrefix(String[] fieldPrefix) {
        this.fieldPrefix = fieldPrefix;
        return this;
    }

    public StrategyConfig fieldPrefix(String... fieldPrefixs) {
        setFieldPrefix(fieldPrefixs);
        return this;
    }

    public StrategyConfig entityTableFieldAnnotationEnable(boolean isEnableAnnotation) {
        entityTableFieldAnnotationEnable = isEnableAnnotation;
        return this;
    }

    public boolean isEntityTableFieldAnnotationEnable() {
        return entityTableFieldAnnotationEnable;
    }
}
