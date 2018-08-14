package com.baomidou.mybatisplus.test.h2;

import java.io.IOException;
import java.sql.SQLException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import com.baomidou.mybatisplus.test.h2.config.H2Db;
import com.baomidou.mybatisplus.test.h2.entity.persistent.H2User;
import com.baomidou.mybatisplus.test.h2.service.IH2UserService;

/**
 * 测试XML配置
 * @author nieqiurong 2018/8/14 13:30.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:h2/spring-test-xml-h2.xml"})
public class TestXmlConfig {

    @Autowired
    protected IH2UserService userService;

    @BeforeClass
    public static void initDB() throws SQLException, IOException {
        H2Db.initH2User();
    }

    @Test
    public void test() {
        H2User user = userService.getById(101L);
        Assert.assertNotNull(user);
    }

}
