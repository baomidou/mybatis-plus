package com.baomidou.mybatisplus.activerecord.conn.impl;

import com.baomidou.mybatisplus.MybatisActiveRecordConnect;
import com.baomidou.mybatisplus.MybatisConfiguration;
import com.baomidou.mybatisplus.MybatisSqlSessionFactoryHolder;
import com.baomidou.mybatisplus.activerecord.DB;
import com.baomidou.mybatisplus.activerecord.conn.Connector;
import com.baomidou.mybatisplus.exceptions.MybatisPlusException;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

/**
 * <p>
 * 获取连接接口实现
 * </p>
 *
 * @author Caratacus
 * @date 2016-10-13
 */
public class ConnectorImpl implements Connector {

	protected MybatisActiveRecordConnect mybatisConnect;

	@Override
	public DB open() {
		SqlSessionFactory sessionFactory = MybatisSqlSessionFactoryHolder.getSqlSessionFactory();
		return initDB(sessionFactory);
	}

	@Override
	public DB open(String driver, String url, String username, String password) {
		JdbcTransactionFactory factory = new JdbcTransactionFactory();
		PooledDataSource pool = new PooledDataSource(driver, url, username, password);
		pool.setPoolPingEnabled(true);
		// 初始化 Environment
		Environment environment = new Environment(JdbcTransactionFactory.class.getName(), factory, pool);
		MybatisConfiguration configuration = new MybatisConfiguration(environment);
		SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
		SqlSessionFactory sessionFactory = sqlSessionFactoryBuilder.build(configuration);
		return initDB(sessionFactory);
	}

	@Override
	public DB open(SqlSessionFactory sessionFactory) {
		return initDB(sessionFactory);
	}

	/**
	 * 初始化DB
	 *
	 * @param sessionFactory
	 * @return
	 */
	private DB initDB(SqlSessionFactory sessionFactory) {
		if (sessionFactory == null) {
			throw new MybatisPlusException("Get the SqlSessionFactory exception");
		}
		this.mybatisConnect = new MybatisActiveRecordConnect(sessionFactory);
		return DB.open(this.mybatisConnect.getDataSource());
	}

}
