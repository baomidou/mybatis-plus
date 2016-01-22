package com.baomidou.mybatisplus.handler;


import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 */
public class BeanHandler<T> implements ResultSetHandler<T> {
	private final Class<T> type;

	private BeanProcessor convert;
	
	public BeanHandler(Class<T> type) {
		this.type = type;
		this.convert = new BeanProcessor();
	}

	public T handle(ResultSet rs)  {
		 try {
			return rs.next() ? this.convert.toBean(rs, this.type) : null;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
