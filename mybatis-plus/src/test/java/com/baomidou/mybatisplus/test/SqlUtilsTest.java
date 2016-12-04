package com.baomidou.mybatisplus.test;

import org.junit.Assert;
import org.junit.Test;

import com.baomidou.mybatisplus.entity.CountOptimize;
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
	 * 测试select的count语句 复杂 orderby
	 */
	@Test
	public void sqlCountOptimize1() {

		CountOptimize countOptimize = SqlUtils
				.getCountOptimize(
						"select * from user a left join (select uuid from user2) b on b.id = a.aid where a=1 order by (select 1 from dual)",
						"jsqlparser", "mysql", true);
		String countsql = countOptimize.getCountSQL();
		System.out.println(countsql);
		Assert.assertEquals("SELECT COUNT(1) FROM user a LEFT JOIN (SELECT uuid FROM user2) b ON b.id = a.aid WHERE a = 1",
				countsql);
	}

	/**
	 * 测试select的count语句 distinct
	 */
	@Test
	public void sqlCountOptimize2() {
		CountOptimize countOptimize = SqlUtils
				.getCountOptimize(
						"select distinct * from user a left join (select uuid from user2) b on b.id = a.aid where a=1 order by (select 1 from dual)",
						"jsqlparser", "mysql", true);
		String countsql = countOptimize.getCountSQL();
		System.out.println(countsql);
		Assert.assertEquals(
				"SELECT COUNT(1) FROM ( SELECT DISTINCT * FROM user a LEFT JOIN (SELECT uuid FROM user2) b ON b.id = a.aid WHERE a = 1 )",
				countsql);
	}

	/**
	 * 测试select的count语句 group by
	 */
	@Test
	public void sqlCountOptimize3() {
		CountOptimize countOptimize = SqlUtils
				.getCountOptimize(
						"select * from user a left join (select uuid from user2) b on b.id = a.aid where a=1 group by a.id order by (select 1 from dual)",
						"jsqlparser", "mysql", true);
		String countsql = countOptimize.getCountSQL();
		System.out.println(countsql);
		Assert.assertEquals(
				"SELECT COUNT(1) FROM ( SELECT * FROM user a LEFT JOIN (SELECT uuid FROM user2) b ON b.id = a.aid WHERE a = 1 GROUP BY a.id ORDER BY (SELECT 1 FROM dual) )",
				countsql);
	}

}
