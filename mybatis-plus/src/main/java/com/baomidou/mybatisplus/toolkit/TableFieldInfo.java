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

/**
 * <p>
 * 数据库表字段反射信息
 * </p>
 * 
 * @author hubin
 * @Date 2016-02-29
 */
public class TableFieldInfo {

	/**
	 * <p>
	 * 是否有存在字段名与属性名关联
	 * </p>
	 * true , false
	 */
	private boolean related;

	/**
	 * 字段名
	 */
	private String column;

	/**
	 * 属性名
	 */
	private String property;


	public TableFieldInfo( boolean related, String column, String property ) {
		this.related = related;
		this.column = column;
		this.property = property;
	}


	public TableFieldInfo( String column ) {
		this.related = false;
		this.column = column;
		this.property = column;
	}


	public boolean isRelated() {
		return related;
	}


	public void setRelated( boolean related ) {
		this.related = related;
	}


	public String getColumn() {
		return column;
	}


	public void setColumn( String column ) {
		this.column = column;
	}


	public String getProperty() {
		return property;
	}


	public void setProperty( String property ) {
		this.property = property;
	}

}
