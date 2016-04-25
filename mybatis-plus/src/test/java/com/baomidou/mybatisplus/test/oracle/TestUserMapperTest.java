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
package com.baomidou.mybatisplus.test.oracle;

import java.io.InputStream;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.BasicConfigurator;

import com.baomidou.mybatisplus.MybatisSessionFactoryBuilder;
import com.baomidou.mybatisplus.test.mysql.User;

/**
 * <p>
 * MybatisPlus 测试类
 * </p>
 * 
 * @author hubin
 * @Date 2016-04-25
 */
public class TestUserMapperTest {

	private static final String RESOURCE = "oracle-config.xml";


	/**
	 * 
	 * RUN 测试
	 * 
	 */
	public static void main( String[] args ) {
		//使用缺省Log4j环境
		BasicConfigurator.configure();

		//加载配置文件
		InputStream in = TestUserMapperTest.class.getClassLoader().getResourceAsStream(RESOURCE);

		/*
		 * 此处采用 MybatisSessionFactoryBuilder 构建
		 * SqlSessionFactory，目的是引入AutoMapper功能
		 */
		SqlSessionFactory sessionFactory = new MybatisSessionFactoryBuilder().build(in);
		SqlSession session = sessionFactory.openSession();
		TestUserMapper testUserMapper = session.getMapper(TestUserMapper.class);
		System.err.println(" debug run 查询执行 test_user 表数据变化！ ");
		session.delete("deleteAll");

		/**
		 * 插入
		 */
		int rlt = testUserMapper.insert(new TestUser("10001", "abc", 18, 1));
		System.err.println("\n--------------insert-------" + rlt);
		sleep();

		/* 删除测试数据  */
//		rlt = session.delete("deleteAll");
//		System.err.println("清空测试数据！ rlt=" + rlt);

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
			System.out.println("\n user: id="
					+ user.getId() + ", name=" + user.getName() + ", age=" + user.getAge() + ", testType="
					+ user.getTestType());
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
		} catch ( InterruptedException e ) {
			e.printStackTrace();
		}
	}
}
