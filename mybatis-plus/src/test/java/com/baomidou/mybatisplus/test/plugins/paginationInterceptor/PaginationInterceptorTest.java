package com.baomidou.mybatisplus.test.plugins.paginationInterceptor;

import java.io.Reader;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.session.SqlSession;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.test.plugins.paginationInterceptor.entity.PageUser;
import com.baomidou.mybatisplus.test.plugins.paginationInterceptor.service.PageUserService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/plugins/paginationInterceptor.xml" })
public class PaginationInterceptorTest {
	@Autowired
	private SqlSessionTemplate sqlSessionTemplate;
	@Autowired
	private PageUserService pageUserService;

	@Before
	public void setUp() throws Exception {
		SqlSession session = sqlSessionTemplate.getSqlSessionFactory().openSession();
		Connection conn = session.getConnection();
		Reader reader = Resources.getResourceAsReader("com/baomidou/mybatisplus/test/plugins/paginationInterceptor/CreateDB.sql");
		ScriptRunner runner = new ScriptRunner(conn);
		runner.setLogWriter(null);
		runner.runScript(reader);
		reader.close();
		session.close();
		List<PageUser> users = new ArrayList<>();
		for (int i = 0; i < 200; i++) {
			PageUser pageUser = new PageUser();
			pageUser.setId(i+1);
			pageUser.setName("编号" + i);
			pageUser.setAge((short) i);
			users.add(pageUser);
		}
		pageUserService.insertBatch(users);
	}

	@Test
	public void test() {
		Page<PageUser> page = new Page<>(1, 10);
		Page<PageUser> result = pageUserService.selectPage(page);
		System.out.println(result);
	}
}
