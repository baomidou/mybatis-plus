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
import com.baomidou.mybatisplus.test.h2.entity.mapper.H2UserMapper;
import com.baomidou.mybatisplus.test.h2.entity.persistent.H2User;

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
public class H2UserMapperTest extends BaseTest {

    @Autowired
    protected H2UserMapper userMapper;

    @BeforeClass
    public static void initDB() throws SQLException, IOException {
        H2Db.initH2User();
    }

    @Test
    public void insert() {
        H2User h2User = new H2User();
        h2User.setName("聂秋秋");
        h2User.setAge(1);
        h2User.setDesc("这是一个不错的小伙子");
        Assert.assertTrue(1 == userMapper.insert(h2User));

        log(h2User.getId());
    }

}
