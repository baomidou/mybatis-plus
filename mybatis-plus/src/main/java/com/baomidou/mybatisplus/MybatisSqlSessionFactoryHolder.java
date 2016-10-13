package com.baomidou.mybatisplus;

import org.apache.ibatis.session.SqlSessionFactory;

/**
 * <p>
 * 缓存当前SqlSessionFactory
 * </p>
 *
 * @author Caratacus
 * @date 2016-10-13
 */
public abstract class MybatisSqlSessionFactoryHolder {

	private static SqlSessionFactory sqlSessionFactory;

	public static void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		MybatisSqlSessionFactoryHolder.sqlSessionFactory = sqlSessionFactory;
	}

	public static SqlSessionFactory getSqlSessionFactory() {
		return sqlSessionFactory;
	}

}
