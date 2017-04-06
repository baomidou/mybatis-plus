package com.baomidou.mybatisplus.test.plugins.paginationInterceptor;

import java.io.Reader;
import java.sql.Connection;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.test.plugins.RandomUtils;
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
	}

	@Test
	public void pageTest() {
		// 随机当前页和分页大小
		int size = RandomUtils.nextInt(1, 50);
		int current = RandomUtils.nextInt(1, 200 / size);
		System.err.println("当前页为:" + current + " 分页大小为" + size);
		//最基础分页
		
		Page<PageUser> page1 = new Page<PageUser>(current, size);
		Page<PageUser> result1 = pageUserService.selectPage(page1);
		Assert.assertTrue(!result1.getRecords().isEmpty());
		//带OrderBy
		
		Page<PageUser> page2 = new Page<PageUser>(current, size,"name");
		Page<PageUser> result2 = pageUserService.selectPage(page2);
		Assert.assertTrue(!result2.getRecords().isEmpty());
		//倒叙
		Page<PageUser> page3 = new Page<PageUser>(current, size);
		page3.setAsc(false);
		Page<PageUser> result3 = pageUserService.selectPage(page3);
		Assert.assertTrue(!result3.getRecords().isEmpty());
	}
}
