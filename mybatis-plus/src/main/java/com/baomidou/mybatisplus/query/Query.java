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
package com.baomidou.mybatisplus.query;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.plugins.Page;

/**
 * <p>
 * Query 查询模式接口
 * </p>
 *
 * @author Caratacus
 * @Date 2016-12-11
 */
public interface Query {

	/**
	 * <p>
	 * 执行插入SQL
	 * </p>
	 *
	 * @param sql
	 *            SQL语句
	 * @param args
	 *            参数
	 * @return
	 */
	public boolean insert(String sql, Object... args);

	/**
	 * <p>
	 * 执行删除SQL
	 * </p>
	 *
	 * @param sql
	 *            SQL语句
	 * @param args
	 *            参数
	 * @return
	 */
	public boolean delete(String sql, Object... args);

	/**
	 * <p>
	 * 执行更新SQL
	 * </p>
	 *
	 * @param sql
	 *            SQL语句
	 * @param args
	 *            参数
	 * @return
	 */
	public boolean update(String sql, Object... args);

	/**
	 * <p>
	 * 执行SQL查询
	 * </p>
	 *
	 * @param sql
	 *            SQL 语句
	 * @param args
	 *            参数
	 * @return
	 */
	public List<Map<String, Object>> selectList(String sql, Object... args);

	/**
	 * <p>
	 * 执行SQL查询总数
	 * </p>
	 *
	 * @param sql
	 *            SQL 语句
	 * @param args
	 *            参数
	 * @return
	 */
	public int selectCount(String sql, Object... args);

	/**
	 * <p>
	 * 执行SQL单条查询
	 * </p>
	 *
	 * @param sql
	 *            SQL 语句
	 * @param args
	 *            参数
	 * @return
	 */
	public Map<String, Object> selectOne(String sql, Object... args);

	/**
	 * <p>
	 * 执行SQL查询，查询全部记录（并翻页）
	 * </p>
	 *
	 * @param page
	 *            分页对象
	 * @param sql
	 *            SQL语句
	 * @param args
	 *            参数
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Page<Map<String, Object>> selectPage(Page page, String sql, Object... args);

}
