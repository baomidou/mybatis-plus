package com.baomidou.mybatisplus.test.h2;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.baomidou.mybatisplus.test.h2.mapper.H2UserMapper;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DataChangeRecorderInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.test.h2.entity.H2User;
import com.baomidou.mybatisplus.test.h2.enums.AgeEnum;
import com.baomidou.mybatisplus.test.h2.mapper.H2StudentMapper;
import com.baomidou.mybatisplus.test.h2.service.IH2UserService;

import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.select.Select;

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
    @Autowired
    SqlSessionFactory sqlSessionFactory;

    @Autowired
    private H2StudentMapper h2StudentMapper;

    public void initBatchLimitation(int limitation) {
        if (sqlSessionFactory instanceof DefaultSqlSessionFactory) {
            Configuration configuration = sqlSessionFactory.getConfiguration();
            for (Interceptor interceptor : configuration.getInterceptors()) {
                if (interceptor instanceof MybatisPlusInterceptor) {
                    List<InnerInterceptor> innerInterceptors = ((MybatisPlusInterceptor) interceptor).getInterceptors();
                    for (InnerInterceptor innerInterceptor : innerInterceptors) {
                        if (innerInterceptor instanceof DataChangeRecorderInnerInterceptor) {
                            ((DataChangeRecorderInnerInterceptor) innerInterceptor).setBatchUpdateLimit(limitation).openBatchUpdateLimitation();
                        }
                    }
                }
            }
        }
    }

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
    @Order(7)
    void testLambdaTypeHandler() {
        // 演示 json 格式 Wrapper TypeHandler 查询
        H2User h2User = userService.getOne(Wrappers.<H2User>lambdaQuery()
            .apply("name={0,typeHandler=" + H2userNameJsonTypeHandler.class.getCanonicalName() + "}",
                "{\"id\":101,\"name\":\"Tomcat\"}"));
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
        final LocalDateTime dateTime = LocalDateTime.of(2024, 3, 29, 10, 0, 0);
        user.setCreatedDt(dateTime);
        userService.save(user);

        H2User userDB = userService.getById(id);
        Assertions.assertEquals(1, userDB.getVersion().intValue());
        Assertions.assertTrue(userDB.getCreatedDt().compareTo(dateTime) == 0);

        userDB.setName("992");
        userDB.setCreatedDt(dateTime);
        System.out.println("===============================================");
        userService.updateById(userDB);
        Assertions.assertEquals(2, userDB.getVersion().intValue(), "updated version value should be updated to entity");

        userDB = userService.getById(id);
        Assertions.assertEquals(2, userDB.getVersion().intValue());
        Assertions.assertEquals("992", userDB.getName());
        userDB.setCreatedDt(LocalDateTime.now());
        userService.updateById(userDB);
        System.out.println("===============================================");
        userService.lambdaUpdate().set(H2User::getAge, AgeEnum.THREE).eq(H2User::getTestId, id).update();

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
        try {
            initBatchLimitation(3);
            userService.update(new H2User().setPrice(BigDecimal.ZERO), null);
            Assertions.fail("SHOULD NOT REACH HERE");
        } catch (Exception e) {
            Assertions.assertTrue(checkIsDataUpdateLimitationException(e));
        }
    }

    private boolean checkIsDataUpdateLimitationException(Throwable e) {
        if (e instanceof DataChangeRecorderInnerInterceptor.DataUpdateLimitationException) {
            return true;
        }
        if (e.getCause() == null) {
            return false;
        }
        return checkIsDataUpdateLimitationException(e.getCause());
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
        Assertions.assertTrue(userService.saveBatch(List.of(new H2User("saveBatch0"))));
        Assertions.assertTrue(userService.saveBatch(List.of(new H2User("saveBatch1"), new H2User("saveBatch2"), new H2User("saveBatch3"), new H2User("saveBatch4"))));
        Assertions.assertEquals(5, userService.count(new QueryWrapper<H2User>().like("name", "saveBatch")));
        Assertions.assertTrue(userService.saveBatch(List.of(new H2User("saveBatch5"), new H2User("saveBatch6"), new H2User("saveBatch7"), new H2User("saveBatch8")), 2));
        Assertions.assertEquals(9, userService.count(new QueryWrapper<H2User>().like("name", "saveBatch")));
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
            Assertions.assertInstanceOf(DataAccessException.class, e);
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
    @Order(31)
    void testSpaceCharacter() {
        Assertions.assertFalse(StringUtils.isNotBlank(" "));
        Assertions.assertTrue(StringUtils.checkValNotNull(" "));
        H2User h2User = new H2User();
        h2User.setName(" ");
        Assertions.assertTrue(CollectionUtils.isEmpty(userService.list(new QueryWrapper<>(h2User)
            .gt("age", 1).lt("age", 5))));
    }

    @Test
    @Order(32)
    void testSqlInjectionByCustomSqlSegment() {
        // Preparing: select * from h2user WHERE (name LIKE ?)
        // Parameters: %y%%(String)
        List<H2User> h2Users = userService.testCustomSqlSegment(new QueryWrapper<H2User>().like("name", "y%"));
        Assertions.assertEquals(2, h2Users.size());
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
        return new AbstractList<>() {

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
        lambdaQueryWrapper.orderByDesc(H2User::getName);
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

    @Test
    void testLogicDelWithFill() {
        H2User h2User = new H2User("逻辑删除(根据ID)不填充", AgeEnum.TWO);
        userService.save(h2User);
        userService.removeById(h2User.getTestId());
        Assertions.assertNull(h2User.getLastUpdatedDt());
        h2User = new H2User("测试逻辑(根据实体)删除填充", AgeEnum.TWO);
        userService.save(h2User);
        userService.removeById(h2User);
        Assertions.assertNotNull(h2User.getLastUpdatedDt());
    }

    /**
     * 观察 {@link com.baomidou.mybatisplus.core.toolkit.LambdaUtils#extract(SFunction)}
     */
    @RepeatedTest(1000)
    void testLambdaCache() {
        lambdaCache();
    }

    private void lambdaCache() {
        Wrappers.<H2User>lambdaQuery()
            .eq(H2User::getAge, 2)
            .eq(H2User::getName, 2)
            .eq(H2User::getPrice, 2)
            .getTargetSql();
    }

    @Test
    void testRemove() {
        //不报错即可，无需关注返回值
        H2User h2User = new H2User(12L, "test");
//        userService.removeById((short) 100);
//        userService.removeById(100.00);
//        userService.removeById((float) 100);
//        userService.removeById(100);
        userService.removeById(100000L);
//        userService.removeById(new BigDecimal("100"));
//        userService.removeById("100000");
        userService.removeById(h2User);
        userService.removeByIds(Arrays.asList(10000L, h2User));
        userService.removeByIds(Arrays.asList(10000L, h2User), false);
        h2User = new H2User("test");
        H2UserMapper h2UserMapper = (H2UserMapper) userService.getBaseMapper();
        h2UserMapper.insert(h2User);
        h2UserMapper.deleteById(h2User.getTestId());
        h2User = h2UserMapper.getById(h2User.getTestId());
        Assertions.assertNotNull(h2User.getLastUpdatedDt());

        h2User = new H2User("test");
        h2UserMapper.insert(h2User);
        h2UserMapper.deleteById(h2User.getTestId(), false);
        h2User = h2UserMapper.getById(h2User.getTestId());
        Assertions.assertNull(h2User.getLastUpdatedDt());

        h2User = new H2User("test");
        h2UserMapper.insert(h2User);
        h2UserMapper.deleteById(String.valueOf(h2User.getTestId()));
        h2User = h2UserMapper.getById(h2User.getTestId());
        Assertions.assertNotNull(h2User.getLastUpdatedDt());
    }

    @Test
    void testPageOrderBy() {
        // test https://gitee.com/baomidou/mybatis-plus/issues/I4BGE2
        Page<H2User> page = Page.of(1, 10);
        Assertions.assertTrue(userService.page(page, Wrappers.<H2User>query().select("test_id,name")
            .orderByDesc("test_id")).getPages() > 0);
        Assertions.assertTrue(userService.page(page, Wrappers.<H2User>lambdaQuery()
            .orderByDesc(H2User::getTestId)).getPages() > 0);
    }

    @Test
    void testPageNegativeSize() {
        Page<H2User> page = Page.of(1, -1);
        userService.lambdaQuery().page(page);
        Assertions.assertEquals(page.getTotal(), 0);
        Assertions.assertEquals(userService.lambdaQuery().list(Page.of(1, -1, false)).size(), page.getRecords().size());
    }

    @Test
    void testDeleteByFill() {
        H2User h2User = new H2User(3L, "test");
        userService.removeById(1L);
        userService.removeById(1L, true);
        userService.removeById(1, true);
        userService.removeById("1", true);
        userService.removeById(1L, false);
        userService.removeById(h2User);
        userService.removeById(h2User, true);
        userService.removeById(h2User, false);
        userService.removeBatchByIds(Arrays.asList(1L, 2L, h2User));
    }

    @Test
    @Order(25)
    void testServiceImplInnerLambdaQueryConstructorSetEntity() {
        H2User condition = new H2User();
        condition.setName("Tomcat");
        H2User user = userService.lambdaQuery(condition).one();
        Assertions.assertNotNull(user);
        Assertions.assertEquals("Tomcat", user.getName());
        H2User h2User = userService.lambdaQuery().setEntity(condition).one();
        Assertions.assertNotNull(h2User);
        Assertions.assertEquals("Tomcat", h2User.getName());
    }

    @Test
    @Order(26)
    void testServiceGetOptById() {
        H2User user = new H2User(1L, "Evan");
        userService.save(user);
        Optional<H2User> optional = userService.getOptById(1L);
        optional.ifPresent(u -> log(u.toString()));
    }

    @Test
    @Order(27)
    void testServiceGetOneOpt() {
        userService.getOneOpt(Wrappers.<H2User>lambdaQuery().eq(H2User::getName, "David"))
            .ifPresent(u -> log(u.toString()));
    }

    @Test
    @Order(28)
    void testServiceGetOneOptThrowEx() {
        userService.getOneOpt(new LambdaQueryWrapper<H2User>().eq(H2User::getName, "test1"), false)
            .ifPresent(u -> log(u.toString()));

        userService.getOneOpt(new LambdaQueryWrapper<H2User>().eq(H2User::getName, "test"), false)
            .ifPresent(u -> log(u.toString()));

        // 异常情况
        Assertions.assertThrows(TooManyResultsException.class, () -> userService.getOneOpt(Wrappers.<H2User>lambdaQuery()
            .like(H2User::getName, "tes")));
    }

    @Test
    void testInsertFill() {
        H2User h2User;
        h2User = new H2User("insertFillByCustomMethod1", AgeEnum.ONE);
        h2StudentMapper.insertFillByCustomMethod1(h2User);
        Assertions.assertNotNull(h2User.getTestType());

        h2User = new H2User("insertFillByCustomMethod2", AgeEnum.ONE);
        h2StudentMapper.insertFillByCustomMethod2(h2User);
        Assertions.assertNotNull(h2User.getTestType());

        h2User = new H2User("insertFillByCustomMethod3", AgeEnum.ONE);
        h2StudentMapper.insertFillByCustomMethod3(h2User, "fillByCustomMethod3");
        Assertions.assertNotNull(h2User.getTestType());

        List<H2User> list;
        list = Arrays.asList(new H2User("insertFillByCustomMethod4-1", AgeEnum.ONE), new H2User("insertFillByCustomMethod4-2", AgeEnum.ONE));
        h2StudentMapper.insertFillByCustomMethod4(list);
        list.forEach(user -> Assertions.assertNotNull(user.getTestType()));

        list = Arrays.asList(new H2User("insertFillByCustomMethod5-1", AgeEnum.ONE), new H2User("insertFillByCustomMethod5-2", AgeEnum.ONE));
        h2StudentMapper.insertFillByCustomMethod5(list);
        list.forEach(user -> Assertions.assertNotNull(user.getTestType()));

        list = Arrays.asList(new H2User("insertFillByCustomMethod6-1", AgeEnum.ONE), new H2User("insertFillByCustomMethod6-2", AgeEnum.ONE));
        h2StudentMapper.insertFillByCustomMethod6(list);
        list.forEach(user -> Assertions.assertNotNull(user.getTestType()));

        list = Arrays.asList(new H2User("insertFillByCustomMethod7-1", AgeEnum.ONE), new H2User("insertFillByCustomMethod7-2", AgeEnum.ONE));
        h2StudentMapper.insertFillByCustomMethod7(list);
        list.forEach(user -> Assertions.assertNotNull(user.getTestType()));

        H2User[] h2Users;
        h2Users = new H2User[]{new H2User("insertFillByCustomMethod8-1", AgeEnum.ONE), new H2User("insertFillByCustomMethod8-2", AgeEnum.ONE)};
        h2StudentMapper.insertFillByCustomMethod8(h2Users);
        Arrays.stream(h2Users).forEach(user -> Assertions.assertNotNull(user.getTestType()));

        h2Users = new H2User[]{new H2User("insertFillByCustomMethod9-1", AgeEnum.ONE), new H2User("insertFillByCustomMethod9-2", AgeEnum.ONE)};
        h2StudentMapper.insertFillByCustomMethod9(h2Users);
        Arrays.stream(h2Users).forEach(user -> Assertions.assertNotNull(user.getTestType()));

        Map<String, Object> map;
        h2User = new H2User("insertFillByCustomMethod10", AgeEnum.ONE);
        map = new HashMap<>();
        map.put("et", h2User);
        h2StudentMapper.insertFillByCustomMethod10(map);
        Assertions.assertNotNull(h2User.getTestType());

        list = Arrays.asList(new H2User("insertFillByCustomMethod11-1", AgeEnum.ONE), new H2User("insertFillByCustomMethod11-2", AgeEnum.ONE));
        map = new HashMap<>();
        map.put("list", list);
        h2StudentMapper.insertFillByCustomMethod11(map);
        list.forEach(user -> Assertions.assertNotNull(user.getTestType()));

        list = Arrays.asList(new H2User("insertFillByCustomMethod12-1", AgeEnum.ONE), new H2User("insertFillByCustomMethod12-2", AgeEnum.ONE));
        map = new HashMap<>();
        map.put("coll", list);
        h2StudentMapper.insertFillByCustomMethod12(map);
        list.forEach(user -> Assertions.assertNotNull(user.getTestType()));

        h2Users = new H2User[]{new H2User("insertFillByCustomMethod13-1", AgeEnum.ONE), new H2User("insertFillByCustomMethod13-2", AgeEnum.ONE)};
        map = new HashMap<>();
        map.put("array", h2Users);
        h2StudentMapper.insertFillByCustomMethod13(map);
        list.forEach(user -> Assertions.assertNotNull(user.getTestType()));
    }

    @Test
    void testUpdateFill() {
        Map<String, Object> map;
        H2User h2User;
        h2User = new H2User();
        map = new HashMap<>();
        map.put("et", h2User);
        map.put("list", Arrays.asList(1L, 2L, 3L));
        h2StudentMapper.updateFillByCustomMethod1(map);
        Assertions.assertNotNull(h2User.getLastUpdatedDt());

        h2User = new H2User();
        h2StudentMapper.updateFillByCustomMethod2(Arrays.asList(1L, 2L, 3L), h2User);
        Assertions.assertNotNull(h2User.getLastUpdatedDt());

        h2User = new H2User();
        h2StudentMapper.updateFillByCustomMethod3(Arrays.asList(1L, 2L, 3L), h2User);
        Assertions.assertNotNull(h2User.getLastUpdatedDt());

        h2User = new H2User();
        h2StudentMapper.updateFillByCustomMethod4(Arrays.asList(1L, 2L, 3L), h2User);
        Assertions.assertNotNull(h2User.getLastUpdatedDt());

    }

    @Test
    void testListMapsByPage() {
        Assertions.assertEquals(userService.listMaps().size(), userService.count());
        Assertions.assertEquals(userService.listMaps(new Page<>(1, 2)).size(), userService.page(new Page<>(1, 2)).getRecords().size());
        Assertions.assertEquals(userService.listMaps(new Page<>(2, 2)).size(), userService.page(new Page<>(2, 2)).getRecords().size());

        Assertions.assertEquals(
            userService.pageMaps(new Page<>(1, 2, false)).getRecords().size(),
            userService.listMaps(new Page<>(1, 2, false)).size()
        );
        Assertions.assertEquals(
            userService.pageMaps(new Page<>(2, 2, false)).getRecords().size(),
            userService.listMaps(new Page<>(2, 2, false)).size()
        );

        Assertions.assertEquals(
            userService.pageMaps(new Page<>(1, 2, false), Wrappers.emptyWrapper()).getRecords().size(),
            userService.listMaps(new Page<>(1, 2, false), Wrappers.emptyWrapper()).size()
        );
        Assertions.assertEquals(
            userService.pageMaps(new Page<>(2, 2, false), Wrappers.emptyWrapper()).getRecords().size(),
            userService.listMaps(new Page<>(2, 2, false), Wrappers.emptyWrapper()).size()
        );
    }

    @Test
    void testListByPage() {
        Assertions.assertEquals(userService.list().size(), userService.count());
        Assertions.assertEquals(userService.list(new Page<>(1, 2)).size(), userService.page(new Page<>(1, 2)).getRecords().size());
        Assertions.assertEquals(userService.list(new Page<>(2, 2)).size(), userService.page(new Page<>(2, 2)).getRecords().size());
        Assertions.assertEquals(
            userService.list(new Page<>(1, 2, false), Wrappers.emptyWrapper()).size(),
            userService.page(new Page<>(1, 2, false), Wrappers.emptyWrapper()).getRecords().size()
        );

        List<H2User> list = userService.list(new Page<>(2, 2, false));

        Assertions.assertEquals(
            userService.list(new Page<>(2, 2, false), Wrappers.emptyWrapper()).size(),
            userService.page(new Page<>(2, 2, false), Wrappers.emptyWrapper()).getRecords().size()
        );
    }

    @Test
    void testUnchecked() {
        Wrappers.<H2User>lambdaQuery()
            .select(H2User::getAge, H2User::getAge).select(true, H2User::getDeleted, H2User::getDeleted)
            .orderBy(true, true, H2User::getAge, H2User::getAge)
            .orderByAsc(H2User::getAge, H2User::getDeleted).orderByAsc(true, H2User::getAge, H2User::getTestType)
            .orderByDesc(H2User::getDeleted, H2User::getPrice).orderByDesc(true, H2User::getDeleted, H2User::getTestType)
            .groupBy(H2User::getAge, H2User::getTestType).groupBy(true, H2User::getAge, H2User::getTestType);

        new LambdaQueryChainWrapper<>(H2User.class)
            .select(H2User::getAge).select(true, H2User::getDeleted, H2User::getDeleted)
            .orderBy(true, true, H2User::getAge, H2User::getAge)
            .orderByAsc(H2User::getAge, H2User::getDeleted).orderByAsc(true, H2User::getAge, H2User::getTestType)
            .orderByDesc(H2User::getDeleted, H2User::getPrice).orderByDesc(true, H2User::getDeleted, H2User::getTestType)
            .groupBy(H2User::getAge, H2User::getTestType).groupBy(true, H2User::getAge, H2User::getTestType);

        // 重写方法保留支持.
        new LambdaQueryChainWrapper<>(H2User.class) {
            @Override
            protected LambdaQueryChainWrapper<H2User> doOrderByDesc(boolean condition, SFunction<H2User, ?> column, List<SFunction<H2User, ?>> columns) {
                System.out.println("-------处理OrderByDesc----------");
                return super.doOrderByDesc(condition, column, columns);
            }

            @Override
            protected LambdaQueryChainWrapper<H2User> doOrderByAsc(boolean condition, SFunction<H2User, ?> column, List<SFunction<H2User, ?>> columns) {
                System.out.println("-------处理OrderByAsc----------");
                return super.doOrderByAsc(condition, column, columns);
            }

            @Override
            protected LambdaQueryChainWrapper<H2User> doOrderBy(boolean condition, boolean isAsc, SFunction<H2User, ?> column, List<SFunction<H2User, ?>> columns) {
                System.out.println("-------处理OrderBy----------");
                return super.doOrderBy(condition, isAsc, column, columns);
            }

            @Override
            protected LambdaQueryChainWrapper<H2User> doGroupBy(boolean condition, SFunction<H2User, ?> column, List<SFunction<H2User, ?>> columns) {
                System.out.println("-------处理GroupBy----------");
                return super.doGroupBy(condition, column, columns);
            }

            @Override
            protected LambdaQueryChainWrapper<H2User> doSelect(boolean condition, List<SFunction<H2User, ?>> columns) {
                System.out.println("-------处理Select----------");
                return super.doSelect(condition, columns);
            }
        }
            .select(H2User::getAge)
            .select(true, H2User::getDeleted, H2User::getDeleted)
            .orderBy(true, true, H2User::getAge, H2User::getAge)
            .orderByAsc(H2User::getAge, H2User::getDeleted).orderByAsc(true, H2User::getAge, H2User::getTestType)
            .orderByDesc(H2User::getDeleted, H2User::getPrice).orderByDesc(true, H2User::getDeleted, H2User::getTestType)
            .groupBy(H2User::getAge, H2User::getTestType).groupBy(true, H2User::getAge, H2User::getTestType);
    }

    @Test
    void testSelectObjs() {
        for (Object o : userService.listObjs()) {
            Assertions.assertEquals(o.getClass(), Long.class);
        }
        for (Long id : userService.<Long>listObjs()) {
            System.out.println(id);
        }
    }

    @Test
    void testResultSet() {
        BaseMapper<H2User> baseMapper = userService.getBaseMapper();
        Page<H2User> page = new Page<>(1, 1000000);
        System.out.println("--------------------------------------------");
        baseMapper.selectList(page, Wrappers.emptyWrapper());
        List<Long> ids = new ArrayList<>();
        System.out.println("---------------selectListByPage-------------------");
        baseMapper.selectList(page, Wrappers.emptyWrapper(), resultContext -> {
            H2User resultObject = resultContext.getResultObject();
            ids.add(resultObject.getTestId());
            System.out.println(resultObject);
        });
        System.out.println("---------------selectBatchIds-------------------");
        baseMapper.selectByIds(ids, resultContext -> System.out.println(resultContext.getResultObject()));
        System.out.println("---------------selectList-------------------");
        System.out.println("---------------selectObjs-------------------");
        baseMapper.selectObjs(Wrappers.emptyWrapper(), (ResultHandler<Long>) resultContext -> System.out.println(resultContext.getResultObject()));
        System.out.println("---------------selectByMap-------------------");
        baseMapper.selectByMap(new HashMap<>(), resultContext -> System.out.println(resultContext.getResultObject()));
        System.out.println("---------------selectMapsByPage-------------------");
        baseMapper.selectMaps(Page.of(1, 100000), Wrappers.emptyWrapper(), resultContext -> resultContext.getResultObject().forEach((k, v) -> System.out.println(k + "--------" + v)));
        System.out.println("---------------selectMaps-------------------");
        baseMapper.selectMaps(Wrappers.emptyWrapper(), resultContext -> resultContext.getResultObject().forEach((k, v) -> System.out.println(k + "--------" + v)));
    }

    @Test
    void testSelectOne() {
        Assertions.assertTrue(userService.list().size() > 2);
        Assertions.assertThrows(TooManyResultsException.class, () -> userService.getBaseMapper().selectOne(Wrappers.emptyWrapper()));
        Assertions.assertNotNull(userService.getBaseMapper().selectOne(Wrappers.emptyWrapper(), false));
    }

    @Test
    void testSaveOrUpdateTransactional1() {
        var id = IdWorker.getId();
        var userList = List.of(new H2User(id, "test-1"), new H2User(IdWorker.getId(), "test-2"), new H2User(id, "test-3"));
        Assertions.assertThrowsExactly(PersistenceException.class, () -> userService.testSaveOrUpdateTransactional1(userList));
    }

    @Test
    void testSaveOrUpdateTransactional2() {
        var id = IdWorker.getId();
        var userList = List.of(new H2User(id, "test-1"), new H2User(IdWorker.getId(), "test-2"), new H2User(id, "test-3"));
        userService.testSaveOrUpdateTransactional2(userList);
        Assertions.assertEquals(userService.getById(id).getName(), "test-3");
    }

}
