package com.baomidou.mybatisplus.test.h2;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.baomidou.mybatisplus.entity.Column;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.test.h2.base.AbstractH2UserTest;
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
public class H2UserTest extends AbstractH2UserTest {

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
        insertSimpleCase();
    }

    @Test
    public void testInsertBatch() {
        insertBatchSimpleCase();
    }

    @Test
    public void testSelectBatch() {
        List<Long> idList = new ArrayList<>(4);
        idList.add(101L);
        idList.add(102L);
        idList.add(103L);
        idList.add(104L);
        List<H2User> userList = userService.selectBatchIds(idList);
        for (H2User u : userList) {
            System.out.println(u);
        }
    }


    @Test
    public void testDelete() {
        deleteSimpleCase();
    }

    @Test
    public void testSelectByid() {
        Long userId = 101L;
        Assert.assertNotNull(userService.selectById(userId));
    }

    @Test
    public void testSelectOne() {
        selectOneSimpleCase();
    }

    @Test
    public void testSelectList() {
        selectListSimpleCase();
    }

    @Test
    public void testSelectPage() {
        selectPageSimpleCase();
    }

    @Test
    public void testPageHelper() {
        selectPageHelperCase();
    }

    @Test
    public void testUpdateByIdOptLock() {
        updateByIdWithOptLock();
    }

    @Test
    public void testUpdateAllColumnByIdOptLock() {
        updateAllColumnByIdCase();
    }

    @Test
    public void testUpdateByEntityWrapperOptLock() {
        updateByEntityWrapperOptLock();
    }

    @Test
    public void testUpdateByEntityWrapperOptLockWithoutVersionVal() {
        updateByEntityWrapperOptLockWithoutVersionVal();
    }

    @Test
    public void testUpdateByEntityWrapperNoEntity() {
        updateByEntityWrapperNoEntity();
    }

    @Test
    public void testUpdateByEntityWrapperNull() {
        updateByEntityWrapperNull();
    }

    @Test
    public void testUpdateBatch() {
        updateBatchSimpleCase();
    }

    @Test
    public void testUpdateInLoop() {
        updateInLoopCase();
    }

    @Test
    public void testUpdateAllColumnInLoop() {
        updateAllColumnInLoop();
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
        updateByMySql();
    }


    @Test
    public void testConditionInPage() {
        selectWithPageCondition();
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
        Assert.assertNotEquals(0L, page.getTotal());

    }

    @Test
    public void testDistinctColumn() {
        EntityWrapper<H2User> ew = new EntityWrapper<>();
        ew.setSqlSelect(Column.create().column("distinct test_type"));//setMapUnderscoreToCamelCase(true)
        ew.isNotNull("test_type");
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
