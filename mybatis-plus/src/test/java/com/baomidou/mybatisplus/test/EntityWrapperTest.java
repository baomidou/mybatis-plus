/**
 * Copyright (c) 2011-2020, hubin (jobob@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
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
	public void test1() {
		ew.setEntity(new User(1));
		ew.addFilter("name={0}", "'123'").addFilterIfNeed(false, " ORDER BY id");
		String sqlSegment = ew.getSqlSegment();
		System.err.println(sqlSegment);
		Assert.assertEquals(" AND name='123'", sqlSegment);
	}

	@Test
	public void test2() {
		ew.setEntity(new User(1));
		ew.addFilter("name={0} order by id desc", "'123'");
		String sqlSegment = ew.getSqlSegment();
		System.err.println(sqlSegment);
		Assert.assertEquals(" AND name='123' order by id desc", sqlSegment);
	}

	@Test
	public void test3() {
		ew.setEntity(new User(1));
		ew.addFilter(" Order By id desc");
		String sqlSegment = ew.getSqlSegment();
		System.err.println(sqlSegment);
		Assert.assertEquals(" Order By id desc", sqlSegment);
	}

	@Test
	public void test21() {
		ew.addFilter("name={0}", "'123'").addFilterIfNeed(false, " ORDER BY id");
		String sqlSegment = ew.getSqlSegment();
		System.err.println(sqlSegment);
		Assert.assertEquals(" WHERE name='123'", sqlSegment);
	}

	@Test
	public void test22() {
		ew.addFilter("name={0} order by id desc", "'123'");
		String sqlSegment = ew.getSqlSegment();
		System.err.println(sqlSegment);
		Assert.assertEquals(" WHERE name='123' order by id desc", sqlSegment);
	}

	@Test
	public void test23() {
		ew.addFilter(" Order By id desc");
		String sqlSegment = ew.getSqlSegment();
		System.err.println(sqlSegment);
		Assert.assertEquals(" Order By id desc", sqlSegment);
	}

}
