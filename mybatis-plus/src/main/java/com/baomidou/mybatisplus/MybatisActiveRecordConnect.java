package com.baomidou.mybatisplus;

import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.transaction.Transaction;

import javax.sql.DataSource;

/**
 * <p>
 * Mybatis ActiveRecord 连接
 * </p>
 *
 * @author Caratacus
 * @date 2016-10-13
 */
public class MybatisActiveRecordConnect {

	/*
	 * Mybatis SqlSessionFactory
	 */
	protected SqlSessionFactory factory;
	/*
	 * Mybatis Configuration
	 */
	protected Configuration configuration;
	/*
	 * Mybatis DataSource
	 */
	protected DataSource dataSource;

	public MybatisActiveRecordConnect(SqlSessionFactory factory) {
		this.factory = factory;
		this.configuration = factory.getConfiguration();
		this.dataSource = factory.getConfiguration().getEnvironment().getDataSource();
	}

	/**
	 * 获取Mybatis Transaction(jdbc调用)
	 *
	 * @return Transaction
	 */
	public Transaction getTransaction() {
		Environment environment = configuration.getEnvironment();
		DataSource ds = environment.getDataSource();
		Transaction trans = environment.getTransactionFactory().newTransaction(ds, null, false);
		return trans;
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public SqlSessionFactory getSessionFactory() {
		return factory;
	}

}
