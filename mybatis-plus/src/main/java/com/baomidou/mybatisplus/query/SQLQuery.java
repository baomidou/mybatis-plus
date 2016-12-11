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

import com.baomidou.mybatisplus.activerecord.Record;
import com.baomidou.mybatisplus.entity.GlobalConfiguration;
import com.baomidou.mybatisplus.entity.TableInfo;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.toolkit.StringUtils;
import com.baomidou.mybatisplus.toolkit.TableInfoHelper;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.session.SqlSession;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * ActiveRecord 模式 CRUD
 * </p>
 *
 * @author hubin
 * @Date 2016-11-06
 */
@SuppressWarnings({ "serial", "rawtypes" })
public class SQLQuery implements Query {
	private static final Log logger = LogFactory.getLog(SQLQuery.class);

	private SqlSession sqlSession;
	private TableInfo tableInfo;

	public SQLQuery() {
		this.tableInfo = TableInfoHelper.getRandomTableInfo();
		String configMark = tableInfo.getConfigMark();
		GlobalConfiguration globalConfiguration = GlobalConfiguration.GlobalConfig(configMark);
		this.sqlSession = globalConfiguration.getSqlSessionFactory().openSession(true);

	}

	public SQLQuery(Class clazz) {
		this.tableInfo = Record.table(clazz);
		String configMark = tableInfo.getConfigMark();
		GlobalConfiguration globalConfiguration = GlobalConfiguration.GlobalConfig(configMark);
		this.sqlSession = globalConfiguration.getSqlSessionFactory().openSession(true);
	}

	public boolean insert(String sql, Object... args) {
		return retBool(sqlSession().insert(sqlStatement("insertSql"), StringUtils.sqlArgsFill(sql, args)));
	}

	public boolean delete(String sql, Object... args) {
		return retBool(sqlSession().delete(sqlStatement("deleteSql"), StringUtils.sqlArgsFill(sql, args)));
	}

	public boolean update(String sql, Object... args) {
		return retBool(sqlSession().update(sqlStatement("updateSql"), StringUtils.sqlArgsFill(sql, args)));
	}

	public List<Map<String, Object>> selectList(String sql, Object... args) {
		return sqlSession().selectList(sqlStatement("selectListSql"), StringUtils.sqlArgsFill(sql, args));
	}

	public int selectCount(String sql, Object... args) {
		return sqlSession().<Integer> selectOne(sqlStatement("selectCountSql"), StringUtils.sqlArgsFill(sql, args));
	}

	public Map<String, Object> selectOne(String sql, Object... args) {
		List<Map<String, Object>> list = selectList(sql, args);
		if (CollectionUtils.isNotEmpty(list)) {
			int size = list.size();
			if (size > 1) {
				logger.warn(String.format("Warn: selectOne Method There are  %s results.", size));
			}
			return list.get(0);
		}
		return Collections.emptyMap();
	}

	public Page<Map<String, Object>> selectPage(Page page, String sql, Object... args) {
		List<Map<String, Object>> list = sqlSession().selectList(sqlStatement("selectPageSql"),
				StringUtils.sqlArgsFill(sql, args), page);
		page.setRecords(list);
		return page;
	}

	/**
	 * 获取默认的SqlQuery(适用于单库)
	 * 
	 * @return
	 */
	public static Query create() {
		return new SQLQuery();
	}

	/**
	 * 根据当前class对象获取SqlQuery(适用于多库)
	 * 
	 * @param clazz
	 * @return
	 */
	public static Query create(Class clazz) {
		return new SQLQuery(clazz);
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

	/**
	 * <p>
	 * 获取Session 默认自动提交
	 * <p/>
	 */
	private SqlSession sqlSession() {
		return sqlSession;
	}

	private String sqlStatement(String sqlMethod) {
		return tableInfo.getSqlStatement(sqlMethod);
	}
}
