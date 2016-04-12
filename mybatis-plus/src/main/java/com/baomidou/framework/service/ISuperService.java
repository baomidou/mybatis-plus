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
package com.baomidou.framework.service;

import java.util.List;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;

/**
 * <p>
 * 抽象 Service
 * </p>
 * 
 * @author hubin
 * @Date 2016-03-23
 */
public interface ISuperService<T> {


	/**
	 * <p>
	 * 插入一条记录
	 * </p>
	 * @param entity
	 * 				实体对象
	 * @return boolean
	 */
	boolean insert( T entity );


	/**
	 * <p>
	 * 插入一条记录（选择字段， null 字段不插入）
	 * </p>
	 * @param entity
	 * 				实体对象
	 * @return int
	 */
	boolean insertSelective( T entity );


	/**
	 * <p>
	 * 插入（批量），该方法不适合 Oracle
	 * </p>
	 * @param entityList
	 * 				实体对象列表
	 * @return boolean
	 */
	boolean insertBatch( List<T> entityList );


	/**
	 * <p>
	 * 根据 ID 删除
	 * </p>
	 * @param id
	 * 			主键ID
	 * @return boolean
	 */
	boolean deleteById( Long id );


	/**
	 * <p>
	 * 根据 entity 条件，删除记录
	 * </p>
	 * @param entity
	 * 				实体对象
	 * @return boolean
	 */
	boolean deleteSelective( T entity );


	/**
	 * <p>
	 * 删除（根据ID 批量删除）
	 * </p>
	 * @param idList
	 * 				主键ID列表
	 * @return boolean
	 */
	boolean deleteBatchIds( List<Long> idList );


	/**
	 * <p>
	 * 根据 ID 修改
	 * </p>
	 * @param entity
	 * 				实体对象
	 * @return boolean
	 */
	boolean updateById( T entity );


	/**
	 * <p>
	 * 根据 ID 选择修改
	 * </p>
	 * @param entity
	 * 				实体对象
	 */
	boolean updateSelectiveById( T entity );


	/**
	 * <p>
	 * 根据 whereEntity 条件，更新记录
	 * </p>
	 * @param entity
	 * 				实体对象
	 * @return whereEntity
	 * 				实体查询条件
	 */
	boolean update( T entity, T whereEntity );


	/**
	 * <p>
	 * 根据 whereEntity 条件，选择更新记录
	 * </p>
	 * @param entity
	 * 				实体对象
	 * @return whereEntity
	 * 				实体查询条件
	 */
	boolean updateSelective( T entity, T whereEntity );


	/**
	 * <p>
	 * 根据 ID 查询
	 * </p>
	 * @param id
	 * 			主键ID
	 * @return T
	 */
	T selectById( Long id );


	/**
	 * <p>
	 * 查询（根据ID 批量查询）
	 * </p>
	 * @param idList
	 * 				主键ID列表
	 * @return List<T>
	 */
	List<T> selectBatchIds( List<Long> idList );


	/**
	 * <p>
	 * 根据 entity 条件，查询一条记录
	 * </p>
	 * @param entity
	 * 				实体对象
	 * @return T
	 */
	T selectOne( T entity );


	/**
	 * <p>
	 * 查询列表
	 * </p>
	 * 
	 * @param entity
	 * 				实体对象
	 * @param orderByField
	 * 				对应 EntityWrapper 类中 orderByField 字段
	 * 				{@link EntityWrapper}
	 * @return
	 */
	List<T> selectList( T entity, String orderByField );


	List<T> selectList( T entity );


	/**
	 * <p>
	 * 翻页查询
	 * </p>
	 * 
	 * @param page
	 * 				翻页对象
	 * @param entity
	 * 				实体对象
	 * @param orderByField
	 * 				对应 EntityWrapper 类中 orderByField 字段
	 * 				{@link EntityWrapper}
	 * @return
	 */
	Page<T> selectPage( Page<T> page, T entity, String orderByField );


	Page<T> selectPage( Page<T> page, T entity );

}
