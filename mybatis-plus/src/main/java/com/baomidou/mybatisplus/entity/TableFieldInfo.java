/**
 * Copyright (c) 2011-2014, hubin (jobob@qq.com).
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
package com.baomidou.mybatisplus.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableLogic;
import com.baomidou.mybatisplus.enums.FieldStrategy;
import com.baomidou.mybatisplus.toolkit.SqlReservedWords;
import com.baomidou.mybatisplus.toolkit.StringUtils;

import java.lang.reflect.Field;

/**
 * <p>
 * 数据库表字段反射信息
 * </p>
 *
 * @author hubin sjy willenfoo
 * @Date 2016-09-09
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
    private String propertyType;

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
     * <p>
     * 存在 TableField 注解构造函数
     * </p>
     */
    public TableFieldInfo(GlobalConfiguration globalConfig, TableInfo tableInfo, String column,
                          String el, Field field, TableField tableField) {
        this.property = field.getName();
        this.propertyType = field.getType().getName();
        /*
         * 1、开启字段下划线申明<br>
         * 2、没有开启下划线申明，但是column与property不等的情况<br>
         * 设置 related 为 true
         */
        if (globalConfig.isDbColumnUnderline()) {
             /* 开启字段下划线申明 */
            this.related = true;
            this.setColumn(globalConfig, StringUtils.camelToUnderline(column));
        } else {
            this.setColumn(globalConfig, column);
            if (!column.equals(this.property)) {
                this.related = true;
            }
        }
        this.el = el;
        /*
         * 优先使用单个字段注解，否则使用全局配置<br>
         * 自定义字段验证策略 fixed-239
		 */
        if (FieldStrategy.NOT_NULL != tableField.validate()) {
            this.fieldStrategy = tableField.validate();
        } else {
            this.fieldStrategy = globalConfig.getFieldStrategy();
        }
        tableInfo.setLogicDelete(this.initLogicDelete(globalConfig, field));
    }

    public TableFieldInfo(GlobalConfiguration globalConfig, TableInfo tableInfo, Field field) {
        if (globalConfig.isDbColumnUnderline()) {
            /* 开启字段下划线申明 */
            this.related = true;
            this.setColumn(globalConfig, StringUtils.camelToUnderline(field.getName()));
        } else {
            this.setColumn(globalConfig, field.getName());
        }
        this.property = field.getName();
        this.el = field.getName();
        this.fieldStrategy = globalConfig.getFieldStrategy();
        this.propertyType = field.getType().getName();
        tableInfo.setLogicDelete(this.initLogicDelete(globalConfig, field));
    }

    /**
     * <p>
     * 逻辑删除初始化
     * </p>
     *
     * @param globalConfig 全局配置
     * @param field        字段属性对象
     */
    private boolean initLogicDelete(GlobalConfiguration globalConfig, Field field) {
        if (null == globalConfig.getLogicDeleteValue()) {
            // 未设置逻辑删除值不进行
            return false;
        }
        /* 获取注解属性，逻辑处理字段 */
        TableLogic tableLogic = field.getAnnotation(TableLogic.class);
        if (null != tableLogic) {
            if (StringUtils.isNotEmpty(tableLogic.value())) {
                this.logicNotDeleteValue = tableLogic.value();
            } else {
                this.logicNotDeleteValue = globalConfig.getLogicNotDeleteValue();
            }
            if (StringUtils.isNotEmpty(tableLogic.delval())) {
                this.logicDeleteValue = tableLogic.delval();
            } else {
                this.logicDeleteValue = globalConfig.getLogicDeleteValue();
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

    public void setColumn(GlobalConfiguration globalConfig, String column) {
        String temp = SqlReservedWords.convert(globalConfig, column);
        if (globalConfig.isCapitalMode() && !isRelated()) {
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

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(String propertyType) {
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

}
