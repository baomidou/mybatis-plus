package com.baomidou.mybatisplus;

import org.apache.ibatis.session.SqlSessionFactory;

/**
 * <p>
 * MybatisSqlSessionFactoryHolder
 * </p>
 *
 * @author Caratacus
 * @Date 2016-10-26
 */
public class MybatisPlusHolder {

	private static SqlSessionFactory sqlSessionFactory;

	public static void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		MybatisPlusHolder.sqlSessionFactory = sqlSessionFactory;
	}

	public static SqlSessionFactory getSqlSessionFactory() {
		return sqlSessionFactory;
	}
}
