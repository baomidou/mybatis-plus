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

import com.baomidou.mybatisplus.core.conditions.select.EntityWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.test.h2.config.H2Db;
import com.baomidou.mybatisplus.test.h2.entity.mapper.H2UserMapper;
import com.baomidou.mybatisplus.test.h2.entity.persistent.H2User;

/**
 * <p>
 * Mybatis Plus H2 Junit Test
 * </p>
 *
 * @author hubin
 * @since 2018-06-05
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:h2/spring-test-h2.xml"})
public class H2UserMapperTest extends BaseTest {

    @Autowired
    protected H2UserMapper userMapper;

    @BeforeClass
    public static void aInitDB() throws SQLException, IOException {
        H2Db.initH2User();
    }

    @Test
    public void bInsert() {
        H2User h2User = new H2User();
        h2User.setName(NQQ);
        h2User.setAge(1);
        h2User.setDesc("这是一个不错的小伙子");
        Assert.assertTrue(1 == userMapper.insert(h2User));

        log(h2User.getId());
    }

    @Test
    public void cUpdate() {
        H2User h2User = new H2User();
        h2User.setAge(2);
        Assert.assertTrue(1 == userMapper.update(h2User,
            new EntityWrapper<H2User>().eq("name", NQQ)));

        h2User.setAge(3);
        Assert.assertTrue(1 == userMapper.update(h2User,
            new EntityWrapper<H2User>().stream().eq(H2User::getName, NQQ)));
    }

    @Test
    public void dSelectOne() {
        H2User h2User = userMapper.selectOne(new H2User().setName(NQQ));
        Assert.assertNotNull(h2User);

        log(h2User.toString());
    }

}
