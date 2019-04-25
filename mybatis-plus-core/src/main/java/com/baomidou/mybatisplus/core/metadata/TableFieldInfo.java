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
package com.baomidou.mybatisplus.core.metadata;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlScriptUtils;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.lang.reflect.Field;

/**
 * 数据库表字段反射信息
 *
 * @author hubin sjy willenfoo tantan
 * @since 2016-09-09
 */
@Getter
@ToString
@EqualsAndHashCode
public class TableFieldInfo implements Constants {

    /**
     * 是否有存在字段名与属性名关联
     * <p>true: 表示要进行 as</p>
     */
    private final boolean related;
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
     * 属性类型
     */
    private final Class<?> propertyType;
    /**
     * 属性是否是 CharSequence 类型
     */
    private final boolean isCharSequence;
    /**
     * 字段策略【 默认，自判断 null 】
     */
    private final FieldStrategy fieldStrategy;
    /**
     * 是否进行 select 查询
     * <p>大字段可设置为 false 不加入 select 查询范围</p>
     */
    private boolean select = true;
    /**
     * 是否是乐观锁字段
     */
    private boolean version;
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
     * 缓存 sql select
     */
    @Getter(AccessLevel.NONE)
    private String sqlSelect;

    /**
     * 存在 TableField 注解时, 使用的构造函数
     */
    public TableFieldInfo(GlobalConfig.DbConfig dbConfig, TableInfo tableInfo, Field field,
                          String column, String el, TableField tableField) {
        this.version = field.getAnnotation(Version.class) != null;
        this.property = field.getName();
        this.propertyType = field.getType();
        this.isCharSequence = StringUtils.isCharSequence(this.propertyType);
        this.fieldFill = tableField.fill();
        this.update = tableField.update();
        this.el = el;
        tableInfo.setLogicDelete(this.initLogicDelete(dbConfig, field));

        if (StringUtils.isEmpty(tableField.value())) {
            if (tableInfo.isUnderCamel()) {
                column = StringUtils.camelToUnderline(column);
            }
        }
        this.column = column;
        this.related = TableInfoHelper.checkRelated(tableInfo.isUnderCamel(), this.property, this.column);

        /*
         * 优先使用单个字段注解，否则使用全局配置
         */
        if (tableField.strategy() == FieldStrategy.DEFAULT) {
            this.fieldStrategy = dbConfig.getFieldStrategy();
        } else {
            this.fieldStrategy = tableField.strategy();
        }

        if (StringUtils.isNotEmpty(tableField.condition())) {
            // 细粒度条件控制
            this.condition = tableField.condition();
        } else {
            // 全局配置
            this.setCondition(dbConfig);
        }

        // 字段是否注入查询
        this.select = tableField.select();
    }

    /**
     * 不存在 TableField 注解时, 使用的构造函数
     */
    public TableFieldInfo(GlobalConfig.DbConfig dbConfig, TableInfo tableInfo, Field field) {
        this.version = field.getAnnotation(Version.class) != null;
        this.property = field.getName();
        this.propertyType = field.getType();
        this.isCharSequence = StringUtils.isCharSequence(this.propertyType);
        this.el = field.getName();
        this.fieldStrategy = dbConfig.getFieldStrategy();
        this.setCondition(dbConfig);
        tableInfo.setLogicDelete(this.initLogicDelete(dbConfig, field));

        String column = field.getName();
        if (tableInfo.isUnderCamel()) {
            /* 开启字段下划线申明 */
            column = StringUtils.camelToUnderline(column);
        }
        if (dbConfig.isCapitalMode()) {
            /* 开启字段全大写申明 */
            column = column.toUpperCase();
        }

        String columnFormat = dbConfig.getColumnFormat();
        if (StringUtils.isNotEmpty(columnFormat)) {
            column = String.format(columnFormat, column);
        }

        this.column = column;
        this.related = TableInfoHelper.checkRelated(tableInfo.isUnderCamel(), this.property, this.column);
    }

    /**
     * 逻辑删除初始化
     *
     * @param dbConfig 数据库全局配置
     * @param field    字段属性对象
     */
    private boolean initLogicDelete(GlobalConfig.DbConfig dbConfig, Field field) {
        /* 获取注解属性，逻辑处理字段 */
        TableLogic tableLogic = field.getAnnotation(TableLogic.class);
        if (null != tableLogic) {
            if (StringUtils.isNotEmpty(tableLogic.value())) {
                this.logicNotDeleteValue = tableLogic.value();
            } else {
                this.logicNotDeleteValue = dbConfig.getLogicNotDeleteValue();
            }
            if (StringUtils.isNotEmpty(tableLogic.delval())) {
                this.logicDeleteValue = tableLogic.delval();
            } else {
                this.logicDeleteValue = dbConfig.getLogicDeleteValue();
            }
            return true;
        }
        return false;
    }

    /**
     * 是否注解了逻辑删除
     */
    public boolean isLogicDelete() {
        return StringUtils.isNotEmpty(logicDeleteValue);
    }

    /**
     * 全局配置开启字段 LIKE 并且为字符串类型字段
     * <p>注入 LIKE 查询！！！</p>
     */
    private void setCondition(GlobalConfig.DbConfig dbConfig) {
        if (null == condition || SqlCondition.EQUAL.equals(condition)) {
            if (dbConfig.isColumnLike() && isCharSequence) {
                condition = dbConfig.getDbType().getLike();
            }
        }
    }

    /**
     * 获取 select sql 片段
     *
     * @return sql 片段
     */
    public String getSqlSelect() {
        if (sqlSelect != null) {
            return sqlSelect;
        }
        sqlSelect = column;
        if (related) {
            sqlSelect += (" AS " + property);
        }
        return sqlSelect;
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
        String sqlScript = getInsertSqlProperty(prefix);
        if (fieldFill == FieldFill.INSERT || fieldFill == FieldFill.INSERT_UPDATE) {
            return sqlScript;
        }
        return convertIf(sqlScript, property);
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
    public String getInsertSqlColumnMaybeIf() {
        final String sqlScript = getInsertSqlColumn();
        if (fieldFill == FieldFill.INSERT || fieldFill == FieldFill.INSERT_UPDATE) {
            return sqlScript;
        }
        return convertIf(sqlScript, property);
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
        if (StringUtils.isNotEmpty(update)) {
            sqlSet += String.format(update, column);
        } else {
            sqlSet += SqlScriptUtils.safeParam(newPrefix + el);
        }
        sqlSet += COMMA;
        if (ignoreIf) {
            return sqlSet;
        }
        if (fieldFill == FieldFill.UPDATE || fieldFill == FieldFill.INSERT_UPDATE) {
            // 不进行 if 包裹
            return sqlSet;
        }
        return convertIf(sqlSet, newPrefix + property);
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
        return convertIf(sqlScript, newPrefix + property);
    }

    /**
     * 转换成 if 标签的脚本片段
     *
     * @param sqlScript sql 脚本片段
     * @param property  字段名
     * @return if 脚本片段
     */
    private String convertIf(final String sqlScript, final String property) {
        if (fieldStrategy == FieldStrategy.IGNORED) {
            return sqlScript;
        }
        if (fieldStrategy == FieldStrategy.NOT_EMPTY && isCharSequence) {
            return SqlScriptUtils.convertIf(sqlScript, String.format("%s != null and %s != ''", property, property),
                false);
        }
        return SqlScriptUtils.convertIf(sqlScript, String.format("%s != null", property), false);
    }
}
