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

import java.text.MessageFormat;

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
	 * 查询条件
	 */
	protected StringBuffer queryFilter = new StringBuffer();

	public EntityWrapper() {
		// to do nothing
	}

	public EntityWrapper(T entity) {
		this.entity = entity;
	}

	public EntityWrapper(T entity, String sqlSelect) {
		this.entity = entity;
		this.sqlSelect = sqlSelect;
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

	/**
	 * SQL 片段
	 */
	public String getSqlSegment() {
		/*
		 * 无条件
		 */
		String tempQuery = queryFilter.toString().trim();
		if (StringUtils.isEmpty(tempQuery)) {
			return null;
		}

		/*
		 * 只排序、直接返回
		 */
		if (tempQuery.toUpperCase().indexOf("ORDER BY") == 0) {
			return stripSqlInjection(queryFilter.toString());
		}

		/*
		 * SQL 片段
		 */
		StringBuffer sqlSegment = new StringBuffer();
		if (null == this.getEntity()) {
			sqlSegment.append(" WHERE ");
		} else {
			sqlSegment.append(" AND ");
		}
		sqlSegment.append(queryFilter.toString());
		return stripSqlInjection(sqlSegment.toString());
	}

	/**
	 * <p>
	 * 添加查询条件
	 * </p>
	 * <p>
	 * 例如： ew.addFilter("name={0}", "'123'") <br>
	 * 输出：name='123'
	 * </p>
	 * 
	 * @param filter
	 *            SQL 片段内容
	 * @param params
	 *            格式参数
	 * @return
	 */
	public EntityWrapper<T> addFilter(String filter, Object... params) {
		if (StringUtils.isEmpty(filter)) {
			return this;
		}
		if (null != params && params.length >= 1) {
			queryFilter.append(MessageFormat.format(filter, params));
		} else {
			queryFilter.append(filter);
		}
		return this;
	}

	/**
	 * <p>
	 * 添加查询条件
	 * </p>
	 * <p>
	 * 例如： ew.addFilter("name={0}", "'123'").addFilterIfNeed(false,
	 * " ORDER BY id") <br>
	 * 输出：name='123'
	 * </p>
	 * 
	 * @param willAppend
	 *            判断条件 true 输出 SQL 片段，false 不输出
	 * @param filter
	 *            SQL 片段
	 * @param params
	 *            格式参数
	 * @return
	 */
	public EntityWrapper<T> addFilterIfNeed(boolean willAppend, String filter, Object... params) {
		if (willAppend) {
			addFilter(filter, params);
		}
		return this;
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
