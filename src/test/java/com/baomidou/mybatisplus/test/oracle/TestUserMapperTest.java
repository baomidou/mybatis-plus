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
package com.baomidou.mybatisplus.test.oracle;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.baomidou.mybatisplus.MybatisSessionFactoryBuilder;
import com.baomidou.mybatisplus.entity.GlobalConfiguration;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.test.oracle.entity.TestUser;

/**
 * <p>
 * MybatisPlus 测试类
 * </p>
 *
 * @author hubin
 * @Date 2016-04-25
 */
public class TestUserMapperTest {


    /**
     *
     * RUN 测试（ 更多查看 MySql 测试类 ）
     *
     */
    public static void main(String[] args) {

        //加载配置文件
        InputStream in = TestUserMapperTest.class.getClassLoader().getResourceAsStream("oracle-config.xml");

		/*
         * 此处采用 MybatisSessionFactoryBuilder 构建
		 * SqlSessionFactory，目的是引入BaseMapper功能
		 */
        MybatisSessionFactoryBuilder mf = new MybatisSessionFactoryBuilder();

        /** 设置数据库类型为 oracle */
        GlobalConfiguration gc = new GlobalConfiguration();
        gc.setDbType("oracle");
        mf.setGlobalConfig(gc);

		/*
		 * 1、数据库字段驼峰命名不需要任何设置
		 * 2、当前演示是驼峰下划线混合命名
		 * 3、如下开启，表示数据库字段使用下划线命名，该设置是全局的。
		 *	 开启该设置实体可无 @TableId(value = "test_id") 字段映射
		 */
        //mf.setDbColumnUnderline(true);

        SqlSessionFactory sessionFactory = mf.build(in);
        SqlSession session = sessionFactory.openSession();
        TestUserMapper testUserMapper = session.getMapper(TestUserMapper.class);
        System.err.println(" debug run 查询执行 test_user 表数据变化！ ");
        session.delete("deleteAll");

        /**
         * 插入
         */
        int rlt = testUserMapper.insert(new TestUser("10", "abc", 18, 1));
        System.err.println("\n--------------insert-------" + rlt);
        sleep();

        /**
         * 批量插入
         */
        List<TestUser> ul = new ArrayList<>();
        ul.add(new TestUser("11", "1a", 11, 1));
        ul.add(new TestUser("12", "2a", 12, 2));
        ul.add(new TestUser("a", 1, 1));
        ul.add(new TestUser("b", 2, 2));
        ul.add(new TestUser("c", 3, 1));
        for (TestUser u : ul) {
            rlt = testUserMapper.insert(u);
        }
        System.err.println("\n--------------insertBatch-------" + rlt);
        sleep();

        /**
         * 批量更新
         */
        List<TestUser> ul1 = new ArrayList<>();
        ul1.add(new TestUser("10", "update-0a", 11, 1));
        ul1.add(new TestUser("11", "update-1a", 11, 1));
        ul1.add(new TestUser("12", "update-2a", 12, 2));
        for (TestUser u : ul1) {
            rlt = testUserMapper.updateById(u);
        }
        System.err.println("\n--------------updateBatchById-------" + rlt);
        sleep();

        System.err.println("\n------------------list 分页查询 ----查询 testType = 1 的所有数据--id--DESC--排序--------");
        Page<TestUser> page = new Page<>(1, 2);
        EntityWrapper<TestUser> ew = new EntityWrapper<>(new TestUser(1), "TEST_ID DESC");
        List<TestUser> paginList = testUserMapper.selectPage(page, ew);
        page.setRecords(paginList);
        for (int i = 0; i < page.getRecords().size(); i++) {
            print(page.getRecords().get(i));
        }
        System.err.println(" 翻页：" + page.toString());
		
		/* 删除测试数据  */
        rlt = session.delete("deleteAll");
        System.err.println("清空测试数据！ rlt=" + rlt);

        /**
         * 提交
         */
        session.commit();
    }


    /*
     * 打印测试信息
     */
    private static void print(TestUser user) {
        sleep();
        if (user != null) {
            System.out.println("\n user: id="
                    + user.getTestId() + ", name=" + user.getName() + ", age=" + user.getAge() + ", testType="
                    + user.getTestType());
        } else {
            System.out.println("\n user is null.");
        }
    }


    /*
     * 慢点打印
     */
    private static void sleep() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
