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
import com.baomidou.mybatisplus.incrementer.OracleKeyGenerator;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.test.oracle.entity.TestSequser;
import com.baomidou.mybatisplus.test.oracle.mapper.TestSequserMapper;


/**
 * <p>
 * oracle sequence 测试类
 * </p>
 *
 * @author zashitou
 * @Date 2017-04-20
 */
public class TestSequserMapperTest {


    /**
     * Test Oracle Sequence
     */
    public static void main(String[] args) {

        //加载配置文件
        InputStream in = TestSequserMapperTest.class.getClassLoader().getResourceAsStream("oracle-config.xml");

        MybatisSessionFactoryBuilder mf = new MybatisSessionFactoryBuilder();

        /** 设置数据库类型为 oracle */
        GlobalConfiguration gc = new GlobalConfiguration();
        gc.setDbType("oracle");
        gc.setDbColumnUnderline(true);
        gc.setKeyGenerator(new OracleKeyGenerator());
        mf.setGlobalConfig(gc);
        SqlSessionFactory sessionFactory = mf.build(in);
        SqlSession session = sessionFactory.openSession();
        TestSequserMapper testSequserMapper = session.getMapper(TestSequserMapper.class);
        /**
         * 插入
         */
        TestSequser one = new TestSequser("abc", 18, 1);
        int rlt = testSequserMapper.insert(one);
        System.err.println("\n one.id-------:" + one.getId());
//        sleep();

        /**
         * 批量插入
         */
        List<TestSequser> ul = new ArrayList<>();
        ul.add(new TestSequser("abc2", 18, 2));
        ul.add(new TestSequser("abc3", 18, 3));
        ul.add(new TestSequser("abc4", 18, 4));
        ul.add(new TestSequser("abc5", 18, 5));
        ul.add(new TestSequser("abc6", 18, 6));
        for (TestSequser u : ul) {
            rlt = testSequserMapper.insert(u);
        }
//        for (TestSequser u : ul) {
//            System.err.println("\n one.id-------:" + u.getId());
//        }

        System.out.println("********************");
        List<TestSequser> testList = testSequserMapper.getList();
        for (TestSequser u : testList) {
            System.out.println(u);
        }

        testList = testSequserMapper.selectList(new EntityWrapper<TestSequser>());
        for (TestSequser u : testList) {
            System.out.println(u);
        }

        /**
         * 提交
         */
        session.commit();
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
