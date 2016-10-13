package com.baomidou.mybatisplus.activerecord.dialect;

import java.sql.Connection;

/**
 * SQL方言接口。
 * 
 * @since 1.0
 * @author redraiment
 */
public interface Dialect {
	/**
	 * 判断给定的数据库连接是否使用当前方言。
	 * 
	 * @param c
	 *            数据库连接
	 * @return 如果该连接属于当前方言，返回true；否则返回false。
	 */
	public boolean accept(Connection c);

	/**
	 * 返回当前方言定义自增的整数类型主键的方法。
	 * 
	 * @return 返回当前方言定义自增的整数类型主键的方法。
	 */
	public String getIdentity();

	/**
	 * 将给定的标识转换成当前数据库内部的大小写形式。
	 * 
	 * @param identifier
	 *            需要转换的标识。
	 * @return 转换后的标识。
	 */
	public String getCaseIdentifier(String identifier);
}
