package com.baomidou.mybatisplus.test.h2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

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

import com.baomidou.mybatisplus.test.h2.entity.mapper.H2UserMetaobjMapper;
import com.baomidou.mybatisplus.test.h2.entity.persistent.H2UserMetaObj;

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
public class H2UserAutoincrementTest {

    @Autowired
    private H2UserMetaobjMapper userMapper;

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
        String filePath = H2UserAutoincrementTest.class.getClassLoader().getResource("").getPath() + "/h2/" + filename;
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
        String filePath = H2UserAutoincrementTest.class.getClassLoader().getResource("").getPath() + "/h2/" + filename;
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
        H2UserMetaObj user = new H2UserMetaObj();
        user.setAge(1);
        user.setPrice(new BigDecimal("9.99"));
        Assert.assertEquals(1, userMapper.insert(user).intValue());
        Long id1 = user.getId();
        Assert.assertNotNull(id1);
        user.setDesc("Caratacus");
        Assert.assertEquals(1, userMapper.updateById(user).intValue());
        H2UserMetaObj userFromDB = userMapper.selectById(id1);
        Assert.assertEquals("Caratacus", userFromDB.getDesc());

        H2UserMetaObj user2 = new H2UserMetaObj();
        user2.setAge(2);
        Assert.assertEquals(1, userMapper.insert(user2).intValue());
        Long userId2 = user2.getId();
        Assert.assertEquals(id1.intValue()+1, userId2.intValue());
    }

}
