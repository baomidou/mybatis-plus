package com.baomidou.mybatisplus.test.plugin.OptimisticLocker;

import java.io.Reader;
import java.sql.Connection;
import java.util.Date;

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

import com.baomidou.mybatisplus.test.plugin.OptimisticLocker.entity.DateVersionUser;
import com.baomidou.mybatisplus.test.plugin.OptimisticLocker.entity.IntVersionUser;
import com.baomidou.mybatisplus.test.plugin.OptimisticLocker.mapper.DateVersionUserMapper;
import com.baomidou.mybatisplus.test.plugin.OptimisticLocker.mapper.IntVersionUserMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/spring-test.xml" })
public class OptimisticLockerInterceptorWebTest {

	@Autowired
	private IntVersionUserMapper intVersionUserMapper;
	@Autowired
	private DateVersionUserMapper DateVersionUserMapper;

	@Autowired
	private SqlSessionTemplate sqlSessionTemplate;

	@Before
	public void setUp() throws Exception {
		SqlSession session = sqlSessionTemplate.getSqlSessionFactory().openSession();
		Connection conn = session.getConnection();
		Reader reader = Resources.getResourceAsReader("com/baomidou/mybatisplus/test/plugin/OptimisticLocker/CreateDB.sql");
		ScriptRunner runner = new ScriptRunner(conn);
		runner.setLogWriter(null);
		runner.runScript(reader);
		reader.close();
		session.close();
	}

	@Test
	public void intVersionTest() {
		IntVersionUser versionUser = intVersionUserMapper.selectById(1);
		versionUser.setName("红薯");
		Assert.assertTrue(intVersionUserMapper.updateById(versionUser) == 1);
	}

	@Test
	public void dateVersionTest() {
		DateVersionUser dateVersionUser = new DateVersionUser();
		dateVersionUser.setName("zhangsan");
		dateVersionUser.setVersion(new Date());
		// 插入数据
		DateVersionUserMapper.insert(dateVersionUser);
		// 查找出来
		DateVersionUser dbUser = DateVersionUserMapper.selectById(dateVersionUser.getId());
		// 根据Id更新
		dbUser.setName("lisi");
		Assert.assertTrue(DateVersionUserMapper.updateById(dbUser) == 1);
	}

}
