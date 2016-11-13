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
package com.baomidou.mybatisplus.plugins;

import com.baomidou.mybatisplus.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.plugins.pagination.DialectFactory;
import com.baomidou.mybatisplus.plugins.pagination.IDialect;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.baomidou.mybatisplus.toolkit.StringUtils;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.session.RowBounds;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 * <p>
 * 分页拦截器
 * </p>
 *
 * @author hubin
 * @Date 2016-01-23
 */
@Intercepts({ @Signature(type = StatementHandler.class, method = "prepare", args = { Connection.class, Integer.class }) })
public class PaginationInterceptor implements Interceptor {

	/* 溢出总页数，设置第一页 */
	private boolean overflowCurrent = false;

	/* 方言类型 */
	private String dialectType;

	/* 方言实现类 */
	private String dialectClazz;

	public Object intercept(Invocation invocation) throws Throwable {
		Object target = invocation.getTarget();
		if (target instanceof StatementHandler) {
			StatementHandler statementHandler = (StatementHandler) target;
			MetaObject metaStatementHandler = SystemMetaObject.forObject(statementHandler);
			RowBounds rowBounds = (RowBounds) metaStatementHandler.getValue("delegate.rowBounds");

			/* 不需要分页的场合 */
			if (rowBounds == null || rowBounds == RowBounds.DEFAULT) {
				return invocation.proceed();
			}

			/* 定义数据库方言 */
			IDialect dialect = null;
			if (StringUtils.isNotEmpty(dialectType)) {
				dialect = DialectFactory.getDialectByDbtype(dialectType);
			} else {
				if (StringUtils.isNotEmpty(dialectClazz)) {
					try {
						Class<?> clazz = Class.forName(dialectClazz);
						if (IDialect.class.isAssignableFrom(clazz)) {
							dialect = (IDialect) clazz.newInstance();
						}
					} catch (ClassNotFoundException e) {
						throw new MybatisPlusException("Class :" + dialectClazz + " is not found");
					}
				}
			}

			/* 未配置方言则抛出异常 */
			if (dialect == null) {
				throw new MybatisPlusException("The value of the dialect property in mybatis configuration.xml is not defined.");
			}

			/*
			 * <p>
			 * 禁用内存分页
			 * </p>
			 * <p>
			 * 内存分页会查询所有结果出来处理（这个很吓人的），如果结果变化频繁这个数据还会不准。
			 * </p>
			 */
			BoundSql boundSql = (BoundSql) metaStatementHandler.getValue("delegate.boundSql");
			String originalSql = (String) boundSql.getSql();
			metaStatementHandler.setValue("delegate.rowBounds.offset", RowBounds.NO_ROW_OFFSET);
			metaStatementHandler.setValue("delegate.rowBounds.limit", RowBounds.NO_ROW_LIMIT);

			/**
			 * <p>
			 * 分页逻辑
			 * </p>
			 * <p>
			 * 查询总记录数 count
			 * </p>
			 */
			if (rowBounds instanceof Pagination) {
				MappedStatement mappedStatement = (MappedStatement) metaStatementHandler.getValue("delegate.mappedStatement");
				Connection connection = (Connection) invocation.getArgs()[0];
				Pagination page = (Pagination) rowBounds;
				boolean orderBy = true;
				if (page.isSearchCount()) {
					/*
					 * COUNT 查询，去掉 ORDER BY 优化执行 SQL
					 */
					StringBuffer countSql = new StringBuffer("SELECT COUNT(1) AS TOTAL ");
					if (page.isOptimizeCount()) {
						String tempSql = originalSql.replaceAll("(?i)ORDER[\\s]+BY", "ORDER BY");
						String indexOfSql = tempSql.toUpperCase();
						if (!indexOfSql.contains("DISTINCT")) {
							int formIndex = indexOfSql.indexOf("FROM");
							int orderByIndex = indexOfSql.lastIndexOf("ORDER BY");
							if (formIndex > -1) {
								// 无排序情况处理
								if (orderByIndex > -1) {
									tempSql = tempSql.substring(0, orderByIndex);
									countSql.append(tempSql.substring(formIndex));
									orderBy = false;
								} else {
									countSql.append(tempSql.substring(formIndex));
								}
							} else {
								countSql.append("FROM (").append(originalSql).append(") A");
							}
						} else {
							countSql.append("FROM (").append(originalSql).append(") A");
						}
					} else {
						countSql.append("FROM (").append(originalSql).append(") A");
					}
					page = this.count(countSql.toString(), connection, mappedStatement, boundSql, page);
					/** 总数 0 跳出执行 */
					if (page.getTotal() <= 0) {
						return invocation.proceed();
					}
				}
				/* 执行 SQL */
				StringBuffer buildSql = new StringBuffer(originalSql);
				if (orderBy && StringUtils.isNotEmpty(page.getOrderByField())) {
					buildSql.append(" ORDER BY ").append(page.getOrderByField());
					buildSql.append(page.isAsc() ? " ASC " : " DESC ");
				}
				originalSql = dialect.buildPaginationSql(buildSql.toString(), page.getOffsetCurrent(), page.getSize());
			}

			/**
			 * 查询 SQL 设置
			 */
			metaStatementHandler.setValue("delegate.boundSql.sql", originalSql);
		}

		return invocation.proceed();
	}

	/**
	 * 查询总记录条数
	 *
	 * @param sql
	 * @param connection
	 * @param mappedStatement
	 * @param boundSql
	 * @param page
	 */
	public Pagination count(String sql, Connection connection, MappedStatement mappedStatement, BoundSql boundSql, Pagination page) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = connection.prepareStatement(sql);
			BoundSql countBS = new BoundSql(mappedStatement.getConfiguration(), sql, boundSql.getParameterMappings(),
					boundSql.getParameterObject());
			ParameterHandler parameterHandler = new DefaultParameterHandler(mappedStatement, boundSql.getParameterObject(),
					countBS);
			parameterHandler.setParameters(pstmt);
			rs = pstmt.executeQuery();
			int total = 0;
			if (rs.next()) {
				total = rs.getInt(1);
			}
			page.setTotal(total);
			/*
			 * 溢出总页数，设置第一页
			 */
			if (overflowCurrent && (page.getCurrent() > page.getPages())) {
				page = new Pagination(1, page.getSize());
				page.setTotal(total);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return page;
	}

	public Object plugin(Object target) {
		if (target instanceof StatementHandler) {
			return Plugin.wrap(target, this);
		}
		return target;
	}

	public void setProperties(Properties prop) {
		String dialectType = prop.getProperty("dialectType");
		String dialectClazz = prop.getProperty("dialectClazz");
		if (StringUtils.isNotEmpty(dialectType)) {
			this.dialectType = dialectType;
		}
		if (StringUtils.isNotEmpty(dialectClazz)) {
			this.dialectClazz = dialectClazz;
		}
	}

	public void setDialectType(String dialectType) {
		this.dialectType = dialectType;
	}

	public void setDialectClazz(String dialectClazz) {
		this.dialectClazz = dialectClazz;
	}

	public void setOverflowCurrent(boolean overflowCurrent) {
		this.overflowCurrent = overflowCurrent;
	}

}