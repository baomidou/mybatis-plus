/*
 * Copyright (c) 2011-2025, baomidou (jobob@qq.com).
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

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.generator.config.ConstVal;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.IKeyWordsHandler;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.config.builder.Entity;
import com.baomidou.mybatisplus.generator.config.rules.IColumnType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.fill.Column;
import com.baomidou.mybatisplus.generator.fill.Property;
import com.baomidou.mybatisplus.generator.jdbc.DatabaseMetaDataWrapper;
import com.baomidou.mybatisplus.generator.model.AnnotationAttributes;
import org.apache.ibatis.type.JdbcType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 表字段信息
 *
 * @author YangHu
 * @since 2016-12-03
 */
public class TableField {

    /**
     * 是否做注解转换
     */
    private boolean convert;

    /**
     * 是否主键
     */
    private boolean keyFlag;

    /**
     * 主键是否为自增类型
     */
    private boolean keyIdentityFlag;

    /**
     * 字段名称
     */
    private String name;

    /**
     * 字段类型（已弃用，使用 {@link #columnType} 代替）
     */
    @Deprecated
    private String type;

    /**
     * 属性名称
     */
    private String propertyName;

    /**
     * 字段类型
     */
    private IColumnType columnType;

    /**
     * 字段注释
     */
    private String comment;

    /**
     * 填充
     */
    private String fill;

    /**
     * 是否关键字
     *
     * @since 3.3.2
     */
    private boolean keyWords;

    /**
     * 数据库字段（关键字含转义符号）
     *
     * @since 3.3.2
     */
    private String columnName;

    /**
     * 自定义查询字段列表
     */
    private Map<String, Object> customMap;

    /**
     * 字段元数据信息
     *
     * @since 3.5.0
     */
    private MetaInfo metaInfo;

    /**
     * 实体属性配置
     */
    private final Entity entity;

    /**
     * 数据库配置
     */
    private final DataSourceConfig dataSourceConfig;

    /**
     * 全局配置
     */
    private final GlobalConfig globalConfig;

    /**
     * 字段注解
     *
     * @since 3.5.10
     */
    private final List<AnnotationAttributes> annotationAttributesList = new ArrayList<>();

    /**
     * 字段注解处理
     *
     * @since 3.5.10.2
     */
    private Function<List<? extends AnnotationAttributes>, List<AnnotationAttributes>> annotationAttributesFunction;

    /**
     * 构造方法
     *
     * @param configBuilder 配置构建
     * @param name          数据库字段名称
     * @since 3.5.0
     */
    public TableField(@NotNull ConfigBuilder configBuilder, @NotNull String name) {
        this.name = name;
        this.columnName = name;
        this.entity = configBuilder.getStrategyConfig().entity();
        this.dataSourceConfig = configBuilder.getDataSourceConfig();
        this.globalConfig = configBuilder.getGlobalConfig();
    }

    /**
     * 设置属性名称
     *
     * @param propertyName 属性名
     * @param columnType   字段类型
     * @return this
     * @since 3.5.0
     */
    public TableField setPropertyName(@NotNull String propertyName, @NotNull IColumnType columnType) {
        this.columnType = columnType;
        if (entity.isBooleanColumnRemoveIsPrefix()
            && "boolean".equalsIgnoreCase(this.getPropertyType()) && propertyName.startsWith("is")) {
            this.convert = true;
            this.propertyName = StringUtils.removePrefixAfterPrefixToLower(propertyName, 2);
            return this;
        }
        // 下划线转驼峰策略
        if (NamingStrategy.underline_to_camel.equals(this.entity.getColumnNaming())) {
            this.convert = !propertyName.equalsIgnoreCase(NamingStrategy.underlineToCamel(this.columnName));
        }
        // 原样输出策略
        if (NamingStrategy.no_change.equals(this.entity.getColumnNaming())) {
            this.convert = !propertyName.equalsIgnoreCase(this.columnName);
        }
        if (entity.isTableFieldAnnotationEnable()) {
            this.convert = true;
        } else {
            if (this.keyFlag) {
                this.convert = !ConstVal.DEFAULT_ID_NAME.equals(propertyName);
            }
        }
        this.propertyName = propertyName;
        return this;
    }

    public String getPropertyType() {
        if (null != columnType) {
            return columnType.getType();
        }
        return null;
    }

    /**
     * 按 JavaBean 规则来生成 get 和 set 方法后面的属性名称
     * 需要处理一下特殊情况：
     * <p>
     * 1、如果只有一位，转换为大写形式
     * 2、如果多于 1 位，只有在第二位是小写的情况下，才会把第一位转为小写
     * <p>
     * 我们并不建议在数据库对应的对象中使用基本类型，因此这里不会考虑基本类型的情况
     */
    public String getCapitalName() {
        if (propertyName.length() == 1) {
            return propertyName.toUpperCase();
        }
        if (Character.isLowerCase(propertyName.charAt(1))) {
            return Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
        }
        return propertyName;
    }

    /**
     * 获取注解字段名称
     *
     * @return 字段
     * @since 3.3.2
     */
    public String getAnnotationColumnName() {
        if (keyWords) {
            if (columnName.startsWith("\"")) {
                return String.format("\\\"%s\\\"", name);
            }
        }
        return columnName;
    }

    /**
     * 是否为乐观锁字段
     *
     * @return 是否为乐观锁字段
     * @since 3.5.0
     */
    public boolean isVersionField() {
        String propertyName = entity.getVersionPropertyName();
        String columnName = entity.getVersionColumnName();
        return StringUtils.isNotBlank(propertyName) && this.propertyName.equals(propertyName)
            || StringUtils.isNotBlank(columnName) && this.name.equalsIgnoreCase(columnName);
    }

    /**
     * 是否为逻辑删除字段
     *
     * @return 是否为逻辑删除字段
     * @since 3.5.0
     */
    public boolean isLogicDeleteField() {
        String propertyName = entity.getLogicDeletePropertyName();
        String columnName = entity.getLogicDeleteColumnName();
        return StringUtils.isNotBlank(propertyName) && this.propertyName.equals(propertyName)
            || StringUtils.isNotBlank(columnName) && this.name.equalsIgnoreCase(columnName);
    }

    /**
     * 设置主键
     *
     * @param autoIncrement 自增标识
     * @return this
     * @since 3.5.0
     */
    public TableField primaryKey(boolean autoIncrement) {
        this.keyFlag = true;
        this.keyIdentityFlag = autoIncrement;
        return this;
    }

    /**
     * @param type 类型
     * @return this
     */
    public TableField setType(String type) {
        this.type = type;
        return this;
    }

    public TableField setComment(String comment) {
        //TODO 待重构此处
        this.comment = (this.globalConfig.isSwagger() || this.globalConfig.isSpringdoc())
            && StringUtils.isNotBlank(comment) ? comment.replace("\"", "\\\"") : comment;
        return this;
    }

    public TableField setColumnName(String columnName) {
        this.columnName = columnName;
        IKeyWordsHandler keyWordsHandler = dataSourceConfig.getKeyWordsHandler();
        if (keyWordsHandler != null && keyWordsHandler.isKeyWords(columnName)) {
            this.keyWords = true;
            this.columnName = keyWordsHandler.formatColumn(columnName);
        }
        return this;
    }

    public TableField setCustomMap(Map<String, Object> customMap) {
        this.customMap = customMap;
        return this;
    }

    public boolean isConvert() {
        return convert;
    }

    public boolean isKeyFlag() {
        return keyFlag;
    }

    public boolean isKeyIdentityFlag() {
        return keyIdentityFlag;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public IColumnType getColumnType() {
        return columnType;
    }

    public String getComment() {
        return comment;
    }

    public String getFill() {
        if (StringUtils.isBlank(fill)) {
            entity.getTableFillList().stream()
                //忽略大写字段问题
                .filter(tf -> tf instanceof Column && tf.getName().equalsIgnoreCase(name)
                    || tf instanceof Property && tf.getName().equals(propertyName))
                .findFirst().ifPresent(tf -> this.fill = tf.getFieldFill().name());
        }
        return fill;
    }

    public boolean isKeyWords() {
        return keyWords;
    }

    public String getColumnName() {
        return columnName;
    }

    public Map<String, Object> getCustomMap() {
        return customMap;
    }

    public MetaInfo getMetaInfo() {
        return metaInfo;
    }

    public void setMetaInfo(MetaInfo metaInfo) {
        this.metaInfo = metaInfo;
    }

    /**
     * 获取实体配置信息
     *
     * @return 实体配置信息
     * @since 3.5.10
     */
    public Entity getEntity() {
        return this.entity;
    }

    /**
     * 添加字段注解属性
     *
     * @param annotationAttributesList 注解属性集合
     * @since 3.5.10
     */
    public void addAnnotationAttributesList(@NotNull List<AnnotationAttributes> annotationAttributesList) {
        this.addAnnotationAttributesList(annotationAttributesList, null);
    }

    /**
     * @param annotationAttributesList 注解属性集合
     * @since 3.5.10.2
     */
    public void addAnnotationAttributesList(@NotNull List<AnnotationAttributes> annotationAttributesList, Function<List<? extends AnnotationAttributes>, List<AnnotationAttributes>> annotationAttributesFunction) {
        this.annotationAttributesList.addAll(annotationAttributesList);
        this.annotationAttributesFunction = annotationAttributesFunction;
    }

    /**
     * 添加字段注解属性
     *
     * @param annotationAttributes 注解属性
     * @since 3.5.10
     */
    public void addAnnotationAttributesList(@NotNull AnnotationAttributes annotationAttributes) {
        this.annotationAttributesList.add(annotationAttributes);
    }

    /**
     * 获取字段注解属性(默认按{@link AnnotationAttributes#getDisplayName()}长度进行升序)
     *
     * @return 字段注解属性
     * @since 3.5.10
     */
    public List<AnnotationAttributes> getAnnotationAttributesList() {
        return annotationAttributesFunction != null ? annotationAttributesFunction.apply(this.annotationAttributesList) : this.annotationAttributesList.stream()
            .sorted(Comparator.comparingInt(s -> s.getDisplayName().length()))
            .collect(Collectors.toList());
    }

    /**
     * 元数据信息
     *
     * @author nieqiurong 2021/2/8
     * @since 3.5.0
     */
    public static class MetaInfo {

        /**
         * 表名称
         */
        private String tableName;

        /**
         * 字段名称
         */
        private String columnName;

        /**
         * 字段长度
         */
        private int length;

        /**
         * 是否非空
         */
        private boolean nullable;

        /**
         * 字段注释
         */
        private String remarks;

        /**
         * 字段默认值
         */
        private String defaultValue;

        /**
         * 字段精度
         */
        private int scale;

        /**
         * JDBC类型
         */
        private JdbcType jdbcType;

        /**
         * 类型名称(可用做额外判断处理,例如在pg下,json,uuid,jsonb,tsquery这种都认为是OHTER 1111)
         *
         * @since 3.5.3
         */
        private String typeName;

        /**
         * 是否为生成列
         *
         * @since 3.5.8
         */
        private boolean generatedColumn;

        public MetaInfo(DatabaseMetaDataWrapper.Column column, TableInfo tableInfo) {
            if (column != null) {
                this.tableName = tableInfo.getName();
                this.columnName = column.getName();
                this.length = column.getLength();
                this.nullable = column.isNullable();
                this.remarks = column.getRemarks();
                this.defaultValue = column.getDefaultValue();
                this.scale = column.getScale();
                this.jdbcType = column.getJdbcType();
                this.typeName = column.getTypeName();
                this.generatedColumn = column.isGeneratedColumn();
            }
        }

        public String getTableName() {
            return tableName;
        }

        public String getColumnName() {
            return columnName;
        }

        public int getLength() {
            return length;
        }

        public boolean isNullable() {
            return nullable;
        }

        public String getRemarks() {
            return remarks;
        }

        public String getDefaultValue() {
            return defaultValue;
        }

        public int getScale() {
            return scale;
        }

        public JdbcType getJdbcType() {
            return jdbcType;
        }

        public String getTypeName() {
            return typeName;
        }

        @Override
        public String toString() {
            return "MetaInfo{" +
                "tableName='" + tableName + '\'' +
                ", columnName='" + columnName + '\'' +
                ", length=" + length +
                ", nullable=" + nullable +
                ", remarks='" + remarks + '\'' +
                ", defaultValue='" + defaultValue + '\'' +
                ", scale=" + scale +
                ", jdbcType=" + jdbcType +
                ", typeName='" + typeName + '\'' +
                ", generatedColumn=" + generatedColumn +
                '}';
        }
    }
}
