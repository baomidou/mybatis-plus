package com.baomidou.mybatisplus.test.plugins.optimisticLocker;

import java.io.Reader;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Random;

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

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.test.plugins.optimisticLocker.entity.DateVersionUser;
import com.baomidou.mybatisplus.test.plugins.optimisticLocker.entity.IntVersionUser;
import com.baomidou.mybatisplus.test.plugins.optimisticLocker.entity.LongVersionUser;
import com.baomidou.mybatisplus.test.plugins.optimisticLocker.entity.ShortVersionUser;
import com.baomidou.mybatisplus.test.plugins.optimisticLocker.entity.StringVersionUser;
import com.baomidou.mybatisplus.test.plugins.optimisticLocker.entity.TimestampVersionUser;
import com.baomidou.mybatisplus.test.plugins.optimisticLocker.mapper.DateVersionUserMapper;
import com.baomidou.mybatisplus.test.plugins.optimisticLocker.mapper.IntVersionUserMapper;
import com.baomidou.mybatisplus.test.plugins.optimisticLocker.mapper.LongVersionUserMapper;
import com.baomidou.mybatisplus.test.plugins.optimisticLocker.mapper.ShortVersionUserMapper;
import com.baomidou.mybatisplus.test.plugins.optimisticLocker.mapper.StringVersionUserMapper;
import com.baomidou.mybatisplus.test.plugins.optimisticLocker.mapper.TimestampVersionUserMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/plugins/optimisticLockerInterceptor.xml" })
public class OptimisticLockerInterceptorTest {

	@Autowired
	private ShortVersionUserMapper shortVersionUserMapper;
	@Autowired
	private IntVersionUserMapper intVersionUserMapper;
	@Autowired
	private LongVersionUserMapper longVersionUserMapper;
	@Autowired
	private DateVersionUserMapper dateVersionUserMapper;
	@Autowired
	private TimestampVersionUserMapper timestampVersionUserMapper;
	@Autowired
	private StringVersionUserMapper stringersionUserMapper;
	@Autowired
	private SqlSessionTemplate sqlSessionTemplate;

	@Before
	public void setUp() throws Exception {
		SqlSession session = sqlSessionTemplate.getSqlSessionFactory().openSession();
		Connection conn = session.getConnection();
		Reader reader = Resources.getResourceAsReader("com/baomidou/mybatisplus/test/plugins/optimisticLocker/CreateDB.sql");
		ScriptRunner runner = new ScriptRunner(conn);
		runner.setLogWriter(null);
		runner.runScript(reader);
		reader.close();
		session.close();
	}

	@Test
	public void shortVersionTest() {
		// 查询数据
		ShortVersionUser versionUser = shortVersionUserMapper.selectById(1);
		Short originVersion = versionUser.getVersion();
		// 更新数据
		versionUser.setName("苗神");
		shortVersionUserMapper.updateById(versionUser);
		Assert.assertTrue(versionUser.getVersion() == originVersion + 1);
	}

	@Test
	public void intVersionTest() {
		// 查询数据
		IntVersionUser versionUser = intVersionUserMapper.selectById(1);
		Integer originVersion = versionUser.getVersion();
		// 更新数据
		versionUser.setName("苗神");
		intVersionUserMapper.updateById(versionUser);
		Assert.assertTrue(versionUser.getVersion() == originVersion + 1);

		// 重复测试一次,验证动态参数覆盖
		// 查询数据
		IntVersionUser versionUser2 = intVersionUserMapper.selectById(2);
		Integer originVersion2 = versionUser2.getVersion();
		versionUser2.setAge(16);
		// 更新数据
		versionUser2.setName("苗神");
		intVersionUserMapper.updateById(versionUser2);
		Assert.assertTrue(versionUser2.getVersion() == originVersion2 + 1);

		// 测试一次数据库中version为null
		IntVersionUser versionUser3 = intVersionUserMapper.selectById(3);
		// 更新数据
		versionUser3.setName("苗神");
		intVersionUserMapper.updateById(versionUser3);
		Assert.assertTrue(versionUser3.getVersion() == null);

	}

	@Test
	public void longVersionTest() {
		// 查询数据
		LongVersionUser versionUser = longVersionUserMapper.selectById(1);
		Long originVersion = versionUser.getVersion();
		// 更新数据
		versionUser.setName("苗神");
		longVersionUserMapper.updateById(versionUser);
		Assert.assertTrue(versionUser.getVersion() == originVersion + 1);
	}

	@Test
	public void dateVersionTest() {
		// 插入数据
		DateVersionUser versionUser = new DateVersionUser();
		versionUser.setId(15L);
		versionUser.setName("苗神");
		Date originVersion = new Date();
		versionUser.setVersion(originVersion);
		dateVersionUserMapper.insert(versionUser);
		// 更新数据
		versionUser.setName("小锅盖");
		dateVersionUserMapper.updateById(versionUser);
		Assert.assertTrue(versionUser.getVersion().after(originVersion));
	}

	@Test
	public void timestampVersionTest() {
		// 插入数据
		TimestampVersionUser versionUser = new TimestampVersionUser();
		versionUser.setId(15L);
		versionUser.setName("苗神");
		Timestamp originVersion = new Timestamp(new Date().getTime());
		versionUser.setVersion(originVersion);
		timestampVersionUserMapper.insert(versionUser);
		// 更新数据
		versionUser.setName("小锅盖");
		timestampVersionUserMapper.updateById(versionUser);
		Assert.assertTrue(versionUser.getVersion().after(originVersion));
	}

	@Test
	public void stringVersionTest() {
		// 查询数据
		StringVersionUser versionUser = stringersionUserMapper.selectById(1);
		String originVersion = versionUser.getVersion();
		// 更新数据
		versionUser.setName("苗神");
		stringersionUserMapper.updateById(versionUser);
		Assert.assertEquals(versionUser.getVersion(), String.valueOf(Long.parseLong(originVersion) + 1));
	}

	@Test
	public void multiThreadVersionTest() {
		final Random random = new Random();
		for (int i = 50; i < 150; i++) {
			new Thread(new Runnable() {
				public void run() {
					IntVersionUser intVersionUser = new IntVersionUser();
					intVersionUser.setId(random.nextLong());
					int version = random.nextInt();
					intVersionUser.setName("改前" + version);
					intVersionUser.setVersion(version);
					intVersionUserMapper.insert(intVersionUser);
					intVersionUser.setName("改后" + version);
					intVersionUserMapper.updateById(intVersionUser);
					Assert.assertTrue(intVersionUser.getVersion() == version + 1);
				}
			}, "编号" + i).start();
		}

		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void multiParamVersionTest() {
		// 查询数据
		IntVersionUser versionUser = intVersionUserMapper.selectById(2);
		Integer originVersion = versionUser.getVersion();
		// 更新数据
		IntVersionUser intVersionUser = new IntVersionUser();
		intVersionUser.setName("苗神");
		intVersionUserMapper.update(versionUser, new EntityWrapper<IntVersionUser>(versionUser));
		Assert.assertTrue(versionUser.getVersion() == originVersion + 1);
	}
}
