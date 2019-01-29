package com.baomidou.mybatisplus.test.h2;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.test.h2.entity.enums.AgeEnum;
import com.baomidou.mybatisplus.test.h2.entity.persistent.H2User;
import com.baomidou.mybatisplus.test.h2.service.IH2UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mybatis Plus H2 Junit Test
 *
 * @author Caratacus
 * @since 2017/4/1
 */
// TODO junit 5.4 开始提供支持，预计 2019-02-06 发布，等这之后升级版本并使用 @TestMethodOrder 代替 @FixMethodOrder
// @FixMethodOrder(MethodSorters.JVM)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:h2/spring-test-h2.xml"})
public class H2UserTest extends BaseTest {

    @Autowired
    protected IH2UserService userService;


    @Test
    void testInsertMy() {
        String name = "自定义insert";
        int version = 1;
        int row = userService.myInsert(name, version);
        Assertions.assertEquals(1, row);
    }

    @Test
    void testInsertObjectWithParam() {
        String name = "自定义insert带Param注解";
        int version = 1;
        int row = userService.myInsertWithParam(name, version);
        Assertions.assertEquals(1, row);
    }

    @Test
    void testInsertObjectWithoutParam() {
        String name = "自定义insert带Param注解";
        int version = 1;
        int row = userService.myInsertWithoutParam(name, version);
        Assertions.assertEquals(1, row);
    }

    @Test
    void testEntityWrapperSelectSql() {
        QueryWrapper<H2User> ew = new QueryWrapper<>();
        ew.select("test_id as testId, name, age");
        List<H2User> list = userService.list(ew);
        for (H2User u : list) {
            Assertions.assertNotNull(u.getTestId());
            Assertions.assertNotNull(u.getName());
            Assertions.assertNull(u.getPrice());
        }
    }

    @Test
    void testQueryWithParamInSelectStatement() {
        Map<String, Object> param = new HashMap<>();
        String nameParam = "selectStmtParam";
        param.put("nameParam", nameParam);
        param.put("ageFrom", 1);
        param.put("ageTo", 100);
        List<H2User> list = userService.queryWithParamInSelectStatememt(param);
        Assertions.assertNotNull(list);
        for (H2User u : list) {
            Assertions.assertEquals(nameParam, u.getName());
            Assertions.assertNotNull(u.getTestId());
        }
    }

//    @Test
//    void testQueryWithParamInSelectStatement4Page() {
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
//    }

    @Test
    void testSelectCountWithParamInSelectItems() {
        Map<String, Object> param = new HashMap<>();
        String nameParam = "selectStmtParam";
        param.put("nameParam", nameParam);
        param.put("ageFrom", 1);
        param.put("ageTo", 100);
        int count = userService.selectCountWithParamInSelectItems(param);
        Assertions.assertNotEquals(0, count);
    }

    @Test
    void testUpdateByIdWithOptLock() {
        Long id = 991L;
        H2User user = new H2User();
        user.setTestId(id);
        user.setName("991");
        user.setAge(AgeEnum.ONE);
        user.setPrice(BigDecimal.TEN);
        user.setDesc("asdf");
        user.setTestType(1);
        user.setVersion(1);
        userService.save(user);

        H2User userDB = userService.getById(id);
        Assertions.assertEquals(1, userDB.getVersion().intValue());

        userDB.setName("992");
        userService.updateById(userDB);
        Assertions.assertEquals(2, userDB.getVersion().intValue(), "updated version value should be updated to entity");

        userDB = userService.getById(id);
        Assertions.assertEquals(2, userDB.getVersion().intValue());
        Assertions.assertEquals("992", userDB.getName());
    }

    @Test
    void testUpdateByEwWithOptLock() {
        H2User userInsert = new H2User();
        userInsert.setName("optLockerTest");
        userInsert.setAge(AgeEnum.THREE);
        userInsert.setPrice(BigDecimal.TEN);
        userInsert.setDesc("asdf");
        userInsert.setTestType(1);
        userInsert.setVersion(99);
        userService.save(userInsert);

        QueryWrapper<H2User> ew = new QueryWrapper<>();
        ew.ge("age", AgeEnum.TWO.getValue());
        Long id99 = null;
        for (H2User u : userService.list(ew)) {
            System.out.println(u.getName() + "," + u.getAge() + "," + u.getVersion());
            if (u.getVersion() != null && u.getVersion() == 99) {
                id99 = u.getTestId();
            }
        }
        userService.update(new H2User().setPrice(BigDecimal.TEN).setVersion(99), ew);
        System.out.println("============after update");
        ew = new QueryWrapper<>();
        ew.ge("age", AgeEnum.TWO.getValue());
        for (H2User u : userService.list(ew)) {
            System.out.println(u.getName() + "," + u.getAge() + "," + u.getVersion());
            if (u.getTestId().equals(id99)) {
                Assertions.assertEquals(100, u.getVersion().intValue(), "optLocker should update version+=1");
            }
        }
    }

    @Test
    void testOptLocker4WrapperIsNull() {
        H2User userInsert = new H2User();
        userInsert.setName("optLockerTest");
        userInsert.setAge(AgeEnum.THREE);
        userInsert.setPrice(BigDecimal.TEN);
        userInsert.setDesc("asdf");
        userInsert.setTestType(1);
        userInsert.setVersion(99);
        userService.save(userInsert);

        QueryWrapper<H2User> ew = new QueryWrapper<>();
        ew.ge("age", AgeEnum.TWO.getValue());
        Long id99 = null;
        Map<Long, BigDecimal> idPriceMap = new HashMap<>();
        for (H2User u : userService.list(ew)) {
            System.out.println(u.getName() + "," + u.getAge() + "," + u.getVersion());
            idPriceMap.put(u.getTestId(), u.getPrice());
            if (u.getVersion() != null && u.getVersion() == 99) {
                id99 = u.getTestId();
            }
        }
        userService.update(new H2User().setPrice(BigDecimal.TEN).setVersion(99), null);
        System.out.println("============after update");
        ew = new QueryWrapper<>();
        ew.ge("age", AgeEnum.TWO.getValue());
        for (H2User u : userService.list(ew)) {
            System.out.println(u.getName() + "," + u.getAge() + "," + u.getVersion());
            if (u.getTestId().equals(id99)) {
                Assertions.assertEquals(100, u.getVersion().intValue(), "optLocker should update version+=1");
            } else {
                Assertions.assertEquals(idPriceMap.get(u.getTestId()), u.getPrice(), "other records should not be updated");
            }
        }
        userService.update(new H2User().setPrice(BigDecimal.ZERO), null);
        for (H2User u : userService.list(new QueryWrapper<>())) {
            System.out.println(u.getName() + "," + u.getAge() + "," + u.getVersion());
            Assertions.assertEquals(u.getPrice().setScale(2, RoundingMode.HALF_UP).intValue(), BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP).intValue(), "all records should be updated");
        }

    }

    @Test
    void testBatchTransactional() {
        try {
            userService.testBatchTransactional();
        } catch (MybatisPlusException e) {
            List<H2User> list = userService.list(new QueryWrapper<H2User>().like("name", "batch"));
            Assertions.assertTrue(CollectionUtils.isEmpty(list));
        }
    }

    @Test
    void testSimpleTransactional() {
        try {
            userService.testSimpleTransactional();
        } catch (MybatisPlusException e) {
            List<H2User> list = userService.list(new QueryWrapper<H2User>().like("name", "simple"));
            Assertions.assertTrue(CollectionUtils.isEmpty(list));
        }
    }

    @Test
    void testSaveOrUpdateBatchTransactional() {
        try {
            userService.testSaveOrUpdateBatchTransactional();
        } catch (MybatisPlusException e) {
            List<H2User> list = userService.list(new QueryWrapper<H2User>().like("name", "savOrUpdate"));
            Assertions.assertTrue(CollectionUtils.isEmpty(list));
        }
    }

    @Test
    void testSaveBatch() {
        Assertions.assertTrue(userService.saveBatch(Arrays.asList(new H2User("saveBatch1"), new H2User("saveBatch2"), new H2User("saveBatch3"), new H2User("saveBatch4"))));
        Assertions.assertTrue(userService.saveBatch(Arrays.asList(new H2User("saveBatch5"), new H2User("saveBatch6"), new H2User("saveBatch7"), new H2User("saveBatch8")), 2));
    }

    @Test
    void testUpdateBatch() {
        Assertions.assertTrue(userService.updateBatchById(Arrays.asList(new H2User(1010L, "batch1010"), new H2User(1011L, "batch1011"), new H2User(1010L, "batch1010"), new H2User(1012L, "batch1012"))));
        Assertions.assertTrue(userService.updateBatchById(Arrays.asList(new H2User(1010L, "batch1010A"), new H2User(1011L, "batch1011A"), new H2User(1010L, "batch1010"), new H2User(1012L, "batch1012")), 1));
    }

    @Test
    void testSaveOrUpdateBatch() {
        Assertions.assertTrue(userService.saveOrUpdateBatch(Arrays.asList(new H2User(1010L, "batch1010"), new H2User("batch1011"), new H2User(1010L, "batch1010"), new H2User("batch1015"))));
        Assertions.assertTrue(userService.saveOrUpdateBatch(Arrays.asList(new H2User(1010L, "batch1010A"), new H2User("batch1011A"), new H2User(1010L, "batch1010"), new H2User("batch1016")), 1));
    }

    @Test
    void testSimpleAndBatch() {
        Assertions.assertTrue(userService.save(new H2User("testSimpleAndBatch1", 0)));
        Assertions.assertTrue(userService.saveOrUpdateBatch(Arrays.asList(new H2User("testSimpleAndBatch2"), new H2User("testSimpleAndBatch3"), new H2User("testSimpleAndBatch4")), 1));
    }

    @Test
    void testSimpleAndBatchTransactional() {
        try {
            userService.testSimpleAndBatchTransactional();
        } catch (MybatisPlusException e) {
            List<H2User> list = userService.list(new QueryWrapper<H2User>().like("name", "simpleAndBatchTx"));
            Assertions.assertTrue(CollectionUtils.isEmpty(list));
        }
    }

    @Test
    void testServiceImplInnerLambdaQuery() {
        H2User tomcat = userService.lambdaQuery().eq(H2User::getName, "Tomcat").one();
        Assertions.assertNotNull(tomcat);
        Assertions.assertNotEquals(0L, userService.lambdaQuery().like(H2User::getName, "a").count().longValue());
        userService.lambdaQuery().like(H2User::getName, "T")
            .ne(H2User::getAge, AgeEnum.TWO)
            .ge(H2User::getVersion, 1)
            .isNull(H2User::getPrice)
            .list();
    }

    @Test
    void testServiceChainQuery() {
        H2User tomcat = userService.query().eq("name", "Tomcat").one();
        Assertions.assertNotNull(tomcat, "tomcat should not be null");
        userService.query().nested(i -> i.eq("name", "Tomcat")).list();
        userService.lambdaUpdate().set(H2User::getName, "Tom").eq(H2User::getName, "Tomcat").update();
    }
}
