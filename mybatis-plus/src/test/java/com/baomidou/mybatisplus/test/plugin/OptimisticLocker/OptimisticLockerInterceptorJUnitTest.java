package com.baomidou.mybatisplus.test.plugin.OptimisticLocker;

import java.io.Reader;
import java.sql.Connection;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.BeforeClass;
import org.junit.Test;

import com.baomidou.mybatisplus.MybatisSessionFactoryBuilder;
import com.baomidou.mybatisplus.test.plugin.OptimisticLocker.entity.VersionUser;
import com.baomidou.mybatisplus.test.plugin.OptimisticLocker.mapper.VersionUserMapper;

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
	public void baseVersion() {
		SqlSession sqlSession = sqlSessionFactory.openSession();
		VersionUserMapper mapper = sqlSession.getMapper(VersionUserMapper.class);
		VersionUser versionUser = mapper.selectById(1);
		mapper.update(versionUser, null);
		sqlSession.close();
	}

}
