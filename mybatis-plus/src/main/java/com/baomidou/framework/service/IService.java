/**
 * Copyright (c) 2011-2016, hubin (jobob@qq.com).
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

import com.baomidou.mybatisplus.activerecord.Table;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 顶级 Service
 * </p>
 * 
 * @author hubin
 * @Date 2016-04-20
 */
public interface IService<T> {

	/**
	 * <p>
	 * 插入一条记录
	 * </p>
	 * 
	 * @param entity
	 *            实体对象
	 * @return boolean
	 */
	boolean insert(T entity);

	/**
	 * <p>
	 * 插入一条记录（选择字段， null 字段不插入）
	 * </p>
	 * 
	 * @param entity
	 *            实体对象
	 * @return boolean
	 */
	boolean insertSelective(T entity);

	/**
	 * <p>
	 * 插入（批量），该方法不适合 Oracle
	 * </p>
	 * 
	 * @param entityList
	 *            实体对象列表
	 * @return boolean
	 */
	boolean insertBatch(List<T> entityList);

	/**
	 * <p>
	 * 根据 ID 删除
	 * </p>
	 * 
	 * @param id
	 *            主键ID
	 * @return boolean
	 */
	boolean deleteById(Serializable id);

	/**
	 * <p>
	 * 根据 columnMap 条件，删除记录
	 * </p>
	 * 
	 * @param columnMap
	 *            表字段 map 对象
	 * @return boolean
	 */
	boolean deleteByMap(Map<String, Object> columnMap);

	/**
	 * <p>
	 * 根据 entity 条件，删除记录
	 * </p>
	 * 
	 * @param entity
	 *            实体对象
	 * @return boolean
	 */
	boolean deleteSelective(T entity);

	/**
	 * <p>
	 * 删除（根据ID 批量删除）
	 * </p>
	 * 
	 * @param idList
	 *            主键ID列表
	 * @return boolean
	 */
	boolean deleteBatchIds(List<? extends Serializable> idList);

	/**
	 * <p>
	 * 根据 ID 修改
	 * </p>
	 * 
	 * @param entity
	 *            实体对象
	 * @return boolean
	 */
	boolean updateById(T entity);

	/**
	 * <p>
	 * 根据 ID 选择修改
	 * </p>
	 * 
	 * @param entity
	 *            实体对象
	 * @return boolean
	 */
	boolean updateSelectiveById(T entity);

	/**
	 * <p>
	 * 根据 whereEntity 条件，更新记录
	 * </p>
	 * 
	 * @param entity
	 *            实体对象
	 * @param whereEntity
	 *            实体查询条件（可以为 null）
	 * @return boolean
	 */
	boolean update(T entity, T whereEntity);

	/**
	 * <p>
	 * 根据 whereEntity 条件，选择更新记录
	 * </p>
	 * 
	 * @param entity
	 *            实体对象
	 * @param whereEntity
	 *            实体查询条件（可以为 null）
	 * @return boolean
	 */
	boolean updateSelective(T entity, T whereEntity);

	/**
	 * <p>
	 * 根据ID 批量更新
	 * </p>
	 * 
	 * @param entityList
	 *            实体对象列表
	 * @return boolean
	 */
	boolean updateBatchById(List<T> entityList);

	/**
	 * <p>
	 * TableId 注解存在更新记录，否插入一条记录
	 * </p>
	 *
	 * @param entity
	 *            实体对象
	 * @return boolean
	 */
	boolean insertOrUpdate(T entity);
	/**
	 * <p>
	 * TableId 注解存在更新记录，否插入一条记录 （选择字段， null 字段不插入）
	 * </p>
	 *
	 * @param entity
	 *            实体对象
	 * @return boolean
	 */
	boolean insertOrUpdateSelective(T entity);

	/**
	 * <p>
	 * 根据 ID 查询
	 * </p>
	 * 
	 * @param id
	 *            主键ID
	 * @return T
	 */
	T selectById(Serializable id);

	/**
	 * <p>
	 * 查询（根据ID 批量查询）
	 * </p>
	 * 
	 * @param idList
	 *            主键ID列表
	 * @return List<T>
	 */
	List<T> selectBatchIds(List<? extends Serializable> idList);

	/**
	 * <p>
	 * 查询（根据 columnMap 条件）
	 * </p>
	 * 
	 * @param columnMap
	 *            表字段 map 对象
	 * @return List<T>
	 */
	List<T> selectByMap(Map<String, Object> columnMap);

	/**
	 * <p>
	 * 根据 entity 条件，查询一条记录
	 * </p>
	 * 
	 * @param entity
	 *            实体对象
	 * @return T
	 */
	T selectOne(T entity);

	/**
	 * <p>
	 * 根据 EntityWrapper，查询一条记录
	 * </p>
	 *
	 * @param entityWrapper
	 *            实体对象
	 * @return T
	 */
	T selectOne(EntityWrapper<T> entityWrapper);

	/**
	 * <p>
	 * 根据 entity 条件，查询总记录数
	 * </p>
	 * 
	 * @param entity
	 *            实体对象
	 * @return int
	 */
	int selectCount(T entity);

	/**
	 * <p>
	 * 根据 EntityWrapper 条件，查询总记录数
	 * </p>
	 *
	 * @param entityWrapper
	 *            实体对象
	 * @return int
	 */
	int selectCount(EntityWrapper<T> entityWrapper);

	/**
	 * <p>
	 * 查询列表
	 * </p>
	 *
	 * @param entityWrapper
	 *            实体包装类 {@link EntityWrapper}
	 * @return
	 */
	List<T> selectList(EntityWrapper<T> entityWrapper);

	/**
	 * <p>
	 * 翻页查询
	 * </p>
	 * 
	 * @param page
	 *            翻页对象
	 * @param entityWrapper
	 *            实体包装类 {@link EntityWrapper}
	 * @return
	 */
	Page<T> selectPage(Page<T> page, EntityWrapper<T> entityWrapper);

	/**
	 * <p>
	 * ActiveRecord 模型操作 DB
	 * </p>
	 */
	Table db();
}
