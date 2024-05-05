/*
 * Copyright (c) 2011-2024, baomidou (jobob@qq.com).
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
package com.baomidou.mybatisplus.generator.config.builder;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.core.handlers.AnnotationHandler;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.generator.IFill;
import com.baomidou.mybatisplus.generator.ITemplate;
import com.baomidou.mybatisplus.generator.config.ConstVal;
import com.baomidou.mybatisplus.generator.config.INameConvert;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.function.ConverterFileName;
import com.baomidou.mybatisplus.generator.util.ClassUtils;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 实体属性配置
 *
 * @author nieqiurong 2020/10/11.
 * @since 3.5.0
 */
public class Entity implements ITemplate {

    private final AnnotationHandler annotationHandler = new AnnotationHandler(){};

    private static final Logger LOGGER = LoggerFactory.getLogger(Entity.class);

    /**
     * Java模板默认路径
     *
     * @since 3.5.6
     */
    @Getter
    private String javaTemplate = ConstVal.TEMPLATE_ENTITY_JAVA;

    /**
     * Kotlin模板默认撸
     */
    @Getter
    private String kotlinTemplate = ConstVal.TEMPLATE_ENTITY_KT;

    private Entity() {
    }

    /**
     * 名称转换
     */
    private INameConvert nameConvert;

    /**
     * 自定义继承的Entity类全称，带包名
     */
    private String superClass;

    /**
     * 自定义基础的Entity类，公共字段
     */
    @Getter
    private final Set<String> superEntityColumns = new HashSet<>();

    /**
     * 自定义忽略字段
     * <a href="https://github.com/baomidou/generator/issues/46">...</a>
     */
    private final Set<String> ignoreColumns = new HashSet<>();

    /**
     * 实体是否生成 serialVersionUID
     */
    @Getter
    private boolean serialVersionUID = true;

    /**
     * 【实体】是否生成字段常量（默认 false）<br>
     * -----------------------------------<br>
     * public static final String ID = "test_id";
     */
    @Getter
    private boolean columnConstant;

    /**
     * 【实体】是否为链式模型（默认 false）
     *
     * @since 3.3.2
     */
    @Getter
    private boolean chain;

    /**
     * 【实体】是否为lombok模型（默认 false）<br>
     * <a href="https://projectlombok.org/">document</a>
     */
    @Getter
    private boolean lombok;

    /**
     * Boolean类型字段是否移除is前缀（默认 false）<br>
     * 比如 : 数据库字段名称 : 'is_xxx',类型为 : tinyint. 在映射实体的时候则会去掉is,在实体类中映射最终结果为 xxx
     */
    @Getter
    private boolean booleanColumnRemoveIsPrefix;

    /**
     * 是否生成实体时，生成字段注解（默认 false）
     */
    @Getter
    private boolean tableFieldAnnotationEnable;

    /**
     * 乐观锁字段名称(数据库字段)
     *
     * @since 3.5.0
     */
    private String versionColumnName;

    /**
     * 乐观锁属性名称(实体字段)
     *
     * @since 3.5.0
     */
    private String versionPropertyName;

    /**
     * 逻辑删除字段名称(数据库字段)
     *
     * @since 3.5.0
     */
    private String logicDeleteColumnName;

    /**
     * 逻辑删除属性名称(实体字段)
     *
     * @since 3.5.0
     */
    private String logicDeletePropertyName;

    /**
     * 表填充字段
     */
    private final List<IFill> tableFillList = new ArrayList<>();

    /**
     * 数据库表映射到实体的命名策略，默认下划线转驼峰命名
     */
    private NamingStrategy naming = NamingStrategy.underline_to_camel;

    /**
     * 数据库表字段映射到实体的命名策略
     * <p>未指定按照 naming 执行</p>
     */
    private NamingStrategy columnNaming = null;

    /**
     * 开启 ActiveRecord 模式（默认 false）
     *
     * @since 3.5.0
     */
    @Getter
    private boolean activeRecord;

    /**
     * 指定生成的主键的ID类型
     *
     * @since 3.5.0
     */
    private IdType idType;

    /**
     * 转换输出文件名称
     *
     * @since 3.5.0
     */
    private ConverterFileName converterFileName = (entityName -> entityName);

    /**
     * 是否覆盖已有文件（默认 false）
     *
     * @since 3.5.2
     */
    @Getter
    private boolean fileOverride;


    /**
     * 是否生成
     *
     * @since 3.5.6
     */
    @Getter
    private boolean generate = true;

    /**
     * <p>
     * 父类 Class 反射属性转换为公共字段
     * </p>
     *
     * @param clazz 实体父类 Class
     */
    public void convertSuperEntityColumns(Class<?> clazz) {
        List<Field> fields = TableInfoHelper.getAllFields(clazz, annotationHandler);
        this.superEntityColumns.addAll(fields.stream().map(field -> {
            TableId tableId = annotationHandler.getAnnotation(field, TableId.class);
            if (tableId != null && StringUtils.isNotBlank(tableId.value())) {
                return tableId.value();
            }
            TableField tableField = annotationHandler.getAnnotation(field, TableField.class);
            if (tableField != null && StringUtils.isNotBlank(tableField.value())) {
                return tableField.value();
            }
            if (null == columnNaming || columnNaming == NamingStrategy.no_change) {
                return field.getName();
            }
            return StringUtils.camelToUnderline(field.getName());
        }).collect(Collectors.toSet()));
    }

    @NotNull
    public NamingStrategy getColumnNaming() {
        // 未指定以 naming 策略为准
        return Optional.ofNullable(columnNaming).orElse(naming);
    }

    /**
     * 匹配父类字段(忽略大小写)
     *
     * @param fieldName 字段名
     * @return 是否匹配
     * @since 3.5.0
     */
    public boolean matchSuperEntityColumns(String fieldName) {
        // 公共字段判断忽略大小写【 部分数据库大小写不敏感 】
        return superEntityColumns.stream().anyMatch(e -> e.equalsIgnoreCase(fieldName));
    }

    /**
     * 匹配忽略字段(忽略大小写)
     *
     * @param fieldName 字段名
     * @return 是否匹配
     * @since 3.5.0
     */
    public boolean matchIgnoreColumns(String fieldName) {
        return ignoreColumns.stream().anyMatch(e -> e.equalsIgnoreCase(fieldName));
    }

    @NotNull
    public INameConvert getNameConvert() {
        return nameConvert;
    }

    @Nullable
    public String getSuperClass() {
        return superClass;
    }

    @Nullable
    public String getVersionColumnName() {
        return versionColumnName;
    }

    @Nullable
    public String getVersionPropertyName() {
        return versionPropertyName;
    }

    @Nullable
    public String getLogicDeleteColumnName() {
        return logicDeleteColumnName;
    }

    @Nullable
    public String getLogicDeletePropertyName() {
        return logicDeletePropertyName;
    }

    @NotNull
    public List<IFill> getTableFillList() {
        return tableFillList;
    }

    @NotNull
    public NamingStrategy getNaming() {
        return naming;
    }

    @Nullable
    public IdType getIdType() {
        return idType;
    }

    @NotNull
    public ConverterFileName getConverterFileName() {
        return converterFileName;
    }

    @Override
    @NotNull
    public Map<String, Object> renderData(@NotNull TableInfo tableInfo) {
        Map<String, Object> data = new HashMap<>();
        data.put("idType", idType == null ? null : idType.toString());
        data.put("logicDeleteFieldName", this.logicDeleteColumnName);
        data.put("versionFieldName", this.versionColumnName);
        data.put("activeRecord", this.activeRecord);
        data.put("entitySerialVersionUID", this.serialVersionUID);
        data.put("entityColumnConstant", this.columnConstant);
        data.put("entityBuilderModel", this.chain);
        data.put("chainModel", this.chain);
        data.put("entityLombokModel", this.lombok);
        data.put("entityBooleanColumnRemoveIsPrefix", this.booleanColumnRemoveIsPrefix);
        data.put("superEntityClass", ClassUtils.getSimpleName(this.superClass));
        return data;
    }

    public static class Builder extends BaseBuilder {

        private final Entity entity = new Entity();

        public Builder(StrategyConfig strategyConfig) {
            super(strategyConfig);
            this.entity.nameConvert = new INameConvert.DefaultNameConvert(strategyConfig);
        }

        /**
         * 名称转换实现
         *
         * @param nameConvert 名称转换实现
         * @return this
         */
        public Builder nameConvert(INameConvert nameConvert) {
            this.entity.nameConvert = nameConvert;
            return this;
        }

        /**
         * 自定义继承的Entity类全称
         *
         * @param clazz 类
         * @return this
         */
        public Builder superClass(@NotNull Class<?> clazz) {
            return superClass(clazz.getName());
        }

        /**
         * 自定义继承的Entity类全称，带包名
         *
         * @param superEntityClass 类全称
         * @return this
         */
        public Builder superClass(String superEntityClass) {
            this.entity.superClass = superEntityClass;
            return this;
        }

        /**
         * 禁用生成serialVersionUID
         *
         * @return this
         * @since 3.5.0
         */
        public Builder disableSerialVersionUID() {
            this.entity.serialVersionUID = false;
            return this;
        }

        /**
         * 开启生成字段常量
         *
         * @return this
         * @since 3.5.0
         */
        public Builder enableColumnConstant() {
            this.entity.columnConstant = true;
            return this;
        }

        /**
         * 开启链式模型
         *
         * @return this
         * @since 3.5.0
         */
        public Builder enableChainModel() {
            this.entity.chain = true;
            return this;
        }

        /**
         * 开启lombok模型
         *
         * @return this
         * @since 3.5.0
         */
        public Builder enableLombok() {
            this.entity.lombok = true;
            return this;
        }

        /**
         * 开启Boolean类型字段移除is前缀
         *
         * @return this
         * @since 3.5.0
         */
        public Builder enableRemoveIsPrefix() {
            this.entity.booleanColumnRemoveIsPrefix = true;
            return this;
        }

        /**
         * 开启生成实体时生成字段注解
         *
         * @return this
         * @since 3.5.0
         */
        public Builder enableTableFieldAnnotation() {
            this.entity.tableFieldAnnotationEnable = true;
            return this;
        }

        /**
         * 开启 ActiveRecord 模式
         *
         * @return this
         * @since 3.5.0
         */
        public Builder enableActiveRecord() {
            this.entity.activeRecord = true;
            return this;
        }

        /**
         * 设置乐观锁数据库表字段名称
         *
         * @param versionColumnName 乐观锁数据库字段名称
         * @return this
         */
        public Builder versionColumnName(String versionColumnName) {
            this.entity.versionColumnName = versionColumnName;
            return this;
        }

        /**
         * 设置乐观锁实体属性字段名称
         *
         * @param versionPropertyName 乐观锁实体属性字段名称
         * @return this
         */
        public Builder versionPropertyName(String versionPropertyName) {
            this.entity.versionPropertyName = versionPropertyName;
            return this;
        }

        /**
         * 逻辑删除数据库字段名称
         *
         * @param logicDeleteColumnName 逻辑删除字段名称
         * @return this
         */
        public Builder logicDeleteColumnName(String logicDeleteColumnName) {
            this.entity.logicDeleteColumnName = logicDeleteColumnName;
            return this;
        }

        /**
         * 逻辑删除实体属性名称
         *
         * @param logicDeletePropertyName 逻辑删除实体属性名称
         * @return this
         */
        public Builder logicDeletePropertyName(String logicDeletePropertyName) {
            this.entity.logicDeletePropertyName = logicDeletePropertyName;
            return this;
        }

        /**
         * 数据库表映射到实体的命名策略
         *
         * @param namingStrategy 数据库表映射到实体的命名策略
         * @return this
         */
        public Builder naming(NamingStrategy namingStrategy) {
            this.entity.naming = namingStrategy;
            return this;
        }

        /**
         * 数据库表字段映射到实体的命名策略
         *
         * @param namingStrategy 数据库表字段映射到实体的命名策略
         * @return this
         */
        public Builder columnNaming(NamingStrategy namingStrategy) {
            this.entity.columnNaming = namingStrategy;
            return this;
        }

        /**
         * 添加父类公共字段
         *
         * @param superEntityColumns 父类字段(数据库字段列名)
         * @return this
         * @since 3.5.0
         */
        public Builder addSuperEntityColumns(@NotNull String... superEntityColumns) {
            return addSuperEntityColumns(Arrays.asList(superEntityColumns));
        }

        public Builder addSuperEntityColumns(@NotNull List<String> superEntityColumnList) {
            this.entity.superEntityColumns.addAll(superEntityColumnList);
            return this;
        }

        /**
         * 添加忽略字段
         *
         * @param ignoreColumns 需要忽略的字段(数据库字段列名)
         * @return this
         * @since 3.5.0
         */
        public Builder addIgnoreColumns(@NotNull String... ignoreColumns) {
            return addIgnoreColumns(Arrays.asList(ignoreColumns));
        }

        public Builder addIgnoreColumns(@NotNull List<String> ignoreColumnList) {
            this.entity.ignoreColumns.addAll(ignoreColumnList);
            return this;
        }

        /**
         * 添加表字段填充
         *
         * @param tableFills 填充字段
         * @return this
         * @since 3.5.0
         */
        public Builder addTableFills(@NotNull IFill... tableFills) {
            return addTableFills(Arrays.asList(tableFills));
        }

        /**
         * 添加表字段填充
         *
         * @param tableFillList 填充字段集合
         * @return this
         * @since 3.5.0
         */
        public Builder addTableFills(@NotNull List<IFill> tableFillList) {
            this.entity.tableFillList.addAll(tableFillList);
            return this;
        }

        /**
         * 指定生成的主键的ID类型
         *
         * @param idType ID类型
         * @return this
         * @since 3.5.0
         */
        public Builder idType(IdType idType) {
            this.entity.idType = idType;
            return this;
        }

        /**
         * 转换输出文件名称
         *
         * @param converter 　转换处理
         * @return this
         * @since 3.5.0
         */
        public Builder convertFileName(@NotNull ConverterFileName converter) {
            this.entity.converterFileName = converter;
            return this;
        }

        /**
         * 格式化文件名称
         *
         * @param format 　格式
         * @return this
         * @since 3.5.0
         */
        public Builder formatFileName(String format) {
            return convertFileName((entityName) -> String.format(format, entityName));
        }

        /**
         * 覆盖已有文件（该方法后续会删除，替代方法为enableFileOverride方法）
         *
         * @see #enableFileOverride()
         */
        @Deprecated
        public Builder fileOverride() {
            LOGGER.warn("fileOverride方法后续会删除，替代方法为enableFileOverride方法");
            this.entity.fileOverride = true;
            return this;
        }

        /**
         * 覆盖已有文件
         *
         * @since 3.5.3
         */
        public Builder enableFileOverride() {
            this.entity.fileOverride = true;
            return this;
        }

        /**
         * 指定模板路径
         *
         * @param template 模板路径
         * @return this
         * @since 3.5.6
         */
        public Builder javaTemplate(String template) {
            this.entity.javaTemplate = template;
            return this;
        }

        /**
         * 指定模板路径
         *
         * @param template 模板路径
         * @return this
         * @since 3.5.6
         */
        public Builder kotlinTemplatePath(String template) {
            this.entity.kotlinTemplate = template;
            return this;
        }

        /**
         * 禁用实体生成
         *
         * @return this
         * @since 3.5.6
         */
        public Builder disable() {
            this.entity.generate = false;
            return this;
        }

        public Entity get() {
            String superClass = this.entity.superClass;
            if (StringUtils.isNotBlank(superClass)) {
                tryLoadClass(superClass).ifPresent(this.entity::convertSuperEntityColumns);
            } else {
                if (!this.entity.superEntityColumns.isEmpty()) {
                    LOGGER.warn("Forgot to set entity supper class ?");
                }
            }
            return this.entity;
        }

        private Optional<Class<?>> tryLoadClass(String className) {
            try {
                return Optional.of(ClassUtils.toClassConfident(className));
            } catch (Exception e) {
                //当父类实体存在类加载器的时候,识别父类实体字段，不存在的情况就只有通过指定superEntityColumns属性了。
            }
            return Optional.empty();
        }
    }
}
