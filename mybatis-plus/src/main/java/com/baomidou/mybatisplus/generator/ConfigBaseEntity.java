/**
 * Copyright (c) 2011-2020, hubin (jobob@qq.com).
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
package com.baomidou.mybatisplus.generator;

import com.baomidou.mybatisplus.toolkit.StringUtils;

/**
 * <p>
 * 基础实体配置
 * </p>
 * 
 * @author hubin
 * @Date 2016-09-22
 */
public class ConfigBaseEntity {
	/*
	 * 包名，不设置默认使用 config.getEntityPackage() 配置内容
	 */
	private String packageName;

	/*
	 * 类名，默认 BaseEntity
	 */
	private String className = "BaseEntity";

	/*
	 * 公共字段数组
	 */
	private String[] columns;

	/**
	 * <p>
	 * 判断是否为公共字段
	 * </p>
	 * 
	 * @param column
	 *            判断字段内容
	 * @return
	 */
	public boolean includeColumns(String column) {
		if (StringUtils.isNotEmpty(column)) {
			for (String cl : columns) {
				if (column.equals(cl)) {
					return true;
				}
			}
		}
		return false;
	}

	public String getPackageName() {
		if (StringUtils.isNotEmpty(packageName)) {
			return packageName + "." + className;
		}
		return null;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String[] getColumns() {
		return columns;
	}

	public void setColumns(String[] columns) {
		this.columns = columns;
	}

}
