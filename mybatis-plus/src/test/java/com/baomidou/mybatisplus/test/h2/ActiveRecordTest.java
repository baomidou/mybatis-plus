package com.baomidou.mybatisplus.test.h2;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.test.h2.entity.persistent.H2Student;
import com.baomidou.mybatisplus.test.h2.service.IH2StudentService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
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
// TODO junit 5.4 开始提供支持，预计 2019-02-06 发布，等这之后升级版本并使用 @TestMethodOrder 代替 @FixMethodOrder
// @FixMethodOrder(MethodSorters.JVM)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:h2/spring-test-h2.xml"})
public class ActiveRecordTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActiveRecordTest.class);

    @Autowired
    private IH2StudentService h2StudentService;

    @Test
    @Transactional
    public void testInsert() {
        H2Student student = new H2Student(null, "测试学生", 2);
        Assertions.assertTrue(student.insert());
        Assertions.assertTrue(student.insert());
    }

    @Test
    public void testUpdate() {
        H2Student student = new H2Student(1L, "Tom长大了", 2);
        Assertions.assertTrue(student.updateById());
        student.setName("不听话的学生");
        Assertions.assertTrue(student.update(new QueryWrapper<H2Student>().gt("id", 10)));
    }

    @Test
    public void testSelect() {
        H2Student student = new H2Student();
        student.setId(1L);
        Assertions.assertNotNull(student.selectById());
        Assertions.assertNotNull(student.selectById(1L));
    }

    @Test
    public void testSelectList() {
        H2Student student = new H2Student();
        List<H2Student> students = student.selectList(new QueryWrapper<>(student));
        students.forEach($this -> LOGGER.info("用户信息:{}", $this));
        Assertions.assertTrue(students.size() > 1);
    }

    @Test
    public void testSelectPage() {
        IPage<H2Student> page = new Page<>(1, 10);
        H2Student student = new H2Student();
        page = student.selectPage(page, new QueryWrapper<>(student));
        List<H2Student> records = page.getRecords();
        LOGGER.info("总数:{}", page.getTotal());
        records.forEach($this -> LOGGER.info("用户信息:{}", $this));
        Assertions.assertTrue(page.getTotal() > 1);
    }

    @Test
    public void testSelectCount() {
        H2Student student = new H2Student();
        int count = new H2Student().selectCount(new QueryWrapper<>(student));
        LOGGER.info("count:{}", count);
        Assertions.assertTrue(count > 1);
    }

    @Test
    public void testInsertOrUpdate() {
        H2Student student = new H2Student(2L, "Jerry也长大了", 2);
        Assertions.assertTrue(student.insertOrUpdate());
        student.setId(null);
        Assertions.assertTrue(student.insertOrUpdate());
    }

    @Test
    public void testSelectAll() {
        H2Student student = new H2Student();
        List<H2Student> students = student.selectAll();
        Assertions.assertNotNull(students);
        students.forEach($this -> LOGGER.info("用户信息:{}", $this));
    }

    @Test
    public void testSelectOne() {
        H2Student student = new H2Student();
        Assertions.assertNotNull(student.selectOne(new QueryWrapper<>()));
    }

    @Test
    public void testTransactional() {
        try {
            h2StudentService.testTransactional();
        } catch (MybatisPlusException e) {
            List<H2Student> students = new H2Student().selectList(new QueryWrapper<H2Student>().lambda().like(H2Student::getName, "tx"));
            Assertions.assertTrue(CollectionUtils.isEmpty(students));
        }
    }

    @Test
    public void testDelete() {
        H2Student student = new H2Student();
        student.setId(2L);
        Assertions.assertTrue(student.deleteById());
        Assertions.assertTrue(student.deleteById(12L));
        Assertions.assertTrue(student.delete(new QueryWrapper<H2Student>().gt("id", 10)));
    }
}
