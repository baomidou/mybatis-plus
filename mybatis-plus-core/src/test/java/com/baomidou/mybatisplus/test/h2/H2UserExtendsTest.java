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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.test.h2.base.H2Test;
import com.baomidou.mybatisplus.test.h2.config.ServiceConfig;
import com.baomidou.mybatisplus.test.h2.entity.persistent.H2UserIntVersionExtendTO;
import com.baomidou.mybatisplus.test.h2.service.IH2UserExtendsService;

/**
 * <p>
 * #328 实体继承属性测试
 * </p>
 *
 * @author yuxiaobin
 * @date 2017/6/26
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ServiceConfig.class})
public class H2UserExtendsTest extends H2Test {

    @Autowired
    IH2UserExtendsService userService;

    @BeforeClass
    public static void init() throws SQLException, IOException {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.register(ServiceConfig.class);
        context.refresh();
        DataSource ds = (DataSource) context.getBean("dataSource");
        try (Connection conn = ds.getConnection()) {
            initData(conn);
        }
    }

    @Test
    public void testInsert() {
        H2UserIntVersionExtendTO user = new H2UserIntVersionExtendTO();
        user.setAge(1);
        user.setPrice(new BigDecimal("9.99"));
        userService.insert(user);
        Assert.assertNotNull(user.getId());
        user.setDesc("Caratacus");
        userService.insertOrUpdate(user);
        H2UserIntVersionExtendTO userFromDB = userService.selectById(user.getId());
        Assert.assertEquals("Caratacus", userFromDB.getDesc());
    }

    @Test
    public void testUpdate() {
        H2UserIntVersionExtendTO user = new H2UserIntVersionExtendTO();
        user.setAge(1);
        user.setPrice(new BigDecimal("9.99"));
        userService.insert(user);
        Long id = user.getId();
        Assert.assertNotNull(id);
        user.setDesc("Caratacus");
        userService.insertOrUpdate(user);
        H2UserIntVersionExtendTO userFromDB = userService.selectById(id);
        Assert.assertEquals("Caratacus", userFromDB.getDesc());

        user = new H2UserIntVersionExtendTO();
        user.setId(id);
        user.setDesc("Caratacus2");
        userService.updateById(user);
        userFromDB = userService.selectById(id);
        Assert.assertEquals("Caratacus2", userFromDB.getDesc());
        Assert.assertEquals(new BigDecimal("9.99"), userFromDB.getPrice());
    }


    @Test
    public void testDelete() {
        H2UserIntVersionExtendTO user = new H2UserIntVersionExtendTO();
        user.setAge(1);
        user.setPrice(new BigDecimal("9.91"));
        userService.insert(user);
        Long userId = user.getId();
        Assert.assertNotNull(userId);
        Assert.assertNotNull(userService.selectById(userId));
        userService.deleteById(userId);
        Assert.assertNull(userService.selectById(userId));
    }

    @Test
    public void testSelectPage() {
        Page<H2UserIntVersionExtendTO> page = userService.selectPage(new Page<H2UserIntVersionExtendTO>(1, 3));
        Assert.assertEquals(3, page.getRecords().size());
    }

    @Test
    public void testUpdateByIdOptLock() {
        Long id = 991L;
        H2UserIntVersionExtendTO user = new H2UserIntVersionExtendTO();
        user.setId(id);
        user.setName("991");
        user.setAge(91);
        user.setPrice(BigDecimal.TEN);
        user.setDesc("asdf");
        user.setTestType(1);
        user.setVersion(1);
        userService.insertAllColumn(user);

        H2UserIntVersionExtendTO userDB = userService.selectById(id);
        Assert.assertEquals(1, userDB.getVersion().intValue());

        userDB.setName("992");
        userService.updateById(userDB);

        userDB = userService.selectById(id);
        Assert.assertEquals(2, userDB.getVersion().intValue());
        Assert.assertEquals("992", userDB.getName());
    }

    @Test
    public void testUpdateByEntityWrapperOptLock() {
        Long id = 992L;
        H2UserIntVersionExtendTO user = new H2UserIntVersionExtendTO();
        user.setId(id);
        user.setName("992");
        user.setAge(92);
        user.setPrice(BigDecimal.TEN);
        user.setDesc("asdf");
        user.setTestType(1);
        user.setVersion(1);
        userService.insertAllColumn(user);

        H2UserIntVersionExtendTO userDB = userService.selectById(id);
        Assert.assertEquals(1, userDB.getVersion().intValue());

        H2UserIntVersionExtendTO updUser = new H2UserIntVersionExtendTO();
        updUser.setName("999");

        userService.update(updUser, new EntityWrapper<H2UserIntVersionExtendTO>());

        userDB = userService.selectById(id);
        Assert.assertEquals(1, userDB.getVersion().intValue());
        Assert.assertEquals("999", userDB.getName());
        updUser.setVersion(1);
        updUser.setName("998");
        userService.update(updUser, new EntityWrapper<H2UserIntVersionExtendTO>());
        userDB = userService.selectById(id);
        Assert.assertEquals(2, userDB.getVersion().intValue());
        Assert.assertEquals("998", userDB.getName());
    }


    @Test
    public void testUpdateByEntityWrapperOptLockWithoutVersionVal() {
        Long id = 993L;
        H2UserIntVersionExtendTO user = new H2UserIntVersionExtendTO();
        user.setId(id);
        user.setName("992");
        user.setAge(92);
        user.setPrice(BigDecimal.TEN);
        user.setDesc("asdf");
        user.setTestType(1);
        user.setVersion(1);
        userService.insertAllColumn(user);

        H2UserIntVersionExtendTO userDB = userService.selectById(id);
        Assert.assertEquals(1, userDB.getVersion().intValue());

        H2UserIntVersionExtendTO updUser = new H2UserIntVersionExtendTO();
        updUser.setName("999");
        userDB.setVersion(null);
        EntityWrapper<H2UserIntVersionExtendTO> ew = new EntityWrapper<>();
        ew.eq("test_id", id);
        userService.update(updUser, ew);

        userDB = userService.selectById(id);
        Assert.assertEquals(1, userDB.getVersion().intValue());
        Assert.assertEquals("999", userDB.getName());
    }

    @Test
    public void testUpdateByEntityWrapperNoEntity() {
        Long id = 998L;
        H2UserIntVersionExtendTO user = new H2UserIntVersionExtendTO();
        user.setId(id);
        user.setName("992");
        user.setAge(92);
        user.setPrice(BigDecimal.TEN);
        user.setDesc("asdf");
        user.setTestType(1);
        user.setVersion(1);
        userService.insertAllColumn(user);

        H2UserIntVersionExtendTO userDB = userService.selectById(id);
        Assert.assertEquals(1, userDB.getVersion().intValue());
        H2UserIntVersionExtendTO updateUser = new H2UserIntVersionExtendTO();
        updateUser.setName("998");
        boolean result = userService.update(updateUser, new EntityWrapper<H2UserIntVersionExtendTO>());
        Assert.assertTrue(result);
        userDB = userService.selectById(id);
        Assert.assertEquals(1, userDB.getVersion().intValue());
        EntityWrapper<H2UserIntVersionExtendTO> param = new EntityWrapper<>();
        param.eq("name", "998");
        List<H2UserIntVersionExtendTO> userList = userService.selectList(param);
        Assert.assertTrue(userList.size() > 1);
    }

    @Test
    public void testUpdateByEntityWrapperNull() {
        Long id = 918L;
        int oldVersionInt = 99;
        H2UserIntVersionExtendTO user = new H2UserIntVersionExtendTO();
        user.setId(id);
        user.setName("992");
        user.setAge(92);
        user.setPrice(BigDecimal.TEN);
        user.setDesc("asdf");
        user.setTestType(1);
        user.setVersion(oldVersionInt);
        userService.insertAllColumn(user);

        H2UserIntVersionExtendTO userDB = userService.selectById(id);
        Assert.assertEquals(oldVersionInt, userDB.getVersion().intValue());
        H2UserIntVersionExtendTO updateUser = new H2UserIntVersionExtendTO();
        updateUser.setName("918");
        updateUser.setVersion(oldVersionInt);
        Assert.assertTrue(userService.update(updateUser, null));
        EntityWrapper<H2UserIntVersionExtendTO> ew = new EntityWrapper<>();
        ew.eq("name", "918").eq("version", oldVersionInt + 1);
        int count2 = userService.selectCount(ew);
        List<H2UserIntVersionExtendTO> userList = userService.selectList(new EntityWrapper<H2UserIntVersionExtendTO>());
        for (H2UserIntVersionExtendTO u : userList) {
            System.out.println(u);
        }
        Assert.assertTrue(count2 > 0);
        Assert.assertEquals(1, count2);
    }

    @Test
    public void testUpdateBatch() {
        List<H2UserIntVersionExtendTO> list = userService.selectList(new EntityWrapper<H2UserIntVersionExtendTO>());
        Map<Long, Integer> userVersionMap = new HashMap<>();
        for (H2UserIntVersionExtendTO u : list) {
            userVersionMap.put(u.getId(), u.getVersion());
        }

        Assert.assertTrue(userService.updateBatchById(list));
        list = userService.selectList(new EntityWrapper<H2UserIntVersionExtendTO>());
        for (H2UserIntVersionExtendTO user : list) {
            Assert.assertEquals(userVersionMap.get(user.getId()) + 1, user.getVersion().intValue());
        }

    }

    @Test
    public void testUpdateInLoop() {
        List<H2UserIntVersionExtendTO> list = userService.selectList(new EntityWrapper<H2UserIntVersionExtendTO>());
        Map<Long, Integer> versionBefore = new HashMap<>();
        Map<Long, String> nameExpect = new HashMap<>();
        for (H2UserIntVersionExtendTO h2User : list) {
            Long id = h2User.getId();
            Integer versionVal = h2User.getVersion();
            versionBefore.put(id, versionVal);
            String randomName = h2User.getName() + "_" + new Random().nextInt(10);
            nameExpect.put(id, randomName);
            h2User.setName(randomName);
            userService.updateById(h2User);
        }

        list = userService.selectList(new EntityWrapper<H2UserIntVersionExtendTO>());
        for (H2UserIntVersionExtendTO u : list) {
            Assert.assertEquals(u.getName(), nameExpect.get(u.getId()));
            if (u.getVersion() != null)
                Assert.assertEquals(versionBefore.get(u.getId()) + 1, u.getVersion().intValue());
        }
    }

    @Test
    public void testUpdateAllColumnInLoop() {
        List<H2UserIntVersionExtendTO> list = userService.selectList(new EntityWrapper<H2UserIntVersionExtendTO>());
        Map<Long, Integer> versionBefore = new HashMap<>();
        Map<Long, String> nameExpect = new HashMap<>();
        for (H2UserIntVersionExtendTO h2User : list) {
            Long id = h2User.getId();
            Integer versionVal = h2User.getVersion();
            versionBefore.put(id, versionVal);
            String randomName = h2User.getName() + "_" + new Random().nextInt(10);
            nameExpect.put(id, randomName);
            h2User.setName(randomName);
            userService.updateAllColumnById(h2User);
        }

        list = userService.selectList(new EntityWrapper<H2UserIntVersionExtendTO>());
        for (H2UserIntVersionExtendTO u : list) {
            Assert.assertEquals(u.getName(), nameExpect.get(u.getId()));
            if (u.getVersion() != null) {
                Assert.assertEquals(versionBefore.get(u.getId()) + 1, u.getVersion().intValue());
            }
        }
    }
}
