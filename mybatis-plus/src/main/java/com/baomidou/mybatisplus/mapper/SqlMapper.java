package com.baomidou.mybatisplus.mapper;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

/**
 * Mybatis执行sql工具,主要为AR方式调用
 *
 * @author Caratacus
 * @since 2016-10-18
 */
public class SqlMapper {
	private final SqlSessionFactory sqlSessionFactory;
	private final SqlSession sqlSession;

	/**
	 * 构造方法，默认缓存 SqlSessionFactory
	 *
	 * @param sqlSessionFactory
	 */
	public SqlMapper(SqlSessionFactory sqlSessionFactory) {
		this.sqlSessionFactory = sqlSessionFactory;
		this.sqlSession = sqlSessionFactory.openSession();
	}

	public SqlSessionFactory getSqlSessionFactory() {
		return this.sqlSessionFactory;
	}

	public SqlSession getSqlSession() {
		return this.sqlSession;
	}

}