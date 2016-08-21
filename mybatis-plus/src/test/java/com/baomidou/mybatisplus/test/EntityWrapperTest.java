/**
 * Copyright (c) 2011-2020, hubin (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR EntityWrapperS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.test;

import org.junit.Assert;
import org.junit.Test;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.test.mysql.entity.User;

/**
 * <p>
 * 条件查询测试
 * </p>
 *
 * @author hubin
 * @date 2016-08-19
 */
public class EntityWrapperTest {

	/*
	 * User 查询包装器
	 */
	private EntityWrapper<User> ew = new EntityWrapper<User>();

	@Test
	public void test() {
		/*
		 * 无条件测试
		 */
		Assert.assertNull(ew.getSqlSegment());
	}

	@Test
	public void test11() {
		ew.setEntity(new User(1));
		ew.where("name={0}", "'123'").andIfNeed(false, "id=12");
		String sqlSegment = ew.getSqlSegment();
		System.err.println("test11 = " + sqlSegment);
		Assert.assertEquals(" AND name='123'", sqlSegment);
	}

	@Test
	public void test12() {
		ew.setEntity(new User(1));
		ew.where("name={0}", "'123'").orderBy("id", false);
		String sqlSegment = ew.getSqlSegment();
		System.err.println("test12 = " + sqlSegment);
		Assert.assertEquals(" AND name='123' ORDER BY id DESC ", sqlSegment);
	}

	@Test
	public void test13() {
		/*
		 * 存在实体查询，只排序
		 */
		ew.setEntity(new User(1));
		ew.orderBy("id", false);
		String sqlSegment = ew.getSqlSegment();
		System.err.println("test13 = " + sqlSegment);
		Assert.assertEquals(" ORDER BY id DESC ", sqlSegment);
	}

	@Test
	public void test21() {
		ew.where("name={0}", "'123'").andIfNeed(false, "id=1").orderBy("id");
		String sqlSegment = ew.getSqlSegment();
		System.err.println("test21 = " + sqlSegment);
		Assert.assertEquals(" WHERE name='123' ORDER BY id", sqlSegment);
	}

	@Test
	public void test22() {
		ew.where("name={0}", "'123'").orderBy("id", false);
		String sqlSegment = ew.getSqlSegment();
		System.err.println("test22 = " + sqlSegment);
		Assert.assertEquals(" WHERE name='123' ORDER BY id DESC ", sqlSegment);
	}

	@Test
	public void test23() {
		/*
		 * 无实体查询，只排序
		 */
		ew.orderBy("id", false);
		String sqlSegment = ew.getSqlSegment();
		System.err.println("test23 = " + sqlSegment);
		Assert.assertEquals(" ORDER BY id DESC ", sqlSegment);
	}

	@Test
	public void testBlend() {
		/*
		 * 实体查询，混合 SQL 原样输出
		 */
		ew.setEntity(new User(1));
		ew.addFilter("name={0}", "'123'").orderBy("id", false);
		String sqlSegment = ew.getSqlSegment();
		System.err.println("testBlend = " + sqlSegment);
		Assert.assertEquals(" AND name='123' ORDER BY id DESC ", sqlSegment);
	}

	@Test
	public void testNoTSQL() {
		/*
		 * 非 T-SQL 实体查询
		 */
		ew.setEntity(new User(1));
		ew.addFilter("name={0}", "'123'").addFilterIfNeed(true, " order by id");
		String sqlSegment = ew.getSqlSegment();
		System.err.println("testNoTSQL = " + sqlSegment);
		Assert.assertEquals(" AND name='123' order by id", sqlSegment);
	}

	@Test
	public void testNoTSQL1() {
		/*
		 * 非 T-SQL 无实体查询
		 */
		ew.addFilter("name={0}", "'123'").addFilterIfNeed(false, " order by id");
		String sqlSegment = ew.getSqlSegment();
		System.err.println("testNoTSQL1 = " + sqlSegment);
		Assert.assertEquals(" WHERE name='123'", sqlSegment);
	}

}
