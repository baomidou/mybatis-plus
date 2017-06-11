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

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Assert;
import org.junit.Test;

import com.baomidou.mybatisplus.MybatisSessionFactoryBuilder;
import com.baomidou.mybatisplus.mapper.SqlRunner;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.toolkit.TableInfoHelper;

/**
 * <p>
 * 测试SqlRunner
 * </p>
 *
 * @author Caratacus
 * @date 2016-12-19
 */
public class SqlRunnerTest {

    @Test
    public void test1() {
        /*
		 * 加载配置文件
		 */
        InputStream in = SqlRunnerTest.class.getClassLoader().getResourceAsStream("mysql-config.xml");
        MybatisSessionFactoryBuilder mf = new MybatisSessionFactoryBuilder();
        SqlSessionFactory sessionFactory = mf.build(in);
        TableInfoHelper.initSqlSessionFactory(sessionFactory);

        boolean b = SqlRunner.db().insert("INSERT INTO `test` (`id`, `type`) VALUES ('107880983085826048', 't1021')");
        System.out.println(b);
        Assert.assertTrue(b);
        boolean b1 = SqlRunner.db().update("UPDATE `test` SET `type`='tttttttt' WHERE (`id`=107880983085826048)");
        System.out.println(b1);

        Assert.assertTrue(b1);
        List<Map<String, Object>> maps = SqlRunner.db().selectList("select * from test WHERE (`id`=107880983085826048)");
        System.out.println(maps);
        String type = (String) maps.get(0).get("type");
        System.out.println(type);
        Assert.assertEquals("tttttttt", type);
        boolean b2 = SqlRunner.db().delete("DELETE from test WHERE (`id`=107880983085826048)");
        System.out.println(b2);
        Assert.assertTrue(b2);
        List<Map<String, Object>> maps1 = SqlRunner.db().selectList("select * from test WHERE (`id`=107880983085826048)");
        System.out.println(maps1);
        if (CollectionUtils.isEmpty(maps1)) {
            maps1 = null;
        }
        Assert.assertNull(maps1);
        Page<Map<String, Object>> mapPage = SqlRunner.db().selectPage(new Page<>(1, 5), "select * from test ");
        System.out.println(mapPage);
        int i = SqlRunner.db().selectCount("select count(0) from test ");
        System.out.println("count:" + i);

    }

}
