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
package com.baomidou.mybatisplus.mapper;

import com.baomidou.mybatisplus.toolkit.StringUtils;

/**
 * <p>
 * Entity 对象封装操作类
 * </p>
 * 
 * @author hubin
 * @Date 2016-03-15
 */
public class EntityWrapper<T> {

	/**
	 * 数据库表映射实体类
	 */
	private T entity = null;

	/**
	 * SQL 查询字段内容，例如：id,name,age
	 */
	private String sqlSelect = null;

	/**
	 * SQL 片段
	 */
	private String sqlSegment = null;

	protected EntityWrapper() {
		/* 保护 */
	}

	public EntityWrapper(T entity) {
		this.entity = entity;
	}

	public EntityWrapper(T entity, String sqlSegment) {
		this.entity = entity;
		this.sqlSegment = sqlSegment;
	}

	public EntityWrapper(T entity, String sqlSelect, String sqlSegment) {
		this.entity = entity;
		this.sqlSelect = sqlSelect;
		this.sqlSegment = sqlSegment;
	}

	public T getEntity() {
		return entity;
	}

	public void setEntity(T entity) {
		this.entity = entity;
	}

	public String getSqlSelect() {
		if (StringUtils.isEmpty(sqlSelect)) {
			return null;
		}
		return stripSqlInjection(sqlSelect);
	}

	public void setSqlSelect(String sqlSelect) {
		if (StringUtils.isNotEmpty(sqlSelect)) {
			this.sqlSelect = sqlSelect;
		}
	}

	public String getSqlSegment() {
		if (StringUtils.isEmpty(sqlSegment)) {
			return null;
		}
		StringBuffer andOr = new StringBuffer();
		if (null == entity) {
			andOr.append("WHERE ");
		} else {
			andOr.append("AND ");
		}
		andOr.append(sqlSegment);
		return stripSqlInjection(andOr.toString());
	}

	public void setSqlSegment(String sqlSegment) {
		if (StringUtils.isNotEmpty(sqlSegment)) {
			this.sqlSegment = sqlSegment;
		}
	}

	/**
	 * <p>
	 * SQL注入内容剥离
	 * </p>
	 * 
	 * @param value
	 *            待处理内容
	 * @return
	 */
	protected String stripSqlInjection(String value) {
		return value.replaceAll("('.+--)|(--)|(\\|)|(%7C)", "");
	}

}
