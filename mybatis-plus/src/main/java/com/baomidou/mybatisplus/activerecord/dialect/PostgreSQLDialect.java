package com.baomidou.mybatisplus.activerecord.dialect;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import com.baomidou.mybatisplus.activerecord.exception.DBOpenException;

/**
 * PostgreSQL方言。
 * 
 * @since 1.0
 * @author redraiment
 */
public class PostgreSQLDialect implements Dialect {
	/**
	 * 判断当前数据库的名称里是否包含postgresql（忽略大小写）。
	 * 
	 * @param c
	 *            数据库连接
	 * @return 如果数据库名称包含postgresql，则返回true；否则返回false。
	 */
	public boolean accept(Connection c) {
		try {
			DatabaseMetaData d = c.getMetaData();
			String name = d.getDatabaseProductName(); // PostgreSQL
			return name.toLowerCase().contains("postgresql");
		} catch (SQLException e) {
			throw new DBOpenException(e);
		}
	}

	/**
	 * 返回PostgreSQL中定义自增长的整型主键语句。
	 * 
	 * @return serial primary key。
	 */
	public String getIdentity() {
		return "serial primary key";
	}

	/**
	 * 将标识转换成为小写。
	 * 
	 * @param identifier
	 *            待转换的标识。
	 * @return 小写形式的标识。
	 */
	public String getCaseIdentifier(String identifier) {
		return identifier.toLowerCase();
	}
}
