package com.baomidou.mybatisplus.test.h2;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.baomidou.mybatisplus.test.h2.base.AbstractH2UserTest;
import com.baomidou.mybatisplus.test.h2.entity.persistent.H2UserIntVersionAR;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * AR测试
 * </p>
 *
 * @author yuxiaobin
 * @date 2018/3/15
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:h2/spring-test-h2.xml"})
public class H2ARTest extends AbstractH2UserTest {

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
    @Transactional
    public void testAROptimisticLock() {
        H2UserIntVersionAR user = new H2UserIntVersionAR();
        user = user.selectById(105);
        System.out.println(user);
        Assert.assertNotNull(user);
        int version = user.getVersion();
        String updateName = UUID.randomUUID().toString().substring(0, 20);
        user.setName(updateName);
        user.updateById();
        user = user.selectById();
        Assert.assertEquals("name shoud be updated", updateName, user.getName());
        Assert.assertEquals("version shoud be updated", version + 1, user.getVersion().intValue());
    }

}
