package com.baomidou.mybatisplus.test.activerecord;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Test;

import com.baomidou.mybatisplus.MybatisSessionFactoryBuilder;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.mapper.SqlMapper;
import com.baomidou.mybatisplus.test.mysql.TestMapper;
import com.baomidou.mybatisplus.toolkit.TableInfoHelper;

public class ArTest {

	@Test
	public void test() {

		InputStream in = TestMapper.class.getClassLoader().getResourceAsStream("mysql-config.xml");
		MybatisSessionFactoryBuilder mf = new MybatisSessionFactoryBuilder();
		SqlSessionFactory sqlSessionFactory = mf.build(in);
		TableInfoHelper.cacheSqlSessionFactory(sqlSessionFactory);
		SqlMapper mapper = Model.mapper(User.class);
		boolean rlt = mapper.insert("insert into user (test_id, name) values (10, 'test1'),(21, 'test2')");

		// Fetching all
		List<Map<String, Object>> users = new User().all();
		System.out.println(users.size());

		// Find by id
		System.out.println(new User().find(10));

		// Saving
		User user = new User();
		user.setName("test");
		System.out.println(user.save());

		// Eager loading associations
		User user2 = new User();
		user2.setId(10L);
		user2.eagerLoad();
		System.out.println(user2.save());
		// Queries
		List<Map<String, Object>> name = new User().where("name = ?", "João");
		System.out.println(name);
	}

}
