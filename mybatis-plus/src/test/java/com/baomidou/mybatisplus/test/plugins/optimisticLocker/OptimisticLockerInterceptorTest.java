package com.baomidou.mybatisplus.test.plugins.optimisticLocker;

import java.io.Reader;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Objects;
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
import com.baomidou.mybatisplus.test.plugins.optimisticLocker.entity.StringVersionUser;
import com.baomidou.mybatisplus.test.plugins.optimisticLocker.entity.TimestampVersionUser;
import com.baomidou.mybatisplus.test.plugins.optimisticLocker.mapper.DateVersionUserMapper;
import com.baomidou.mybatisplus.test.plugins.optimisticLocker.mapper.IntVersionUserMapper;
import com.baomidou.mybatisplus.test.plugins.optimisticLocker.mapper.LongVersionUserMapper;
import com.baomidou.mybatisplus.test.plugins.optimisticLocker.mapper.StringVersionUserMapper;
import com.baomidou.mybatisplus.test.plugins.optimisticLocker.mapper.TimestampVersionUserMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/plugins/optimisticLockerInterceptor.xml" })
public class OptimisticLockerInterceptorTest {

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
		Reader reader = Resources
				.getResourceAsReader("com/baomidou/mybatisplus/test/plugins/optimisticLocker/CreateDB.sql");
		ScriptRunner runner = new ScriptRunner(conn);
		runner.setLogWriter(null);
		runner.runScript(reader);
		reader.close();
		session.close();
	}

	@Test
	public void intVersionTest() {
		// 查询数据
		IntVersionUser versionUser = intVersionUserMapper.selectById(1);
		Integer originVersion = versionUser.getVersion();
		// 更新数据
		versionUser.setName("苗神");
		intVersionUserMapper.updateById(versionUser);
		Assert.assertTrue(intVersionUserMapper.selectById(1).getVersion() == originVersion + 1);

		// 重复测试一次,验证动态参数覆盖
		// 查询数据
		IntVersionUser versionUser2 = intVersionUserMapper.selectById(2);
		Integer originVersion2 = versionUser2.getVersion();
		versionUser2.setAge(16);
		// 更新数据
		versionUser2.setName("苗神");
		intVersionUserMapper.updateById(versionUser2);
		Assert.assertTrue(intVersionUserMapper.selectById(2).getVersion() == originVersion2 + 1);

		// 测试一次数据库中version为null
		IntVersionUser versionUser3 = intVersionUserMapper.selectById(3);
		// 更新数据
		versionUser3.setName("苗神");
		intVersionUserMapper.updateById(versionUser3);
		Assert.assertTrue(intVersionUserMapper.selectById(3).getVersion() == null);

	}

	@Test
	public void longVersionTest() {
		// 查询数据
		LongVersionUser versionUser = longVersionUserMapper.selectById(1);
		Long originVersion = versionUser.getVersion();
		// 更新数据
		versionUser.setName("苗神");
		longVersionUserMapper.updateById(versionUser);
		Assert.assertTrue(longVersionUserMapper.selectById(1).getVersion() == originVersion + 1);
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
		DateVersionUser q = dateVersionUserMapper.selectById(15);
		q.setName("小锅盖");
		dateVersionUserMapper.updateById(q);
		Assert.assertTrue(dateVersionUserMapper.selectById(15L).getVersion().after(originVersion));
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
		TimestampVersionUser q = timestampVersionUserMapper.selectById(15);
		q.setName("小锅盖");
		timestampVersionUserMapper.updateById(q);
		Assert.assertTrue(timestampVersionUserMapper.selectById(15L).getVersion().after(originVersion));
	}

	@Test
	public void stringVersionTest() {
		// 查询数据
		StringVersionUser versionUser = stringersionUserMapper.selectById(1);
		String originVersion = versionUser.getTt();
		// 更新数据
		versionUser.setName("苗神");
		stringersionUserMapper.updateById(versionUser);
		Assert.assertEquals(stringersionUserMapper.selectById(1).getTt(),
				String.valueOf(Long.parseLong(originVersion) + 1));
	}

	@Test
	public void multiThreadVersionTest() {
		final Random random = new Random();
		for (int i = 50; i < 150; i++) {
			new Thread(new Runnable() {
				public void run() {
					IntVersionUser intVersionUser = new IntVersionUser();
					long id = random.nextLong();
					intVersionUser.setId(id);
					int version = random.nextInt();
					intVersionUser.setName("改前" + version);
					intVersionUser.setVersion(version);
					intVersionUserMapper.insert(intVersionUser);
					intVersionUser.setName("改后" + version);
					intVersionUserMapper.updateById(intVersionUser);
					Assert.assertTrue(intVersionUserMapper.selectById(id).getVersion() == version + 1);
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
		// null条件
		intVersionUserMapper.update(versionUser, null);
		Assert.assertTrue(Objects.equals(intVersionUserMapper.selectById(2).getVersion(), originVersion));
		// 空条件
		intVersionUserMapper.update(versionUser, new EntityWrapper<IntVersionUser>());
		Assert.assertTrue(Objects.equals(intVersionUserMapper.selectById(2).getVersion(), originVersion));
		// 正常查询不带version
		IntVersionUser wrapper = new IntVersionUser();
		wrapper.setName("lisi");
		intVersionUserMapper.update(versionUser, new EntityWrapper<>(wrapper));
		Assert.assertTrue(intVersionUserMapper.selectById(2).getVersion() == originVersion + 1);
		// 原始条件带version按原始逻辑走
		IntVersionUser wrapper2 = new IntVersionUser();
		wrapper2.setName("lisi");
		wrapper2.setVersion(originVersion + 1);
		intVersionUserMapper.update(versionUser, new EntityWrapper<>(wrapper2));
		Assert.assertTrue(intVersionUserMapper.selectById(1).getVersion() == originVersion + 1);

	}
}
