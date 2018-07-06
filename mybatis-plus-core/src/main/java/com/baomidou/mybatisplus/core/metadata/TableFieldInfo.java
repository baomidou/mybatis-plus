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
package com.baomidou.mybatisplus.core.metadata;

import java.lang.reflect.Field;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.SqlCondition;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;

/**
 * <p>
 * 数据库表字段反射信息
 * </p>
 *
 * @author hubin sjy willenfoo tantan
 * @since 2016-09-09
 */
public class TableFieldInfo {

    /**
     * <p>
     * 是否有存在字段名与属性名关联
     * </p>
     * true , false
     */
    private boolean related = false;

    /**
     * 字段名
     */
    private String column;

    /**
     * 属性名
     */
    private String property;

    /**
     * 属性表达式#{property}, 可以指定jdbcType, typeHandler等
     */
    private String el;
    /**
     * 属性类型
     */
    private Class<?> propertyType;

    /**
     * 字段策略【 默认，自判断 null 】
     */
    private FieldStrategy fieldStrategy = FieldStrategy.NOT_NULL;

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
     * <p>
     * 存在 TableField 注解构造函数
     * </p>
     */
    public TableFieldInfo(boolean underCamel, GlobalConfig.DbConfig dbConfig, TableInfo tableInfo,
                          String column, String el, Field field, TableField tableField) {
        this.property = field.getName();
        this.propertyType = field.getType();
        /*
         * 1、注解 value 不存在，开启字段下划线申明<br>
         * 2、没有开启下划线申明，但是column与property不等的情况<br>
         * 设置 related 为 true
         */
        if (StringUtils.isEmpty(tableField.value())) {
            /* 开启字段下划线申明 */
            if (dbConfig.isColumnUnderline()) {
                column = StringUtils.camelToUnderline(column);
            }
            /* 未开启下划线转驼峰模式 AS 转换 */
            if (!underCamel) {
                this.related = true;
            }
        } else if (!column.equals(this.property)) {
            this.related = true;
        }
        this.setColumn(dbConfig, column);
        this.el = el;
        /*
         * 优先使用单个字段注解，否则使用全局配置
         */
        if (dbConfig.getFieldStrategy() != tableField.strategy()) {
            this.fieldStrategy = tableField.strategy();
        } else {
            this.fieldStrategy = dbConfig.getFieldStrategy();
        }
        tableInfo.setLogicDelete(this.initLogicDelete(dbConfig, field));
        this.update = tableField.update();
        if (StringUtils.isNotEmpty(tableField.condition())) {
            // 细粒度条件控制
            this.condition = tableField.condition();
        } else {
            // 全局配置
            this.setCondition(dbConfig);
        }
        /*
         * 保存当前字段的填充策略
         */
        this.fieldFill = tableField.fill();
    }

    public TableFieldInfo(boolean underCamel, GlobalConfig.DbConfig dbConfig, TableInfo tableInfo, Field field) {
        if (dbConfig.isColumnUnderline()) {
            /* 开启字段下划线申明 */
            this.setColumn(dbConfig, StringUtils.camelToUnderline(field.getName()));
            /* 未开启下划线转驼峰模式 AS 转换 */
            if (!underCamel) {
                this.related = true;
            }
        } else {
            this.setColumn(dbConfig, field.getName());
        }
        this.property = field.getName();
        this.el = field.getName();
        this.fieldStrategy = dbConfig.getFieldStrategy();
        this.propertyType = field.getType();
        this.setCondition(dbConfig);
        tableInfo.setLogicDelete(this.initLogicDelete(dbConfig, field));
    }

    /**
     * <p>
     * 逻辑删除初始化
     * </p>
     *
     * @param dbConfig 数据库全局配置
     * @param field    字段属性对象
     */
    private boolean initLogicDelete(GlobalConfig.DbConfig dbConfig, Field field) {
        if (null == dbConfig.getLogicDeleteValue()) {
            // 未设置逻辑删除值不进行
            return false;
        }
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

    public boolean isRelated() {
        return related;
    }

    public void setRelated(boolean related) {
        this.related = related;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(GlobalConfig.DbConfig dbConfig, String column) {
        String temp = dbConfig.getReservedWordsHandler().convert(dbConfig.getDbType(), column);
        if (dbConfig.isCapitalMode() && !isRelated()) {
            // 全局大写，非注解指定
            temp = temp.toUpperCase();
        }
        this.column = temp;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getEl() {
        return el;
    }

    public void setEl(String el) {
        this.el = el;
    }

    public FieldStrategy getFieldStrategy() {
        return fieldStrategy;
    }

    public void setFieldStrategy(FieldStrategy fieldStrategy) {
        this.fieldStrategy = fieldStrategy;
    }

    public Class<?> getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(Class<?> propertyType) {
        this.propertyType = propertyType;
    }

    /**
     * 是否开启逻辑删除
     */
    public boolean isLogicDelete() {
        return StringUtils.isNotEmpty(logicDeleteValue);
    }

    public String getLogicDeleteValue() {
        return logicDeleteValue;
    }

    public void setLogicDeleteValue(String logicDeleteValue) {
        this.logicDeleteValue = logicDeleteValue;
    }

    public String getLogicNotDeleteValue() {
        return logicNotDeleteValue;
    }

    public void setLogicNotDeleteValue(String logicNotDeleteValue) {
        this.logicNotDeleteValue = logicNotDeleteValue;
    }

    public String getUpdate() {
        return update;
    }

    public void setUpdate(String update) {
        this.update = update;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(GlobalConfig.DbConfig dbConfig) {
        /**
         * 全局配置开启字段 LIKE 并且为字符串类型字段
         * 注入 LIKE 查询！！！
         */
        if (null == this.condition || SqlCondition.EQUAL.equals(this.condition)) {
            if (dbConfig.isColumnLike() && StringUtils.isCharSequence(this.propertyType)) {
                this.condition = dbConfig.getDbType().getLike();
            }
        }
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public FieldFill getFieldFill() {
        return fieldFill;
    }

    public void setFieldFill(FieldFill fieldFill) {
        this.fieldFill = fieldFill;
    }
}
