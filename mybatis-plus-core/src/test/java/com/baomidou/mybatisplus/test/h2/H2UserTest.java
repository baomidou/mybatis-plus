package com.baomidou.mybatisplus.test.h2;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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

import com.baomidou.mybatisplus.entity.Column;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.test.h2.entity.mapper.H2UserMapper;
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
@ContextConfiguration(locations = {"classpath:h2/spring-test-h2.xml"})
public class H2UserTest extends H2Test {

    @Autowired
    private IH2UserService userService;

    @Autowired
    H2UserMapper userMapper;

    @BeforeClass
    public static void initDB() throws SQLException, IOException {
        @SuppressWarnings("resource")
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:h2/spring-test-h2.xml");
        DataSource ds = (DataSource) context.getBean("dataSource");
        try (Connection conn = ds.getConnection()) {
            String createTableSql = readFile("user.ddl.sql");
            Statement stmt = conn.createStatement();
            stmt.execute(createTableSql);
            stmt.execute("truncate table h2user");
            executeSql(stmt, "user.insert.sql");
            conn.commit();
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
    public void testInsertBatch() {
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
        Assert.assertEquals(2, userDB.getVersion().intValue());
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
        Assert.assertEquals(2, userDB.getVersion().intValue());
        Assert.assertEquals("991", userDB.getName());

        userDB.setName("990");
        userService.updateById(userDB);

        userDB = userService.selectById(id);
        Assert.assertEquals(3, userDB.getVersion().intValue());
        Assert.assertEquals("990", userDB.getName());
    }

    @Test
    public void testUpdateByEntityWrapperOptLock() {
        Long id = 1992L;
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
        Assert.assertEquals(2, userDB.getVersion().intValue());
        Assert.assertEquals("999", userDB.getName());
    }

    @Test
    public void testUpdateByEntityWrapperOptLockWithoutVersionVal() {
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
    public void testUpdateByEntityWrapperNoEntity() {
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

    @Test
    public void testUpdateByEntityWrapperNull() {
        Long id = 918L;
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
        updateUser.setName("918");
        updateUser.setVersion(1);
        Assert.assertTrue(userService.update(updateUser, null));
        EntityWrapper<H2User> ew = new EntityWrapper<>();
        int count1 = userService.selectCount(ew);
        ew.eq("name", "918").eq("version", 1);
        int count2 = userService.selectCount(ew);
        List<H2User> userList = userService.selectList(new EntityWrapper<H2User>());
        for (H2User u : userList) {
            System.out.println(u);
        }
        System.out.println("count1=" + count1 + ", count2=" + count2);
        Assert.assertTrue(count2 > 0);
        Assert.assertEquals(count1, count2);
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
            Assert.assertEquals(userVersionMap.get(user.getId()) + 1, user.getVersion().intValue());
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
            if (u.getVersion() != null)
                Assert.assertEquals(versionBefore.get(u.getId()) + 1, u.getVersion().intValue());
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
            Assert.assertEquals(versionBefore.get(u.getId()) + 1, u.getVersion().intValue());
        }
    }

    @Test
    public void testInsertMy() {
        String name = "自定义insert";
        int version = 1;
        int row = userService.myInsert(name, version);
        Assert.assertEquals(1, row);
    }

    @Test
    public void testInsertObjectWithParam() {
        String name = "自定义insert带Param注解";
        int version = 1;
        int row = userService.myInsertWithParam(name, version);
        Assert.assertEquals(1, row);
    }

    @Test
    public void testInsertObjectWithoutParam() {
        String name = "自定义insert带Param注解";
        int version = 1;
        int row = userService.myInsertWithoutParam(name, version);
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


    @Test
    public void testCondition() {
        Page<H2User> page = new Page<>(1, 3);
        Map<String, Object> condition = new HashMap<>();
        condition.put("test_type", 1);
        page.setCondition(condition);
        Page<H2User> pageResult = userService.selectPage(page);
        for (H2User u : pageResult.getRecords()) {
            System.out.println(u);
        }
        System.out.println(pageResult.getTotal());

    }


    @Test
    public void testEntityWrapperSelectSql() {
        EntityWrapper<H2User> ew = new EntityWrapper<>();
        ew.setSqlSelect("test_id as id, name, age");
        List<H2User> list = userService.selectList(ew);
        for (H2User u : list) {
            Assert.assertNotNull(u.getId());
            Assert.assertNotNull(u.getName());
            Assert.assertNull(u.getPrice());
        }

    }

    @Test
    public void testQueryWithParamInSelectStatement() {
        Map<String, Object> param = new HashMap<>();
        String nameParam = "selectStmtParam";
        param.put("nameParam", nameParam);
        param.put("ageFrom", 1);
        param.put("ageTo", 100);
        List<H2User> list = userService.queryWithParamInSelectStatememt(param);
        Assert.assertNotNull(list);
        for (H2User u : list) {
            Assert.assertEquals(nameParam, u.getName());
            Assert.assertNotNull(u.getId());
        }
    }

    @Test
    public void testQueryWithParamInSelectStatement4Page() {
        Map<String, Object> param = new HashMap<>();
        String nameParam = "selectStmtParam";
        param.put("nameParam", nameParam);
        param.put("ageFrom", 1);
        param.put("ageTo", 100);
        Page<H2User> page = userService.queryWithParamInSelectStatememt4Page(param, new Page<H2User>(0, 10));
        Assert.assertNotNull(page.getRecords());
        for (H2User u : page.getRecords()) {
            Assert.assertEquals(nameParam, u.getName());
            Assert.assertNotNull(u.getId());
        }
        Assert.assertNotEquals(0, page.getTotal());
    }

    @Test
    public void testSelectCountWithParamInSelectItems() {
        Map<String, Object> param = new HashMap<>();
        String nameParam = "selectStmtParam";
        param.put("nameParam", nameParam);
        param.put("ageFrom", 1);
        param.put("ageTo", 100);
        int count = userService.selectCountWithParamInSelectItems(param);
        Assert.assertNotEquals(0, count);
    }

    @Test
    public void testPageWithDollarParamInSelectItems() {
        Map<String, Object> param = new HashMap<>();
        String nameParam = "selectStmtParam";
        param.put("nameParam", nameParam);
        param.put("ageFrom", 1);
        param.put("ageTo", 100);
        Page<H2User> page = new Page<H2User>(0, 10);
        userMapper.selectUserWithDollarParamInSelectStatememt4Page(param, page);
        Assert.assertNotEquals(0, page.getTotal());

    }

    @Test
    public void testDistinctColumn() {
        EntityWrapper<H2User> ew = new EntityWrapper<>();
        ew.setSqlSelect(Column.create().column("distinct test_type"));//setMapUnderscoreToCamelCase(true)
        List<H2User> list = userService.selectList(ew);
        for (H2User u : list) {
            System.out.println("getTestType=" + u.getTestType());
            Assert.assertNotNull(u.getTestType());
        }
        ew.setSqlSelect("distinct test_type as testType");
        for (H2User u : userService.selectList(ew)) {
            System.out.println("testType=" + u.getTestType());
            Assert.assertNotNull(u.getTestType());
        }
    }

}
