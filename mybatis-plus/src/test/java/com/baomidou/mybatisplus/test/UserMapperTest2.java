/**
 * Copyright (c) 2011-2014, hubin (jobob@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.BasicConfigurator;

import com.baomidou.mybatisplus.MybatisSessionFactoryBuilder;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.test.entity.User;
import com.baomidou.mybatisplus.test.mapper.UserMapper;
import com.baomidou.mybatisplus.toolkit.IdWorker;

/**
 * <p>
 * MybatisPlus 测试类
 * </p>
 * 
 * @author hubin
 * @Date 2016-01-23
 */
public class UserMapperTest2 {

	private static final String RESOURCE = "mybatis-config.xml";


	/**
	 * 
	 * RUN 测试
	 * 
	 * <p>
	 * MybatisPlus 加载 SQL 顺序：
	 * </p>
	 * 1、加载XML中的SQL<br>
	 * 2、加载sqlProvider中的SQL<br>
	 * 3、xmlSql 与 sqlProvider不能包含相同的SQL<br>
	 * <br>
	 * 调整后的SQL优先级：xmlSql > sqlProvider > crudSql
	 * <br>
	 */
	public static void main( String[] args ) {
		//使用缺省Log4j环境
		BasicConfigurator.configure();

		//加载配置文件
		InputStream in = UserMapperTest2.class.getClassLoader().getResourceAsStream(RESOURCE);

		/*
		 * 此处采用 MybatisSessionFactoryBuilder 构建
		 * SqlSessionFactory，目的是引入AutoMapper功能
		 */
		SqlSessionFactory sessionFactory = new MybatisSessionFactoryBuilder().build(in);
		SqlSession session = sessionFactory.openSession();
		UserMapper userMapper = session.getMapper(UserMapper.class);
		System.err.println(" debug run 查询执行 user 表数据变化！ ");
		session.delete("deleteAll");

		/**
		 * 插入
		 */
		long id = IdWorker.getId();
		System.err.println("----id---d----"+id);
		int rlt = userMapper.insertAbc(new User("abc", 18, 1));
		System.err.println("----rlt----"+ rlt);

		/**
		 * 提交
		 */
		session.commit();
	}

}
