package com.baomidou.mybatisplus.mapper;

import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory;

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
	 * 构造方法，默认缓存 MappedStatement
	 *
	 * @param sqlSessionFactory
	 */
	public SqlMapper(MapperBuilderAssistant builderAssistant) {
		this.sqlSessionFactory = new DefaultSqlSessionFactory(builderAssistant.getConfiguration());
		this.sqlSession = sqlSessionFactory.openSession();
	}

	public SqlSessionFactory getSqlSessionFactory() {
		return this.sqlSessionFactory;
	}

	public SqlSession getSqlSession() {
		return this.sqlSession;
	}

}