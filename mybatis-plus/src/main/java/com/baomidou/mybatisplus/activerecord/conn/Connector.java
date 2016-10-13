package com.baomidou.mybatisplus.activerecord.conn;

import com.baomidou.mybatisplus.activerecord.DB;
import org.apache.ibatis.session.SqlSessionFactory;

/**
 * <p>
 * 获取连接接口
 * </p>
 *
 * @author Caratacus
 * @date 2016-10-13
 */
public interface Connector {

	/**
	 * spring配置获取DB
	 * 
	 * @return
	 */
	public DB open();

	/**
	 * sessionFactory获取DB
	 * 
	 * @param sessionFactory
	 * @return
	 */
	public DB open(SqlSessionFactory sessionFactory);

	/**
	 * JDBC获取DB
	 * 
	 * @param driver
	 * @param url
	 * @param username
	 * @param password
	 * @return
	 */
	public DB open(String driver, String url, String username, String password);

}
