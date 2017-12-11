package com.baomidou.mybatisplus.test.h2;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.test.h2.base.H2Test;
import com.baomidou.mybatisplus.test.h2.entity.mapper.H2UserNoVersionMapper;
import com.baomidou.mybatisplus.test.h2.entity.persistent.H2UserNoVersion;
import com.baomidou.mybatisplus.test.h2.service.IH2UserNoVersionService;

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
public class H2UserNoVersionTest extends H2Test {

    @Autowired
    IH2UserNoVersionService userService;

    @Autowired
    H2UserNoVersionMapper userMapper;

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
        H2UserNoVersion user = new H2UserNoVersion();
        user.setAge(1);
        user.setPrice(new BigDecimal("9.99"));
        userService.insert(user);
        Assert.assertNotNull(user.getId());
        user.setDesc("Caratacus");
        userService.insertOrUpdate(user);
        H2UserNoVersion userFromDB = userService.selectById(user.getId());
        Assert.assertEquals("Caratacus", userFromDB.getDesc());
    }

    @Test
    public void testDelete() {
        H2UserNoVersion user = new H2UserNoVersion();
        user.setAge(1);
        user.setPrice(new BigDecimal("9.99"));
        userService.insert(user);
        Long userId = user.getId();
        Assert.assertNotNull(userId);
        userService.deleteById(userId);
        Assert.assertNull(userService.selectById(userId));
    }

    @Test
    public void testSelectByid() {
        Long userId = 101L;
        Assert.assertNotNull(userService.selectById(userId));
    }

    @Test
    public void testSelectOne() {
        H2UserNoVersion user = new H2UserNoVersion();
        user.setId(105L);
        EntityWrapper<H2UserNoVersion> ew = new EntityWrapper<>(user);
        H2UserNoVersion userFromDB = userService.selectOne(ew);
        Assert.assertNotNull(userFromDB);
    }

    @Test
    public void testSelectList() {
        H2UserNoVersion user = new H2UserNoVersion();
        EntityWrapper<H2UserNoVersion> ew = new EntityWrapper<>(user);
        List<H2UserNoVersion> list = userService.selectList(ew);
        Assert.assertNotNull(list);
        Assert.assertNotEquals(0, list.size());
    }

    @Test
    public void testSelectPage() {
        Page<H2UserNoVersion> page = userService.selectPage(new Page<H2UserNoVersion>(1, 3));
        Assert.assertEquals(3, page.getRecords().size());
    }

    @Test
    public void testUpdateById() {
        Long id = 991L;
        H2UserNoVersion user = new H2UserNoVersion();
        user.setId(id);
        user.setName("991");
        user.setAge(91);
        user.setPrice(BigDecimal.TEN);
        user.setDesc("asdf");
        user.setTestType(1);
        user.setVersion(1);
        userService.insertAllColumn(user);

        H2UserNoVersion userDB = userService.selectById(id);
        Assert.assertEquals(1, userDB.getVersion().intValue());

        userDB.setName("991");
        userService.updateById(userDB);

        userDB = userService.selectById(id);
        Assert.assertEquals(1, userDB.getVersion().intValue());
        Assert.assertEquals("991", userDB.getName());
    }

    @Test
    public void testUpdateByEntityWrapper() {
        Long id = 992L;
        H2UserNoVersion user = new H2UserNoVersion();
        user.setId(id);
        user.setName("992");
        user.setAge(92);
        user.setPrice(BigDecimal.TEN);
        user.setDesc("asdf");
        user.setTestType(1);
        user.setVersion(1);
        userService.insertAllColumn(user);

        H2UserNoVersion userDB = userService.selectById(id);
        Assert.assertEquals(1, userDB.getVersion().intValue());

        H2UserNoVersion updUser = new H2UserNoVersion();
        updUser.setName("999");

        userService.update(updUser, new EntityWrapper<>(userDB));

        userDB = userService.selectById(id);
        Assert.assertEquals(1, userDB.getVersion().intValue());
        Assert.assertEquals("999", userDB.getName());
    }

    @Test
    public void testUpdateByEntityWrapper2() {
        Long id = 993L;
        H2UserNoVersion user = new H2UserNoVersion();
        user.setId(id);
        user.setName("992");
        user.setAge(92);
        user.setPrice(BigDecimal.TEN);
        user.setDesc("asdf");
        user.setTestType(1);
        user.setVersion(1);
        userService.insertAllColumn(user);

        H2UserNoVersion userDB = userService.selectById(id);
        Assert.assertEquals(1, userDB.getVersion().intValue());

        H2UserNoVersion updUser = new H2UserNoVersion();
        updUser.setName("999");
        userDB.setVersion(null);
        userService.update(updUser, new EntityWrapper<>(userDB));

        userDB = userService.selectById(id);
        Assert.assertEquals(1, userDB.getVersion().intValue());
        Assert.assertEquals("999", userDB.getName());
    }

    @Test
    public void testUpdateBatch() {
        List<H2UserNoVersion> list = userService.selectList(new EntityWrapper<H2UserNoVersion>());
        Map<Long, Integer> userVersionMap = new HashMap<>();
        for (H2UserNoVersion u : list) {
            userVersionMap.put(u.getId(), u.getVersion());
        }
        userService.updateBatchById(list);

        list = userService.selectList(new EntityWrapper<H2UserNoVersion>());
        for (H2UserNoVersion user : list) {
            Assert.assertEquals(userVersionMap.get(user.getId()).intValue(), user.getVersion().intValue());
        }
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
        Long id = 10087L;
        H2UserNoVersion user = new H2UserNoVersion();
        user.setId(id);
        user.setName("myUpdate");
        user.setVersion(1);
        userService.insert(user);

        H2UserNoVersion dbUser = userService.selectById(id);
        Assert.assertNotNull(dbUser);
        Assert.assertEquals("myUpdate", dbUser.getName());

        Assert.assertEquals(1, userMapper.myUpdateWithNameId(id, "updateMy"));

        dbUser = userService.selectById(id);
        Assert.assertNotNull(dbUser);
        Assert.assertEquals("updateMy", dbUser.getName());
        Assert.assertEquals(1, user.getVersion().intValue());

    }
}
