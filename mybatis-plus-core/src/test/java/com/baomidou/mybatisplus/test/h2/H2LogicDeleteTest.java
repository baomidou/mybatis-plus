package com.baomidou.mybatisplus.test.h2;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
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

import com.baomidou.mybatisplus.mapper.Condition;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.test.h2.base.H2Test;
import com.baomidou.mybatisplus.test.h2.config.ServiceConfig;
import com.baomidou.mybatisplus.test.h2.entity.persistent.H2UserLogicDelete;
import com.baomidou.mybatisplus.test.h2.service.IH2UserLogicDeleteService;

/**
 * <p>
 * H2LogicDeleteTest
 * </p>
 *
 * @author yuxiaobin
 * @date 2017/6/15
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ServiceConfig.class})
public class H2LogicDeleteTest extends H2Test {

    @BeforeClass
    public static void init() throws SQLException, IOException {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:h2/spring-test-h2.xml");
        DataSource ds = (DataSource) context.getBean("dataSource");
        try (Connection conn = ds.getConnection()) {
            initData(conn);
        }
    }

    @Autowired
    private IH2UserLogicDeleteService userService;

    @Test
    public void testInsert() {
        H2UserLogicDelete user = new H2UserLogicDelete();
        user.setAge(1);
        user.setPrice(new BigDecimal("9.99"));
        user.setVersion(1);
        userService.insert(user);
        Long id = user.getId();
        Assert.assertNotNull(id);
        user.setDesc("Caratacus");
        userService.insertOrUpdate(user);
        System.out.println("************************************");
        System.out.println("*********" + user.getVersion());
        System.out.println("************************************");
        user = new H2UserLogicDelete();
        user.setId(id);
        EntityWrapper<H2UserLogicDelete> ew = new EntityWrapper<>(user);
        List<H2UserLogicDelete> list = userService.selectList(ew);
        System.out.println("************************************");
        System.out.println("*********" + list.size());
        System.out.println("************************************");
        H2UserLogicDelete userFromDB = userService.selectById(user.getId());
        Assert.assertEquals("Caratacus", userFromDB.getDesc());
        Assert.assertEquals(1, userFromDB.getVersion().intValue());
        Page page = new Page();

        page.setOrderByField("desc");
        userService.selectPage(page, Condition.create().eq("desc", "111"));
        userService.deleteById(id);
        list = userService.selectList(ew);
        System.out.println("************************************");
        System.out.println("*********" + list.size());
        System.out.println("************************************");
    }

    @Test
    public void testLogicDeleted() {
        H2UserLogicDelete user = new H2UserLogicDelete();
        user.setAge(1);
        user.setPrice(new BigDecimal("9.99"));
        user.setVersion(-1);
        userService.insert(user);
        Long id = user.getId();
        Assert.assertNotNull(id);
        Assert.assertNotNull(userService.selectList(Condition.create().orderBy("age")));
        H2UserLogicDelete userFromDB = userService.selectById(user.getId());
        Assert.assertNull(userFromDB);
    }

    @Test
    public void testDelete() {
        H2UserLogicDelete user = new H2UserLogicDelete();
        user.setAge(1);
        user.setPrice(new BigDecimal("9.99"));
        user.setVersion(1);
        userService.insert(user);
        Long id = user.getId();
        Assert.assertNotNull(id);
        Assert.assertTrue(userService.deleteById(id));
        H2UserLogicDelete fromDB = userService.selectByIdMy(id);
        Assert.assertNotNull(fromDB);
        System.out.println(fromDB);
    }

    @Test
    public void testDeleteBatch() {
        List<Long> idList = new ArrayList<>(4);
        idList.add(101L);
        idList.add(102L);
        idList.add(103L);
        idList.add(104L);
        userService.deleteBatchIds(idList);
        Assert.assertNull(userService.selectById(101));
        Assert.assertNull(userService.selectById(102));
        Assert.assertNull(userService.selectById(103));
        Assert.assertNull(userService.selectById(104));
    }
}
