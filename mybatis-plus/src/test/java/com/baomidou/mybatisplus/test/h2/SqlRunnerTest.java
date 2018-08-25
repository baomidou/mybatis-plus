package com.baomidou.mybatisplus.test.h2;


import java.io.IOException;
import java.sql.SQLException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.matchers.GreaterThan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import com.baomidou.mybatisplus.extension.toolkit.SqlRunner;
import com.baomidou.mybatisplus.test.h2.config.H2Db;
import com.baomidou.mybatisplus.test.h2.entity.persistent.H2Student;


/**
 *  SqlRunner测试
 * @author nieqiurong 2018/8/25 11:05.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:h2/spring-test-h2.xml"})
public class SqlRunnerTest {

    @BeforeClass
    public static void InitDB() throws SQLException, IOException {
        H2Db.initH2Student();
    }

    @Test
    public void testSelectCount(){
        int count = SqlRunner.db().selectCount("select count(1) from h2student");
        Assert.assertThat(count, new GreaterThan<>(0));
        count = SqlRunner.db().selectCount("select count(1) from h2student where id > {0}",0);
        Assert.assertThat(count, new GreaterThan<>(0));
        count = SqlRunner.db(H2Student.class).selectCount("select count(1) from h2student");
        Assert.assertThat(count, new GreaterThan<>(0));
        count = SqlRunner.db(H2Student.class).selectCount("select count(1) from h2student where id > {0}",0);
        Assert.assertThat(count, new GreaterThan<>(0));
    }

    @Test
    public void testInsert(){
        Assert.assertTrue(SqlRunner.db().insert("INSERT INTO h2student ( name, age ) VALUES ( {0}, {1} )","测试学生",2));
        Assert.assertTrue(SqlRunner.db(H2Student.class).insert("INSERT INTO h2student ( name, age ) VALUES ( {0}, {1} )","测试学生2",3));
    }
}
