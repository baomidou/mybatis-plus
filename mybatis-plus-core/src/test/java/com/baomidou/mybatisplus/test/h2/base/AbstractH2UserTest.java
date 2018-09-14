package com.baomidou.mybatisplus.test.h2.base;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.plugins.pagination.PageHelper;
import com.baomidou.mybatisplus.test.h2.entity.mapper.H2UserMapper;
import com.baomidou.mybatisplus.test.h2.entity.persistent.H2User;
import com.baomidou.mybatisplus.test.h2.service.IH2UserService;

/**
 * <p>
 * test cases for {@link H2User}
 * </p>
 *
 * @author yuxiaobin
 * @date 2018/1/5
 */
public abstract class AbstractH2UserTest extends H2Test {

    @Autowired
    protected IH2UserService userService;

    @Autowired
    protected H2UserMapper userMapper;

    protected void insertSimpleCase() {
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

    protected void insertBatchSimpleCase() {
        userService.insert(new H2User("sanmao", 1));
        List<H2User> h2Users = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            H2User user = new H2User();
            user.setAge(1);
            user.setPrice(new BigDecimal("6" + i));
            user.setDesc("testBatch" + i);
            h2Users.add(user);
        }
        Assert.assertTrue(userService.insertBatch(h2Users));
    }

    protected void deleteSimpleCase() {
        H2User user = new H2User();
        user.setAge(1);
        user.setPrice(new BigDecimal("9.99"));
        userService.insert(user);
        Long userId = user.getId();
        Assert.assertNotNull(userId);
        userService.deleteById(userId);
        Assert.assertNull(userService.selectById(userId));
    }

    protected void selectOneSimpleCase() {
        H2User user = new H2User();
        user.setId(105L);
        EntityWrapper<H2User> ew = new EntityWrapper<>(user);
        H2User userFromDB = userService.selectOne(ew);
        Assert.assertNotNull(userFromDB);
    }

    protected void selectListSimpleCase() {
        H2User user = new H2User();
        EntityWrapper<H2User> ew = new EntityWrapper<>(user);
        List<H2User> list = userService.selectList(ew);
        Assert.assertNotNull(list);
        Assert.assertNotEquals(0, list.size());
    }

    protected void selectPageSimpleCase() {
        int size = 3;
        Page<H2User> page = userService.selectPage(new Page<H2User>(1, size));
        Assert.assertEquals(size, page.getRecords().size());
        long total = page.getTotal();
        page = userService.selectPage(new Page<H2User>(2, size));
        if (total >= size * 2) {
            Assert.assertEquals(size, page.getRecords().size());
        } else {
            Assert.assertEquals(total - size, page.getRecords().size());
        }
    }

    protected void selectPageHelperCase() {
        PageHelper.startPage(1, 3);
        List<H2User> userList = userService.selectList(new EntityWrapper<H2User>());
        System.out.println("total=" + PageHelper.freeTotal());
        Assert.assertEquals("Should do pagination", 3, userList.size());
        userList = userService.selectList(new EntityWrapper<H2User>());
        Assert.assertNotEquals("Should not do pagination", 3, userList.size());
    }

    protected void updateByIdWithOptLock() {
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
        Assert.assertEquals(2, userDB.getVersion().intValue());
        Assert.assertEquals("991", userDB.getName());
    }

    protected void updateAllColumnByIdCase() {
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
        Assert.assertEquals(2, userDB.getVersion().intValue());
        Assert.assertEquals("991", userDB.getName());

        userDB.setName("990");
        userService.updateById(userDB);

        userDB = userService.selectById(id);
        Assert.assertEquals(3, userDB.getVersion().intValue());
        Assert.assertEquals("990", userDB.getName());
    }

    protected void updateByEntityWrapperOptLock() {
        Long id = 1992L;
        H2User user = new H2User();
        user.setId(id);
        user.setName("992");
        user.setAge(92);
        user.setPrice(BigDecimal.TEN);
        user.setDesc("asdf");
        user.setTestType(1);
        int version = 99;
        user.setVersion(version);
        userService.insertAllColumn(user);

        H2User userDB = userService.selectById(id);
        Assert.assertEquals(version, userDB.getVersion().intValue());

        H2User updUser = new H2User();
        updUser.setName("999");
        updUser.setVersion(version);

        userService.update(updUser, null);

        userDB = userService.selectById(id);
        Assert.assertEquals(version + 1, userDB.getVersion().intValue());
        Assert.assertEquals("999", userDB.getName());
    }

    protected void updateByEntityWrapperOptLockWithoutVersionVal() {
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

    protected void updateByEntityWrapperNoEntity() {
        Long id = 998L;
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
        H2User updateUser = new H2User();
        updateUser.setName("998");
        boolean result = userService.update(updateUser, new EntityWrapper<H2User>());
        Assert.assertTrue(result);
        userDB = userService.selectById(id);
        Assert.assertEquals(1, userDB.getVersion().intValue());
        EntityWrapper<H2User> param = new EntityWrapper<>();
        param.eq("name", "998");
        List<H2User> userList = userService.selectList(param);
        Assert.assertTrue(userList.size() > 1);
    }

    protected void updateByEntityWrapperNull() {
        Long id = 918L;
        H2User user = new H2User();
        user.setId(id);
        user.setName("992");
        user.setAge(92);
        user.setPrice(BigDecimal.TEN);
        user.setDesc("asdf");
        user.setTestType(1);
        int version = 1;
        user.setVersion(version);
        userService.insertAllColumn(user);

        EntityWrapper<H2User> ew = new EntityWrapper<>();
        ew.eq("version", version);
        int count1 = userService.selectCount(ew);

        H2User userDB = userService.selectById(id);
        Assert.assertEquals(version, userDB.getVersion().intValue());
        H2User updateUser = new H2User();
        updateUser.setName("918");
        updateUser.setVersion(version);
        Assert.assertTrue(userService.update(updateUser, null));

        ew = new EntityWrapper<>();
        ew.eq("name", "918").eq("version", version + 1);
        int count2 = userService.selectCount(ew);
        List<H2User> userList = userService.selectList(new EntityWrapper<H2User>());
        for (H2User u : userList) {
            System.out.println(u);
        }
        System.out.println("count1=" + count1 + ", count2=" + count2);
        Assert.assertTrue(count2 > 0);
        Assert.assertEquals(count1, count2);
    }

    protected void updateBatchSimpleCase() {
        List<H2User> list = userService.selectList(new EntityWrapper<H2User>());
        Map<Long, Integer> userVersionMap = new HashMap<>();
        for (H2User u : list) {
            userVersionMap.put(u.getId(), u.getVersion());
        }

        Assert.assertTrue(userService.updateBatchById(list));
        list = userService.selectList(new EntityWrapper<H2User>());
        for (H2User user : list) {
            Assert.assertEquals(userVersionMap.get(user.getId()) + 1, user.getVersion().intValue());
        }
    }

    protected void updateInLoopCase() {
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
            if (u.getVersion() != null)
                Assert.assertEquals(versionBefore.get(u.getId()) + 1, u.getVersion().intValue());
        }
    }

    protected void updateAllColumnInLoop() {
        List<H2User> list = userService.selectList(new EntityWrapper<H2User>());
        Map<Long, Integer> versionExpect = new HashMap<>();
        Map<Long, String> nameExpect = new HashMap<>();
        for (H2User h2User : list) {
            Long id = h2User.getId();
            Integer versionVal = h2User.getVersion();
            if (versionVal != null) {
                versionExpect.put(id, versionVal + 1);
            }
            String randomName = h2User.getName() + "_" + new Random().nextInt(10);
            nameExpect.put(id, randomName);
            h2User.setName(randomName);
            userService.updateAllColumnById(h2User);
        }

        list = userService.selectList(new EntityWrapper<H2User>());
        for (H2User u : list) {
            Long id = u.getId();
            Assert.assertEquals(u.getName(), nameExpect.get(id));
            if (versionExpect.containsKey(id))
                Assert.assertEquals(versionExpect.get(id).intValue(), u.getVersion().intValue());
        }
    }

    protected void updateByMySql() {
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

    protected void selectWithPageCondition() {
        H2User user = new H2User();
        user.setName("pageCondition");
        user.setTestType(99);
        user.setVersion(1);
        userService.insert(user);
        Page<H2User> page = new Page<>(1, 3);
        Map<String, Object> condition = new HashMap<>();
        condition.put("test_type", 99);
        page.setCondition(condition);
        Page<H2User> pageResult = userService.selectPage(page);
        Assert.assertEquals("testType=99 should only have 1 record", 1, pageResult.getTotal());
        for (H2User u : pageResult.getRecords()) {
            System.out.println(u);
        }
        System.out.println(pageResult.getTotal());
    }
    
    protected void insertOrUpdateBatchSimpleCase() {
        List<H2User> h2Users = new ArrayList<>();
        H2User user;
        for (int i = 0; i < 10; i++) {
            user = new H2User();
            user.setAge(1);
            user.setPrice(new BigDecimal("6" + i));
            user.setDesc("insertOrUpdateBatch" + i);
            h2Users.add(user);
        }
        Assert.assertTrue(userService.insertOrUpdateBatch(h2Users));
    }
}
