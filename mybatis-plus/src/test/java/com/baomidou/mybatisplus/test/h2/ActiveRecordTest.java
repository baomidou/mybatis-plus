package com.baomidou.mybatisplus.test.h2;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.test.h2.config.H2Db;
import com.baomidou.mybatisplus.test.h2.entity.persistent.H2Student;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;


/**
 *  ActiveRecord 测试
 * @author nieqiurong 2018/7/27.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:h2/spring-test-h2.xml"})
public class ActiveRecordTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActiveRecordTest.class);

    @BeforeClass
    public static void InitDB() throws SQLException, IOException {
        H2Db.initH2Student();
    }

    @Test
    public void testInsert() {
        H2Student student = new H2Student(null, "测试学生", 2);
        Assert.assertTrue(student.insert());
    }

    @Test
    public void testUpdate(){
        H2Student student = new H2Student(1L,"Tom长大了",2);
        Assert.assertTrue(student.updateById());
    }

    @Test
    public void testSelect(){
        H2Student student = new H2Student();
        student.setId(1L);
        LOGGER.info("student:{}",student.selectById());
    }

    @Test
    public void testDelete(){
        H2Student student = new H2Student();
        student.setId(2L);
        Assert.assertTrue(student.deleteById());
    }

    @Test
    public void testSelectList(){
        H2Student h2Student = new H2Student();
        h2Student.selectList(new QueryWrapper<>(h2Student)).forEach(student -> LOGGER.info("用户信息:{}",student));
    }

    @Test
    public void testSelectPage(){
        IPage<H2Student> page = new Page<>(1,10);
        H2Student h2Student = new H2Student();
        page = h2Student.selectPage(page, new QueryWrapper<>(h2Student));
        List<H2Student> records = page.getRecords();
        LOGGER.info("总数:{}",page.getTotal());
        records.forEach(student -> LOGGER.info("用户信息:{}",student));
    }
}
