package com.baomidou.mybatisplus.test;

import org.junit.Assert;
import org.junit.Test;

import com.baomidou.mybatisplus.plugins.entity.CountOptimize;
import com.baomidou.mybatisplus.toolkit.SqlUtils;

/**
 * <p>
 * 测试SqlUtils工具类
 * </p>
 *
 * @author Caratacus
 * @Date 2016-11-3
 */
public class SqlUtilsTest {
	/**
	 * 测试不规则Order by
	 */
	@Test
	public void test1() {
		String sql = "select * from test orDer    \r\n   by name";
		CountOptimize countOptimize = SqlUtils.getCountOptimize(sql, true);
		Assert.assertEquals("CountOptimize{orderBy=false, countSQL='SELECT COUNT(1) AS TOTAL from test '}",
				countOptimize.toString());

	}

	/**
	 * 测试distinct 如果存在不优化count sql
	 */
	@Test
	public void test2() {
		String sql = "select distinct name from test orDer       by name";
		CountOptimize countOptimize = SqlUtils.getCountOptimize(sql, true);
		Assert.assertEquals("CountOptimize{orderBy=true, countSQL='SELECT COUNT(1) AS TOTAL FROM (select distinct name from test orDer       by name) A'}",
				countOptimize.toString());

	}

	/**
	 * 测试没有from的情况 并且有格式化时间的情况
	 */
	@Test
	public void test3() {
		String sql = "select DATE_FORMAT('2016-05-01 18:31:33','%Y-%m-%d')";
		CountOptimize countOptimize = SqlUtils.getCountOptimize(sql, true);
		Assert.assertEquals(
				"CountOptimize{orderBy=true, countSQL='SELECT COUNT(1) AS TOTAL FROM (select DATE_FORMAT('2016-05-01 18:31:33','%Y-%m-%d')) A'}",
				countOptimize.toString());

	}

	/**
	 * 测试没有order by的情况
	 */
	@Test
	public void test4() {
		String sql = "select DATE_FORMAT('2016-05-01 18:31:33','%Y-%m-%d') from test";
		CountOptimize countOptimize = SqlUtils.getCountOptimize(sql, true);
		Assert.assertEquals("CountOptimize{orderBy=true, countSQL='SELECT COUNT(1) AS TOTAL from test'}",
				countOptimize.toString());

	}

}
