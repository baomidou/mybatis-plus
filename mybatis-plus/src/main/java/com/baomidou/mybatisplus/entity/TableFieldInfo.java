/**
 * Copyright (c) 2011-2014, hubin (jobob@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.entity;

import com.baomidou.mybatisplus.enums.FieldStrategy;
import com.baomidou.mybatisplus.toolkit.SqlReservedWords;
import com.baomidou.mybatisplus.toolkit.StringUtils;

/**
 * <p>
 * 数据库表字段反射信息
 * </p>
 *
 * @author hubin sjy
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
	 * <p>
	 * 存在 TableField 注解构造函数
	 * </p>
	 */
	public TableFieldInfo(GlobalConfiguration globalConfig, String column, String property, String el,
			FieldStrategy fieldStrategy, String propertyType) {
		if (globalConfig.isDbColumnUnderline()) {
			/* 开启字段下划线申明 */
			this.related = true;
		} else if (!column.equals(property)) {
			/* 没有开启下划线申明 但是column与property不等的情况下设置related为true */
			this.related = true;
		}
		this.setColumn(globalConfig, column);
		this.property = property;
		this.el = el;
		/*
		 * 优先使用单个字段注解，否则使用全局配置
		 */
		if (fieldStrategy != FieldStrategy.NOT_NULL) {
			this.fieldStrategy = fieldStrategy;
		} else {
			this.fieldStrategy = globalConfig.getFieldStrategy();
		}
		this.propertyType = propertyType;
	}

	public TableFieldInfo(GlobalConfiguration globalConfig, String column, String propertyType) {
		if (globalConfig.isDbColumnUnderline()) {
			/* 开启字段下划线申明 */
			this.related = true;
			this.setColumn(globalConfig, StringUtils.camelToUnderline(column));
		} else {
			this.setColumn(globalConfig, column);
		}
		this.property = column;
		this.el = column;
		this.fieldStrategy = globalConfig.getFieldStrategy();
		this.propertyType = propertyType;
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
}
