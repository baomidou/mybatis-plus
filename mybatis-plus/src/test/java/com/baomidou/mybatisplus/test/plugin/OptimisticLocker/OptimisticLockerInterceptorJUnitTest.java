package com.baomidou.mybatisplus.test.plugin.OptimisticLocker;

import java.io.Reader;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.baomidou.mybatisplus.MybatisSessionFactoryBuilder;
import com.baomidou.mybatisplus.test.plugin.OptimisticLocker.entity.DateVersionUser;
import com.baomidou.mybatisplus.test.plugin.OptimisticLocker.entity.IntVersionUser;
import com.baomidou.mybatisplus.test.plugin.OptimisticLocker.entity.LongVersionUser;
import com.baomidou.mybatisplus.test.plugin.OptimisticLocker.entity.ShortVersionUser;
import com.baomidou.mybatisplus.test.plugin.OptimisticLocker.entity.TimestampVersionUser;
import com.baomidou.mybatisplus.test.plugin.OptimisticLocker.mapper.DateVersionUserMapper;
import com.baomidou.mybatisplus.test.plugin.OptimisticLocker.mapper.IntVersionUserMapper;
import com.baomidou.mybatisplus.test.plugin.OptimisticLocker.mapper.LongVersionUserMapper;
import com.baomidou.mybatisplus.test.plugin.OptimisticLocker.mapper.ShortVersionUserMapper;
import com.baomidou.mybatisplus.test.plugin.OptimisticLocker.mapper.TimestampVersionUserMapper;

public class OptimisticLockerInterceptorJUnitTest {

	private static SqlSessionFactory sqlSessionFactory;

	@BeforeClass
	public static void setUp() throws Exception {
		Reader reader = Resources.getResourceAsReader("com/baomidou/mybatisplus/test/plugin/OptimisticLocker/mybatis-config.xml");
		sqlSessionFactory = new MybatisSessionFactoryBuilder().build(reader);
		reader.close();
		SqlSession session = sqlSessionFactory.openSession();
		Connection conn = session.getConnection();
		reader = Resources.getResourceAsReader("com/baomidou/mybatisplus/test/plugin/OptimisticLocker/CreateDB.sql");
		ScriptRunner runner = new ScriptRunner(conn);
		runner.setLogWriter(null);
		runner.runScript(reader);
		reader.close();
		session.close();
	}

	@Test
	public void shorttVersionTest() {
		SqlSession sqlSession = sqlSessionFactory.openSession();
		ShortVersionUserMapper mapper = sqlSession.getMapper(ShortVersionUserMapper.class);
		// 查到数据
		ShortVersionUser versionUser = mapper.selectById(1);
		// 根据Id更新
		Assert.assertTrue(mapper.updateById(versionUser) == 1);
		sqlSession.close();
	}

	@Test
	public void intVersionTest() {
		SqlSession sqlSession = sqlSessionFactory.openSession();
		IntVersionUserMapper mapper = sqlSession.getMapper(IntVersionUserMapper.class);
		// 查到数据
		IntVersionUser versionUser = mapper.selectById(1);
		// 根据Id更新
		Assert.assertTrue(mapper.updateById(versionUser) == 1);
		sqlSession.close();
	}

	@Test
	public void longVersionTest() {
		SqlSession sqlSession = sqlSessionFactory.openSession();
		LongVersionUserMapper mapper = sqlSession.getMapper(LongVersionUserMapper.class);
		// 查到数据
		LongVersionUser versionUser = mapper.selectById(1);
		// 根据Id更新
		Assert.assertTrue(mapper.updateById(versionUser) == 1);
		sqlSession.close();
	}

	@Test
	public void dateVersionTest() {
		SqlSession sqlSession = sqlSessionFactory.openSession();
		DateVersionUserMapper mapper = sqlSession.getMapper(DateVersionUserMapper.class);
		DateVersionUser dateVersionUser = new DateVersionUser();
		dateVersionUser.setName("zhangsan");
		dateVersionUser.setVersion(new Timestamp(new Date().getTime()));
		// 插入数据
		mapper.insert(dateVersionUser);
		// 查找出来
		DateVersionUser dbUser = mapper.selectById(dateVersionUser.getId());
		// 根据Id更新
		dbUser.setName("lisi");
		Assert.assertTrue(mapper.updateById(dbUser) == 1);
		sqlSession.close();
	}

	@Test
	public void timestampVersionTest() {
		SqlSession sqlSession = sqlSessionFactory.openSession();
		TimestampVersionUserMapper mapper = sqlSession.getMapper(TimestampVersionUserMapper.class);
		TimestampVersionUser timeVersionUser = new TimestampVersionUser();
		timeVersionUser.setName("zhangsan");
		timeVersionUser.setVersion(new Timestamp(new Date().getTime()));
		// 插入数据
		mapper.insert(timeVersionUser);
		// 查找出来
		TimestampVersionUser dbUser = mapper.selectById(timeVersionUser.getId());
		// 根据Id更新
		dbUser.setName("lisi");
		Assert.assertTrue(mapper.updateById(dbUser) == 1);
		sqlSession.close();
	}

}
