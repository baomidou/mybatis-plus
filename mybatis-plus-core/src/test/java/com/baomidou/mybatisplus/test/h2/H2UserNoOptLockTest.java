package com.baomidou.mybatisplus.test.h2;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
import com.baomidou.mybatisplus.test.h2.entity.persistent.H2User;
import com.baomidou.mybatisplus.test.h2.service.IH2UserService;

/**
 * <p>
 * Mybatis Plus H2 Junit Test
 * </p>
 *
 * @author Caratacus
 * @date 2017/4/1
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:h2/spring-test-no-opt-lock-h2.xml"})
public class H2UserNoOptLockTest extends H2Test {

    @Autowired
    private IH2UserService userService;

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
        H2User user = new H2User();
        user.setAge(1);
        user.setPrice(new BigDecimal("9.99"));
        userService.insert(user);
        Assert.assertNotNull(user.getId());
        user.setDesc("Caratacus");
        userService.insertOrUpdate(user);
        H2User userFromDB = userService.selectById(user.getId());
        Assert.assertEquals("Caratacus", userFromDB.getDesc());
    }

    @Test
    public void testDelete() {
        H2User user = new H2User();
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
        H2User user = new H2User();
        user.setId(105L);
        EntityWrapper<H2User> ew = new EntityWrapper<>(user);
        H2User userFromDB = userService.selectOne(ew);
        Assert.assertNotNull(userFromDB);
    }

    @Test
    public void testSelectList() {
        H2User user = new H2User();
        EntityWrapper<H2User> ew = new EntityWrapper<>(user);
        List<H2User> list = userService.selectList(ew);
        Assert.assertNotNull(list);
        Assert.assertNotEquals(0, list.size());
    }

    @Test
    public void testSelectPage() {
        Page<H2User> page = userService.selectPage(new Page<H2User>(1, 3));
        Assert.assertEquals(3, page.getRecords().size());
    }

    @Test
    public void testUpdateByIdOptLock() {
        Long id = 991L;
        H2User user = new H2User();
        user.setId(id);
        user.setName("991");
        user.setAge(91);
        user.setPrice(BigDecimal.TEN);
        user.setDesc("asdf");
        user.setTestType(1);
        user.setVersion(1);
        userService.insertAllColumn(user);

        H2User userDB = userService.selectById(id);
        Assert.assertEquals(1, userDB.getVersion().intValue());

        userDB.setName("991");
        userService.updateById(userDB);

        userDB = userService.selectById(id);
        Assert.assertEquals(1, userDB.getVersion().intValue());
        Assert.assertEquals("991", userDB.getName());
    }

    @Test
    public void testUpdateAllColumnByIdOptLock() {
        Long id = 997L;
        H2User user = new H2User();
        user.setId(id);
        user.setName("991");
        user.setAge(91);
        user.setPrice(BigDecimal.TEN);
        user.setDesc("asdf");
        user.setTestType(1);
        user.setVersion(1);
        userService.insertAllColumn(user);

        H2User userDB = userService.selectById(id);
        Assert.assertEquals(1, userDB.getVersion().intValue());

        userDB.setName("991");
        userService.updateAllColumnById(userDB);

        userDB = userService.selectById(id);
        Assert.assertEquals(1, userDB.getVersion().intValue());
        Assert.assertEquals("991", userDB.getName());

        userDB.setName("990");
        userService.updateById(userDB);

        userDB = userService.selectById(id);
        Assert.assertEquals(1, userDB.getVersion().intValue());
        Assert.assertEquals("990", userDB.getName());
    }

    @Test
    public void testUpdateByEntityWrapperOptLock() {
        Long id = 992L;
        H2User user = new H2User();
        user.setId(id);
        user.setName("992");
        user.setAge(92);
        user.setPrice(BigDecimal.TEN);
        user.setDesc("asdf");
        user.setTestType(1);
        user.setVersion(1);
        userService.insertAllColumn(user);

        H2User userDB = userService.selectById(id);
        Assert.assertEquals(1, userDB.getVersion().intValue());

        H2User updUser = new H2User();
        updUser.setName("999");

        userService.update(updUser, new EntityWrapper<>(userDB));

        userDB = userService.selectById(id);
        Assert.assertEquals(1, userDB.getVersion().intValue());
        Assert.assertEquals("999", userDB.getName());
    }

    @Test
    public void testUpdateByEntityWrapperOptLockWithoutVersion() {
        Long id = 993L;
        H2User user = new H2User();
        user.setId(id);
        user.setName("992");
        user.setAge(92);
        user.setPrice(BigDecimal.TEN);
        user.setDesc("asdf");
        user.setTestType(1);
        user.setVersion(1);
        userService.insertAllColumn(user);

        H2User userDB = userService.selectById(id);
        Assert.assertEquals(1, userDB.getVersion().intValue());

        H2User updUser = new H2User();
        updUser.setName("999");
        userDB.setVersion(null);
        userService.update(updUser, new EntityWrapper<>(userDB));

        userDB = userService.selectById(id);
        Assert.assertEquals(1, userDB.getVersion().intValue());
        Assert.assertEquals("999", userDB.getName());
    }

    @Test
    public void testUpdateBatch() {
        List<H2User> list = userService.selectList(new EntityWrapper<H2User>());
        Map<Long, Integer> userVersionMap = new HashMap<>();
        for (H2User u : list) {
            userVersionMap.put(u.getId(), u.getVersion());
        }

        Assert.assertTrue(userService.updateBatchById(list));
        list = userService.selectList(new EntityWrapper<H2User>());
        for (H2User user : list) {
            Assert.assertEquals(userVersionMap.get(user.getId()).intValue(), user.getVersion().intValue());
        }

    }

    @Test
    public void testUpdateInLoop() {
        List<H2User> list = userService.selectList(new EntityWrapper<H2User>());
        Map<Long, Integer> versionBefore = new HashMap<>();
        Map<Long, String> nameExpect = new HashMap<>();
        for (H2User h2User : list) {
            Long id = h2User.getId();
            Integer versionVal = h2User.getVersion();
            versionBefore.put(id, versionVal);
            String randomName = h2User.getName() + "_" + new Random().nextInt(10);
            nameExpect.put(id, randomName);
            h2User.setName(randomName);
            userService.updateById(h2User);
        }

        list = userService.selectList(new EntityWrapper<H2User>());
        for (H2User u : list) {
            Assert.assertEquals(u.getName(), nameExpect.get(u.getId()));
            Assert.assertEquals(versionBefore.get(u.getId()).intValue(), u.getVersion().intValue());
        }
    }

    @Test
    public void testUpdateAllColumnInLoop() {
        List<H2User> list = userService.selectList(new EntityWrapper<H2User>());
        Map<Long, Integer> versionBefore = new HashMap<>();
        Map<Long, String> nameExpect = new HashMap<>();
        for (H2User h2User : list) {
            Long id = h2User.getId();
            Integer versionVal = h2User.getVersion();
            versionBefore.put(id, versionVal);
            String randomName = h2User.getName() + "_" + new Random().nextInt(10);
            nameExpect.put(id, randomName);
            h2User.setName(randomName);
            userService.updateAllColumnById(h2User);
        }

        list = userService.selectList(new EntityWrapper<H2User>());
        for (H2User u : list) {
            Assert.assertEquals(u.getName(), nameExpect.get(u.getId()));
            Assert.assertEquals(versionBefore.get(u.getId()).intValue(), u.getVersion().intValue());
        }
    }


    @Test
    public void testInsertMy() {
        String name = "testInsertMy";
        int version = 1;
        int row = userService.myInsert(name, version);
        Assert.assertEquals(1, row);
    }

    @Test
    public void testUpdateMy() {
        Long id = 10087L;
        H2User user = new H2User();
        user.setId(id);
        user.setName("myUpdate");
        user.setVersion(1);
        userService.insert(user);

        H2User dbUser = userService.selectById(id);
        Assert.assertNotNull(dbUser);
        Assert.assertEquals("myUpdate", dbUser.getName());

        Assert.assertEquals(1, userService.myUpdate(id, "updateMy"));

        dbUser = userService.selectById(id);
        Assert.assertNotNull(dbUser);
        Assert.assertEquals("updateMy", dbUser.getName());
        Assert.assertEquals(1, user.getVersion().intValue());

    }
}
