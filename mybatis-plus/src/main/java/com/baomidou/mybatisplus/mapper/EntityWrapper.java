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
	 * SQL 片段
	 */
	private String sqlSegment = null;

	/**
	 * <p>SQL 排序 ORDER BY 字段，例如： id DESC（根据id倒序查询）</p>
	 * <p>
	 *  DESC 表示按倒序排序(即：从大到小排序)<br> ACS  表示按正序排序(即：从小到大排序)
	 * </p>
	 */
	private String orderByField = null;


	protected EntityWrapper() {
		/* 保护 */
	}


	public EntityWrapper( T entity) {
		this.entity = entity;
	}


	public EntityWrapper( T entity, String orderByField ) {
		this.entity = entity;
		this.orderByField = orderByField;
	}
	
	
	public EntityWrapper( T entity, String sqlSegment, String orderByField ) {
		this.entity = entity;
		this.sqlSegment = sqlSegment;
		this.orderByField = orderByField;
	}

	
	public T getEntity() {
		return entity;
	}


	public void setEntity( T entity ) {
		this.entity = entity;
	}


	public String getSqlSegment() {
		if ( sqlSegment == null && orderByField == null ) {
			return null;
		}
		StringBuffer andOrSql = new StringBuffer();
		if ( sqlSegment != null ) {
			andOrSql.append(sqlSegment);
		}
		if ( orderByField != null ) {
			andOrSql.append(" ORDER BY ").append(orderByField);
		}
		return stripSqlInjection(andOrSql.toString());
	}


	public void setSqlSegment( String sqlSegment ) {
		if ( sqlSegment != null && !"".equals(sqlSegment) ) {
			this.sqlSegment = sqlSegment;
		}
	}


	public String getOrderByField() {
		return orderByField;
	}


	public void setOrderByField( String orderByField ) {
		if ( orderByField != null && !"".equals(orderByField) ) {
			this.orderByField = orderByField;
		}
	}
	
	/**
	 * <p>
	 * SQL注入内容剥离
	 * </p>
	 * @param value
	 * 				待处理内容
	 * @return
	 */
	protected String stripSqlInjection(String value) {
		return value.replaceAll("('.+--)|(--)|(\\|)|(%7C)", "");
	}

}
