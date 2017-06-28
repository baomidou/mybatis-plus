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

import com.baomidou.mybatisplus.test.h2.config.DBConfig;
import com.baomidou.mybatisplus.test.h2.config.MybatisPlusNoOptLockConfig;
import com.baomidou.mybatisplus.test.h2.entity.mapper.H2UserFillMapper;
import com.baomidou.mybatisplus.test.h2.entity.persistent.H2UserFill;

/**
 * <p>
 * </p>
 *
 * @author yuxiaobin
 * @date 2017/5/31
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {DBConfig.class, MybatisPlusNoOptLockConfig.class})
public class H2FillFieldTest extends H2Test {

    @Autowired
    H2UserFillMapper fillMapper;

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
            executeSql(stmt, "user.insert.sql");
            conn.commit();
        }
    }


    @Test
    public void testFill() {
        H2UserFill u = new H2UserFill();
        u.setName("ignoreTest");
        u.setTestType(1);
        u.setDesc("ignoreDesc");
        Assert.assertEquals(1, fillMapper.insert(u).intValue());

        Long id = u.getId();
        Assert.assertNotNull(id);

        H2UserFill dbUser = fillMapper.selectById(id);
        Assert.assertNull(dbUser.getTestType());
        Assert.assertNotNull(dbUser.getDesc());

        dbUser.setTestType(2);
        dbUser.setDesc("ignoreDesc2");
        Assert.assertEquals(1, fillMapper.updateById(dbUser).intValue());

        dbUser = fillMapper.selectById(id);
        Assert.assertEquals("ignoreDesc", dbUser.getDesc());
        Assert.assertEquals(2, dbUser.getTestType().intValue());
    }

}
