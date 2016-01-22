package com.baomidou.mybatisplus.handler;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * 
 *
 */
public class BeanListHandler<T> implements ResultSetHandler<List<T>> {

	private final Class<T> type;

	private BeanProcessor convert = new BeanProcessor();
   
	public BeanListHandler(Class<T> type) {
		this.type = type;
		this.convert = new BeanProcessor();
    }

    public List<T> handle(ResultSet rs)  {
        try {
			return this.convert.toBeanList(rs, type);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
    }
}