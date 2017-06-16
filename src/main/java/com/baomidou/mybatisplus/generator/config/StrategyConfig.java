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
package com.baomidou.mybatisplus.generator.config;

import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.toolkit.StringUtils;

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
    public static boolean DB_COLUMN_UNDERLINE = false;

    /**
     * 是否大写命名
     */
    private boolean isCapitalMode = false;

    /**
     * 数据库表映射到实体的命名策略
     */
    private NamingStrategy naming = NamingStrategy.nochange;

    /**
     * 表前缀
     */
    private String[] tablePrefix;

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
    private String superMapperClass = ConstVal.SUPERD_MAPPER_CLASS;

    /**
     * 自定义继承的Service类全称，带包名
     */
    private String superServiceClass = ConstVal.SUPERD_SERVICE_CLASS;

    /**
     * 自定义继承的ServiceImpl类全称，带包名
     */
    private String superServiceImplClass = ConstVal.SUPERD_SERVICEIMPL_CLASS;

    /**
     * 自定义继承的Controller类全称，带包名
     */
    private String superControllerClass;

    /*
     * 需要包含的表名（与exclude二选一配置）
     */
    private String[] include = null;

    /**
     * 需要排除的表名
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
     * Controller中是否生成基本CURD方法,默认不生成
     * 
     */
    private boolean controllerBasicMethod = false;
    /**
     * Controller中基本的分页方法是以mybatis plus默认的分页,还是以PageHelper为分页插件,默认为mybatis plus的分页方法 <br/>
     * 要求 : {@link #controllerBasicMethod} 为 <code>true</code>
     */
    private boolean controllerBasicPagingMethodByPageHelper = false;
            
   

    public void setDbColumnUnderline(boolean dbColumnUnderline) {
        DB_COLUMN_UNDERLINE = dbColumnUnderline;
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
        return isCapitalMode && StringUtils.isCapitalMode( word);
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

    public void setCapitalMode(boolean isCapitalMode) {
        this.isCapitalMode = isCapitalMode;
    }

    public NamingStrategy getNaming() {
        return naming;
    }

    public void setNaming(NamingStrategy naming) {
        this.naming = naming;
    }

    public String[] getTablePrefix() {
        return tablePrefix;
    }

    public void setTablePrefix(String[] tablePrefix) {
        this.tablePrefix = tablePrefix;
    }

    public String getSuperEntityClass() {
        return superEntityClass;
    }

    public void setSuperEntityClass(String superEntityClass) {
        this.superEntityClass = superEntityClass;
    }

    public boolean includeSuperEntityColumns(String fieldName) {
        if (null != superEntityColumns) {
            for (String column : superEntityColumns) {
                if (column.contains(fieldName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public String[] getSuperEntityColumns() {
        return superEntityColumns;
    }

    public void setSuperEntityColumns(String[] superEntityColumns) {
        this.superEntityColumns = superEntityColumns;
    }

    public String getSuperMapperClass() {
        return superMapperClass;
    }

    public void setSuperMapperClass(String superMapperClass) {
        this.superMapperClass = superMapperClass;
    }

    public String getSuperServiceClass() {
        return superServiceClass;
    }

    public void setSuperServiceClass(String superServiceClass) {
        this.superServiceClass = superServiceClass;
    }

    public String getSuperServiceImplClass() {
        return superServiceImplClass;
    }

    public void setSuperServiceImplClass(String superServiceImplClass) {
        this.superServiceImplClass = superServiceImplClass;
    }

    public String getSuperControllerClass() {
        return superControllerClass;
    }

    public void setSuperControllerClass(String superControllerClass) {
        this.superControllerClass = superControllerClass;
    }

    public String[] getInclude() {
        return include;
    }

    public void setInclude(String[] include) {
        this.include = include;
    }

    public String[] getExclude() {
        return exclude;
    }

    public void setExclude(String[] exclude) {
        this.exclude = exclude;
    }

    public boolean isEntityColumnConstant() {
        return entityColumnConstant;
    }

    public void setEntityColumnConstant(boolean entityColumnConstant) {
        this.entityColumnConstant = entityColumnConstant;
    }

    public boolean isEntityBuilderModel() {
        return entityBuilderModel;
    }

    public void setEntityBuilderModel(boolean entityBuilderModel) {
        this.entityBuilderModel = entityBuilderModel;
    }

    public boolean isEntityLombokModel () {
        return entityLombokModel;
    }

    public void setEntityLombokModel ( boolean entityLombokModel ) {
        this.entityLombokModel = entityLombokModel;
    }

    public boolean isEntityBooleanColumnRemoveIsPrefix () {
        return entityBooleanColumnRemoveIsPrefix;
    }

    public void setEntityBooleanColumnRemoveIsPrefix ( boolean entityBooleanColumnRemoveIsPrefix ) {
        this.entityBooleanColumnRemoveIsPrefix = entityBooleanColumnRemoveIsPrefix;
    }

    public boolean isRestControllerStyle () {
        return restControllerStyle;
    }

    public void setRestControllerStyle ( boolean restControllerStyle ) {
        this.restControllerStyle = restControllerStyle;
    }

    public boolean isControllerMappingHyphenStyle () {
        return controllerMappingHyphenStyle;
    }

    public void setControllerMappingHyphenStyle ( boolean controllerMappingHyphenStyle ) {
        this.controllerMappingHyphenStyle = controllerMappingHyphenStyle;
    }

    public boolean isControllerBasicMethod () {
        return controllerBasicMethod;
    }

    public void setControllerBasicMethod ( boolean controllerBasicMethod ) {
        this.controllerBasicMethod = controllerBasicMethod;
    }

    public boolean isControllerBasicPagingMethodByPageHelper () {
        return controllerBasicPagingMethodByPageHelper;
    }

    public void setControllerBasicPagingMethodByPageHelper ( boolean controllerBasicPagingMethodByPageHelper ) {
        this.controllerBasicPagingMethodByPageHelper = controllerBasicPagingMethodByPageHelper;
    }
}
