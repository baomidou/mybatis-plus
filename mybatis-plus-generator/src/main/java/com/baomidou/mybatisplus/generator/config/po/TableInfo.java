/*
 * Copyright (c) 2011-2023, baomidou (jobob@qq.com).
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
package com.baomidou.mybatisplus.generator.config.po;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.config.builder.Entity;
import com.baomidou.mybatisplus.generator.config.builder.Service;
import com.baomidou.mybatisplus.generator.config.rules.IColumnType;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 表信息，关联到当前字段信息
 *
 * @author YangHu, lanjerry
 * @since 2016/8/30
 */
public class TableInfo {

    /**
     * 策略配置
     */
    private final StrategyConfig strategyConfig;

    /**
     * 全局配置信息
     */
    private final GlobalConfig globalConfig;

    /**
     * 包导入信息
     */
    private final Set<String> importPackages = new TreeSet<>();

    /**
     * 是否转换
     */
    @Getter
    private boolean convert;

    /**
     * 表名称
     */
    @Getter
    private String name;

    /**
     * 表注释
     */
    @Getter
    private String comment;

    /**
     * 实体名称
     */
    @Getter
    private String entityName;

    /**
     * mapper名称
     */
    @Getter
    private String mapperName;

    /**
     * xml名称
     */
    @Getter
    private String xmlName;

    /**
     * service名称
     */
    @Getter
    private String serviceName;

    /**
     * serviceImpl名称
     */
    @Getter
    private String serviceImplName;

    /**
     * controller名称
     */
    @Getter
    private String controllerName;

    /**
     * 表字段
     */
    private final List<TableField> fields = new ArrayList<>();

    /**
     * 是否有主键
     */
    @Getter
    private boolean havePrimaryKey;

    /**
     * 公共字段
     */
    private final List<TableField> commonFields = new ArrayList<>();

    /**
     * 字段名称集
     */
    private String fieldNames;

    /**
     * 实体
     */
    private final Entity entity;

    /**
     * 构造方法
     *
     * @param configBuilder 配置构建
     * @param name          表名
     * @since 3.5.0
     */
    public TableInfo(@NotNull ConfigBuilder configBuilder, @NotNull String name) {
        this.strategyConfig = configBuilder.getStrategyConfig();
        this.globalConfig = configBuilder.getGlobalConfig();
        this.entity = configBuilder.getStrategyConfig().entity();
        this.name = name;
    }

    /**
     * @since 3.5.0
     */
    protected TableInfo setConvert() {
        if (strategyConfig.startsWithTablePrefix(name) || entity.isTableFieldAnnotationEnable()) {
            this.convert = true;
        } else {
            this.convert = !entityName.equalsIgnoreCase(name);
        }
        return this;
    }

    public String getEntityPath() {
        return entityName.substring(0, 1).toLowerCase() + entityName.substring(1);
    }

    /**
     * @param entityName 实体名称
     * @return this
     */
    public TableInfo setEntityName(@NotNull String entityName) {
        this.entityName = entityName;
        setConvert();
        return this;
    }

    /**
     * 添加字段
     *
     * @param field 字段
     * @since 3.5.0
     */
    public void addField(@NotNull TableField field) {
        if (entity.matchIgnoreColumns(field.getColumnName())) {
            // 忽略字段不在处理
            return;
        } else if (entity.matchSuperEntityColumns(field.getColumnName())) {
            this.commonFields.add(field);
        } else {
            this.fields.add(field);
        }
    }

    /**
     * @param pkgs 包空间
     * @return this
     * @since 3.5.0
     */
    public TableInfo addImportPackages(@NotNull String... pkgs) {
        return addImportPackages(Arrays.asList(pkgs));
    }

    public TableInfo addImportPackages(@NotNull List<String> pkgList) {
        importPackages.addAll(pkgList);
        return this;
    }

    /**
     * 转换filed实体为 xml mapper 中的 base column 字符串信息
     */
    public String getFieldNames() {
        if (StringUtils.isBlank(fieldNames)) {
            this.fieldNames = this.fields.stream().map(TableField::getColumnName).collect(Collectors.joining(", "));
        }
        return this.fieldNames;
    }

    /**
     * 导包处理
     *
     * @since 3.5.0
     */
    public void importPackage() {
        String superEntity = entity.getSuperClass();
        if (StringUtils.isNotBlank(superEntity)) {
            // 自定义父类
            this.importPackages.add(superEntity);
        } else {
            if (entity.isActiveRecord()) {
                // 无父类开启 AR 模式
                this.importPackages.add(Model.class.getCanonicalName());
            }
        }
        if (entity.isSerialVersionUID() || entity.isActiveRecord()) {
            this.importPackages.add(Serializable.class.getCanonicalName());
        }
        if (this.isConvert()) {
            this.importPackages.add(TableName.class.getCanonicalName());
        }
        IdType idType = entity.getIdType();
        if (null != idType && this.isHavePrimaryKey()) {
            // 指定需要 IdType 场景
            this.importPackages.add(IdType.class.getCanonicalName());
            this.importPackages.add(TableId.class.getCanonicalName());
        }
        this.fields.forEach(field -> {
            IColumnType columnType = field.getColumnType();
            if (null != columnType && null != columnType.getPkg()) {
                importPackages.add(columnType.getPkg());
            }
            if (field.isKeyFlag()) {
                // 主键
                if (field.isConvert() || field.isKeyIdentityFlag()) {
                    importPackages.add(TableId.class.getCanonicalName());
                }
                // 自增
                if (field.isKeyIdentityFlag()) {
                    importPackages.add(IdType.class.getCanonicalName());
                }
            } else if (field.isConvert()) {
                // 普通字段
                importPackages.add(com.baomidou.mybatisplus.annotation.TableField.class.getCanonicalName());
            }
            if (null != field.getFill()) {
                // 填充字段
                importPackages.add(com.baomidou.mybatisplus.annotation.TableField.class.getCanonicalName());
                //TODO 好像default的不用处理也行,这个做优化项目.
                importPackages.add(FieldFill.class.getCanonicalName());
            }
            if (field.isVersionField()) {
                this.importPackages.add(Version.class.getCanonicalName());
            }
            if (field.isLogicDeleteField()) {
                this.importPackages.add(TableLogic.class.getCanonicalName());
            }
        });
    }

    /**
     * 处理表信息(文件名与导包)
     *
     * @since 3.5.0
     */
    public void processTable() {
        String entityName = entity.getNameConvert().entityNameConvert(this);
        this.setEntityName(entity.getConverterFileName().convert(entityName));
        this.mapperName = strategyConfig.mapper().getConverterMapperFileName().convert(entityName);
        this.xmlName = strategyConfig.mapper().getConverterXmlFileName().convert(entityName);
        this.serviceName = strategyConfig.service().getConverterServiceFileName().convert(entityName);
        this.serviceImplName = strategyConfig.service().getConverterServiceImplFileName().convert(entityName);
        this.controllerName = strategyConfig.controller().getConverterFileName().convert(entityName);
        this.importPackage();
    }

    public TableInfo setComment(String comment) {
        //TODO 暂时挪动到这
        this.comment = this.globalConfig.isSwagger()
            && StringUtils.isNotBlank(comment) ? comment.replace("\"", "\\\"") : comment;
        return this;
    }

    public TableInfo setHavePrimaryKey(boolean havePrimaryKey) {
        this.havePrimaryKey = havePrimaryKey;
        return this;
    }

    @NotNull
    public Set<String> getImportPackages() {
        return importPackages;
    }

    public TableInfo setConvert(boolean convert) {
        this.convert = convert;
        return this;
    }

    @NotNull
    public List<TableField> getFields() {
        return fields;
    }

    @NotNull
    public List<TableField> getCommonFields() {
        return commonFields;
    }

    /**
     * 获取是否生成service接口
     * @deprecated {@link Service.Builder#disableService()}
     */
    @Deprecated
    public boolean isServiceInterface() {
        return globalConfig.isServiceInterface();
    }
}
