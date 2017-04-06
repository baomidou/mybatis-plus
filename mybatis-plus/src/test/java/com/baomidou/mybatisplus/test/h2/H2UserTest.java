package com.baomidou.mybatisplus.test.h2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

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

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.test.h2.entity.persistent.H2User;
import com.baomidou.mybatisplus.test.h2.entity.service.IH2UserService;

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
public class H2UserTest {

    @Autowired
    private IH2UserService userService;

    @BeforeClass
    public static void initDB() throws SQLException, IOException {
        @SuppressWarnings("resource")
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:h2/spring-test-h2.xml");
        DataSource ds = (DataSource) context.getBean("dataSource");
        try (Connection conn = ds.getConnection()) {
            String createTableSql = readFile("user.ddl.sql");
            Statement stmt = conn.createStatement();
            stmt.execute(createTableSql);
            stmt.execute("truncate table h2user");
            insertUsers(stmt);
            conn.commit();
        }
    }

    private static void insertUsers(Statement stmt) throws SQLException, IOException {
        String filename = "user.insert.sql";
        String filePath = H2UserTest.class.getClassLoader().getResource("").getPath() + "/h2/" + filename;
        try (
                BufferedReader reader = new BufferedReader(new FileReader(filePath))
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                stmt.execute(line.replace(";", ""));
            }
        }
    }

    private static String readFile(String filename) {
        StringBuilder builder = new StringBuilder();
        String filePath = H2UserTest.class.getClassLoader().getResource("").getPath() + "/h2/" + filename;
        try (
                BufferedReader reader = new BufferedReader(new FileReader(filePath))
        ) {
            String line;
            while ((line = reader.readLine()) != null)
                builder.append(line).append(" ");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    @Test
    public void testInsert() {
        H2User user = new H2User();
        user.setAge(1);
        user.setPrice(new BigDecimal("9.99"));
        userService.insert(user);
        Assert.assertNotNull(user.getId());
        user.setDesc("Caratacus");
        userService.insertOrUpdate(user);
        H2User userFromDB = userService.selectById(user.getId());
        Assert.assertEquals("Caratacus", userFromDB.getDesc());
    }

    @Test
    public void testUpdate() {

    }

    @Test
    public void testDelete() {
        H2User user = new H2User();
        user.setAge(1);
        user.setPrice(new BigDecimal("9.99"));
        userService.insert(user);
        Long userId = user.getId();
        Assert.assertNotNull(userId);
        userService.deleteById(userId);
        Assert.assertNull(userService.selectById(userId));
    }

    @Test
    public void testSelectByid() {
        Long userId = 101L;
        Assert.assertNotNull(userService.selectById(userId));
    }

    @Test
    public void testSelectOne() {
        H2User user = new H2User();
        user.setId(105L);
        EntityWrapper<H2User> ew = new EntityWrapper<>(user);
        H2User userFromDB = userService.selectOne(ew);
        Assert.assertNotNull(userFromDB);
    }

    @Test
    public void testSelectList() {
        H2User user = new H2User();
        EntityWrapper<H2User> ew = new EntityWrapper<>(user);
        List<H2User> list = userService.selectList(ew);
        Assert.assertNotNull(list);
        Assert.assertNotEquals(0, list.size());
    }

    @Test
    public void testSelectPage() {
        Page<H2User> page = userService.selectPage(new Page<H2User>(1, 3));
        Assert.assertEquals(3, page.getRecords().size());
    }
}
