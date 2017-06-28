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

import com.baomidou.mybatisplus.test.h2.entity.mapper.H2UserIgnoreMapper;
import com.baomidou.mybatisplus.test.h2.entity.persistent.H2UserIgnore;

/**
 * <p>
 * </p>
 *
 * @author yuxiaobin
 * @date 2017/5/31
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:h2/spring-test-h2-metaobj.xml"})
public class H2IgnoreFieldTest extends H2Test {

    @Autowired
    H2UserIgnoreMapper ignoreMapper;

    @BeforeClass
    public static void initDB() throws SQLException, IOException {
        @SuppressWarnings("resource")
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:h2/spring-test-h2-metaobj.xml");
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
    public void testIgnore(){
        H2UserIgnore u = new H2UserIgnore();
        u.setName("ignoreTest");
        u.setTestType(1);
        u.setDesc("ignoreDesc");
        Assert.assertEquals(1, ignoreMapper.insert(u).intValue());

        Long id = u.getId();
        Assert.assertNotNull(id);

        H2UserIgnore dbUser = ignoreMapper.selectById(id);
        Assert.assertNull(dbUser.getTestType());

        dbUser.setTestType(2);
        dbUser.setDesc("ignoreDesc2");
        Assert.assertEquals(1, ignoreMapper.updateById(dbUser).intValue());

        dbUser = ignoreMapper.selectById(id);
        Assert.assertEquals("ignoreDesc", dbUser.getDesc());
        Assert.assertEquals(2, dbUser.getTestType().intValue());

    }



}
