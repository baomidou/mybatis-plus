package com.baomidou.mybatisplus.activerecord;

import org.apache.ibatis.session.SqlSessionFactory;

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
