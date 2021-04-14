/*
 * Copyright (c) 2011-2021, baomidou (jobob@qq.com).
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
package com.baomidou.mybatisplus.core.metadata;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlScriptUtils;
import lombok.*;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.reflection.Reflector;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.*;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * 数据库表字段反射信息
 *
 * @author hubin sjy willenfoo tantan
 * @since 2016-09-09
 */
@Getter
@ToString
@EqualsAndHashCode
@SuppressWarnings("serial")
public class TableFieldInfo implements Constants {

    /**
     * 属性
     *
     * @since 3.3.1
     */
    private final Field field;
    /**
     * 字段名
     */
    private final String column;
    /**
     * 属性名
     */
    private final String property;
    /**
     * 属性表达式#{property}, 可以指定jdbcType, typeHandler等
     */
    private final String el;
    /**
     * jdbcType, typeHandler等部分
     */
    private final String mapping;
    /**
     * 属性类型
     */
    private final Class<?> propertyType;
    /**
     * 是否是基本数据类型
     *
     * @since 3.4.0 @2020-6-19
     */
    private final boolean isPrimitive;
    /**
     * 属性是否是 CharSequence 类型
     */
    private final boolean isCharSequence;
    /**
     * 字段验证策略之 insert
     * Refer to {@link TableField#insertStrategy()}
     *
     * @since added v_3.1.2 @2019-5-7
     */
    private final FieldStrategy insertStrategy;
    /**
     * 字段验证策略之 update
     * Refer to {@link TableField#updateStrategy()}
     *
     * @since added v_3.1.2 @2019-5-7
     */
    private final FieldStrategy updateStrategy;
    /**
     * 字段验证策略之 where
     * Refer to {@link TableField#whereStrategy()}
     *
     * @since added v_3.1.2 @2019-5-7
     */
    private final FieldStrategy whereStrategy;
    /**
     * 是否是乐观锁字段
     */
    private final boolean version;
    /**
     * 是否进行 select 查询
     * <p>大字段可设置为 false 不加入 select 查询范围</p>
     */
    private boolean select = true;
    /**
     * 是否是逻辑删除字段
     */
    private boolean logicDelete = false;
    /**
     * 逻辑删除值
     */
    private String logicDeleteValue;
    /**
     * 逻辑未删除值
     */
    private String logicNotDeleteValue;
    /**
     * 字段 update set 部分注入
     */
    private String update;
    /**
     * where 字段比较条件
     */
    private String condition = SqlCondition.EQUAL;
    /**
     * 字段填充策略
     */
    private FieldFill fieldFill = FieldFill.DEFAULT;
    /**
     * 表字段是否启用了插入填充
     *
     * @since 3.3.0
     */
    private boolean withInsertFill;
    /**
     * 表字段是否启用了更新填充
     *
     * @since 3.3.0
     */
    private boolean withUpdateFill;
    /**
     * 缓存 sql select
     */
    @Setter(AccessLevel.NONE)
    private String sqlSelect;
    /**
     * JDBC类型
     *
     * @since 3.1.2
     */
    private JdbcType jdbcType;
    /**
     * 类型处理器
     *
     * @since 3.1.2
     */
    private Class<? extends TypeHandler<?>> typeHandler;

    /**
     * 全新的 存在 TableField 注解时使用的构造函数
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public TableFieldInfo(GlobalConfig.DbConfig dbConfig, TableInfo tableInfo, Field field, TableField tableField,
                          Reflector reflector, boolean existTableLogic) {
        field.setAccessible(true);
        this.field = field;
        this.version = field.getAnnotation(Version.class) != null;
        this.property = field.getName();
        this.propertyType = reflector.getGetterType(this.property);
        this.isPrimitive = this.propertyType.isPrimitive();
        this.isCharSequence = StringUtils.isCharSequence(this.propertyType);
        this.fieldFill = tableField.fill();
        this.withInsertFill = this.fieldFill == FieldFill.INSERT || this.fieldFill == FieldFill.INSERT_UPDATE;
        this.withUpdateFill = this.fieldFill == FieldFill.UPDATE || this.fieldFill == FieldFill.INSERT_UPDATE;
        this.update = tableField.update();
        JdbcType jdbcType = tableField.jdbcType();
        final Class<? extends TypeHandler> typeHandler = tableField.typeHandler();
        final String numericScale = tableField.numericScale();
        String el = this.property;
        if (JdbcType.UNDEFINED != jdbcType) {
            this.jdbcType = jdbcType;
            el += (COMMA + SqlScriptUtils.mappingJdbcType(jdbcType));
        }
        if (UnknownTypeHandler.class != typeHandler) {
            this.typeHandler = (Class<? extends TypeHandler<?>>) typeHandler;
            if (tableField.javaType()) {
                String javaType = null;
                TypeAliasRegistry registry = tableInfo.getConfiguration().getTypeAliasRegistry();
                Map<String, Class<?>> typeAliases = registry.getTypeAliases();
                for (Map.Entry<String, Class<?>> entry : typeAliases.entrySet()) {
                    if (entry.getValue().equals(propertyType)) {
                        javaType = entry.getKey();
                        break;
                    }
                }
                if (javaType == null) {
                    javaType = propertyType.getName();
                    registry.registerAlias(javaType, propertyType);
                }
                el += (COMMA + "javaType=" + javaType);
            }
            el += (COMMA + SqlScriptUtils.mappingTypeHandler(this.typeHandler));
        }
        if (StringUtils.isNotBlank(numericScale)) {
            el += (COMMA + SqlScriptUtils.mappingNumericScale(Integer.valueOf(numericScale)));
        }
        this.el = el;
        int index = el.indexOf(COMMA);
        this.mapping = index > 0 ? el.substring(++index) : null;
        this.initLogicDelete(dbConfig, field, existTableLogic);

        String column = tableField.value();
        if (StringUtils.isBlank(column)) {
            column = this.property;
            if (tableInfo.isUnderCamel()) {
                /* 开启字段下划线申明 */
                column = StringUtils.camelToUnderline(column);
            }
            if (dbConfig.isCapitalMode()) {
                /* 开启字段全大写申明 */
                column = column.toUpperCase();
            }
        }
        String columnFormat = dbConfig.getColumnFormat();
        if (StringUtils.isNotBlank(columnFormat) && tableField.keepGlobalFormat()) {
            column = String.format(columnFormat, column);
        }

        this.column = column;
        this.sqlSelect = column;
        if (tableInfo.getResultMap() == null && !tableInfo.isAutoInitResultMap() &&
            TableInfoHelper.checkRelated(tableInfo.isUnderCamel(), this.property, this.column)) {
            /* 未设置 resultMap 也未开启自动构建 resultMap, 字段规则又不符合 mybatis 的自动封装规则 */
            String propertyFormat = dbConfig.getPropertyFormat();
            String asProperty = this.property;
            if (StringUtils.isNotBlank(propertyFormat)) {
                asProperty = String.format(propertyFormat, this.property);
            }
            this.sqlSelect += (AS + asProperty);
        }

        this.insertStrategy = this.chooseFieldStrategy(tableField.insertStrategy(), dbConfig.getInsertStrategy());
        this.updateStrategy = this.chooseFieldStrategy(tableField.updateStrategy(), dbConfig.getUpdateStrategy());
        this.whereStrategy = this.chooseFieldStrategy(tableField.whereStrategy(), dbConfig.getSelectStrategy());

        if (StringUtils.isNotBlank(tableField.condition())) {
            // 细粒度条件控制
            this.condition = tableField.condition();
        }

        // 字段是否注入查询
        this.select = tableField.select();
    }

    /**
     * 优先使用单个字段注解，否则使用全局配置
     */
    private FieldStrategy chooseFieldStrategy(FieldStrategy fromAnnotation, FieldStrategy fromDbConfig) {
        return fromAnnotation == FieldStrategy.DEFAULT ? fromDbConfig : fromAnnotation;
    }

    /**
     * 不存在 TableField 注解时, 使用的构造函数
     */
    public TableFieldInfo(GlobalConfig.DbConfig dbConfig, TableInfo tableInfo, Field field, Reflector reflector,
                          boolean existTableLogic) {
        field.setAccessible(true);
        this.field = field;
        this.version = field.getAnnotation(Version.class) != null;
        this.property = field.getName();
        this.propertyType = reflector.getGetterType(this.property);
        this.isPrimitive = this.propertyType.isPrimitive();
        this.isCharSequence = StringUtils.isCharSequence(this.propertyType);
        this.el = this.property;
        this.mapping = null;
        this.insertStrategy = dbConfig.getInsertStrategy();
        this.updateStrategy = dbConfig.getUpdateStrategy();
        this.whereStrategy = dbConfig.getSelectStrategy();
        this.initLogicDelete(dbConfig, field, existTableLogic);

        String column = this.property;
        if (tableInfo.isUnderCamel()) {
            /* 开启字段下划线申明 */
            column = StringUtils.camelToUnderline(column);
        }
        if (dbConfig.isCapitalMode()) {
            /* 开启字段全大写申明 */
            column = column.toUpperCase();
        }

        String columnFormat = dbConfig.getColumnFormat();
        if (StringUtils.isNotBlank(columnFormat)) {
            column = String.format(columnFormat, column);
        }

        this.column = column;
        this.sqlSelect = column;
        if (tableInfo.getResultMap() == null && !tableInfo.isAutoInitResultMap() &&
            TableInfoHelper.checkRelated(tableInfo.isUnderCamel(), this.property, this.column)) {
            /* 未设置 resultMap 也未开启自动构建 resultMap, 字段规则又不符合 mybatis 的自动封装规则 */
            String propertyFormat = dbConfig.getPropertyFormat();
            String asProperty = this.property;
            if (StringUtils.isNotBlank(propertyFormat)) {
                asProperty = String.format(propertyFormat, this.property);
            }
            this.sqlSelect += (AS + asProperty);
        }
    }

    /**
     * 逻辑删除初始化
     *
     * @param dbConfig 数据库全局配置
     * @param field    字段属性对象
     */
    private void initLogicDelete(GlobalConfig.DbConfig dbConfig, Field field, boolean existTableLogic) {
        /* 获取注解属性，逻辑处理字段 */
        TableLogic tableLogic = field.getAnnotation(TableLogic.class);
        if (null != tableLogic) {
            if (StringUtils.isNotBlank(tableLogic.value())) {
                this.logicNotDeleteValue = tableLogic.value();
            } else {
                this.logicNotDeleteValue = dbConfig.getLogicNotDeleteValue();
            }
            if (StringUtils.isNotBlank(tableLogic.delval())) {
                this.logicDeleteValue = tableLogic.delval();
            } else {
                this.logicDeleteValue = dbConfig.getLogicDeleteValue();
            }
            this.logicDelete = true;
        } else if (!existTableLogic) {
            String deleteField = dbConfig.getLogicDeleteField();
            if (StringUtils.isNotBlank(deleteField) && this.property.equals(deleteField)) {
                this.logicNotDeleteValue = dbConfig.getLogicNotDeleteValue();
                this.logicDeleteValue = dbConfig.getLogicDeleteValue();
                this.logicDelete = true;
            }
        }
    }

    /**
     * 获取 insert 时候插入值 sql 脚本片段
     * <p>insert into table (字段) values (值)</p>
     * <p>位于 "值" 部位</p>
     *
     * <li> 不生成 if 标签 </li>
     *
     * @return sql 脚本片段
     */
    public String getInsertSqlProperty(final String prefix) {
        final String newPrefix = prefix == null ? EMPTY : prefix;
        return SqlScriptUtils.safeParam(newPrefix + el) + COMMA;
    }

    /**
     * 获取 insert 时候插入值 sql 脚本片段
     * <p>insert into table (字段) values (值)</p>
     * <p>位于 "值" 部位</p>
     *
     * <li> 根据规则会生成 if 标签 </li>
     *
     * @return sql 脚本片段
     */
    public String getInsertSqlPropertyMaybeIf(final String prefix) {
        final String newPrefix = prefix == null ? EMPTY : prefix;
        String sqlScript = getInsertSqlProperty(newPrefix);
        if (withInsertFill) {
            return sqlScript;
        }
        return convertIf(sqlScript, newPrefix + property, insertStrategy);
    }

    /**
     * 获取 insert 时候字段 sql 脚本片段
     * <p>insert into table (字段) values (值)</p>
     * <p>位于 "字段" 部位</p>
     *
     * <li> 不生成 if 标签 </li>
     *
     * @return sql 脚本片段
     */
    public String getInsertSqlColumn() {
        return column + COMMA;
    }

    /**
     * 获取 insert 时候字段 sql 脚本片段
     * <p>insert into table (字段) values (值)</p>
     * <p>位于 "字段" 部位</p>
     *
     * <li> 根据规则会生成 if 标签 </li>
     *
     * @return sql 脚本片段
     */
    public String getInsertSqlColumnMaybeIf(final String prefix) {
        final String newPrefix = prefix == null ? EMPTY : prefix;
        final String sqlScript = getInsertSqlColumn();
        if (withInsertFill) {
            return sqlScript;
        }
        return convertIf(sqlScript, newPrefix + property, insertStrategy);
    }

    /**
     * 获取 set sql 片段
     *
     * @param prefix 前缀
     * @return sql 脚本片段
     */
    public String getSqlSet(final String prefix) {
        return getSqlSet(false, prefix);
    }

    /**
     * 获取 set sql 片段
     *
     * @param ignoreIf 忽略 IF 包裹
     * @param prefix   前缀
     * @return sql 脚本片段
     */
    public String getSqlSet(final boolean ignoreIf, final String prefix) {
        final String newPrefix = prefix == null ? EMPTY : prefix;
        // 默认: column=
        String sqlSet = column + EQUALS;
        if (StringUtils.isNotBlank(update)) {
            sqlSet += String.format(update, column);
        } else {
            sqlSet += SqlScriptUtils.safeParam(newPrefix + el);
        }
        sqlSet += COMMA;
        if (ignoreIf) {
            return sqlSet;
        }
        if (withUpdateFill) {
            // 不进行 if 包裹
            return sqlSet;
        }
        return convertIf(sqlSet, convertIfProperty(newPrefix, property), updateStrategy);
    }

    private String convertIfProperty(String prefix, String property) {
        return StringUtils.isNotBlank(prefix) ? prefix.substring(0, prefix.length() - 1) + "['" + property + "']" : property;
    }

    /**
     * 获取 查询的 sql 片段
     *
     * @param prefix 前缀
     * @return sql 脚本片段
     */
    public String getSqlWhere(final String prefix) {
        final String newPrefix = prefix == null ? EMPTY : prefix;
        // 默认:  AND column=#{prefix + el}
        String sqlScript = " AND " + String.format(condition, column, newPrefix + el);
        // 查询的时候只判非空
        return convertIf(sqlScript, convertIfProperty(newPrefix, property), whereStrategy);
    }

    /**
     * 获取 ResultMapping
     *
     * @param configuration MybatisConfiguration
     * @return ResultMapping
     */
    ResultMapping getResultMapping(final Configuration configuration) {
        ResultMapping.Builder builder = new ResultMapping.Builder(configuration, property,
            StringUtils.getTargetColumn(column), propertyType);
        TypeHandlerRegistry registry = configuration.getTypeHandlerRegistry();
        if (jdbcType != null && jdbcType != JdbcType.UNDEFINED) {
            builder.jdbcType(jdbcType);
        }
        if (typeHandler != null && typeHandler != UnknownTypeHandler.class) {
            TypeHandler<?> typeHandler = registry.getMappingTypeHandler(this.typeHandler);
            if (typeHandler == null) {
                typeHandler = registry.getInstance(propertyType, this.typeHandler);
                // todo 这会有影响 registry.register(typeHandler);
            }
            builder.typeHandler(typeHandler);
        }
        return builder.build();
    }

    public String getVersionOli(final String alias, final String prefix) {
        final String oli = " AND " + column + EQUALS + SqlScriptUtils.safeParam(MP_OPTLOCK_VERSION_ORIGINAL);
        final String ognlStr = convertIfProperty(prefix, property);
        if (isCharSequence) {
            return SqlScriptUtils.convertIf(oli, String.format("%s != null and %s != null and %s != ''", alias, ognlStr, ognlStr), false);
        } else {
            return SqlScriptUtils.convertIf(oli, String.format("%s != null and %s != null", alias, ognlStr), false);
        }
    }

    /**
     * 转换成 if 标签的脚本片段
     *
     * @param sqlScript     sql 脚本片段
     * @param property      字段名
     * @param fieldStrategy 验证策略
     * @return if 脚本片段
     */
    private String convertIf(final String sqlScript, final String property, final FieldStrategy fieldStrategy) {
        if (fieldStrategy == FieldStrategy.NEVER) {
            return null;
        }
        if (isPrimitive || fieldStrategy == FieldStrategy.IGNORED) {
            return sqlScript;
        }
        if (fieldStrategy == FieldStrategy.NOT_EMPTY && isCharSequence) {
            return SqlScriptUtils.convertIf(sqlScript, String.format("%s != null and %s != ''", property, property),
                false);
        }
        return SqlScriptUtils.convertIf(sqlScript, String.format("%s != null", property), false);
    }
}
