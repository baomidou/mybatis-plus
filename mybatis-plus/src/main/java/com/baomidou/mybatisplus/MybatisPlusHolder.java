package com.baomidou.mybatisplus;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.baomidou.mybatisplus.mapper.SqlMapper;

/**
 * <p>
 * MybatisSqlSessionFactoryHolder
 * </p>
 *
 * @author Caratacus
 * @Date 2016-10-26
 */
public class MybatisPlusHolder {

	private static SqlMapper sqlMapper;
	private static SqlSession sqlSession;
	private static SqlSessionFactory sqlSessionFactory;

	public static void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		MybatisPlusHolder.sqlSessionFactory = sqlSessionFactory;
		MybatisPlusHolder.sqlSession = sqlSessionFactory.openSession();
		MybatisPlusHolder.sqlMapper = new SqlMapper(MybatisPlusHolder.sqlSession);
	}

	public static SqlSessionFactory getSqlSessionFactory() {
		return sqlSessionFactory;
	}

	public static SqlSession getSqlSession() {
		return sqlSession;
	}

	public static SqlMapper getSqlMapper() {
		return sqlMapper;
	}

}
