/**
 * Copyright (c) 2011-2016, hubin (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.framework.service.impl;

import com.baomidou.framework.service.IService;
import com.baomidou.mybatisplus.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.toolkit.StringUtils;
import com.baomidou.mybatisplus.toolkit.TableInfo;
import com.baomidou.mybatisplus.toolkit.TableInfoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * IService 实现类（ 泛型：M 是 mapper 对象，T 是实体 ， PK 是主键泛型 ）
 * </p>
 *
 * @author hubin
 * @Date 2016-04-20
 */
public class ServiceImpl<M extends BaseMapper<T, PK>, T, PK extends Serializable> implements IService<T, PK> {

	@Autowired
	protected M baseMapper;

	/**
	 * 判断数据库操作是否成功
	 *
	 * @param result
	 *            数据库操作返回影响条数
	 * @return boolean
	 */
	protected boolean retBool(int result) {
		return result >= 1;
	}

	/**
	 * <p>
	 * TableId 注解存在更新记录，否插入一条记录
	 * </p>
	 *
	 * @param entity
	 *            实体对象
	 * @param selective
	 *            true 选择字段 false 不选择字段
	 * @return boolean
	 */
	@Transactional(rollbackFor = Exception.class)
	public boolean insertOrUpdate(T entity, boolean selective) {
		if (null != entity) {
			Class<?> cls = entity.getClass();
			TableInfo tableInfo = TableInfoHelper.getTableInfo(cls);
			if (null != tableInfo) {
				try {
					Method m = cls.getMethod("get" + StringUtils.capitalize(tableInfo.getKeyProperty()));
					Object idVal = m.invoke(entity);
					if (null != idVal) {
						return selective ? updateSelectiveById(entity) : updateById(entity);
					} else {
						return selective ? insertSelective(entity) : insert(entity);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				throw new MybatisPlusException("Error:  Cannot execute. Could not find @TableId.");
			}
		}
		return false;
	}

	public boolean insertOrUpdate(T entity) {
		return insertOrUpdate(entity, false);
	}

	public boolean insertOrUpdateSelective(T entity) {
		return insertOrUpdate(entity, true);
	}

	@Transactional(rollbackFor = Exception.class)
	public boolean insert(T entity) {
		return retBool(baseMapper.insert(entity));
	}

	@Transactional(rollbackFor = Exception.class)
	public boolean insertSelective(T entity) {
		return retBool(baseMapper.insertSelective(entity));
	}

	@Transactional(rollbackFor = Exception.class)
	public boolean insertBatch(List<T> entityList) {
		return retBool(baseMapper.insertBatch(entityList));
	}

	@Transactional(rollbackFor = Exception.class)
	public boolean deleteById(PK id) {
		return retBool(baseMapper.deleteById(id));
	}

	@Transactional(rollbackFor = Exception.class)
	public boolean deleteByMap(Map<String, Object> columnMap) {
		return retBool(baseMapper.deleteByMap(columnMap));
	}

	@Transactional(rollbackFor = Exception.class)
	public boolean deleteSelective(T entity) {
		return retBool(baseMapper.deleteSelective(entity));
	}

	@Transactional(rollbackFor = Exception.class)
	public boolean deleteBatchIds(List<PK> idList) {
		return retBool(baseMapper.deleteBatchIds(idList));
	}

	@Transactional(rollbackFor = Exception.class)
	public boolean updateById(T entity) {
		return retBool(baseMapper.updateById(entity));
	}

	@Transactional(rollbackFor = Exception.class)
	public boolean updateSelectiveById(T entity) {
		return retBool(baseMapper.updateSelectiveById(entity));
	}

	@Transactional(rollbackFor = Exception.class)
	public boolean update(T entity, T whereEntity) {
		return retBool(baseMapper.update(entity, whereEntity));
	}

	@Transactional(rollbackFor = Exception.class)
	public boolean updateSelective(T entity, T whereEntity) {
		return retBool(baseMapper.updateSelective(entity, whereEntity));
	}

	@Transactional(rollbackFor = Exception.class)
	public boolean updateBatchById(List<T> entityList) {
		return retBool(baseMapper.updateBatchById(entityList));
	}

	public T selectById(PK id) {
		return baseMapper.selectById(id);
	}

	public List<T> selectBatchIds(List<PK> idList) {
		return baseMapper.selectBatchIds(idList);
	}

	public List<T> selectByMap(Map<String, Object> columnMap) {
		return baseMapper.selectByMap(columnMap);
	}

	public T selectOne(T entity) {
		return baseMapper.selectOne(entity);
	}

	public int selectCount(T entity) {
		return baseMapper.selectCount(entity);
	}

	public List<T> selectList(EntityWrapper<T> entityWrapper) {
		return baseMapper.selectList(entityWrapper);
	}

	public Page<T> selectPage(Page<T> page, EntityWrapper<T> entityWrapper) {
		if (null != entityWrapper) {
			entityWrapper.orderBy(page.getOrderByField(), page.isAsc());
		}
		page.setRecords(baseMapper.selectPage(page, entityWrapper));
		return page;
	}

}
