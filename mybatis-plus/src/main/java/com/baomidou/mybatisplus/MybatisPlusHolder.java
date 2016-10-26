package com.baomidou.mybatisplus;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

/**
 * <p>
 * MybatisPlusHolder
 * </p>
 *
 * @author Caratacus
 * @Date 2016-10-26
 */
public class MybatisPlusHolder {

	private static SqlSession sqlSession;
	private static SqlSessionFactory sqlSessionFactory;

	public static void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		MybatisPlusHolder.sqlSessionFactory = sqlSessionFactory;
		MybatisPlusHolder.sqlSession = sqlSessionFactory.openSession();
	}

	public static SqlSessionFactory getSqlSessionFactory() {
		return sqlSessionFactory;
	}

	public static SqlSession getSqlSession() {
		return sqlSession;
	}

}
