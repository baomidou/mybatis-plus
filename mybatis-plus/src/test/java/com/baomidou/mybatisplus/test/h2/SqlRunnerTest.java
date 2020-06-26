/*
 * Copyright (c) 2011-2019, hubin (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.test.h2;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.SqlRunner;
import com.baomidou.mybatisplus.test.h2.entity.H2Student;
import com.baomidou.mybatisplus.test.h2.service.IH2StudentService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * SqlRunner测试
 * @author nieqiurong 2018/8/25 11:05.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:h2/spring-test-h2.xml"})
class SqlRunnerTest {

    @Autowired
    private IH2StudentService studentService;

    @Test
    @Order(3)
    void testSelectCount(){
        int count = SqlRunner.db().selectCount("select count(1) from h2student");
        Assertions.assertTrue(count > 0);
        count = SqlRunner.db().selectCount("select count(1) from h2student where id > {0}",0);
        Assertions.assertTrue(count > 0);
        count = SqlRunner.db(H2Student.class).selectCount("select count(1) from h2student");
        Assertions.assertTrue(count > 0);
        count = SqlRunner.db(H2Student.class).selectCount("select count(1) from h2student where id > {0}",0);
        Assertions.assertTrue(count > 0);
    }

    @Test
    @Transactional
    @Order(1)
    void testInsert(){
        Assertions.assertTrue(SqlRunner.db().insert("INSERT INTO h2student ( name, age ) VALUES ( {0}, {1} )","测试学生",2));
        Assertions.assertTrue(SqlRunner.db(H2Student.class).insert("INSERT INTO h2student ( name, age ) VALUES ( {0}, {1} )","测试学生2",3));
    }

    @Test
    @Order(2)
    void testTransactional(){
        try {
            studentService.testSqlRunnerTransactional();
        } catch (RuntimeException e){
            List<H2Student> list = studentService.list(new QueryWrapper<H2Student>().like("name", "sqlRunnerTx"));
            Assertions.assertTrue(CollectionUtils.isEmpty(list));
        }
    }

    @Test
    void testSelectPage() {
        IPage page1 = SqlRunner.db().selectPage(new Page(1, 3), "select * from h2student");
        Assertions.assertEquals(page1.getRecords().size(), 3);
        IPage page2 = SqlRunner.db().selectPage(new Page(1, 3), "select * from h2student where id >= {0}", 0);
        Assertions.assertEquals(page2.getRecords().size(), 3);
        IPage page3 = SqlRunner.db().selectPage(new Page(1, 3), "select * from h2student where id = {0}", 10086);
        Assertions.assertEquals(page3.getRecords().size(), 0);
    }
}
