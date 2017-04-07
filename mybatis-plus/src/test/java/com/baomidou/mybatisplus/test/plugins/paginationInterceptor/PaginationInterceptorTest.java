package com.baomidou.mybatisplus.test.plugins.paginationInterceptor;

import java.io.Reader;
import java.sql.Connection;
import java.util.List;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.session.RowBounds;
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
import com.baomidou.mybatisplus.test.plugins.paginationInterceptor.mapper.PageUserMapper;
import com.baomidou.mybatisplus.test.plugins.paginationInterceptor.service.PageUserService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/plugins/paginationInterceptor.xml" })
public class PaginationInterceptorTest {
	@Autowired
	private SqlSessionTemplate sqlSessionTemplate;
	@Autowired
	private PageUserService pageUserService;
	@Autowired
	private PageUserMapper pageUserMapper;

	private int current;
	private int size;

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
		// 随机当前页和分页大小
		size = RandomUtils.nextInt(1, 50);
		current = RandomUtils.nextInt(1, 200 / size);
		System.err.println("当前页为:" + current + " 分页大小为" + size);
	}

	@Test
	public void pageSimpleTest() {
		// 最基础分页
		Page<PageUser> page1 = new Page<>(current, size);
		Page<PageUser> result1 = pageUserService.selectPage(page1);
		Assert.assertTrue(!result1.getRecords().isEmpty());

	}

	@Test
	public void pageOrderByTest() {
		// 带OrderBy
		Page<PageUser> page2 = new Page<>(current, size, "name");
		Page<PageUser> result2 = pageUserService.selectPage(page2);
		Assert.assertTrue(!result2.getRecords().isEmpty());
		// 没有orderby但是设置了倒叙
		Page<PageUser> page3 = new Page<>(current, size);
		page3.setAsc(false);
		Page<PageUser> result3 = pageUserService.selectPage(page3);
		Assert.assertTrue(!result3.getRecords().isEmpty());
		// 有orderby设置了倒叙
		Page<PageUser> page4 = new Page<>(current, size, "name");
		page3.setAsc(false);
		Page<PageUser> result4 = pageUserService.selectPage(page4);
		Assert.assertTrue(!result4.getRecords().isEmpty());
	}

	@Test
	public void pageCountTest() {
		// 设置不count
		Page<PageUser> page = new Page<>(current, size);
		page.setSearchCount(false);
		Page<PageUser> result = pageUserService.selectPage(page);
		Assert.assertTrue(result.getTotal() == 0);
	}

	@Test
	public void rowBoundTest() {
		System.err.println("测试原生RowBounds分页");
		int offset = RandomUtils.nextInt(1, 190);
		int limit = RandomUtils.nextInt(1,20);
		RowBounds rowBounds = new RowBounds(offset, limit);
		List<PageUser> result = pageUserMapper.selectPage(rowBounds, null);
		Assert.assertTrue(!result.isEmpty());
	}
}
