package com.baomidou.mybatisplus.test.h2;

import com.baomidou.mybatisplus.test.h2.config.H2Db;
import com.baomidou.mybatisplus.test.h2.entity.mapper.H2StudentMapper;
import com.baomidou.mybatisplus.test.h2.entity.persistent.H2Student;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import java.io.IOException;
import java.sql.SQLException;


/**
 *  ActiveRecord 测试
 * @author nieqiurong 2018/7/27.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:h2/spring-test-h2.xml"})
public class ActiveRecordTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActiveRecordTest.class);

    @Autowired
    private H2StudentMapper studentMapper;

    @BeforeClass
    public static void InitDB() throws SQLException, IOException {
        H2Db.initH2Student();
    }

    @Test
    public void testInsert() {
        H2Student student = new H2Student(null, "测试学生", 2);
        Assert.assertTrue(student.insert());
    }

}
