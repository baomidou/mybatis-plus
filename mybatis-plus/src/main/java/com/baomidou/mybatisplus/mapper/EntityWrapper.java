/**
 * Copyright (c) 2011-2014, hubin (jobob@qq.com).
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
package com.baomidou.mybatisplus.mapper;

import com.baomidou.mybatisplus.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.toolkit.StringUtils;

/**
 * <p>
 * Entity 对象封装操作类，定义T-SQL语法
 * </p>
 *
 * @author hubin , yanghu , Dyang
 * @Date 2016-03-15
 */
public class EntityWrapper<T> extends QueryFilter {

	/**
	 * WHERE关键字
	 */
	protected final String WHERE = " WHERE ";

	/**
	 * AND关键字
	 */
	protected final String AND = " AND ";

	/**
	 * OR关键字
	 */
	protected final String OR = " OR ";

	/**
	 * GROUP BY关键字
	 */
	protected final String GROUPBY = " GROUP BY ";

	/**
	 * HAVING关键字
	 */
	protected final String HAVING = " HAVING ";

	/**
	 * ORDER BY关键字
	 */
	protected final String ORDERBY = " ORDER BY ";

	/**
	 * ORDER BY语句中排序的 DESC关键字
	 */
	protected final String DESC = " DESC ";

	/**
	 * ORDER BY语句中排序的 ASC关键字
	 */
	protected final String ASC = " ASC ";

	/**
	 * 是否使用了 T-SQL 语法
	 */
	protected boolean tsql = false;

	/**
	 * 数据库表映射实体类
	 */
	protected T entity = null;

	/**
	 * SQL 查询字段内容，例如：id,name,age
	 */
	protected String sqlSelect = null;

	public EntityWrapper() {
		// to do nothing
	}

	public EntityWrapper(T entity) {
		this.entity = entity;
	}

	public EntityWrapper(T entity, String sqlSelect) {
		this.entity = entity;
		this.sqlSelect = sqlSelect;
	}

	public T getEntity() {
		return entity;
	}

	public void setEntity(T entity) {
		this.entity = entity;
	}

	public String getSqlSelect() {
		if (StringUtils.isEmpty(sqlSelect)) {
			return null;
		}
		return stripSqlInjection(sqlSelect);
	}

	public void setSqlSelect(String sqlSelect) {
		if (StringUtils.isNotEmpty(sqlSelect)) {
			this.sqlSelect = sqlSelect;
		}
	}

	/**
	 * SQL 片段
	 */
	public String getSqlSegment() {
		/*
		 * 无条件
		 */
		String tempQuery = queryFilter.toString();
		if (StringUtils.isEmpty(tempQuery)) {
			return null;
		}

		/*
		 * 只排序、直接返回
		 */
		if (tempQuery.trim().toUpperCase().indexOf("ORDER BY") == 0) {
			return stripSqlInjection(queryFilter.toString());
		}

		/*
		 * SQL 片段，兼容非 T-SQL 语法
		 */
		if (!tsql) {
			StringBuffer sqlSegment = new StringBuffer();
			if (null == this.getEntity()) {
				sqlSegment.append(WHERE);
			} else {
				sqlSegment.append(AND);
			}
			sqlSegment.append(queryFilter.toString());
			return stripSqlInjection(sqlSegment.toString());
		}

		/*
		 * 使用防SQL注入处理后返回
		 */
		return stripSqlInjection(queryFilter.toString());
	}

	/**
	 * <p>
	 * SQL中WHERE关键字跟的条件语句
	 * </p>
	 * <p>
	 * eg: ew.where("name='zhangsan'").and("id={0}",22).and(
	 * "password is not null")
	 * </p>
	 *
	 * @param sqlWhere
	 *            where语句
	 * @param params
	 *            参数集
	 * @return
	 */
	public EntityWrapper<T> where(String sqlWhere, Object... params) {
		if (tsql) {
			throw new MybatisPlusException("SQL already contains the string where.");
		}
		/*
		 * 使用 T-SQL 语法
		 */
		tsql = true;
		if (null == this.getEntity()) {
			addFilter(WHERE, sqlWhere, params);
		} else {
			addFilter(AND, sqlWhere, params);
		}
		return this;
	}

	/**
	 * <p>
	 * SQL中 AND 关键字跟的条件语句
	 * </p>
	 * <p>
	 * eg: ew.where("name='zhangsan'").and("id={0}",22).and(
	 * "password is not null")
	 * </p>
	 *
	 * @param sqlAnd
	 *            and连接串
	 * @param params
	 *            参数集
	 * @return
	 */
	public EntityWrapper<T> and(String sqlAnd, Object... params) {
		addFilter(AND, sqlAnd, params);
		return this;
	}

	/**
	 * <p>
	 * 与 AND 方法的区别是 可根据需要判断是否添加该条件
	 * </p>
	 *
	 * @param need
	 *            是否需要使用该and条件
	 * @param sqlAnd
	 *            and条件语句
	 * @param params
	 *            参数集
	 * @return
	 */
	public EntityWrapper<T> andIfNeed(boolean need, String sqlAnd, Object... params) {
		addFilterIfNeed(need, AND, sqlAnd, params);
		return this;
	}

	/**
	 * <p>
	 * SQL中AND关键字跟的条件语句
	 * </p>
	 * <p>
	 * eg: ew.where("name='zhangsan'").or("password is not null")
	 * </p>
	 *
	 * @param sqlOr
	 *            or条件语句
	 * @param params
	 *            参数集
	 * @return
	 */
	public EntityWrapper<T> or(String sqlOr, Object... params) {
		addFilter(OR, sqlOr, params);
		return this;
	}

	/**
	 * <p>
	 * 与or方法的区别是 可根据需要判断是否添加该条件
	 * </p>
	 *
	 * @param need
	 *            是否需要使用OR条件
	 * @param sqlOr
	 *            OR条件语句
	 * @param params
	 *            参数集
	 * @return
	 */
	public EntityWrapper<T> orIfNeed(boolean need, String sqlOr, Object... params) {
		addFilterIfNeed(need, OR, sqlOr, params);
		return this;
	}

	/**
	 * <p>
	 * SQL中groupBy关键字跟的条件语句
	 * </p>
	 * <p>
	 * eg: ew.where("name='zhangsan'").and("id={0}",22).and(
	 * "password is not null") .groupBy("id,name")
	 * </p>
	 *
	 * @param sqlGroupBy
	 *            SQL 中的 Group by 语句，无需输入 Group By 关键字
	 * @return this
	 */
	public EntityWrapper<T> groupBy(String sqlGroupBy) {
		addFilter(GROUPBY, sqlGroupBy);
		return this;
	}

	/**
	 * <p>
	 * SQL中having关键字跟的条件语句
	 * </p>
	 * <p>
	 * eg: ew.groupBy("id,name").having("id={0}",22).and("password is not null")
	 * </p>
	 *
	 * @param sqlHaving
	 *            having关键字后面跟随的语句
	 * @param params
	 *            参数集
	 * @return EntityWrapper
	 */
	public EntityWrapper<T> having(String sqlHaving, Object... params) {
		addFilter(HAVING, sqlHaving, params);
		return this;
	}

	/**
	 * <p>
	 * SQL中orderby关键字跟的条件语句
	 * </p>
	 * <p>
	 * eg: ew.groupBy("id,name").having("id={0}",22).and("password is not null"
	 * ).orderBy("id,name")
	 * </p>
	 *
	 * @param sqlOrderBy
	 *            SQL 中的 order by 语句，无需输入 Order By 关键字
	 * @return this
	 */
	public EntityWrapper<T> orderBy(String sqlOrderBy) {
		addFilter(ORDERBY, sqlOrderBy);
		return this;
	}

	/**
	 * <p>
	 * SQL中orderby关键字跟的条件语句，可根据变更动态排序
	 * </p>
	 *
	 * @param sqlOrderBy
	 *            SQL 中的 order by 语句，无需输入 Order By 关键字
	 * @param isAsc
	 *            是否为升序
	 * @return
	 */
	public EntityWrapper<T> orderBy(String sqlOrderBy, boolean isAsc) {
		addFilter(ORDERBY, sqlOrderBy);
		if (isAsc) {
			queryFilter.append(ASC);
		} else {
			queryFilter.append(DESC);
		}
		return this;
	}

	/**
	 * <p>
	 * SQL注入内容剥离
	 * </p>
	 *
	 * @param value
	 *            待处理内容
	 * @return
	 */
	protected String stripSqlInjection(String value) {
		return value.replaceAll("('.+--)|(--)|(\\|)|(%7C)", "");
	}

}
