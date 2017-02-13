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
package com.baomidou.mybatisplus.plugins.pagination;

import com.baomidou.mybatisplus.enums.DBType;
import com.baomidou.mybatisplus.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.plugins.pagination.dialects.DB2Dialect;
import com.baomidou.mybatisplus.plugins.pagination.dialects.H2Dialect;
import com.baomidou.mybatisplus.plugins.pagination.dialects.HSQLDialect;
import com.baomidou.mybatisplus.plugins.pagination.dialects.MySqlDialect;
import com.baomidou.mybatisplus.plugins.pagination.dialects.OracleDialect;
import com.baomidou.mybatisplus.plugins.pagination.dialects.PostgreDialect;
import com.baomidou.mybatisplus.plugins.pagination.dialects.SQLServer2005Dialect;
import com.baomidou.mybatisplus.plugins.pagination.dialects.SQLServerDialect;
import com.baomidou.mybatisplus.plugins.pagination.dialects.SQLiteDialect;
import com.baomidou.mybatisplus.toolkit.StringUtils;

/**
 * <p>
 * 分页方言工厂类
 * </p>
 * 
 * @author hubin
 * @Date 2016-01-23
 */
public class DialectFactory {

	/**
	 * <p>
	 * 生成翻页执行 SQL
	 * </p>
	 * 
	 * @param page
	 *            翻页对象
	 * @param buildSql
	 *            执行 SQL
	 * @param dialectType
	 *            方言类型
	 * @param dialectClazz
	 *            自定义方言实现类
	 * @return
	 * @throws Exception
	 */
	public static String buildPaginationSql(Pagination page, String buildSql, String dialectType, String dialectClazz)
			throws Exception {
		// fix #172
		return getiDialect(dialectType, dialectClazz).buildPaginationSql(buildSql, page.getOffset(), page.getLimit());
	}

	/**
	 * <p>
	 * 获取数据库方言
	 * </p>
	 * 
	 * @param dialectType
	 *            方言类型
	 * @param dialectClazz
	 *            自定义方言实现类
	 * @return
	 * @throws Exception
	 */
	private static IDialect getiDialect(String dialectType, String dialectClazz) throws Exception {
		IDialect dialect = null;
		if (StringUtils.isNotEmpty(dialectType)) {
			dialect = getDialectByDbtype(dialectType);
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
		return dialect;
	}

	/**
	 * <p>
	 * 根据数据库类型选择不同分页方言
	 * </p>
	 * 
	 * @param dbtype
	 *            数据库类型
	 * @return
	 * @throws Exception
	 */
	private static IDialect getDialectByDbtype(String dbtype) throws Exception {
		IDialect dialect = null;
		if (DBType.MYSQL.getDb().equalsIgnoreCase(dbtype)) {
			dialect = MySqlDialect.INSTANCE;
		} else if (DBType.ORACLE.getDb().equalsIgnoreCase(dbtype)) {
			dialect = OracleDialect.INSTANCE;
		} else if (DBType.DB2.getDb().equalsIgnoreCase(dbtype)) {
			dialect = DB2Dialect.INSTANCE;
		} else if (DBType.H2.getDb().equalsIgnoreCase(dbtype)) {
			dialect = H2Dialect.INSTANCE;
		} else if (DBType.SQLSERVER.getDb().equalsIgnoreCase(dbtype)) {
			dialect = SQLServerDialect.INSTANCE;
		} else if (DBType.SQLSERVER2005.getDb().equalsIgnoreCase(dbtype)) {
			dialect = SQLServer2005Dialect.INSTANCE;
		} else if (DBType.POSTGRE.getDb().equalsIgnoreCase(dbtype)) {
			dialect = PostgreDialect.INSTANCE;
		} else if (DBType.HSQL.getDb().equalsIgnoreCase(dbtype)) {
			dialect = HSQLDialect.INSTANCE;
		} else if (DBType.SQLITE.getDb().equalsIgnoreCase(dbtype)) {
			dialect = SQLiteDialect.INSTANCE;
		} else {
			throw new MybatisPlusException("The database is not supported！dbtype:" + dbtype);
		}
		return dialect;
	}

}
