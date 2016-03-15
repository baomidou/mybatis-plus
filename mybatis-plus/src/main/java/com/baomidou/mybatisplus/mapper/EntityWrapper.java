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

import com.baomidou.mybatisplus.exceptions.MybatisPlusException;

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
	 * <p>SQL 排序 ORDER BY 字段，例如： id DESC（根据id倒序查询）</p>
	 * <p>
	 *  DESC 表示按倒序排序(即：从大到小排序)<br> ACS  表示按正序排序(即：从小到大排序)
	 * </p>
	 */
	private String orderByField = null;


	protected EntityWrapper() {
		/* 保护 */
	}


	public EntityWrapper( T entity, String orderByField ) {
		this.setEntity(entity);
		this.setOrderByField(orderByField);
	}


	public T getEntity() {
		return entity;
	}


	public void setEntity( T entity ) {
		this.entity = entity;
	}


	public String getOrderByField() {
		if ( this.orderByField != null ) {
			StringBuffer ob = new StringBuffer(" ORDER BY ");
			ob.append(this.orderByField);
			return ob.toString();
		}
		return null;
	}


	public void setOrderByField( String orderByField ) {
		if ( orderByField != null && !"".equals(orderByField) ) {
			/**
			 * 判断是否存在 SQL 注入
			 */
			String ob = orderByField.toLowerCase();
			if ( ob.contains("INSERT") || ob.contains("DELETE") 
					|| ob.contains("UPDATE") || ob.contains("SELECT") ) {
				throw new MybatisPlusException(" orderBy=[" + orderByField + "], There may be SQL injection");
			} else {
				this.orderByField = orderByField;
			}
		}
	}

}
