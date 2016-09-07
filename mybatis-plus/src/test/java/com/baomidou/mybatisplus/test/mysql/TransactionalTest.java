/**
 * Copyright (c) 2011-2014, hubin (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.test.mysql;

import java.io.InputStream;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.baomidou.mybatisplus.MybatisSessionFactoryBuilder;
import com.baomidou.mybatisplus.test.mysql.entity.User;
import com.baomidou.mybatisplus.toolkit.IdWorker;

public class TransactionalTest {

	/**
	 * <p>
	 * 事务测试
	 * </p>
	 */
	public static void main(String[] args) {
		/*
		 * 加载配置文件
		 */
		InputStream in = TransactionalTest.class.getClassLoader().getResourceAsStream("mysql-config.xml");
		MybatisSessionFactoryBuilder mf = new MybatisSessionFactoryBuilder();
		mf.setSqlInjector(new MySqlInjector());

		SqlSessionFactory sessionFactory = mf.build(in);
		SqlSession session = sessionFactory.openSession();
		UserMapper userMapper = session.getMapper(UserMapper.class);
		userMapper.deleteAll();

		/**
		 * 插入
		 */
		int rlt = userMapper.insertInjector(new User(IdWorker.getId(), "1", 1, 1));
		System.err.println("--------- insertInjector --------- " + rlt);

//		session.commit();
	}

}
