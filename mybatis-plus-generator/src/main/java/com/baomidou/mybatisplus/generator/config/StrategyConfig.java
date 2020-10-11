/*
 * Copyright (c) 2011-2020, baomidou (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.generator.config;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.generator.config.builder.BaseBuilder;
import com.baomidou.mybatisplus.generator.config.builder.ControllerBuilder;
import com.baomidou.mybatisplus.generator.config.builder.EntityBuilder;
import com.baomidou.mybatisplus.generator.config.builder.MapperBuilder;
import com.baomidou.mybatisplus.generator.config.builder.ServiceBuilder;
import com.baomidou.mybatisplus.generator.config.po.LikeTable;
import com.baomidou.mybatisplus.generator.config.po.TableFill;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 策略配置项
 *
 * @author YangHu, tangguo, hubin
 * @since 2016/8/30
 */
@Data
@Accessors(chain = true)
public class StrategyConfig {
    /**
     * 是否大写命名
     */
    private boolean isCapitalMode = false;
    /**
     * 是否跳过视图
     */
    private boolean skipView = false;
    /**
     * 表前缀
     */
    @Setter(AccessLevel.NONE)
    private final Set<String> tablePrefix = new HashSet<>();
    /**
     * 字段前缀
     */
    @Setter(AccessLevel.NONE)
    private final Set<String> fieldPrefix = new HashSet<>();
    /**
     * 需要包含的表名，允许正则表达式（与exclude二选一配置）<br/>
     * 当{@link #enableSqlFilter}为true时，正则表达式无效.
     */
    @Setter(AccessLevel.NONE)
    private final Set<String> include = new HashSet<>();
    /**
     * 需要排除的表名，允许正则表达式<br/>
     * 当{@link #enableSqlFilter}为true时，正则表达式无效.
     */
    @Setter(AccessLevel.NONE)
    private final Set<String> exclude = new HashSet<>();
    /**
     * 启用sql过滤，语法不能支持使用sql过滤表的话，可以考虑关闭此开关.
     *
     * @since 3.3.1
     */
    private boolean enableSqlFilter = true;
    /**
     * 包含表名
     *
     * @since 3.3.0
     */
    private LikeTable likeTable;
    /**
     * 不包含表名
     *
     * @since 3.3.0
     */
    private LikeTable notLikeTable;

    /**
     * 后续不再公开此构造方法
     *
     * @see Builder
     * @deprecated 3.4.1
     */
    @Deprecated
    public StrategyConfig() {
    }

    @Getter(AccessLevel.NONE)
    private final EntityBuilder entityBuilder = new EntityBuilder(this);

    @Getter(AccessLevel.NONE)
    private final ControllerBuilder controllerBuilder = new ControllerBuilder(this);

    @Getter(AccessLevel.NONE)
    private final MapperBuilder mapperBuilder = new MapperBuilder(this);

    @Getter(AccessLevel.NONE)
    private final ServiceBuilder serviceBuilder = new ServiceBuilder(this);

    /**
     * 实体配置
     *
     * @return 实体配置
     * @since 3.4.1
     */
    public EntityBuilder entity() {
        return entityBuilder;
    }

    /**
     * 控制器配置
     *
     * @return 控制器配置
     * @since 3.4.1
     */
    public ControllerBuilder controller() {
        return controllerBuilder;
    }

    /**
     * Mapper配置
     *
     * @return Mapper配置
     * @since 3.4.1
     */
    public MapperBuilder mapper() {
        return mapperBuilder;
    }

    /**
     * Service配置
     *
     * @return Service配置
     * @since 3.4.1
     */
    public ServiceBuilder service() {
        return serviceBuilder;
    }


    /**
     * 获取名称转换实现
     *
     * @return 名称转换
     * @see EntityBuilder#getNameConvert()
     * @deprecated 3.4.1
     */
    public INameConvert getNameConvert() {
        return entityBuilder.getNameConvert();
    }

    /**
     * 名称转换实现
     *
     * @param nameConvert 名称转换实现
     * @return this
     * @see EntityBuilder#nameConvert(INameConvert)
     * @deprecated 3.4.1
     */
    @Deprecated
    public StrategyConfig setNameConvert(INameConvert nameConvert) {
        this.entityBuilder.nameConvert(nameConvert);
        return this;
    }

    /**
     * 获取数据库表映射到实体的命名策略
     *
     * @return 命名策略
     * @see EntityBuilder#getNaming()
     * @deprecated 3.4.1
     */
    @Deprecated
    public NamingStrategy getNaming() {
        return entityBuilder.getNaming();
    }

    /**
     * 设置数据库表映射到实体的命名策略
     *
     * @param naming 命名策略
     * @return this
     * @see EntityBuilder#naming(NamingStrategy)
     * @deprecated 3.4.1
     */
    @Deprecated
    public StrategyConfig setNaming(NamingStrategy naming) {
        this.entityBuilder.naming(naming);
        return this;
    }

    /**
     * 大写命名、字段符合大写字母数字下划线命名
     *
     * @param word 待判断字符串
     */
    public boolean isCapitalModeNaming(String word) {
        return isCapitalMode && StringUtils.isCapitalMode(word);
    }

    /**
     * 表名称包含指定前缀
     *
     * @param tableName 表名称
     * @deprecated 3.3.2 {@link #startsWithTablePrefix(String)}
     */
    @Deprecated
    public boolean containsTablePrefix(String tableName) {
        return this.tablePrefix.stream().anyMatch(tableName::contains);
    }

    /**
     * 表名称匹配表前缀
     *
     * @param tableName 表名称
     * @since 3.3.2
     */
    public boolean startsWithTablePrefix(String tableName) {
        return this.tablePrefix.stream().anyMatch(tableName::startsWith);
    }

    /**
     * 获取字段命名策略
     *
     * @return 命名策略
     * @see EntityBuilder#getColumnNaming()
     * @deprecated 3.4.1
     */
    @Deprecated
    public NamingStrategy getColumnNaming() {
        return entityBuilder.getColumnNaming();
    }

    /**
     * 设置字段命名策略
     *
     * @param columnNaming 命名策略
     * @return this
     * @deprecated 3.4.1
     */
    @Deprecated
    public StrategyConfig setColumnNaming(NamingStrategy columnNaming) {
        this.entityBuilder.columnNaming(columnNaming);
        return this;
    }

    /**
     * @param tablePrefix 表前缀
     * @return this
     * @see Builder#addTablePrefix(String...)
     * @deprecated 3.4.1
     */
    @Deprecated
    public StrategyConfig setTablePrefix(String... tablePrefix) {
        this.tablePrefix.clear();   //保持语义
        this.tablePrefix.addAll(Arrays.asList(tablePrefix));
        return this;
    }

    /**
     * @param fieldName 字段名
     * @return 是否匹配
     * @see EntityBuilder#matchSuperEntityColumns(String)
     * @deprecated 3.4.1
     */
    @Deprecated
    public boolean includeSuperEntityColumns(String fieldName) {
        // 公共字段判断忽略大小写【 部分数据库大小写不敏感 】
        return entityBuilder.matchSuperEntityColumns(fieldName);
    }

    /**
     * @param superEntityColumns 父类字段
     * @return this
     * @see EntityBuilder#addSuperEntityColumns(String...)
     * @deprecated 3.4.1
     */
    @Deprecated
    public StrategyConfig setSuperEntityColumns(String... superEntityColumns) {
        this.entityBuilder.getSuperEntityColumns().clear();    //保持语义
        this.entityBuilder.getSuperEntityColumns().addAll(Arrays.asList(superEntityColumns));
        return this;
    }

    /**
     * @param include 包含表
     * @return this
     * @see Builder#addInclude(String...)
     * @deprecated 3.4.1
     */
    @Deprecated
    public StrategyConfig setInclude(String... include) {
        this.include.clear();   //保持语义
        this.include.addAll(Arrays.asList(include));
        return this;
    }

    /**
     * @param exclude 排除表
     * @return this
     * @see Builder#addExclude(String...)
     * @deprecated 3.4.1
     */
    @Deprecated
    public StrategyConfig setExclude(String... exclude) {
        this.exclude.clear();   //保持语义
        this.exclude.addAll(Arrays.asList(exclude));
        return this;
    }

    /**
     * @param fieldPrefixs 字段前缀
     * @return this
     * @see Builder#addFieldPrefix(String...)
     * @deprecated 3.4.1
     */
    @Deprecated
    public StrategyConfig setFieldPrefix(String... fieldPrefixs) {
        this.fieldPrefix.clear();   //保持语义
        this.fieldPrefix.addAll(Arrays.asList(fieldPrefixs));
        return this;
    }

    /**
     * 设置实体父类
     *
     * @param superEntityClass 类全名称
     * @return this
     * @see EntityBuilder#superClass(String)
     * @deprecated 3.4.1
     */
    @Deprecated
    public StrategyConfig setSuperEntityClass(String superEntityClass) {
        this.entityBuilder.superClass(superEntityClass);
        return this;
    }

    /**
     * 获取实体父类全名称
     *
     * @return 类全名称
     * @see EntityBuilder#getSuperClass() ()
     * @deprecated 3.4.1
     */
    @Deprecated
    public String getSuperEntityClass() {
        return entityBuilder.getSuperClass();
    }

    /**
     * <p>
     * 设置实体父类，该设置自动识别公共字段<br/>
     * 属性 superEntityColumns 改配置无需再次配置
     * </p>
     * <p>
     * 注意！！字段策略要在设置实体父类之前有效
     * </p>
     *
     * @param clazz 实体父类 Class
     * @return this
     * @see EntityBuilder#superClass(Class)
     * @deprecated 3.4.1
     */
    @Deprecated
    public StrategyConfig setSuperEntityClass(Class<?> clazz) {
        this.entityBuilder.superClass(clazz);
        return this;
    }

    /**
     * <p>
     * 设置实体父类，该设置自动识别公共字段<br/>
     * 属性 superEntityColumns 改配置无需再次配置
     * </p>
     *
     * @param clazz        实体父类 Class
     * @param columnNaming 字段命名策略
     * @return this
     * @see EntityBuilder#columnNaming(NamingStrategy)
     * @see EntityBuilder#superClass(Class)
     * @deprecated 3.4.1 {@link #setSuperEntityClass(Class)} {@link #setColumnNaming(NamingStrategy)}
     */
    @Deprecated
    public StrategyConfig setSuperEntityClass(Class<?> clazz, NamingStrategy columnNaming) {
        this.entityBuilder.columnNaming(columnNaming);
        this.entityBuilder.superClass(clazz);
        return this;
    }

    /**
     * Service接口父类
     *
     * @param clazz 类
     * @return this
     * @see ServiceBuilder#superServiceClass(Class)
     * @deprecated 3.4.1
     */
    @Deprecated
    public StrategyConfig setSuperServiceClass(Class<?> clazz) {
        this.serviceBuilder.superServiceClass(clazz);
        return this;
    }

    /**
     * Service接口父类
     *
     * @param superServiceClass 类名
     * @return this
     * @see ServiceBuilder#superServiceClass(String)
     * @deprecated 3.4.1
     */
    @Deprecated
    public StrategyConfig setSuperServiceClass(String superServiceClass) {
        this.serviceBuilder.superServiceClass(superServiceClass);
        return this;
    }

    /**
     * Service接口父类
     *
     * @return Service接口父类
     * @see ServiceBuilder#getSuperServiceClass()
     * @deprecated 3.4.1
     */
    @Deprecated
    public String getSuperServiceClass() {
        return serviceBuilder.getSuperServiceClass();
    }

    /**
     * Service实现类父类
     *
     * @param clazz 类
     * @return this
     * @see ServiceBuilder#superServiceImplClass(Class)
     * @deprecated 3.4.1
     */
    @Deprecated
    public StrategyConfig setSuperServiceImplClass(Class<?> clazz) {
        this.serviceBuilder.superServiceImplClass(clazz);
        return this;
    }

    /**
     * Service实现类父类
     *
     * @param superServiceImplClass 类名
     * @return this
     * @see ServiceBuilder#superServiceImplClass(Class)
     * @deprecated 3.4.1
     */
    @Deprecated
    public StrategyConfig setSuperServiceImplClass(String superServiceImplClass) {
        this.serviceBuilder.superServiceImplClass(superServiceImplClass);
        return this;
    }

    /**
     * 获取Service实现类父类
     *
     * @return this
     * @see ServiceBuilder#getSuperServiceImplClass()
     * @deprecated 3.4.1
     */
    @Deprecated
    public String getSuperServiceImplClass() {
        return serviceBuilder.getSuperServiceImplClass();
    }

    /**
     * 设置父类控制器
     *
     * @param clazz 父类控制器
     * @return this
     * @see ControllerBuilder#superClass(String)
     * @deprecated 3.4.1
     */
    @Deprecated
    public StrategyConfig setSuperControllerClass(Class<?> clazz) {
        this.controllerBuilder.superClass(clazz);
        return this;
    }

    /**
     * 设置父类控制器
     *
     * @param superControllerClass 父类控制器
     * @return this
     * @see ControllerBuilder#superClass(String)
     * @deprecated 3.4.1
     */
    @Deprecated
    public StrategyConfig setSuperControllerClass(String superControllerClass) {
        this.controllerBuilder.superClass(superControllerClass);
        return this;
    }

    /**
     * 获取父类控制器
     *
     * @return 父类控制器
     * @see ControllerBuilder#getSuperClass()
     * @deprecated 3.4.1
     */
    @Deprecated
    public String getSuperControllerClass() {
        return controllerBuilder.getSuperClass();
    }

    /**
     * 是否生成@RestController控制器
     *
     * @return 是否生成
     * @see ControllerBuilder#isRestStyle()
     */
    @Deprecated
    public boolean isRestControllerStyle() {
        return controllerBuilder.isRestStyle();
    }

    /**
     * 生成@RestController控制器
     *
     * @param restControllerStyle 是否生成
     * @return this
     * @see ControllerBuilder#restStyle(boolean)
     */
    @Deprecated
    public StrategyConfig setRestControllerStyle(boolean restControllerStyle) {
        this.controllerBuilder.restStyle(restControllerStyle);
        return this;
    }

    /**
     * 是否驼峰转连字符
     *
     * @return 是否驼峰转连字符
     * @see ControllerBuilder#isHyphenStyle()
     * @deprecated 3.4.1
     */
    @Deprecated
    public boolean isControllerMappingHyphenStyle() {
        return controllerBuilder.isHyphenStyle();
    }

    /**
     * 是否驼峰转连字符
     *
     * @param controllerMappingHyphenStyle 是否驼峰转连字符
     * @return this
     * @see ControllerBuilder#hyphenStyle(boolean)
     * @deprecated 3.4.1
     */
    @Deprecated
    public StrategyConfig setControllerMappingHyphenStyle(boolean controllerMappingHyphenStyle) {
        this.controllerBuilder.hyphenStyle(controllerMappingHyphenStyle);
        return this;
    }

    /**
     * 设置表填充字段
     *
     * @param tableFillList tableFillList
     * @see EntityBuilder#addTableFills(List)
     * @deprecated 3.4.1
     */
    @Deprecated
    public StrategyConfig setTableFillList(List<TableFill> tableFillList) {
        this.entityBuilder.getTableFillList().clear(); //保持语义
        this.entityBuilder.addTableFills(tableFillList.toArray(new TableFill[]{}));
        return this;
    }

    /**
     * 获取表填充字段
     *
     * @return 表填充字段
     * @see EntityBuilder#getTableFillList()
     * @deprecated 3.4.1
     */
    @Deprecated
    public List<TableFill> getTableFillList() {
        return entityBuilder.getTableFillList();
    }

    /**
     * <p>
     * 父类 Class 反射属性转换为公共字段
     * </p>
     *
     * @param clazz 实体父类 Class
     * @deprecated 3.4.1
     */
    @Deprecated
    protected void convertSuperEntityColumns(Class<?> clazz) {
        entityBuilder.convertSuperEntityColumns(clazz);
    }

    /**
     * 是否为构建者模型
     *
     * @return 是否为构建者模型
     * @deprecated 3.3.2 {@link #isChainModel()}
     */
    @Deprecated
    public boolean isEntityBuilderModel() {
        return isChainModel();
    }

    /**
     * 设置是否为构建者模型
     *
     * @param entityBuilderModel 是否为构建者模型
     * @return this
     * @deprecated 3.3.2 {@link #setChainModel(boolean)}
     */
    @Deprecated
    public StrategyConfig setEntityBuilderModel(boolean entityBuilderModel) {
        return setChainModel(entityBuilderModel);
    }

    /**
     * @return 父类字段
     * @see EntityBuilder#getSuperEntityColumns()
     * @deprecated 3.4.1
     */
    @Deprecated
    public Set<String> getSuperEntityColumns() {
        return this.entityBuilder.getSuperEntityColumns();
    }

    /**
     * 获取实体是否为链式模型
     *
     * @return 是否为链式模型
     * @see EntityBuilder#isChain()
     * @deprecated 3.4.1
     */
    @Deprecated
    public boolean isChainModel() {
        return entityBuilder.isChain();
    }

    /**
     * 设置是否为链式模型
     *
     * @param chainModel 链式模型
     * @return this
     * @see EntityBuilder#chainModel(boolean)
     * @deprecated 3.4.1
     */
    public StrategyConfig setChainModel(boolean chainModel) {
        this.entityBuilder.chainModel(chainModel);
        return this;
    }

    /**
     * 实体是否生成serialVersionUID
     *
     * @return 是否生成
     * @see EntityBuilder#isSerialVersionUID()
     * @deprecated 3.4.1
     */
    public boolean isEntitySerialVersionUID() {
        return entityBuilder.isSerialVersionUID();
    }

    /**
     * @param entitySerialVersionUID 是否生成
     * @return this
     * @see EntityBuilder#serialVersionUID(boolean)
     * @deprecated 3.4.1
     */
    @Deprecated
    public StrategyConfig setEntitySerialVersionUID(boolean entitySerialVersionUID) {
        this.entityBuilder.serialVersionUID(entitySerialVersionUID);
        return this;
    }

    /**
     * 是否为lombok模型
     *
     * @return 是否为lombok模型
     * @deprecated 3.4.1
     */
    @Deprecated
    public boolean isEntityLombokModel() {
        return entityBuilder.isLombok();
    }

    /**
     * 设置是否为lombok模型
     * @param entityLombokModel 是否为lombok模型
     * @return this
     */
    @Deprecated
    public StrategyConfig setEntityLombokModel(boolean entityLombokModel) {
        this.entityBuilder.lombok(entityLombokModel);
        return this;
    }

    /**
     * 是否生成字段常量
     *
     * @return 是否生成字段常量
     * @see EntityBuilder#isColumnConstant()
     * @deprecated 3.4.1
     */
    @Deprecated
    public boolean isEntityColumnConstant() {
        return entityBuilder.isColumnConstant();
    }

    /**
     * 是否生成字段常量
     *
     * @param entityColumnConstant 是否生成字段常量
     * @return this
     * @see EntityBuilder#columnConstant(boolean)
     * @deprecated 3.4.1
     */
    @Deprecated
    public StrategyConfig setEntityColumnConstant(boolean entityColumnConstant) {
        this.entityBuilder.columnConstant(entityColumnConstant);
        return this;
    }

    /**
     * Boolean类型字段是否移除is前缀
     *
     * @return 是否移除
     * @see EntityBuilder#isBooleanColumnRemoveIsPrefix()
     * @deprecated 3.4.1
     */
    @Deprecated
    public boolean isEntityBooleanColumnRemoveIsPrefix() {
        return entityBuilder.isBooleanColumnRemoveIsPrefix();
    }

    /**
     * Boolean类型字段是否移除is前缀
     *
     * @param entityBooleanColumnRemoveIsPrefix 是否移除
     * @return this
     * @see EntityBuilder#booleanColumnRemoveIsPrefix(boolean)
     * @deprecated 3.4.1
     */
    @Deprecated
    public StrategyConfig setEntityBooleanColumnRemoveIsPrefix(boolean entityBooleanColumnRemoveIsPrefix) {
        this.entityBuilder.booleanColumnRemoveIsPrefix(entityBooleanColumnRemoveIsPrefix);
        return this;
    }

    /**
     * 生成实体时，是否生成字段注解
     *
     * @return 是否生成
     * @see EntityBuilder#isTableFieldAnnotationEnable()
     * @deprecated 3.4.1
     */
    @Deprecated
    public boolean isEntityTableFieldAnnotationEnable() {
        return entityBuilder.isTableFieldAnnotationEnable();
    }

    /**
     * 生成实体时，是否生成字段注解
     *
     * @param entityTableFieldAnnotationEnable 是否生成
     * @return this
     * @see EntityBuilder#tableFieldAnnotationEnable(boolean)
     * @deprecated 3.4.1
     */
    @Deprecated
    public StrategyConfig setEntityTableFieldAnnotationEnable(boolean entityTableFieldAnnotationEnable) {
        this.entityBuilder.tableFieldAnnotationEnable(entityTableFieldAnnotationEnable);
        return this;
    }

    /**
     * 乐观锁属性名称
     * @deprecated 3.4.1
     * @return 乐观锁属性名称
     */
    @Deprecated
    public String getVersionFieldName() {
        return entityBuilder.getVersionFieldName();
    }

    /**
     * 乐观锁属性名称
     *
     * @param versionFieldName 乐观锁属性名称
     * @return this
     * @see EntityBuilder#versionFieldName(String)
     * @deprecated 3.4.1
     */
    @Deprecated
    public StrategyConfig setVersionFieldName(String versionFieldName) {
        this.entityBuilder.versionFieldName(versionFieldName);
        return this;
    }

    /**
     * 逻辑删除属性名称
     *
     * @return 逻辑删除属性名称
     * @see EntityBuilder#getLogicDeleteFieldName()
     * @deprecated 3.4.1
     */
    @Deprecated
    public String getLogicDeleteFieldName() {
        return entityBuilder.getLogicDeleteFieldName();
    }

    /**
     * 逻辑删除属性名称
     *
     * @param logicDeleteFieldName 逻辑删除属性名称
     * @return this
     * @see EntityBuilder#logicDeleteFieldName(String)
     * @deprecated 3.4.1
     */
    @Deprecated
    public StrategyConfig setLogicDeleteFieldName(String logicDeleteFieldName) {
        this.entityBuilder.logicDeleteFieldName(logicDeleteFieldName);
        return this;
    }

    /**
     * 父类Mapper
     *
     * @return 类名
     * @see MapperBuilder#getSuperClass()
     * @deprecated 3.4.1
     */
    @Deprecated
    public String getSuperMapperClass() {
        return mapperBuilder.getSuperClass();
    }

    /**
     * 父类Mapper
     *
     * @param superMapperClass 类名
     * @return this
     * @see MapperBuilder#superClass(String)
     * @deprecated 3.4.1
     */
    @Deprecated
    public StrategyConfig setSuperMapperClass(String superMapperClass) {
        this.mapperBuilder.superClass(superMapperClass);
        return this;
    }

    /**
     * 验证配置项
     *
     * @since 3.4.1
     */
    public void validate() {
        boolean isInclude = this.getInclude().size() > 0;
        boolean isExclude = this.getExclude().size() > 0;
        if (isInclude && isExclude) {
            throw new IllegalArgumentException("<strategy> 标签中 <include> 与 <exclude> 只能配置一项！");
        }
        if (this.getNotLikeTable() != null && this.getLikeTable() != null) {
            throw new IllegalArgumentException("<strategy> 标签中 <likeTable> 与 <notLikeTable> 只能配置一项！");
        }
    }

    /**
     * 包含表名匹配
     *
     * @param tableName 表名
     * @return 是否匹配
     * @since 3.4.1
     */
    public boolean matchIncludeTable(String tableName) {
        return matchTable(tableName, this.getInclude());
    }

    /**
     * 排除表名匹配
     *
     * @param tableName 表名
     * @return 是否匹配
     * @since 3.4.1
     */
    public boolean matchExcludeTable(String tableName) {
        return matchTable(tableName, this.getExclude());
    }

    /**
     * 表名匹配
     *
     * @param tableName   表名
     * @param matchTables 匹配集合
     * @return 是否匹配
     * @since 3.4.1
     */
    private boolean matchTable(String tableName, Set<String> matchTables) {
        return matchTables.stream().anyMatch(t -> tableNameMatches(t, tableName));
    }

    /**
     * 表名匹配
     *
     * @param matchTableName 匹配表名
     * @param dbTableName    数据库表名
     * @return 是否匹配
     */
    private boolean tableNameMatches(String matchTableName, String dbTableName) {
        return matchTableName.equalsIgnoreCase(dbTableName) || StringUtils.matches(matchTableName, dbTableName);
    }

    /**
     * 是否大写命名
     *
     * @param capitalMode 是否大写命名
     * @return this
     * @see Builder#capitalMode(boolean)
     * @deprecated 3.4.1
     */
    @Deprecated
    public StrategyConfig setCapitalMode(boolean capitalMode) {
        this.isCapitalMode = capitalMode;
        return this;
    }

    /**
     * 是否跳过视图
     *
     * @param skipView 是否跳过视图
     * @return this
     * @see Builder#skipView(boolean)
     * @deprecated 3.4.1
     */
    @Deprecated
    public StrategyConfig setSkipView(boolean skipView) {
        this.skipView = skipView;
        return this;
    }

    /**
     * 是否启用sql过滤
     *
     * @param enableSqlFilter 是否启用
     * @return this
     * @see Builder#enableSqlFilter(boolean)
     * @deprecated 3.4.1
     */
    @Deprecated
    public StrategyConfig setEnableSqlFilter(boolean enableSqlFilter) {
        this.enableSqlFilter = enableSqlFilter;
        return this;
    }

    /**
     * 包含表名
     *
     * @param likeTable 包含表名
     * @return this
     * @see Builder#likeTable(LikeTable)
     * @deprecated 3.4.1
     */
    @Deprecated
    public StrategyConfig setLikeTable(LikeTable likeTable) {
        this.likeTable = likeTable;
        return this;
    }

    /**
     * 不包含表名
     *
     * @param notLikeTable 不包含表名
     * @return this
     * @see Builder#notLikeTable(LikeTable)
     * @deprecated 3.4.1
     */
    @Deprecated
    public StrategyConfig setNotLikeTable(LikeTable notLikeTable) {
        this.notLikeTable = notLikeTable;
        return this;
    }

    /**
     * 策略配置构建者
     *
     * @author nieqiurong 2020/10/11.
     * @since 3.4.1
     */
    public static class Builder extends BaseBuilder {

        private final StrategyConfig strategyConfig;

        public Builder() {
            super(new StrategyConfig());
            strategyConfig = super.build();
        }

        public Builder capitalMode(boolean capitalMode){
            this.strategyConfig.isCapitalMode = capitalMode;
            return this;
        }

        public Builder skipView(boolean skipView){
            this.strategyConfig.skipView = skipView;
            return this;
        }

        public Builder enableSqlFilter(boolean enableSqlFilter){
            this.strategyConfig.enableSqlFilter = enableSqlFilter;
            return this;
        }

        public Builder likeTable(LikeTable likeTable){
            this.strategyConfig.likeTable = likeTable;
            return this;
        }

        public Builder notLikeTable(LikeTable notLikeTable) {
            this.strategyConfig.notLikeTable = notLikeTable;
            return this;
        }

        /**
         * 增加字段前缀
         *
         * @param fieldPrefix 字段前缀
         * @return this
         * @since 3.4.1
         */
        public Builder addFieldPrefix(String... fieldPrefix) {
            this.strategyConfig.fieldPrefix.addAll(Arrays.asList(fieldPrefix));
            return this;
        }

        /**
         * 增加包含的表名
         *
         * @param include 包含表
         * @return this
         * @since 3.4.1
         */
        public Builder addInclude(String... include) {
            this.strategyConfig.include.addAll(Arrays.asList(include));
            return this;
        }

        /**
         * 增加排除表
         *
         * @param exclude 排除表
         * @return this
         * @since 3.4.1
         */
        public Builder addExclude(String... exclude) {
            this.strategyConfig.exclude.addAll(Arrays.asList(exclude));
            return this;
        }

        /**
         * 增加表前缀
         *
         * @param tablePrefix 表前缀
         * @return this
         * @since 3.4.1
         */
        public Builder addTablePrefix(String... tablePrefix) {
            this.strategyConfig.tablePrefix.addAll(Arrays.asList(tablePrefix));
            return this;
        }

        @Override
        public StrategyConfig build() {
            this.strategyConfig.validate();
            return strategyConfig;
        }
    }
}
