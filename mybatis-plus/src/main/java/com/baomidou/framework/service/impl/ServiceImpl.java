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
package com.baomidou.framework.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.baomidou.framework.service.IService;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;

/**
 * <p>
 * IService 实现类（ 泛型：M 是 mapper 对象， T 是实体 ， I 是主键泛型 ）
 * </p>
 * 
 * @author hubin
 * @Date 2016-04-20
 */
public class ServiceImpl<M extends BaseMapper<T, I>, T, I> implements IService<T, I> {

	@Autowired
	protected M baseMapper;


	/**
	 * 判断数据库操作是否成功
	 * 
	 * @param result
	 *            数据库操作返回影响条数
	 * @return boolean
	 */
	protected boolean retBool( int result ) {
		return (result >= 1) ? true : false;
	}


	public boolean insert( T entity ) {
		return retBool(baseMapper.insert(entity));
	}


	public boolean insertSelective( T entity ) {
		return retBool(baseMapper.insertSelective(entity));
	}


	public boolean insertBatch( List<T> entityList ) {
		return retBool(baseMapper.insertBatch(entityList));
	}


	public boolean deleteById( I id ) {
		return retBool(baseMapper.deleteById(id));
	}


	public boolean deleteByMap( Map<String, Object> columnMap) {
		return retBool(baseMapper.deleteByMap(columnMap));
	}


	public boolean deleteSelective( T entity ) {
		return retBool(baseMapper.deleteSelective(entity));
	}


	public boolean deleteBatchIds( List<I> idList ) {
		return retBool(baseMapper.deleteBatchIds(idList));
	}


	public boolean updateById( T entity ) {
		return retBool(baseMapper.updateById(entity));
	}


	public boolean updateSelectiveById( T entity ) {
		return retBool(baseMapper.updateSelectiveById(entity));
	}


	public boolean update( T entity, T whereEntity ) {
		return retBool(baseMapper.update(entity, whereEntity));
	}


	public boolean updateSelective( T entity, T whereEntity ) {
		return retBool(baseMapper.updateSelective(entity, whereEntity));
	}


	public boolean updateBatchById(List<T> entityList) {
		return retBool(baseMapper.updateBatchById(entityList));
	}


	public T selectById( I id ) {
		return baseMapper.selectById(id);
	}


	public List<T> selectBatchIds( List<I> idList ) {
		return baseMapper.selectBatchIds(idList);
	}


	public List<T> selectByMap( Map<String, Object> columnMap) {
		return baseMapper.selectByMap(columnMap);
	}


	public T selectOne( T entity ) {
		return baseMapper.selectOne(entity);
	}


	public int selectCount(T entity) {
		return baseMapper.selectCount(entity);
	}


	public List<T> selectList( T entity, String sqlSegment, String orderByField ) {
		return baseMapper.selectList(new EntityWrapper<T>(entity, sqlSegment, orderByField));		
	}


	public List<T> selectList( T entity, String orderByField ) {
		return baseMapper.selectList(new EntityWrapper<T>(entity, orderByField));
	}


	public List<T> selectList( T entity ) {
		return baseMapper.selectList(new EntityWrapper<T>(entity, null));
	}
	
	
	public List<T> selectListSqlSegment( String sqlSegment ) {
		return baseMapper.selectList(new EntityWrapper<T>(null, sqlSegment, null));
	}
	
	
	public List<T> selectListSqlSegment( String sqlSegment, String orderByField ) {
		return baseMapper.selectList(new EntityWrapper<T>(null, sqlSegment, orderByField));
	}


	public Page<T> selectPage( Page<T> page, T entity, String sqlSegment, String orderByField ) {
		page.setRecords(baseMapper.selectPage(page, new EntityWrapper<T>(entity, sqlSegment, orderByField)));
		return page;
	}


	public Page<T> selectPage( Page<T> page, T entity, String orderByField ) {
		page.setRecords(baseMapper.selectPage(page, new EntityWrapper<T>(entity, orderByField)));
		return page;
	}


	public Page<T> selectPage( Page<T> page, T entity ) {
		page.setRecords(baseMapper.selectPage(page, new EntityWrapper<T>(entity, null)));
		return page;
	}
	
	
	public Page<T> selectPageSqlSegment( Page<T> page, String sqlSegment ) {
		page.setRecords(baseMapper.selectPage(page, new EntityWrapper<T>(null, sqlSegment, null)));
		return page;
	}
	
	
	public Page<T> selectPageSqlSegment( Page<T> page, String sqlSegment, String orderByField ) {
		page.setRecords(baseMapper.selectPage(page, new EntityWrapper<T>(null, sqlSegment, orderByField)));
		return page;
	}

}
