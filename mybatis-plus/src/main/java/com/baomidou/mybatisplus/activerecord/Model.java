/**
 * Copyright (c) 2011-2020, hubin (jobob@qq.com).
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
package com.baomidou.mybatisplus.activerecord;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import com.baomidou.mybatisplus.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.SqlMapper;
import com.baomidou.mybatisplus.mapper.SqlMethod;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.toolkit.CollectionUtil;
import com.baomidou.mybatisplus.toolkit.StringUtils;
import com.baomidou.mybatisplus.toolkit.TableInfo;
import com.baomidou.mybatisplus.toolkit.TableInfoHelper;

/**
 * <p>
 * ActiveRecord 模式 CRUD
 * </p>
 * 
 * @author hubin
 * @param <T>
 * @Date 2016-11-06
 */
@SuppressWarnings({ "serial", "rawtypes" })
public abstract class Model<T extends Model> implements Serializable {

	/**
	 * <p>
	 * 插入
	 * </p>
	 */
	public boolean insert() {
		return retBool(sqlSession().insert(sqlStatement(SqlMethod.INSERT_ONE), this));
	}

	/**
	 * <p>
	 * 插入 OR 更新
	 * </p>
	 */
	public boolean insertOrUpdate() {
		if (null != getPrimaryKey()) {
			// update
			return retBool(sqlSession().update(sqlStatement(SqlMethod.UPDATE_BY_ID), this));
		} else {
			// insert
			return retBool(sqlSession().insert(sqlStatement(SqlMethod.INSERT_ONE), this));
		}
	}

	/**
	 * <p>
	 * 根据 ID 删除
	 * </p>
	 * 
	 * @param id
	 *            主键ID
	 * @return
	 */
	public boolean deleteById(Serializable id) {
		return retBool(sqlSession().delete(sqlStatement(SqlMethod.DELETE_BY_ID), id));
	}

	public boolean deleteById() {
		return deleteById(getPrimaryKey());
	}

	/**
	 * <p>
	 * 删除记录
	 * </p>
	 * 
	 * @param whereClause
	 *            查询条件
	 * @param args
	 *            查询条件值
	 * @return
	 */
	public boolean delete(String whereClause, Object... args) {
		StringBuffer deleteSql = new StringBuffer();
		deleteSql.append("DELETE FROM ");
		deleteSql.append(table().getTableName());
		if (null != whereClause) {
			deleteSql.append(" WHERE ");
			deleteSql.append(StringUtils.sqlArgsFill(whereClause, args));
		}
		System.out.println(deleteSql.toString());
		return sqlMapper().delete(deleteSql.toString());
	}

	/**
	 * <p>
	 * 更新
	 * </p>
	 */
	public boolean updateById() {
		if (null == getPrimaryKey()) {
			throw new MybatisPlusException("primaryKey is null.");
		}
		// updateById
		return retBool(sqlSession().update(sqlStatement(SqlMethod.UPDATE_BY_ID), this));
	}

	/**
	 * <p>
	 * 执行 SQL 更新
	 * </p>
	 * 
	 * @param whereClause
	 *            查询条件
	 * @param args
	 *            查询条件值
	 * @return
	 */
	public boolean update(String whereClause, Object... args) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("et", this);
		if (StringUtils.isNotEmpty(whereClause)) {
			EntityWrapper<T> ew = new EntityWrapper<T>();
			ew.addFilter(whereClause, args);
			map.put("ew", ew);
		}
		// update
		return retBool(sqlSession().update(sqlStatement(SqlMethod.UPDATE), map));
	}

	/**
	 * <p>
	 * 查询所有
	 * </p>
	 */
	public List<T> selectAll() {
		return sqlSession().selectList(sqlStatement(SqlMethod.SELECT_LIST));
	}

	/**
	 * <p>
	 * 根据 ID 查询
	 * </p>
	 * 
	 * @param id
	 *            主键ID
	 * @return
	 */
	public T selectById(Serializable id) {
		return sqlSession().selectOne(sqlStatement(SqlMethod.SELECT_BY_ID), id);
	}

	public T selectById() {
		return selectById(getPrimaryKey());
	}

	/**
	 * <p>
	 * 查询总记录数
	 * </p>
	 * 
	 * @param columns
	 *            查询字段
	 * @param whereClause
	 *            查询条件
	 * @param args
	 *            查询条件值
	 * @return
	 */
	public List<T> selectList(String columns, String whereClause, Object... args) {
		EntityWrapper<T> ew = new EntityWrapper<T>(null, columns);
		if (StringUtils.isNotEmpty(whereClause)) {
			ew.addFilter(whereClause, args);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ew", ew);
		return sqlSession().selectList(sqlStatement(SqlMethod.SELECT_LIST), map);
	}

	public List<T> selectList(String whereClause, Object... args) {
		return selectList(null, whereClause, args);
	}

	public T selectOne(String columns, String whereClause, Object... args) {
		List<T> tl = selectList(columns, whereClause, args);
		if (CollectionUtil.isEmpty(tl)) {
			return null;
		}
		return tl.get(0);
	}

	public T selectOne(String whereClause, Object... args) {
		return selectOne(null, whereClause, args);
	}

	/**
	 * <p>
	 * 翻页查询
	 * </p>
	 * 
	 * @param page
	 *            翻页查询条件
	 * @param columns
	 *            查询字段
	 * @param whereClause
	 *            查询条件
	 * @param args
	 *            查询条件值
	 * @return
	 */
	public Page<T> selectPage(Page<T> page, String columns, String whereClause, Object... args) {
		EntityWrapper<T> ew = new EntityWrapper<T>(null, columns);
		if (StringUtils.isNotEmpty(whereClause)) {
			ew.addFilter(whereClause, args);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ew", ew);
		List<T> tl = sqlSession().selectList(sqlStatement(SqlMethod.SELECT_PAGE), map, page);
		page.setRecords(tl);
		return page;
	}

	public Page<T> selectPage(Page<T> page, String whereClause, Object... args) {
		return selectPage(page, null, whereClause, args);
	}

	public Page<T> selectPage(Page<T> page) {
		return selectPage(page, null);
	}

	/**
	 * <p>
	 * 查询总数
	 * </p>
	 * 
	 * @param whereClause
	 *            查询条件
	 * @param args
	 *            查询条件值
	 * @return
	 */
	public int selectCount(String whereClause, Object... args) {
		List<T> tl = selectList(whereClause, args);
		if (CollectionUtil.isEmpty(tl)) {
			return 0;
		}
		return tl.size();
	}

	public int selectCount() {
		return selectCount(null);
	}

	/**
	 * <p>
	 * 判断数据库操作是否成功
	 * </p>
	 *
	 * @param result
	 *            数据库操作返回影响条数
	 * @return boolean
	 */
	private boolean retBool(int result) {
		return result >= 1;
	}

	private SqlSession sqlSession() {
		return sqlMapper().getSqlSession();
	}

	private SqlMapper sqlMapper() {
		return table().getSqlMapper();
	}

	private String sqlStatement(SqlMethod sqlMethod) {
		return table().getSqlStatement(sqlMethod);
	}

	private TableInfo table() {
		return TableInfoHelper.getTableInfo(getClass());
	}

	protected abstract Serializable getPrimaryKey();

}
