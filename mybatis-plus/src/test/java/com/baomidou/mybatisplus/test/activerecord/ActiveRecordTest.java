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
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.test.activerecord;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;

import com.baomidou.mybatisplus.MybatisSessionFactoryBuilder;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.mapper.SqlMapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.baomidou.mybatisplus.test.mysql.TestMapper;
import com.baomidou.mybatisplus.test.mysql.entity.Test;
import com.baomidou.mybatisplus.toolkit.TableInfoHelper;

/**
 * <p>
 * ActiveRecord 测试
 * </p>
 *
 * @author Caratacus
 * @date 2016-10-11
 */
public class ActiveRecordTest {

	public static void main(String[] args) {
		// 加载配置文件
		InputStream in = TestMapper.class.getClassLoader().getResourceAsStream("mysql-config.xml");
		MybatisSessionFactoryBuilder mf = new MybatisSessionFactoryBuilder();
		SqlSessionFactory sqlSessionFactory = mf.build(in);
		TableInfoHelper.cacheSqlSessionFactory(sqlSessionFactory);
		SqlMapper mapper = Model.mapper(Test.class);
		boolean rlt = mapper.insert("insert into user (test_id, name) values (1, 'test1'),(2, 'test2')");
		System.err.println("insert:" + rlt);
		List<Map<String, Object>> maps = mapper.selectList("select * from user");
		System.out.println(maps);
		maps = mapper.selectList("select * from user", new Pagination(0, 10));
		System.out.println("page:" + maps);
		rlt = mapper.delete("delete from user where test_id in (1,2)");
		System.err.println("insert:" + rlt);

		List<Test> ts = new Test().all();
		if (null != ts) {
			System.out.println(ts.get(0).getType());
		}
	}
}
