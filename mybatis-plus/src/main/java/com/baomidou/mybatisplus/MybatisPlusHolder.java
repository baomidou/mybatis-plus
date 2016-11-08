package com.baomidou.mybatisplus;

import com.baomidou.mybatisplus.mapper.SqlMapper;
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
	private static SqlMapper sqlMapper;

	public static void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		MybatisPlusHolder.sqlSessionFactory = sqlSessionFactory;
		MybatisPlusHolder.sqlMapper = new SqlMapper(sqlSessionFactory);
	}

	public static SqlSessionFactory getSqlSessionFactory() {
		return sqlSessionFactory;
	}

	public static SqlMapper getSqlMapper() {
		return sqlMapper;
	}

}
