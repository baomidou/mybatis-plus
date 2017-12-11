package com.baomidou.mybatisplus.test.h2;

import java.io.IOException;
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

import com.baomidou.mybatisplus.test.h2.base.H2Test;
import com.baomidou.mybatisplus.test.h2.entity.mapper.H2uuidMapper;
import com.baomidou.mybatisplus.test.h2.entity.persistent.H2uuid;

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
public class H2uuidTest extends H2Test {

    @Autowired
    private H2uuidMapper uuidMapper;

    @BeforeClass
    public static void initDB() throws SQLException, IOException {
        @SuppressWarnings("resource")
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:h2/spring-test-h2.xml");
        DataSource ds = (DataSource) context.getBean("dataSource");
        try (Connection conn = ds.getConnection()) {
            Statement stmt = conn.createStatement();
            stmt.execute(readFile("uuid.sql"));
            stmt.execute("truncate table h2uuid");
            conn.commit();
        }
    }

    @Test
    public void testUuid() {
        H2uuid h2uuid = new H2uuid("3");
        Assert.assertEquals(1, uuidMapper.insert(h2uuid).intValue());
        Assert.assertTrue(h2uuid.getId().length() == 32);
    }

}
