package com.baomidou.mybatisplus.test.h2;


import java.util.List;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.internal.matchers.GreaterThan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.toolkit.SqlRunner;
import com.baomidou.mybatisplus.test.h2.entity.persistent.H2Student;
import com.baomidou.mybatisplus.test.h2.service.IH2StudentService;
import org.springframework.transaction.annotation.Transactional;


/**
 *  SqlRunner测试
 * @author nieqiurong 2018/8/25 11:05.
 */
@FixMethodOrder(MethodSorters.JVM)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:h2/spring-test-h2.xml"})
public class SqlRunnerTest {

    @Autowired
    private IH2StudentService studentService;

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
    @Transactional
    public void testInsert(){
        Assert.assertTrue(SqlRunner.db().insert("INSERT INTO h2student ( name, age ) VALUES ( {0}, {1} )","测试学生",2));
        Assert.assertTrue(SqlRunner.db(H2Student.class).insert("INSERT INTO h2student ( name, age ) VALUES ( {0}, {1} )","测试学生2",3));
    }

    @Test
    public void testTransactional(){
        try {
            studentService.testSqlRunnerTransactional();
        } catch (RuntimeException e){
            List<H2Student> list = studentService.list(new QueryWrapper<H2Student>().like("name", "sqlRunnerTx"));
            Assert.assertTrue(CollectionUtils.isEmpty(list));
        }
    }
}
