package com.baomidou.mybatisplus.test.h2;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.test.h2.config.H2Db;
import com.baomidou.mybatisplus.test.h2.entity.persistent.H2User;
import com.baomidou.mybatisplus.test.h2.service.IH2UserService;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * <p>
 * Mybatis Plus H2 Junit Test
 * </p>
 *
 * @author Caratacus
 * @since 2017/4/1
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:h2/spring-test-h2.xml"})
public class H2UserTest extends BaseTest {

    @Autowired
    protected IH2UserService userService;

    @BeforeClass
    public static void initDB() throws SQLException, IOException {
        H2Db.initH2User();
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
    public void testEntityWrapperSelectSql() {
        QueryWrapper<H2User> ew = new QueryWrapper<>();
        ew.select("test_id as id, name, age");
        List<H2User> list = userService.list(ew);
        for (H2User u : list) {
            Assert.assertNotNull(u.getTestId());
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
            Assert.assertNotNull(u.getTestId());
        }
    }

    @Test
    public void testQueryWithParamInSelectStatement4Page() {
//        Map<String, Object> param = new HashMap<>();
//        String nameParam = "selectStmtParam";
//        param.put("nameParam", nameParam);
//        param.put("ageFrom", 1);
//        param.put("ageTo", 100);
//        Page<H2User> page = userService.queryWithParamInSelectStatememt4Page(param, new Page<H2User>(0, 10));
//        Assert.assertNotNull(page.getRecords());
//        for (H2User u : page.getRecords()) {
//            Assert.assertEquals(nameParam, u.getName());
//            Assert.assertNotNull(u.getId());
//        }
//        Assert.assertNotEquals(0, page.getTotal());
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
    public void testUpdateByIdWitiOptLock(){
        Long id = 991L;
        H2User user = new H2User();
        user.setTestId(id);
        user.setName("991");
        user.setAge(91);
        user.setPrice(BigDecimal.TEN);
        user.setDesc("asdf");
        user.setTestType(1);
        user.setVersion(1);
        userService.save(user);

        H2User userDB = userService.getById(id);
        Assert.assertEquals(1, userDB.getVersion().intValue());

        userDB.setName("992");
        userService.updateById(userDB);
        Assert.assertEquals("updated version value should be updated to entity",2, userDB.getVersion().intValue());

        userDB = userService.getById(id);
        Assert.assertEquals(2, userDB.getVersion().intValue());
        Assert.assertEquals("992", userDB.getName());
    }

    @Test
    public void testUpdateByEwWithOptLock(){
        QueryWrapper<H2User> ew = new QueryWrapper<>();
        ew.gt("age",13);
        for(H2User u: userService.list(ew)){
            System.out.println(u.getName()+","+u.getAge()+","+u.getVersion());
        }
        userService.update(new H2User().setPrice(BigDecimal.TEN), ew);
        for(H2User u: userService.list(ew)){
            System.out.println(u.getName()+","+u.getAge()+","+u.getVersion());
        }
    }


}
