package com.baomidou.mybatisplus;

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

	private static SqlMapper SQL_MAPPER;

	public static synchronized void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		SQL_MAPPER = new SqlMapper(sqlSessionFactory);
	}

	public static SqlMapper getSqlMapper() {
		return SQL_MAPPER;
	}

}
