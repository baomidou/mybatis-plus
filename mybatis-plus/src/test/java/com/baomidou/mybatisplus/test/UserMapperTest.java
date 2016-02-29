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
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
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
public class UserMapperTest {
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
	public static void main(String[] args) {
		//使用缺省Log4j环境
		BasicConfigurator.configure();
		
		//加载配置文件
		InputStream in = UserMapperTest.class.getClassLoader().getResourceAsStream(RESOURCE);

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
		int rlt = userMapper.insert(new User(id, "abc", 18, 0));
		System.err.println("\n--------------insert-------name为空---------\n name=null, age=18");
		sleep();
		
		List<User> ul = new ArrayList<User>();
		ul.add(new User(11L, "1", 1, 0));
		ul.add(new User(12L, "2", 2, 1));
		ul.add(new User(13L, "3", 3, 1));
		ul.add(new User(14L, "delname", 4, 0));
		ul.add(new User(15L, "5", 5, 1));
		ul.add(new User(16L, "6", 6, 0));
		ul.add(new User(17L, "7", 7, 0));
		for ( User u : ul ) {
			userMapper.insert(u);
		}
		
		
		/**
		 * 删除
		 */
		rlt = userMapper.deleteById(id);
		System.err.println("\n\n---------deleteById------- delete id=" + id + " ,result=" + rlt);
		sleep();
		
		List<Object> il = new ArrayList<Object>();
		il.add(16L);
		il.add(17L);
		rlt = userMapper.deleteBatchIds(il);
		System.err.println("\n\n---------deleteBatchIds------- delete id=" + id + " ,result=" + rlt);
		sleep();
		
		rlt = userMapper.deleteByName("delname");
		System.out.println("\n--------------deleteByName------------------\n result=" + rlt);
		sleep();
		
		
		/**
		 * <p>
		 * 修改
		 * </p>
		 * 
		 * updateById 是从 AutoMapper 中继承而来的，UserMapper.xml中并没有申明改sql
		 * 
		 */
		rlt = userMapper.updateById(new User(12L, "MybatisPlus"));
		System.err.println("\n------------------updateById----------------------\n result=" + rlt);
		sleep();
		
		
		
		/**
		 * <p>
		 * 查询
		 * </p>
		 */
		System.err.println("\n------------------selectById----------------------");
		User user = userMapper.selectById(12L);
		print(user);
		
		System.err.println("\n------------------selectBatchIds----------------------");
		List<Object> idList = new ArrayList<Object>();
		idList.add(11L);
		idList.add(12L);
		List<User> ul1 = userMapper.selectBatchIds(idList);
		for (int i = 0; i < ul1.size(); i++) {
			print(ul1.get(i));
		}

		System.err.println("\n------------------selectOne----------------------");
		User userOne = userMapper.selectOne(new User("MybatisPlus"));
		print(userOne);
		
		System.err.println("\n------------------selectList-----查询 testType = 1 的所有数据-----------------");
		List<User> ul2 = userMapper.selectList(RowBounds.DEFAULT, new User(1));
		for (int i = 0; i < ul2.size(); i++) {
			print(ul2.get(i));
		}
		
		System.err.println("\n------------------list 分页查询 ----查询 testType = 1 的所有数据------------------");
		Pagination pagination = new Pagination(1, 2);
		List<User> paginList = userMapper.selectList(pagination, new User(1));
		for (int i = 0; i < paginList.size(); i++) {
			print(paginList.get(i));
		}
		System.err.println(" 翻页：" + pagination.toString());
		
		System.err.println("\n---------------xml---selectListRow 分页查询，不查询总数（此时可自定义 count 查询）----无查询条件--------------");
		List<User> rowList = userMapper.selectListRow(new RowBounds(0, 2));
		for (int i = 0; i < rowList.size(); i++) {
			print(rowList.get(i));
		}

		/* 删除测试数据  */
		rlt = session.delete("deleteAll");
		System.err.println("清空测试数据！ rlt=" + rlt);
		
		/**
		 * 提交
		 */
		session.commit();
	}

	/*
	 * 打印测试信息
	 */
	private static void print( User user ) {
		sleep();
		if ( user != null ) {
			System.out.println("\n user: id=" + user.getId() + ", name=" + user.getName() 
			+ ", age=" + user.getAge() + ", testType=" + user.getTestType());
		} else {
			System.out.println("\n user is null.");
		}
	}
	
	/*
	 * 慢点打印 
	 */
	private static void sleep() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
