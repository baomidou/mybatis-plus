package com.baomidou.mybatisplus.activerecord.dialect;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import com.baomidou.mybatisplus.activerecord.exception.DBOpenException;

/**
 * MySQL方言。
 * 
 * @since 1.0
 * @author redraiment
 */
public class MySQLDialect implements Dialect {

	/**
	 * 判断当前数据库的名称里是否包含mysql（忽略大小写）。
	 * 
	 * @param c
	 *            数据库连接
	 * @return 如果数据库名称包含mysql，则返回true；否则返回false。
	 */
	public boolean accept(Connection c) {
		try {
			DatabaseMetaData d = c.getMetaData();
			String name = d.getDatabaseProductName(); // MySQL
			return name.toLowerCase().contains("mysql");
		} catch (SQLException e) {
			throw new DBOpenException(e);
		}
	}

	/**
	 * 返回MySQL中定义自增长的整型主键语句。
	 * 
	 * @return integer primary key auto_increment。
	 */
	public String getIdentity() {
		return "integer primary key auto_increment";
	}

	/**
	 * 原样返回标识。
	 * 
	 * @param identifier
	 *            待转换的标识。
	 * @return 标识本身。
	 */
	public String getCaseIdentifier(String identifier) {
		return identifier;
	}
}
