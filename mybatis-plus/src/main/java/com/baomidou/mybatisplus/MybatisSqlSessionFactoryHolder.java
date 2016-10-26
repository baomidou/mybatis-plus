package com.baomidou.mybatisplus;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

/**
 * <p>
 * MybatisSqlSessionFactoryHolder
 * </p>
 *
 * @author Caratacus
 * @Date 2016-10-26
 */
public class MybatisSqlSessionFactoryHolder {

	private static SqlSession sqlSession;
	private static SqlSessionFactory sqlSessionFactory;

	public static void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		MybatisSqlSessionFactoryHolder.sqlSessionFactory = sqlSessionFactory;
		MybatisSqlSessionFactoryHolder.sqlSession = sqlSessionFactory.openSession();
	}

	public static SqlSessionFactory getSqlSessionFactory() {
		return sqlSessionFactory;
	}

	public static SqlSession getSqlSession() {
		return sqlSession;
	}

}
