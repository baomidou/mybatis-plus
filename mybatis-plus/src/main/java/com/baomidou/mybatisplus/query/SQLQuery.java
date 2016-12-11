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

	/**
	 * <p>
	 * 执行 SQL 插件
	 * </p>
	 *
	 * @param sql
	 *            SQL语句
	 * @param args
	 *            参数
	 * @return
	 */
	public boolean insertSql(String sql, Object... args) {
		return retBool(sqlSession().insert(sqlStatement("insertSql"), StringUtils.sqlArgsFill(sql, args)));
	}

	/**
	 * <p>
	 * 执行 SQL 删除
	 * </p>
	 *
	 * @param sql
	 *            SQL语句
	 * @param args
	 *            参数
	 * @return
	 */
	public boolean deleteSql(String sql, Object... args) {
		return retBool(sqlSession().delete(sqlStatement("deleteSql"), StringUtils.sqlArgsFill(sql, args)));
	}

	/**
	 * <p>
	 * 执行 SQL 更新
	 * </p>
	 *
	 * @param sql
	 *            SQL语句
	 * @param args
	 *            参数
	 * @return
	 */
	public boolean updateSql(String sql, Object... args) {
		return retBool(sqlSession().update(sqlStatement("updateSql"), StringUtils.sqlArgsFill(sql, args)));
	}

	/**
	 * <p>
	 * 执行 SQL 查询
	 * </p>
	 *
	 * @param sql
	 *            SQL 语句
	 * @param args
	 *            参数
	 * @return
	 */
	public List<Map<String, Object>> selectListSql(String sql, Object... args) {
		return sqlSession().selectList(sqlStatement("selectListSql"), StringUtils.sqlArgsFill(sql, args));
	}

	/**
	 * <p>
	 * 执行 SQL 查询
	 * </p>
	 *
	 * @param sql
	 *            SQL 语句
	 * @param args
	 *            参数
	 * @return
	 */
	public Map<String, Object> selectOneSql(String sql, Object... args) {
		List<Map<String, Object>> list = selectListSql(sql, args);
		if (CollectionUtils.isNotEmpty(list)) {
			int size = list.size();
			if (size > 1) {
				logger.warn(String.format("Warn: selectOne Method There are  %s results.", size));
			}
			return list.get(0);
		}
		return Collections.emptyMap();
	}

	/**
	 * <p>
	 * 执行 SQL 查询，查询全部记录（并翻页）
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
	public Page<Map<String, Object>> selectPageSql(Page page, String sql, Object... args) {
        List<Map<String, Object>> list = sqlSession().selectList(sqlStatement("selectPageSql"), StringUtils.sqlArgsFill(sql, args), page);
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
