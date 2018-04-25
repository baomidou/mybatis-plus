package com.baomidou.mybatisplus.test.h2;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.SqlRunner;
import com.baomidou.mybatisplus.test.h2.base.H2Test;
import com.baomidou.mybatisplus.test.h2.entity.mapper.H2UserMapper;
import com.baomidou.mybatisplus.test.h2.service.IH2UserService;

/**
 * <p>
 * Mybatis Plus H2 Junit Test
 * </p>
 *
 * @author Caratacus
 * @date 2017/4/1
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:h2/spring-test-h2.xml"})
public class H2SqlRunnerTest extends H2Test {

    @Autowired
    private IH2UserService userService;

    @Autowired
    H2UserMapper userMapper;

    @BeforeClass
    public static void initDB() throws SQLException, IOException {
        @SuppressWarnings("resource")
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:h2/spring-test-h2.xml");
        DataSource ds = (DataSource) context.getBean("dataSource");
        try (Connection conn = ds.getConnection()) {
            initData(conn);
        }
    }

    @Test
    public void testSelect() {
        List<Map<String, Object>> list = SqlRunner.db().selectList("select * from h2user where test_type={0}", 1);
        for (Map<String, Object> map : list) {
            System.out.println(JSONObject.toJSON(map));
        }
    }

    @Test
    public void testInsert() {
        SqlRunner.db().insert("insert into h2user(name,age,test_type,test_date,version) values ({0},{1},{2},{3},{4})",
            "Kshen", 19, 1, null, 1);
        Object obj = SqlRunner.db().selectObj("select name,test_id from h2user where name={0}", "Kshen");
        Assert.assertNotNull(obj);
        Assert.assertEquals("only return first column's value", "Kshen", obj);
        System.out.println(obj);
    }

    @Test
    public void testUpdate() {
        List<Map<String, Object>> list = SqlRunner.db().selectList("select * from h2user where test_type={0}", 1);
        Map<String, Object> user1 = list.get(0);
        System.out.println(JSONObject.toJSON(user1));
        Long testId = (Long) user1.get("TEST_ID");
        String name = (String) user1.get("NAME");
        Assert.assertNotNull(testId);
        Assert.assertNotNull(name);
        SqlRunner.db().update("update h2user set name={0} where test_id={1}", "Kshen", testId);
        list = SqlRunner.db().selectList("select * from h2user where test_id={0}", testId);
        user1 = list.get(0);
        System.out.println(JSONObject.toJSON(user1));
        Assert.assertNotEquals("name should be updated", name, user1.get("NAME"));
    }

    @Test
    public void testDelete() {
        List<Map<String, Object>> list = SqlRunner.db().selectList("select * from h2user where test_type={0}", 1);
        Map<String, Object> user1 = list.get(0);
        System.out.println(JSONObject.toJSON(user1));
        Long testId = (Long) user1.get("TEST_ID");
        SqlRunner.db().delete("delete from h2user where test_id={0}", testId);
        list = SqlRunner.db().selectList("select * from h2user where test_id={0}", testId);
        Assert.assertTrue("this record should be deleted", list == null || list.isEmpty());
    }

    @Test
    public void testSelectObjs() {
        List<Object> list = SqlRunner.db().selectObjs("select name,test_id from h2user");
        Assert.assertTrue("only return first column value", list.get(0) instanceof String);

        list = SqlRunner.db().selectObjs("select test_id,name from h2user");
        Assert.assertTrue("only return first column value", list.get(0) instanceof Long);
    }

}
