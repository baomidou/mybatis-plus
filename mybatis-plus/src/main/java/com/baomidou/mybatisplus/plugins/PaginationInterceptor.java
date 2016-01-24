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
package com.baomidou.mybatisplus.plugins;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

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

import com.baomidou.mybatisplus.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.plugins.pagination.DialectFactory;
import com.baomidou.mybatisplus.plugins.pagination.IDialect;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;

/**
 * <p>
 * 分页拦截器
 * </p>
 * 
 * @author hubin
 * @Date 2016-01-23
 */
@Intercepts({ @Signature(type = StatementHandler.class, method = "prepare", args = { Connection.class }) })
public class PaginationInterceptor implements Interceptor {
	
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
			if (dialectType != null && !"".equals(dialectType)) {
				dialect = DialectFactory.getDialectByDbtype(dialectType);
			} else {
				if (dialectClazz != null && !"".equals(dialectClazz)) {
					try {
						Class<?> clazz = Class.forName(dialectClazz);
						if (IDialect.class.isAssignableFrom(clazz))
							dialect = (IDialect) clazz.newInstance();
					} catch (ClassNotFoundException e) {
						throw new MybatisPlusException("Class :" + dialectClazz + " is not found");
					}
				}
			}
			
			/* 未配置方言则抛出异常 */
			if (dialect == null) {
				throw new MybatisPlusException("The value of the dialect property in mybatis configuration.xml is not defined.");
			}

			/* 禁用内存分页 */
			BoundSql boundSql = (BoundSql) metaStatementHandler.getValue("delegate.boundSql");
			
			/* 禁用内存分页 */
			String originalSql = (String) boundSql.getSql();
			String paginationSql = dialect.buildPaginationSql(originalSql, rowBounds.getOffset(), rowBounds.getLimit());
			metaStatementHandler.setValue("delegate.boundSql.sql", paginationSql);
			
			/* 禁用内存分页 */
			metaStatementHandler.setValue("delegate.rowBounds.offset", RowBounds.NO_ROW_OFFSET);
			metaStatementHandler.setValue("delegate.rowBounds.limit", RowBounds.NO_ROW_LIMIT);

			/* 判断是否需要查询总记录条数 */
			if (rowBounds instanceof Pagination) {
				Pagination pagination = (Pagination) rowBounds;
				if (pagination.getTotal() == 0) {
					MappedStatement mappedStatement = (MappedStatement) metaStatementHandler
							.getValue("delegate.mappedStatement");
					Connection connection = (Connection) invocation.getArgs()[0];
					count(originalSql, connection, mappedStatement, boundSql, pagination);
				}
			}
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
	public void count(String sql, Connection connection, MappedStatement mappedStatement, BoundSql boundSql,
			Pagination page) {
		/* 记录总记录数 SQL */
		String countSql = "SELECT COUNT(0) FROM (" + sql + ") AS TOTAL";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = connection.prepareStatement(countSql);
			BoundSql countBS = new BoundSql(mappedStatement.getConfiguration(), countSql,
					boundSql.getParameterMappings(), boundSql.getParameterObject());
			ParameterHandler parameterHandler = new DefaultParameterHandler(mappedStatement,
					boundSql.getParameterObject(), countBS);
			parameterHandler.setParameters(pstmt);
			rs = pstmt.executeQuery();
			int total = 0;
			if (rs.next()) {
				total = rs.getInt(1);
			}
			page.setTotal(total);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
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
		if (dialectType != null && !"".equals(dialectType)) {
			this.dialectType = dialectType;
		}
		if (dialectClazz != null && !"".equals(dialectClazz)) {
			this.dialectClazz = dialectClazz;
		}
	}

	public void setDialectType(String dialectType) {
		this.dialectType = dialectType;
	}

	public void setDialectClazz(String dialectClazz) {
		this.dialectClazz = dialectClazz;
	}

}