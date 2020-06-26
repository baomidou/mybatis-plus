/*
 * Copyright (c) 2011-2019, hubin (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.test.h2;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.test.h2.entity.H2User;
import com.baomidou.mybatisplus.test.h2.enums.AgeEnum;
import com.baomidou.mybatisplus.test.h2.service.IH2UserService;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.select.Select;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * Mybatis Plus H2 Junit Test
 *
 * @author Caratacus
 * @since 2017/4/1
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:h2/spring-test-h2.xml"})
class H2UserTest extends BaseTest {

    @Autowired
    protected IH2UserService userService;

    @Test
    @Order(1)
    void testInsertMy() {
        String name = "自定义insert";
        int version = 1;
        int row = userService.myInsert(name, version);
        Assertions.assertEquals(1, row);
    }

    @Test
    @Order(2)
    void testInsertObjectWithParam() {
        String name = "自定义insert带Param注解";
        int version = 1;
        int row = userService.myInsertWithParam(name, version);
        Assertions.assertEquals(1, row);
    }

    @Test
    @Order(3)
    void testInsertObjectWithoutParam() {
        String name = "自定义insert带Param注解";
        int version = 1;
        int row = userService.myInsertWithoutParam(name, version);
        Assertions.assertEquals(1, row);
    }


    @Test
    @Order(6)
    void testSelectLambdaById() {
        H2User h2User = userService.getOne(Wrappers.<H2User>lambdaQuery().eq(H2User::getTestId, 101));
        Assertions.assertNotNull(h2User);
    }

    @Test
    @Order(10)
    void testEntityWrapperSelectSql() {
        QueryWrapper<H2User> ew = new QueryWrapper<>();
        ew.select("test_id, name, age");
        List<H2User> list = userService.list(ew);
        for (H2User u : list) {
            Assertions.assertNotNull(u.getTestId());
            Assertions.assertNotNull(u.getName());
            Assertions.assertNull(u.getPrice());
        }
    }

    @Test
    @Order(10)
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
//        Assert.assertNotEquals(0, pagemySelectMaps.getTotal());
//    }

    @Test
    @Order(10)
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
    @Order(15)
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
    @Order(16)
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
    @Order(17)
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
    @Order(18)
    void testBatchTransactional() {
        try {
            userService.testBatchTransactional();
        } catch (MybatisPlusException e) {
            List<H2User> list = userService.list(new QueryWrapper<H2User>().like("name", "batch"));
            Assertions.assertTrue(CollectionUtils.isEmpty(list));
        }
    }

    @Test
    @Order(19)
    void testSimpleTransactional() {
        try {
            userService.testSimpleTransactional();
        } catch (MybatisPlusException e) {
            List<H2User> list = userService.list(new QueryWrapper<H2User>().like("name", "simple"));
            Assertions.assertTrue(CollectionUtils.isEmpty(list));
        }
    }

    @Test
    @Order(20)
    void testSaveOrUpdateBatchTransactional() {
        try {
            userService.testSaveOrUpdateBatchTransactional();
        } catch (MybatisPlusException e) {
            List<H2User> list = userService.list(new QueryWrapper<H2User>().like("name", "savOrUpdate"));
            Assertions.assertTrue(CollectionUtils.isEmpty(list));
        }
    }

    @Test
    @Order(21)
    void testSaveBatch() {
        Assertions.assertTrue(userService.saveBatch(Arrays.asList(new H2User("saveBatch1"), new H2User("saveBatch2"), new H2User("saveBatch3"), new H2User("saveBatch4"))));
        Assertions.assertEquals(4, userService.count(new QueryWrapper<H2User>().like("name", "saveBatch")));
        Assertions.assertTrue(userService.saveBatch(Arrays.asList(new H2User("saveBatch5"), new H2User("saveBatch6"), new H2User("saveBatch7"), new H2User("saveBatch8")), 2));
        Assertions.assertEquals(8, userService.count(new QueryWrapper<H2User>().like("name", "saveBatch")));
    }

    @Test
    @Order(22)
    void testUpdateBatch() {
        Assertions.assertTrue(userService.updateBatchById(Arrays.asList(new H2User(1010L, "batch1010"),
            new H2User(1011L, "batch1011"), new H2User(1010L, "batch1010"), new H2User(1012L, "batch1012"))));
        Assertions.assertEquals(userService.getById(1010L).getName(), "batch1010");
        Assertions.assertEquals(userService.getById(1011L).getName(), "batch1011");
        Assertions.assertEquals(userService.getById(1012L).getName(), "batch1012");
        Assertions.assertTrue(userService.updateBatchById(Arrays.asList(new H2User(1010L, "batch1010A"),
            new H2User(1011L, "batch1011A"), new H2User(1010L, "batch1010"), new H2User(1012L, "batch1012")), 1));
        Assertions.assertEquals(userService.getById(1010L).getName(), "batch1010");
        Assertions.assertEquals(userService.getById(1011L).getName(), "batch1011A");
        Assertions.assertEquals(userService.getById(1012L).getName(), "batch1012");
    }

    @Test
    @Order(23)
    void testSaveOrUpdateBatch() {
        Assertions.assertTrue(userService.saveOrUpdateBatch(Arrays.asList(new H2User(1010L, "batch1010"),
            new H2User("batch1011"), new H2User(1010L, "batch1010"), new H2User("batch1015"))));
        Assertions.assertEquals(userService.getById(1010L).getName(), "batch1010");
        Assertions.assertEquals(userService.count(new QueryWrapper<H2User>().eq("name", "batch1011")), 1);
        Assertions.assertEquals(userService.count(new QueryWrapper<H2User>().eq("name", "batch1015")), 1);
        Assertions.assertTrue(userService.saveOrUpdateBatch(Arrays.asList(new H2User(1010L, "batch1010A"),
            new H2User("batch1011AB"), new H2User(1010L, "batch1010"), new H2User("batch1016")), 1));
        Assertions.assertEquals(userService.getById(1010L).getName(), "batch1010");
        Assertions.assertEquals(userService.count(new QueryWrapper<H2User>().eq("name", "batch1011AB")), 1);
        Assertions.assertEquals(userService.count(new QueryWrapper<H2User>().eq("name", "batch1016")), 1);
    }

    @Test
    @Order(24)
    void testSimpleAndBatch() {
        Assertions.assertTrue(userService.save(new H2User("testSimpleAndBatch1", 0)));
        Assertions.assertEquals(1, userService.count(new QueryWrapper<H2User>().eq("name", "testSimpleAndBatch1")));
        Assertions.assertTrue(userService.saveOrUpdateBatch(Arrays.asList(new H2User("testSimpleAndBatch2"), new H2User("testSimpleAndBatch3"), new H2User("testSimpleAndBatch4")), 1));
        Assertions.assertEquals(4, userService.count(new QueryWrapper<H2User>().like("name", "testSimpleAndBatch")));
    }

    @Test
    @Order(25)
    void testSimpleAndBatchTransactional() {
        try {
            userService.testSimpleAndBatchTransactional();
        } catch (MybatisPlusException e) {
            List<H2User> list = userService.list(new QueryWrapper<H2User>().like("name", "simpleAndBatchTx"));
            Assertions.assertTrue(CollectionUtils.isEmpty(list));
        }
    }

    @Test
    @Order(26)
    void testServiceImplInnerLambdaQuery() {
        H2User tomcat = userService.lambdaQuery().eq(H2User::getName, "Tomcat").one();
        Assertions.assertNotNull(tomcat);
        Assertions.assertNotEquals(0L, userService.lambdaQuery().like(H2User::getName, "a").count().longValue());

        List<H2User> users = userService.lambdaQuery().like(H2User::getName, "T")
            .ne(H2User::getAge, AgeEnum.TWO)
            .ge(H2User::getVersion, 1)
            .isNull(H2User::getPrice)
            .list();
        Assertions.assertTrue(users.isEmpty());
    }

    @Test
    @Order(27)
    void testServiceChainQuery() {
        H2User tomcat = userService.query().eq("name", "Tomcat").one();
        Assertions.assertNotNull(tomcat, "tomcat should not be null");
        userService.query().nested(i -> i.eq("name", "Tomcat")).list();
        userService.lambdaUpdate().set(H2User::getName, "Tom").eq(H2User::getName, "Tomcat").update();
    }


    @Test
    @Order(28)
    void testSaveBatchException() {
        try {
            userService.saveBatch(Arrays.asList(
                new H2User(1L, "tom"),
                new H2User(1L, "andy")
            ));
        } catch (Exception e) {
            Assertions.assertTrue(e instanceof DataAccessException);
        }
    }

    @Test
    @Order(29)
    @Transactional
    void testClearSqlSessionCache() {
        H2User h2User;
        h2User = userService.getById(996102919L);
        assert h2User == null;
        userService.saveBatch(Collections.singletonList(new H2User(996102919L, "靓仔")));
        h2User = userService.getById(996102919L);
        Assertions.assertNotNull(h2User);
    }

    @Test
    @Order(30)
    void testSaveBatchNoTransactional1() {
        userService.testSaveBatchNoTransactional1();
        Assertions.assertEquals(3, userService.count(new QueryWrapper<H2User>().like("name", "testSaveBatchNoTransactional1")));
    }

    @Test
    @Order(30)
    void testSaveBatchNoTransactional2() {
        try {
            userService.testSaveBatchNoTransactional2();
        } catch (Exception e) {
            Assertions.assertEquals(3, userService.count(new QueryWrapper<H2User>().like("name", "testSaveBatchNoTransactional2")));
        }
    }

    @Test
    void myQueryWithGroupByOrderBy() {
        userService.mySelectMaps().forEach(System.out::println);
    }

    @Test
    void notParser() throws Exception {
        final String targetSql1 = "SELECT * FROM user WHERE id NOT LIKE ?";
        final Select select = (Select) CCJSqlParserUtil.parse(targetSql1);
        Assertions.assertEquals(select.toString(), targetSql1);


        final String targetSql2 = "SELECT * FROM user WHERE id NOT IN (?)";
        final Select select2 = (Select) CCJSqlParserUtil.parse(targetSql2);
        Assertions.assertEquals(select2.toString(), targetSql2);


        final String targetSql3 = "SELECT * FROM user WHERE id IS NOT NULL";
        final Select select3 = (Select) CCJSqlParserUtil.parse(targetSql3);
        Assertions.assertEquals(select3.toString(), targetSql3);
    }

    /**
     * CTO 说批量插入性能不行，让我们来分析一下问题在哪里
     */
    @Test
    void batchInsertPerformanceTest() {
        List<H2User> users = mockUser(10_000, 99989);
        userService.saveBatch(users);
        // 卧槽，速度挺快的
    }

    /**
     * 模拟一群人
     *
     * @param size     这群人的数量
     * @param cardinal 这群人 id 的起始值
     * @return 返回模拟的一群人
     */
    private List<H2User> mockUser(int size, long cardinal) {
        return new AbstractList<H2User>() {

            @Override
            public H2User get(int index) {
                long id = cardinal + index + 1;
                H2User h2User = new H2User(id, Long.toHexString(id));
                h2User.setVersion(0);
                h2User.setPrice(BigDecimal.ZERO);
                return h2User;
            }

            @Override
            public int size() {
                return size;
            }
        };
    }

    @Test
    void testSimpleWrapperClear() {
        userService.save(new H2User("逗号", AgeEnum.TWO));
        QueryWrapper<H2User> queryWrapper = new QueryWrapper<H2User>().eq("name", "咩咩");
        Assertions.assertEquals(0, userService.count(queryWrapper));
        queryWrapper.clear();
        queryWrapper.eq("name", "逗号");
        Assertions.assertEquals(1, userService.count(queryWrapper));
        UpdateWrapper<H2User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("name", "逗号二号");
        Assertions.assertFalse(userService.update(updateWrapper.eq("name", "逗号一号")));
        updateWrapper.clear();
        updateWrapper.set("name", "逗号一号");
        Assertions.assertTrue(userService.update(updateWrapper.eq("name", "逗号")));
    }

    @Test
    void testLambdaWrapperClear() {
        userService.save(new H2User("小红", AgeEnum.TWO));
        LambdaQueryWrapper<H2User> lambdaQueryWrapper = new QueryWrapper<H2User>().lambda().eq(H2User::getName, "小宝");
        Assertions.assertEquals(0, userService.count(lambdaQueryWrapper));
        lambdaQueryWrapper.clear();
        lambdaQueryWrapper.eq(H2User::getName, "小红");
        Assertions.assertEquals(1, userService.count(lambdaQueryWrapper));
        LambdaUpdateWrapper<H2User> lambdaUpdateWrapper = new UpdateWrapper<H2User>().lambda().set(H2User::getName, "小红二号");
        Assertions.assertFalse(userService.update(lambdaUpdateWrapper.eq(H2User::getName, "小红一号")));
        lambdaUpdateWrapper.clear();
        lambdaUpdateWrapper.set(H2User::getName, "小红一号");
        Assertions.assertTrue(userService.update(lambdaUpdateWrapper.eq(H2User::getName, "小红")));
    }

    /**
     * 观察 {@link com.baomidou.mybatisplus.core.toolkit.LambdaUtils#resolve(SFunction)}
     */
    @Test
    void testLambdaCache() {
        for (int i = 0; i < 1000; i++) {
            lambdaCache();
        }
    }

    private void lambdaCache() {
        Wrappers.<H2User>lambdaQuery()
            .eq(H2User::getAge, 2)
            .eq(H2User::getName, 2)
            .eq(H2User::getPrice, 2)
            .getTargetSql();
    }
}
