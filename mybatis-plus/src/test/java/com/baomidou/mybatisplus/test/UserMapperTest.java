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

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.BasicConfigurator;

import com.baomidou.mybatisplus.MybatisSessionFactoryBuilder;
import com.baomidou.mybatisplus.test.entity.User;
import com.baomidou.mybatisplus.test.mapper.UserMapper;

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
	 * RUN 测试
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
//		List<Long> idList = new ArrayList<Long>();
//		idList.add(1L);
//		idList.add(2L);
//		idList.add(3L);
//		int rlt = userMapper.deleteByIds(idList);
//		System.err.println(rlt);
		
//		List<User> ul = new ArrayList<User>();
//		ul.add(new User(IdWorker.getId(), "1", 1));
//		ul.add(new User(IdWorker.getId(), "2", 2));
//		ul.add(new User(IdWorker.getId(), "3", 3));
//		int rlt = userMapper.insertBatch(ul);
//		System.err.println(rlt);
		
		List<User> ul = new ArrayList<User>();
		ul.add(new User(1L, "1", 1));
		ul.add(new User(2L, "2", 2));
		ul.add(new User(3L, "3", 3));
		int rlt = userMapper.updateBatch(ul);
		System.err.println(rlt);
		
//		List<Long> ids = new ArrayList<Long>();
//		ids.add(1L);
//		ids.add(2L);
//		int rlt = userMapper.deleteBatchIds(ids);
//		System.err.println(rlt);
		
//		int rlt = userMapper.insert(new User(IdWorker.getId(), "6", 6));
//		System.err.println(rlt);
		
//		int result = userMapper.deleteByName("test");
//		System.out.println("\n------------------deleteByName----------------------\n result=" + result);
//		sleep();
//		
//		Long id = IdWorker.getId();
//		userMapper.insert(new User(id, "test", 18));
//		System.out.println("\n------------------insert----------------------\n name=test, age=18");
//		sleep();
//
//		/*
//		 * 此处的 selectById 被UserMapper.xml中的 selectById 覆盖了
//		 */
//		System.err.println("\n------------------selectById----------------------");
//		User user = userMapper.selectById(2L);
//		print(user);
//
//		/*
//		 * updateById 是从 AutoMapper 中继承而来的，UserMapper.xml中并没有申明改sql
//		 */
//		System.err.println("\n------------------updateById----------------------");
//		user.setName("MybatisPlus_" + System.currentTimeMillis());
//		userMapper.updateById(user);
//		sleep();
//		
//		/*
//		 * 此处的 selectById 被UserMapper.xml中的 selectById 覆盖了
//		 */
//		user = userMapper.selectById(user.getId());
//		print(user);
//
//		System.err.println("\n------------------selectAll----------------------");
//		List<User> userList = userMapper.selectAll();
//		for (int i = 0; i < userList.size(); i++) {
//			print(userList.get(i));
//		}
//		
//		System.err.println("\n------------------list 分页查询，不查询总数（此时可自定义 count 查询）----------------------");
//		List<User> rowList = userMapper.list(new RowBounds(0, 2));
//		for (int i = 0; i < rowList.size(); i++) {
//			print(rowList.get(i));
//		}
//		
//		System.err.println("\n------------------list 分页查询，查询总数----------------------");
//		Pagination pagination = new Pagination(0, 2);
//		List<User> paginList = userMapper.list(pagination);
//		for (int i = 0; i < paginList.size(); i++) {
//			print(paginList.get(i));
//		}
//		System.err.println(pagination.toString());
//		
//		System.out.println("\n\n------------------deleteById----------------------");
//		sleep();
//		int del = userMapper.deleteById(id);
//		System.err.println(" delete id=" + id + " ,result=" + del);
		
		/**
		 * 提交
		 */
		session.commit();
	}

	/*
	 * 打印测试信息
	 */
	private static void print(User user) {
		sleep();
		if (user != null) {
			System.out.println("\n user: id=" + user.getId() + ", name=" + user.getName() + ", age=" + user.getAge());
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
