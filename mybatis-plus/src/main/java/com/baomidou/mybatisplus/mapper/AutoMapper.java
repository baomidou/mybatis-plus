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

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

/**
 * <p>
 * Mapper 继承该接口后，无需编写 mapper.xml 文件，即可获得CRUD功能
 * </p>
 * 
 * @author hubin
 * @Date 2016-01-23
 */
public interface AutoMapper<T> {

	/**
	 * <p>
	 * 插入一条记录
	 * </p>
	 * @param entity
	 * 				实体对象
	 * @return int
	 */
	int insert( T entity );


	/**
	 * <p>
	 * 插入（批量），该方法不适合 Oracle
	 * </p>
	 * @param entityList
	 * 				实体对象列表
	 * @return int
	 */
	int insertBatch( List<T> entityList );


	/**
	 * <p>
	 * 根据 ID 删除
	 * </p>
	 * @param id
	 * 			主键ID
	 * @return int
	 */
	int deleteById( Object id );


	/**
	 * <p>
	 * 根据 entity 条件，删除记录
	 * </p>
	 * @param entity
	 * 				实体对象
	 * @return int
	 */
	int deleteSelective( T entity );


	/**
	 * <p>
	 * 删除（根据ID 批量删除）
	 * </p>
	 * @param entity
	 * 				实体对象
	 * @return int
	 */
	int deleteBatchIds( List<Object> idList );


	/**
	 * <p>
	 * 根据 ID 修改
	 * </p>
	 * @param entity
	 * 				实体对象
	 * @return int
	 */
	int updateById( T entity );


	/**
	 * <p>
	 * 根据 ID 查询
	 * </p>
	 * @param id
	 * 			主键ID
	 * @return T
	 */
	T selectById( Object id );


	/**
	 * <p>
	 * 查询（根据ID 批量查询）
	 * </p>
	 * @param idList
	 * 				主键ID列表
	 * @return List<T>
	 */
	List<T> selectBatchIds( List<Object> idList );


	/**
	 * <p>
	 * 根据 entity 条件，查询一条记录
	 * </p>
	 * @param entity
	 * 				实体对象
	 * @return int
	 */
	T selectOne( T entity );


	/**
	 * <p>
	 * 根据 entity 条件，查询全部记录
	 * </p>
	 * @param rowBounds
	 * 					分页查询条件（可以为 RowBounds.DEFAULT）
	 * @param entityWrapper
	 * 					实体对象封装操作类（可以为 null）
	 * @return List<T>
	 */
	List<T> selectList( RowBounds rowBounds, @Param("ew") EntityWrapper<T> entityWrapper);

}
