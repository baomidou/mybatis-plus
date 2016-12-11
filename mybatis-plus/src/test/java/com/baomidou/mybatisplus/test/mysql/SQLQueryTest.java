/**
 * Copyright (c) 2011-2014, hubin (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.test.mysql;

import com.baomidou.mybatisplus.MybatisSessionFactoryBuilder;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.query.SQLQuery;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.toolkit.TableInfoHelper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 测试没有XML同样注入CRUD SQL
 * </p>
 *
 * @author Caratacus
 * @date 2016-09-26
 */
public class SQLQueryTest {

	@Test
	public void test1() {
		/*
		 * 加载配置文件
		 */
		InputStream in = SQLQueryTest.class.getClassLoader().getResourceAsStream("mysql-config.xml");
		MybatisSessionFactoryBuilder mf = new MybatisSessionFactoryBuilder();
		SqlSessionFactory sessionFactory = mf.build(in);
		TableInfoHelper.initSqlSessionFactory(sessionFactory);

		boolean b = SQLQuery.create().insertSql("INSERT INTO `test` (`id`, `type`) VALUES ('107880983085826048', 't1021')");
		System.out.println(b);
		Assert.assertTrue(b);
		boolean b1 = SQLQuery.create().updateSql("UPDATE `test` SET `type`='tttttttt' WHERE (`id`=107880983085826048)");
		System.out.println(b1);

		Assert.assertTrue(b1);
		List<Map<String, Object>> maps = SQLQuery.create().selectListSql("select * from test WHERE (`id`=107880983085826048)");
		System.out.println(maps);
		String type = (String) maps.get(0).get("type");
		System.out.println(type);
		Assert.assertEquals("tttttttt", type);
		boolean b2 = SQLQuery.create().deleteSql("DELETE from test WHERE (`id`=107880983085826048)");
		System.out.println(b2);
		Assert.assertTrue(b2);
		List<Map<String, Object>> maps1 = SQLQuery.create().selectListSql("select * from test WHERE (`id`=107880983085826048)");
		System.out.println(maps1);
		if (CollectionUtils.isEmpty(maps1)) {
			maps1 = null;
		}
		Assert.assertNull(maps1);
        Page page = new Page(1,5);
        Page<Map<String, Object>> mapPage = SQLQuery.create().selectPageSql(page, "select * from test ");
        System.out.println(mapPage);

    }

}
