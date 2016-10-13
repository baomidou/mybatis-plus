package com.baomidou.mybatisplus.activerecord.dialect;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import com.baomidou.mybatisplus.activerecord.exception.DBOpenException;

/**
 * HyperSQL方言。
 * 
 * @since 1.0
 * @author redraiment
 */
public class HyperSQLDialect implements Dialect {
	/**
	 * 判断当前数据库的名称里是否包含hsql（忽略大小写）。
	 * 
	 * @param c
	 *            数据库连接
	 * @return 如果数据库名称包含hsql，则返回true；否则返回false。
	 */
	public boolean accept(Connection c) {
		try {
			DatabaseMetaData d = c.getMetaData();
			String name = d.getDatabaseProductName(); // HSQL Database Engine
			return name.toLowerCase().contains("hsql");
		} catch (SQLException e) {
			throw new DBOpenException(e);
		}
	}

	/**
	 * 返回HyperSQL中定义自增长的整型主键语句。
	 * 
	 * @return integer primary key generated always as identity (start with 1,
	 *         increment by 1)。
	 */
	public String getIdentity() {
		return "integer primary key generated always as identity (start with 1, increment by 1)";
	}

	/**
	 * 将标识转换成为大写。
	 * 
	 * @param identifier
	 *            待转换的标识。
	 * @return 大写形式的标识。
	 */
	public String getCaseIdentifier(String identifier) {
		return identifier.toUpperCase();
	}
}
