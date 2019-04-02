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
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.test.h2.entity.H2Student;
import com.baomidou.mybatisplus.test.h2.service.IH2StudentService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * ActiveRecord 测试
 *
 * @author nieqiurong 2018/7/27.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:h2/spring-test-h2.xml"})
class ActiveRecordTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActiveRecordTest.class);

    @Autowired
    private IH2StudentService h2StudentService;

    @Test
    @Transactional
    @Order(1)
    void testInsert() {
        H2Student student = new H2Student(null, "测试学生", 2);
        Assertions.assertTrue(student.insert());
        Assertions.assertTrue(student.insert());
    }

    @Test
    @Order(2)
    void testUpdate() {
        H2Student student = new H2Student(1L, "Tom长大了", 2);
        Assertions.assertTrue(student.updateById());
        student.setName("不听话的学生");
        Assertions.assertTrue(student.update(new QueryWrapper<H2Student>().gt("id", 10)));
    }

    @Test
    @Order(10)
    void testSelect() {
        H2Student student = new H2Student();
        student.setId(1L);
        Assertions.assertNotNull(student.selectById());
        Assertions.assertNotNull(student.selectById(1L));
    }

    @Test
    @Order(10)
    void testSelectList() {
        H2Student student = new H2Student();
        List<H2Student> students = student.selectList(new QueryWrapper<>(student));
        students.forEach($this -> LOGGER.info("用户信息:{}", $this));
        Assertions.assertTrue(students.size() > 1);
    }

    @Test
    @Order(10)
    void testSelectPage() {
        IPage<H2Student> page = new Page<>(1, 10);
        H2Student student = new H2Student();
        page = student.selectPage(page, new QueryWrapper<>(student));
        List<H2Student> records = page.getRecords();
        LOGGER.info("总数:{}", page.getTotal());
        records.forEach($this -> LOGGER.info("用户信息:{}", $this));
        Assertions.assertTrue(page.getTotal() > 1);
    }

    @Test
    @Order(10)
    void testSelectCount() {
        H2Student student = new H2Student();
        int count = new H2Student().selectCount(new QueryWrapper<>(student));
        LOGGER.info("count:{}", count);
        Assertions.assertTrue(count > 1);
    }

    @Test
    @Order(20)
    void testInsertOrUpdate() {
        H2Student student = new H2Student(2L, "Jerry也长大了", 2);
        Assertions.assertTrue(student.insertOrUpdate());
        student.setId(null);
        Assertions.assertTrue(student.insertOrUpdate());
    }

    @Test
    @Order(21)
    void testSelectAll() {
        H2Student student = new H2Student();
        List<H2Student> students = student.selectAll();
        Assertions.assertNotNull(students);
        students.forEach($this -> LOGGER.info("用户信息:{}", $this));
    }

    @Test
    @Order(21)
    void testSelectOne() {
        H2Student student = new H2Student();
        Assertions.assertNotNull(student.selectOne(new QueryWrapper<>()));
    }

    @Test
    @Order(21)
    void testTransactional() {
        try {
            h2StudentService.testTransactional();
        } catch (MybatisPlusException e) {
            List<H2Student> students = new H2Student().selectList(new QueryWrapper<H2Student>().lambda().like(H2Student::getName, "tx"));
            Assertions.assertTrue(CollectionUtils.isEmpty(students));
        }
    }

    @Test
    @Order(Integer.MAX_VALUE)
    void testDelete() {
        H2Student student = new H2Student();
        student.setId(2L);
        Assertions.assertTrue(student.deleteById());
        Assertions.assertTrue(student.deleteById(12L));
        Assertions.assertTrue(student.delete(new QueryWrapper<H2Student>().gt("id", 10)));
    }
}
