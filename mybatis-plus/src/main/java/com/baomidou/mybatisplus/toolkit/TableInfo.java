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
package com.baomidou.mybatisplus.toolkit;

import java.util.List;

/**
 * <p>
 * 数据库表反射信息
 * </p>
 * 
 * @author hubin
 * @Date 2016-01-23
 */
public class TableInfo {

	/**
	 * 表主键ID 是否自增
	 */
	private boolean autoIncrement;

	/**
	 * 表名称
	 */
	private String tableName;

	/**
	 * 表主键ID 属性名
	 */
	private String keyProperty;

	/**
	 * 表主键ID 字段名
	 */
	private String keyColumn;

	/**
	 * 表字段信息列表
	 */
	private List<TableFieldInfo> fieldList;


	public boolean isAutoIncrement() {
		return autoIncrement;
	}


	public void setAutoIncrement( boolean autoIncrement ) {
		this.autoIncrement = autoIncrement;
	}


	public String getTableName() {
		return tableName;
	}


	public void setTableName( String tableName ) {
		this.tableName = tableName;
	}


	public String getKeyProperty() {
		return keyProperty;
	}


	public void setKeyProperty( String keyProperty ) {
		this.keyProperty = keyProperty;
	}


	public String getKeyColumn() {
		return keyColumn;
	}


	public void setKeyColumn( String keyColumn ) {
		this.keyColumn = keyColumn;
	}


	public List<TableFieldInfo> getFieldList() {
		return fieldList;
	}


	public void setFieldList( List<TableFieldInfo> fieldList ) {
		this.fieldList = fieldList;
	}

}
