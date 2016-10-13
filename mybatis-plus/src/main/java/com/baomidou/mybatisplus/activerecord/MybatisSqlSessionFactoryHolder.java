package com.baomidou.mybatisplus.activerecord;

import org.apache.ibatis.session.SqlSessionFactory;

public abstract class MybatisSqlSessionFactoryHolder {

	private static SqlSessionFactory sqlSessionFactory;


	public static void setSqlSessionFactory( SqlSessionFactory sqlSessionFactory ) {
		MybatisSqlSessionFactoryHolder.sqlSessionFactory = sqlSessionFactory;
	}


	public static SqlSessionFactory getSqlSessionFactory() {
		return sqlSessionFactory;
	}


}
