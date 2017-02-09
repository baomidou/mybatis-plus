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

import com.baomidou.mybatisplus.enums.SqlMethod;
import com.baomidou.mybatisplus.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.mapper.Condition;
import com.baomidou.mybatisplus.mapper.SqlHelper;
import com.baomidou.mybatisplus.mapper.SqlRunner;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.toolkit.StringUtils;
import org.apache.ibatis.session.SqlSession;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		return SqlHelper.retBool(sqlSession().insert(sqlStatement(SqlMethod.INSERT_ONE), this));
	}

	/**
	 * <p>
	 * 插入 OR 更新
	 * </p>
	 */
	public boolean insertOrUpdate() {
		if (StringUtils.checkValNull(pkVal())) {
			// insert
			return insert();
		} else {
			/*
			 * 更新成功直接返回，失败执行插入逻辑
			 */
			boolean rlt = updateById();
			if (!rlt) {
				return insert();
			}
			return rlt;
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
		return SqlHelper.retBool(sqlSession().delete(sqlStatement(SqlMethod.DELETE_BY_ID), id));
	}

	/**
	 * <p>
	 * 根据主键删除
	 * </p>
	 *
	 * @return
	 */
	public boolean deleteById() {
		if (StringUtils.checkValNull(pkVal())) {
			throw new MybatisPlusException("deleteById primaryKey is null.");
		}
		return deleteById(this.pkVal());
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
		return delete(Condition.instance().where(whereClause, args));
	}

	/**
	 * <p>
	 * 删除记录
	 * </p>
	 *
	 * @param wrapper
	 * @return
	 */
	public boolean delete(Wrapper wrapper) {
		Map<String, Object> map = new HashMap<String, Object>();
		// delete
		map.put("ew", wrapper);
		return SqlHelper.retBool(sqlSession().delete(sqlStatement(SqlMethod.DELETE), map));
	}

	/**
	 * <p>
	 * 更新
	 * </p>
	 *
	 * @return
	 */
	public boolean updateById() {
		if (StringUtils.checkValNull(pkVal())) {
			throw new MybatisPlusException("updateById primaryKey is null.");
		}
		// updateById
		return SqlHelper.retBool(sqlSession().update(sqlStatement(SqlMethod.UPDATE_BY_ID), this));
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
		// update
		return update(Condition.instance().where(whereClause, args));
	}

	/**
	 * <p>
	 * 执行 SQL 更新
	 * </p>
	 *
	 * @param wrapper
	 * @return
	 */
	public boolean update(Wrapper wrapper) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("et", this);
		map.put("ew", wrapper);
		// update
		return SqlHelper.retBool(sqlSession().update(sqlStatement(SqlMethod.UPDATE), map));
	}

	/**
	 * <p>
	 * 查询所有
	 * </p>
	 *
	 * @return
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

	/**
	 * <p>
	 * 根据主键查询
	 * </p>
	 *
	 * @return
	 */
	public T selectById() {
		if (StringUtils.checkValNull(pkVal())) {
			throw new MybatisPlusException("selectById primaryKey is null.");
		}
		return selectById(this.pkVal());
	}

	/**
	 * <p>
	 * 查询总记录数
	 * </p>
	 *
	 * @param wrapper
	 * @return
	 */

	public List<T> selectList(Wrapper wrapper) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ew", wrapper);
		return sqlSession().selectList(sqlStatement(SqlMethod.SELECT_LIST), map);
	}

	/**
	 * <p>
	 * 查询所有
	 * </p>
	 *
	 * @param whereClause
	 * @param args
	 * @return
	 */
	public List<T> selectList(String whereClause, Object... args) {
		return selectList(Condition.instance().where(whereClause, args));
	}

	/**
	 * <p>
	 * 查询一条记录
	 * </p>
	 *
	 * @param wrapper
	 * @return
	 */
	public T selectOne(Wrapper wrapper) {
		return SqlHelper.getObject(selectList(wrapper));
	}

	/**
	 * <p>
	 * 查询一条记录
	 * </p>
	 *
	 * @param whereClause
	 * @param args
	 * @return
	 */
	public T selectOne(String whereClause, Object... args) {
		return selectOne(Condition.instance().where(whereClause, args));
	}

	/**
	 * <p>
	 * 翻页查询
	 * </p>
	 *
	 * @param page
	 *            翻页查询条件
	 * @param wrapper
	 * @return
	 */
	public Page<T> selectPage(Page<T> page, Wrapper<T> wrapper) {
		Map<String, Object> map = new HashMap<String, Object>();
		SqlHelper.fillWrapper(page, wrapper);
		map.put("ew", wrapper);
		List<T> tl = sqlSession().selectList(sqlStatement(SqlMethod.SELECT_PAGE), map, page);
		page.setRecords(tl);
		return page;
	}

	/**
	 * <p>
	 * 查询所有(分页)
	 * </p>
	 *
	 * @param page
	 * @param whereClause
	 * @param args
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Page<T> selectPage(Page<T> page, String whereClause, Object... args) {
		return selectPage(page, Condition.instance().where(whereClause, args));
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
		return selectCount(Condition.instance().where(whereClause, args));
	}

	/**
	 * <p>
	 * 查询总数
	 * </p>
	 *
	 * @param wrapper
	 * @return
	 */
	public int selectCount(Wrapper wrapper) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ew", wrapper);
		return SqlHelper.retCount(sqlSession().<Integer> selectOne(sqlStatement(SqlMethod.SELECT_COUNT), map));
	}

	/**
	 * <p>
	 * 执行 SQL
	 * </p>
	 */
	public SqlRunner sql() {
		return new SqlRunner(getClass());
	}

	/**
	 * <p>
	 * 获取Session 默认自动提交
	 * <p/>
	 */
	protected SqlSession sqlSession() {
		return SqlHelper.sqlSession(getClass());
	}

	/**
	 * 获取SqlStatement
	 *
	 * @param sqlMethod
	 * @return
	 */
	protected String sqlStatement(SqlMethod sqlMethod) {
		return sqlStatement(sqlMethod.getMethod());
	}

	/**
	 * 获取SqlStatement
	 *
	 * @param sqlMethod
	 * @return
	 */
	protected String sqlStatement(String sqlMethod) {
		return SqlHelper.table(getClass()).getSqlStatement(sqlMethod);
	}

	/**
	 * 主键值
	 */
	protected abstract Serializable pkVal();

}
