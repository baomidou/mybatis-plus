package com.baomidou.mybatisplus.mapper;

import java.sql.Statement;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;


public class IdKeyGenerator implements KeyGenerator {

	@Override
	public void processBefore( Executor executor, MappedStatement ms, Statement stmt, Object parameter ) {
		// TODO Auto-generated method stub
		System.err.println("---processBefore----");
	}


	@Override
	public void processAfter( Executor executor, MappedStatement ms, Statement stmt, Object parameter ) {
		// TODO Auto-generated method stub
		System.err.println("---processAfter----");

	}

}
