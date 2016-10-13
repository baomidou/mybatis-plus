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
	 * 通过spring配置获取DB
	 * 
	 * @return DB
	 */
	public DB open();

	/**
	 * 通过sqlSessionFactory获取DB
	 * 
	 * @param sessionFactory
	 * @return DB
	 */
	public DB open(SqlSessionFactory sessionFactory);

	/**
	 * 通过jdbc获取DB
	 * 
	 * @param driver
	 * @param url
	 * @param username
	 * @param password
	 * @return DB
	 */
	public DB open(String driver, String url, String username, String password);

}
