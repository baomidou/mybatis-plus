package com.baomidou.mybatisplus.test.h2;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;

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
import com.baomidou.mybatisplus.test.h2.entity.mapper.H2UserMetaobjMapper;
import com.baomidou.mybatisplus.test.h2.entity.persistent.H2UserMetaObj;
import com.baomidou.mybatisplus.test.h2.service.IH2UserMetaobjService;

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
public class H2UserAutoincrementTest extends H2Test {

    @Autowired
    private H2UserMetaobjMapper userMapper;

    @Autowired
    IH2UserMetaobjService userService;

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
        Assert.assertNotNull(user2.getId());
    }


    @Test
    public void testInsertMy() {
        String name = "testInsertMy";
        int version = 1;
        int row = userMapper.myInsertWithNameVersion(name, version);
        Assert.assertEquals(1, row);
    }

    @Test
    public void testUpdateMy() {
        H2UserMetaObj user = new H2UserMetaObj();
        user.setName("myUpdate");
        user.setVersion(1);
        userMapper.insert(user);
        Long id = user.getId();
        H2UserMetaObj dbUser = userMapper.selectById(id);
        Assert.assertNotNull(dbUser);
        Assert.assertEquals("myUpdate", dbUser.getName());

        Assert.assertEquals(1, userMapper.myUpdateWithNameId(id, "updateMy"));

        dbUser = userMapper.selectById(id);
        Assert.assertNotNull(dbUser);
        Assert.assertEquals("updateMy", dbUser.getName());
        Assert.assertEquals(1, user.getVersion().intValue());

    }

}
