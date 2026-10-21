package com.baomidou.mybatisplus.test.h2;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
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
import com.baomidou.mybatisplus.test.h2.mapper.H2UserMapper;
import com.baomidou.mybatisplus.test.h2.service.IH2UserService;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.select.Select;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory;
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

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.fail;

/**
 * Mybatis Plus H2 Junit Test
 *
 * @author Caratacus
 * @since 2017/4/1
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:h2/spring-test-h2.xml"})
class H2UserTest extends H2TestSupport {

    @Autowired
    protected IH2UserService userService;
    @Autowired
    SqlSessionFactory sqlSessionFactory;

    @Autowired
    private H2StudentMapper h2StudentMapper;

    @Autowired
    private H2UserMapper h2UserMapper;

    public void initBatchLimitation(int limitation) {
        if (sqlSessionFactory instanceof DefaultSqlSessionFactory) {
            Configuration configuration = sqlSessionFactory.getConfiguration();
            for (Interceptor interceptor : configuration.getInterceptors()) {
                if (interceptor instanceof MybatisPlusInterceptor) {
                    List<InnerInterceptor> innerInterceptors = ((MybatisPlusInterceptor) interceptor).getInterceptors();
                    for (InnerInterceptor innerInterceptor : innerInterceptors) {
                        if (innerInterceptor instanceof DataChangeRecorderInnerInterceptor) {
                            ((DataChangeRecorderInnerInterceptor) innerInterceptor).setBatchUpdateLimit(limitation)
                                    .openBatchUpdateLimitation();
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
        assertThat(row).isEqualTo(1);
    }

    @Test
    @Order(2)
    void testInsertObjectWithParam() {
        String name = "自定义insert带Param注解";
        int version = 1;
        int row = userService.myInsertWithParam(name, version);
        assertThat(row).isEqualTo(1);
    }

    @Test
    @Order(3)
    void testInsertObjectWithoutParam() {
        String name = "自定义insert带Param注解";
        int version = 1;
        int row = userService.myInsertWithoutParam(name, version);
        assertThat(row).isEqualTo(1);
    }

    @Test
    @Order(6)
    void testSelectLambdaById() {
        H2User h2User = userService.getOne(Wrappers.<H2User>lambdaQuery().eq(H2User::getTestId, 101));
        assertThat(h2User).isNotNull();
    }

    @Test
    @Order(7)
    void testLambdaTypeHandler() {
        // 演示 json 格式 Wrapper TypeHandler 查询
        H2User h2User = userService.getOne(
                Wrappers.<H2User>lambdaQuery().apply(
                        "name={0,typeHandler=" + H2userNameJsonTypeHandler.class.getCanonicalName() + "}",
                        "{\"id\":101,\"name\":\"Tomcat\"}"));
        assertThat(h2User).isNotNull();
    }

    @Test
    @Order(10)
    void testEntityWrapperSelectSql() {
        QueryWrapper<H2User> ew = new QueryWrapper<>();
        ew.select("test_id, name, age");
        List<H2User> list = userService.list(ew);
        for (H2User u : list) {
            assertThat(u.getTestId()).isNotNull();
            assertThat(u.getName()).isNotNull();
            assertThat(u.getPrice()).isNull();
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
        assertThat(list).isNotNull();
        for (H2User u : list) {
            assertThat(u.getName()).isEqualTo(nameParam);
            assertThat(u.getTestId()).isNotNull();
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
        assertThat(count).isNotEqualTo(0);
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
        assertThat(userDB.getVersion().intValue()).isEqualTo(1);
        assertThat(userDB.getCreatedDt().compareTo(dateTime)).isEqualTo(0);

        userDB.setName("992");
        userDB.setCreatedDt(dateTime);
        System.out.println("===============================================");
        userService.updateById(userDB);
        assertThat(userDB.getVersion().intValue()).as("updated version value should be updated to entity").isEqualTo(2);

        userDB = userService.getById(id);
        assertThat(userDB.getVersion().intValue()).isEqualTo(2);
        assertThat(userDB.getName()).isEqualTo("992");
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
                assertThat(u.getVersion().intValue()).as("optLocker should update version+=1").isEqualTo(100);
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
                assertThat(u.getVersion().intValue()).as("optLocker should update version+=1").isEqualTo(100);
            } else {
                assertThat(u.getPrice()).as("other records should not be updated")
                        .isEqualTo(idPriceMap.get(u.getTestId()));
            }
        }
        userService.update(new H2User().setPrice(BigDecimal.ZERO), null);
        for (H2User u : userService.list(new QueryWrapper<>())) {
            System.out.println(u.getName() + "," + u.getAge() + "," + u.getVersion());
            assertThat(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP).intValue()).as("all records should be updated")
                    .isEqualTo(u.getPrice().setScale(2, RoundingMode.HALF_UP).intValue());
        }
        try {
            initBatchLimitation(3);
            userService.update(new H2User().setPrice(BigDecimal.ZERO), null);
            fail("SHOULD NOT REACH HERE");
        } catch (Exception e) {
            assertThat(checkIsDataUpdateLimitationException(e)).isTrue();
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
            assertThat(CollectionUtils.isEmpty(list)).isTrue();
        }
    }

    @Test
    @Order(19)
    void testSimpleTransactional() {
        try {
            userService.testSimpleTransactional();
        } catch (MybatisPlusException e) {
            List<H2User> list = userService.list(new QueryWrapper<H2User>().like("name", "simple"));
            assertThat(CollectionUtils.isEmpty(list)).isTrue();
        }
    }

    @Test
    @Order(20)
    void testSaveOrUpdateBatchTransactional() {
        try {
            userService.testSaveOrUpdateBatchTransactional();
        } catch (MybatisPlusException e) {
            List<H2User> list = userService.list(new QueryWrapper<H2User>().like("name", "savOrUpdate"));
            assertThat(CollectionUtils.isEmpty(list)).isTrue();
        }
    }

    @Test
    @Order(21)
    void testSaveBatch() {
        assertThat(userService.saveBatch(List.of(new H2User("saveBatch0")))).isTrue();
        assertThat(
                userService.saveBatch(
                        List.of(
                                new H2User("saveBatch1"), new H2User("saveBatch2"), new H2User("saveBatch3"),
                                new H2User("saveBatch4"))))
                .isTrue();
        assertThat(userService.count(new QueryWrapper<H2User>().like("name", "saveBatch"))).isEqualTo(5);
        assertThat(
                userService.saveBatch(
                        List.of(
                                new H2User("saveBatch5"), new H2User("saveBatch6"), new H2User("saveBatch7"),
                                new H2User("saveBatch8")),
                        2))
                .isTrue();
        assertThat(userService.count(new QueryWrapper<H2User>().like("name", "saveBatch"))).isEqualTo(9);
    }

    @Test
    @Order(22)
    void testUpdateBatch() {
        assertThat(
                userService.updateBatchById(
                        Arrays.asList(
                                new H2User(1010L, "batch1010"), new H2User(1011L, "batch1011"),
                                new H2User(1010L, "batch1010"), new H2User(1012L, "batch1012"))))
                .isTrue();
        assertThat(userService.getById(1010L).getName()).isEqualTo("batch1010");
        assertThat(userService.getById(1011L).getName()).isEqualTo("batch1011");
        assertThat(userService.getById(1012L).getName()).isEqualTo("batch1012");
        assertThat(
                userService.updateBatchById(
                        Arrays.asList(
                                new H2User(1010L, "batch1010A"), new H2User(1011L, "batch1011A"),
                                new H2User(1010L, "batch1010"), new H2User(1012L, "batch1012")),
                        1))
                .isTrue();
        assertThat(userService.getById(1010L).getName()).isEqualTo("batch1010");
        assertThat(userService.getById(1011L).getName()).isEqualTo("batch1011A");
        assertThat(userService.getById(1012L).getName()).isEqualTo("batch1012");
    }

    @Test
    @Order(23)
    void testSaveOrUpdateBatch() {
        assertThat(
                userService.saveOrUpdateBatch(
                        Arrays.asList(
                                new H2User(1010L, "batch1010"), new H2User("batch1011"), new H2User(1010L, "batch1010"),
                                new H2User("batch1015"))))
                .isTrue();
        assertThat(userService.getById(1010L).getName()).isEqualTo("batch1010");
        assertThat(userService.count(new QueryWrapper<H2User>().eq("name", "batch1011"))).isEqualTo(1);
        assertThat(userService.count(new QueryWrapper<H2User>().eq("name", "batch1015"))).isEqualTo(1);
        assertThat(
                userService.saveOrUpdateBatch(
                        Arrays.asList(
                                new H2User(1010L, "batch1010A"), new H2User("batch1011AB"),
                                new H2User(1010L, "batch1010"), new H2User("batch1016")),
                        1))
                .isTrue();
        assertThat(userService.getById(1010L).getName()).isEqualTo("batch1010");
        assertThat(userService.count(new QueryWrapper<H2User>().eq("name", "batch1011AB"))).isEqualTo(1);
        assertThat(userService.count(new QueryWrapper<H2User>().eq("name", "batch1016"))).isEqualTo(1);
    }

    @Test
    @Order(24)
    void testSimpleAndBatch() {
        assertThat(userService.save(new H2User("testSimpleAndBatch1", 0))).isTrue();
        assertThat(userService.count(new QueryWrapper<H2User>().eq("name", "testSimpleAndBatch1"))).isEqualTo(1);
        assertThat(
                userService.saveOrUpdateBatch(
                        Arrays.asList(
                                new H2User("testSimpleAndBatch2"), new H2User("testSimpleAndBatch3"),
                                new H2User("testSimpleAndBatch4")),
                        1))
                .isTrue();
        assertThat(userService.count(new QueryWrapper<H2User>().like("name", "testSimpleAndBatch"))).isEqualTo(4);
    }

    @Test
    @Order(25)
    void testSimpleAndBatchTransactional() {
        try {
            userService.testSimpleAndBatchTransactional();
        } catch (MybatisPlusException e) {
            List<H2User> list = userService.list(new QueryWrapper<H2User>().like("name", "simpleAndBatchTx"));
            assertThat(CollectionUtils.isEmpty(list)).isTrue();
        }
    }

    @Test
    @Order(26)
    void testServiceImplInnerLambdaQuery() {
        H2User tomcat = userService.lambdaQuery().eq(H2User::getName, "Tomcat").one();
        assertThat(tomcat).isNotNull();
        assertThat(userService.lambdaQuery().like(H2User::getName, "a").count().longValue()).isNotEqualTo(0L);

        List<H2User> users = userService.lambdaQuery().like(H2User::getName, "T").ne(H2User::getAge, AgeEnum.TWO)
                .ge(H2User::getVersion, 1).isNull(H2User::getPrice).list();
        assertThat(users.isEmpty()).isTrue();
    }

    @Test
    @Order(27)
    void testServiceChainQuery() {
        H2User tomcat = userService.query().eq("name", "Tomcat").one();
        assertThat(tomcat).as("tomcat should not be null").isNotNull();
        userService.query().nested(i -> i.eq("name", "Tomcat")).list();
        userService.lambdaUpdate().set(H2User::getName, "Tom").eq(H2User::getName, "Tomcat").update();
    }

    @Test
    @Order(28)
    void testSaveBatchException() {
        try {
            userService.saveBatch(Arrays.asList(new H2User(1L, "tom"), new H2User(1L, "andy")));
        } catch (Exception e) {
            assertThat(e).isInstanceOf(DataAccessException.class);
        }
    }

    @Test
    @Order(29)
    @Transactional
    void testClearSqlSessionCache() {
        H2User h2User;
        h2User = userService.getById(996102919L);
        assertThat(h2User).isNull();
        userService.saveBatch(Collections.singletonList(new H2User(996102919L, "靓仔")));
        h2User = userService.getById(996102919L);
        assertThat(h2User).isNotNull();
    }

    @Test
    @Order(30)
    void testSaveBatchNoTransactional1() {
        userService.testSaveBatchNoTransactional1();
        assertThat(userService.count(new QueryWrapper<H2User>().like("name", "testSaveBatchNoTransactional1")))
                .isEqualTo(3);
    }

    @Test
    @Order(30)
    void testSaveBatchNoTransactional2() {
        try {
            userService.testSaveBatchNoTransactional2();
        } catch (Exception e) {
            assertThat(userService.count(new QueryWrapper<H2User>().like("name", "testSaveBatchNoTransactional2")))
                    .isEqualTo(3);
        }
    }

    @Test
    @Order(31)
    void testSpaceCharacter() {
        assertThat(StringUtils.isNotBlank(" ")).isFalse();
        assertThat(StringUtils.checkValNotNull(" ")).isTrue();
        H2User h2User = new H2User();
        h2User.setName(" ");
        assertThat(CollectionUtils.isEmpty(userService.list(new QueryWrapper<>(h2User).gt("age", 1).lt("age", 5))))
                .isTrue();
    }

    @Test
    @Order(32)
    void testSqlInjectionByCustomSqlSegment() {
        // Preparing: select * from h2user WHERE (name LIKE ?)
        // Parameters: %y%%(String)
        List<H2User> h2Users = userService.testCustomSqlSegment(new QueryWrapper<H2User>().like("name", "y%"));
        assertThat(h2Users.size()).isEqualTo(2);
    }

    @Test
    @Order(33)
    void testWrapperSetAliasByParam() {
        // Preparing: select * from h2user WHERE (name LIKE ?)
        // Parameters: %y%%(String)
        List<H2User> h2Users = userService.testWrapperSetAliasByParam(new QueryWrapper<H2User>().like("name", "y%"));
        assertThat(h2Users.size()).isEqualTo(2);
    }

    @Test
    @Order(34)
    void testMultiWrapperQuery() {
        // Preparing: select * from h2user a inner join h2user b on a.name=b.name WHERE (a.name LIKE ?) and (b.name = ?)
        // Parameters: %y%%(String), Jerry(String)
        QueryWrapper<H2User> leftTable = new QueryWrapper<H2User>() {

            @Override
            protected String columnToString(String column) {
                return "a." + super.columnToString(column);
            }
        }.like("name", "y%");
        System.out.println(leftTable.getCustomSqlSegment());
        QueryWrapper<H2User> rightTable = new QueryWrapper<H2User>() {

            @Override
            protected String columnToString(String column) {
                return "b." + super.columnToString(column);
            }
        }.eq("name", "Jerry");
        List<H2User> h2Users = userService.testMultiWrapperQuery(leftTable, rightTable);
        assertThat(h2Users.size()).isEqualTo(1);
    }

    @Test
    @Order(35)
    void testUpdateWrapperSetAliasByParam() {
        H2User user = new H2User("aliasBefore", AgeEnum.ONE);
        assertThat(userService.save(user)).isTrue();

        UpdateWrapper<H2User> updateWrapper = new UpdateWrapper<H2User>().set("name", "aliasAfter")
                .eq("test_id", user.getTestId());

        assertThat(h2UserMapper.testUpdateWrapperSetAliasByParam(updateWrapper)).isEqualTo(1);
        assertThat(userService.getById(user.getTestId()).getName()).isEqualTo("aliasAfter");
    }

    @Test
    void myQueryWithGroupByOrderBy() {
        // 自带 group by / order by 的自定义语句再叠分页：分页只截断行数，不改列
        List<Map<?, ?>> rows = userService.mySelectMaps();
        assertThat(rows).isNotEmpty().hasSizeLessThanOrEqualTo(3);
        rows.forEach(row -> assertThat(new HashSet<Object>(row.keySet())).contains("NAME", "COUNT(AGE)"));
    }

    @Test
    void notParser() throws Exception {
        final String targetSql1 = "SELECT * FROM user WHERE id NOT LIKE ?";
        final Select select = (Select) CCJSqlParserUtil.parse(targetSql1);
        assertThat(select.toString()).isEqualTo(targetSql1);

        final String targetSql2 = "SELECT * FROM user WHERE id NOT IN (?)";
        final Select select2 = (Select) CCJSqlParserUtil.parse(targetSql2);
        assertThat(select2.toString()).isEqualTo(targetSql2);

        final String targetSql3 = "SELECT * FROM user WHERE id IS NOT NULL";
        final Select select3 = (Select) CCJSqlParserUtil.parse(targetSql3);
        assertThat(select3.toString()).isEqualTo(targetSql3);
    }

    /**
     * CTO 说批量插入性能不行，让我们来分析一下问题在哪里
     */
    @Test
    void batchInsertPerformanceTest() {
        long before = userService.count();
        List<H2User> users = mockUser(10_000, 99989);
        assertThat(userService.saveBatch(users)).isTrue();
        // 卧槽，速度挺快的，而且一万条真的落库了
        assertThat(userService.count()).isEqualTo(before + 10_000L);
    }

    /**
     * 模拟一群人
     *
     * @param size 这群人的数量
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
        assertThat(userService.count(queryWrapper)).isEqualTo(0);
        queryWrapper.clear();
        queryWrapper.eq("name", "逗号");
        assertThat(userService.count(queryWrapper)).isEqualTo(1);
        UpdateWrapper<H2User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("name", "逗号二号");
        assertThat(userService.update(updateWrapper.eq("name", "逗号一号"))).isFalse();
        updateWrapper.clear();
        updateWrapper.set("name", "逗号一号");
        assertThat(userService.update(updateWrapper.eq("name", "逗号"))).isTrue();
    }

    @Test
    void testLambdaWrapperClear() {
        userService.save(new H2User("小红", AgeEnum.TWO));
        LambdaQueryWrapper<H2User> lambdaQueryWrapper = new QueryWrapper<H2User>().lambda().eq(H2User::getName, "小宝");
        lambdaQueryWrapper.orderByDesc(H2User::getName);
        assertThat(userService.count(lambdaQueryWrapper)).isEqualTo(0);
        lambdaQueryWrapper.clear();
        lambdaQueryWrapper.eq(H2User::getName, "小红");
        assertThat(userService.count(lambdaQueryWrapper)).isEqualTo(1);
        LambdaUpdateWrapper<H2User> lambdaUpdateWrapper = new UpdateWrapper<H2User>().lambda()
                .set(H2User::getName, "小红二号");
        assertThat(userService.update(lambdaUpdateWrapper.eq(H2User::getName, "小红一号"))).isFalse();
        lambdaUpdateWrapper.clear();
        lambdaUpdateWrapper.set(H2User::getName, "小红一号");
        assertThat(userService.update(lambdaUpdateWrapper.eq(H2User::getName, "小红"))).isTrue();
    }

    @Test
    void testLogicDelWithFill() {
        H2User h2User = new H2User("逻辑删除(根据ID)不填充", AgeEnum.TWO);
        userService.save(h2User);
        userService.removeById(h2User.getTestId());
        assertThat(h2User.getLastUpdatedDt()).isNull();
        h2User = new H2User("测试逻辑(根据实体)删除填充", AgeEnum.TWO);
        userService.save(h2User);
        userService.removeById(h2User);
        assertThat(h2User.getLastUpdatedDt()).isNotNull();
    }

    /**
     * 观察 {@link com.baomidou.mybatisplus.core.toolkit.LambdaUtils#extract(SFunction)}
     */
    @RepeatedTest(1000)
    void testLambdaCache() {
        lambdaCache();
    }

    private void lambdaCache() {
        assertThat(
                Wrappers.<H2User>lambdaQuery().eq(H2User::getAge, 2).eq(H2User::getName, 2).eq(H2User::getPrice, 2)
                        .getTargetSql())
                .isEqualTo("(age = ? AND name = ? AND price = ?)");
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
        assertThat(h2User.getLastUpdatedDt()).isNotNull();

        h2User = new H2User("test");
        h2UserMapper.insert(h2User);
        h2UserMapper.deleteById(h2User.getTestId(), false);
        h2User = h2UserMapper.getById(h2User.getTestId());
        assertThat(h2User.getLastUpdatedDt()).isNull();

        h2User = new H2User("test");
        h2UserMapper.insert(h2User);
        h2UserMapper.deleteById(String.valueOf(h2User.getTestId()));
        h2User = h2UserMapper.getById(h2User.getTestId());
        assertThat(h2User.getLastUpdatedDt()).isNotNull();
    }

    @Test
    void testPageOrderBy() {
        // test https://gitee.com/baomidou/mybatis-plus/issues/I4BGE2
        Page<H2User> page = Page.of(1, 10);
        assertThat(
                userService.page(page, Wrappers.<H2User>query().select("test_id,name").orderByDesc("test_id"))
                        .getPages() > 0)
                .isTrue();
        assertThat(userService.page(page, Wrappers.<H2User>lambdaQuery().orderByDesc(H2User::getTestId)).getPages() > 0)
                .isTrue();
    }

    @Test
    void testPageNegativeSize() {
        Page<H2User> page = Page.of(1, -1);
        userService.lambdaQuery().page(page);
        assertThat(page.getTotal()).isEqualTo(0);
        assertThat(page.getRecords().size()).isEqualTo(userService.lambdaQuery().list(Page.of(1, -1, false)).size());
    }

    @Test
    void testDeleteByFill() {
        // 共享内存库，只碰自己插入的行，收尾全部逻辑删除，别的用例查不到。
        String own = "deleteByFill-";

        H2User byId = insertUser(own + "byId");
        assertThat(columnOf(byId.getTestId(), "deleted")).as("新行处于未删除状态，后面的 WHERE deleted=0 才拦得住").isEqualTo(0);
        assertThat(userService.removeById(byId.getTestId(), true)).as("useFill=true 会先把 id 包成一个新实体再删，才走得到填充分支")
                .isTrue();
        assertThat(columnOf(byId.getTestId(), "deleted")).isEqualTo(1);
        assertThat(columnOf(byId.getTestId(), "last_updated_dt")).as("走填充分支就写时间戳").isNotNull();
        assertThat(userService.removeById(byId.getTestId(), true)).as("已删除的行被 deleted=0 挡在外面，逻辑删除幂等").isFalse();

        H2User noFill = insertUser(own + "noFill");
        assertThat(userService.removeById(noFill.getTestId(), false)).isTrue();
        assertThat(columnOf(noFill.getTestId(), "deleted")).isEqualTo(1);
        assertThat(columnOf(noFill.getTestId(), "last_updated_dt"))
                .as("useFill=false 把原始 id 直接交给 SQL，参数不是实体，填充那段 set 整个被跳过").isNull();

        H2User stringId = insertUser(own + "stringId");
        assertThat(userService.removeById(String.valueOf(stringId.getTestId()), true)).as("主键是 Long，传字符串照样被转成主键类型")
                .isTrue();
        assertThat(columnOf(stringId.getTestId(), "last_updated_dt")).isNotNull();

        H2User entityArg = insertUser(own + "entityArg");
        assertThat(userService.removeById(entityArg, false)).as("入参是实体时 useFill 不参与判断，实体自己就是填充目标").isTrue();
        assertThat(entityArg.getLastUpdatedDt()).as("填充改写的就是传进来的那个对象").isNotNull();
        assertThat(columnOf(entityArg.getTestId(), "last_updated_dt")).isNotNull();

        assertThat(userService.removeById(IdWorker.getId())).as("库里没有这个主键，影响 0 行").isFalse();
    }

    @Test
    void testRemoveBatchByIdsByFill() {
        // name 列只有 VARCHAR(30)，前缀留短一点
        String own = "bDel-";
        H2User batchId = insertUser(own + "batchId");
        H2User batchEntity = insertUser(own + "batchEntity");
        assertThat(userService.removeBatchByIds(Arrays.asList(batchId.getTestId(), batchEntity))).as("列表里 id 和实体混着放也能删")
                .isTrue();
        assertThat(columnOf(batchId.getTestId(), "deleted")).isEqualTo(1);
        assertThat(columnOf(batchId.getTestId(), "last_updated_dt")).as("批量 useFill=true 挂一个空实体当填充目标，整行一起填")
                .isNotNull();
        assertThat(columnOf(batchEntity.getTestId(), "last_updated_dt")).isNotNull();
        assertThat(batchEntity.getLastUpdatedDt()).as("列表里的实体也会被送去填充，只是落库取的是框架追加的那个填充目标").isNotNull();

        H2User batchNoFillId = insertUser(own + "batchNoFillId");
        H2User batchNoFillEntity = insertUser(own + "batchNoFillEntity");
        assertThat(
                userService.removeByIds(
                        Arrays.asList(batchNoFillId.getTestId(), batchNoFillEntity, IdWorker.getId()), false))
                .as("列表里混一个不存在的主键也不会报错").isTrue();
        assertThat(columnOf(batchNoFillId.getTestId(), "deleted")).isEqualTo(1);
        assertThat(columnOf(batchNoFillId.getTestId(), "last_updated_dt")).as("批量 useFill=false 不挂填充目标，只写 deleted")
                .isNull();
        assertThat(columnOf(batchNoFillEntity.getTestId(), "last_updated_dt")).isNull();
        assertThat(batchNoFillEntity.getLastUpdatedDt()).as("对象仍会被填充器改写，useFill=false 省掉的只是 SQL 里那段 set").isNotNull();
    }

    private H2User insertUser(String name) {
        H2User user = new H2User(name, AgeEnum.ONE);
        assertThat(userService.save(user)).as("插入 %s", name).isTrue();
        assertThat(user.getTestId()).as("主键由雪花生成后回填到实体").isNotNull();
        return user;
    }

    /* 绕过逻辑删除过滤读原始列，才看得到 deleted 和 last_updated_dt 的真实落库值 */
    private Object columnOf(Long id, String column) {
        return jdbcTemplate.queryForObject("select " + column + " from h2user where test_id = " + id, Object.class);
    }

    @Test
    @Order(25)
    void testServiceImplInnerLambdaQueryConstructorSetEntity() {
        H2User condition = new H2User();
        condition.setName("Tomcat");
        H2User user = userService.lambdaQuery(condition).one();
        assertThat(user).isNotNull();
        assertThat(user.getName()).isEqualTo("Tomcat");
        H2User h2User = userService.lambdaQuery().setEntity(condition).one();
        assertThat(h2User).isNotNull();
        assertThat(h2User.getName()).isEqualTo("Tomcat");
    }

    @Test
    @Order(26)
    void testServiceGetOptById() {
        H2User user = new H2User(1L, "Evan");
        userService.save(user);
        Optional<H2User> optional = userService.getOptById(1L);
        assertThat(optional).isPresent();
        assertThat(optional.get().getTestId()).isEqualTo(1L);
        assertThat(optional.get().getName()).isEqualTo("Evan");
    }

    @Test
    @Order(27)
    void testServiceGetOneOpt() {
        String name = "DavidOpt";
        // 查不到时是空 Optional，不是异常也不是 null
        assertThat(userService.getOneOpt(Wrappers.<H2User>lambdaQuery().eq(H2User::getName, name))).isEmpty();
        userService.save(new H2User(name, AgeEnum.ONE));
        Optional<H2User> optional = userService.getOneOpt(Wrappers.<H2User>lambdaQuery().eq(H2User::getName, name));
        assertThat(optional).isPresent();
        assertThat(optional.get().getName()).isEqualTo(name);
    }

    @Test
    @Order(28)
    void testServiceGetOneOptThrowEx() {
        userService.getOneOpt(new LambdaQueryWrapper<H2User>().eq(H2User::getName, "test1"), false)
                .ifPresent(u -> log(u.toString()));

        userService.getOneOpt(new LambdaQueryWrapper<H2User>().eq(H2User::getName, "test"), false)
                .ifPresent(u -> log(u.toString()));

        // 异常情况
        assertThatThrownBy(() -> userService.getOneOpt(Wrappers.<H2User>lambdaQuery().like(H2User::getName, "tes")))
                .isInstanceOf(TooManyResultsException.class);
    }

    @Test
    void testInsertFill() {
        H2User h2User;
        h2User = new H2User("insertFillByCustomMethod1", AgeEnum.ONE);
        h2StudentMapper.insertFillByCustomMethod1(h2User);
        assertThat(h2User.getTestType()).isNotNull();

        h2User = new H2User("insertFillByCustomMethod2", AgeEnum.ONE);
        h2StudentMapper.insertFillByCustomMethod2(h2User);
        assertThat(h2User.getTestType()).isNotNull();

        h2User = new H2User("insertFillByCustomMethod3", AgeEnum.ONE);
        h2StudentMapper.insertFillByCustomMethod3(h2User, "fillByCustomMethod3");
        assertThat(h2User.getTestType()).isNotNull();

        List<H2User> list;
        list = Arrays.asList(
                new H2User("insertFillByCustomMethod4-1", AgeEnum.ONE),
                new H2User("insertFillByCustomMethod4-2", AgeEnum.ONE));
        h2StudentMapper.insertFillByCustomMethod4(list);
        list.forEach(user -> assertThat(user.getTestType()).isNotNull());

        list = Arrays.asList(
                new H2User("insertFillByCustomMethod5-1", AgeEnum.ONE),
                new H2User("insertFillByCustomMethod5-2", AgeEnum.ONE));
        h2StudentMapper.insertFillByCustomMethod5(list);
        list.forEach(user -> assertThat(user.getTestType()).isNotNull());

        list = Arrays.asList(
                new H2User("insertFillByCustomMethod6-1", AgeEnum.ONE),
                new H2User("insertFillByCustomMethod6-2", AgeEnum.ONE));
        h2StudentMapper.insertFillByCustomMethod6(list);
        list.forEach(user -> assertThat(user.getTestType()).isNotNull());

        list = Arrays.asList(
                new H2User("insertFillByCustomMethod7-1", AgeEnum.ONE),
                new H2User("insertFillByCustomMethod7-2", AgeEnum.ONE));
        h2StudentMapper.insertFillByCustomMethod7(list);
        list.forEach(user -> assertThat(user.getTestType()).isNotNull());

        H2User[] h2Users;
        h2Users = new H2User[]{new H2User("insertFillByCustomMethod8-1", AgeEnum.ONE),
                new H2User("insertFillByCustomMethod8-2", AgeEnum.ONE)};
        h2StudentMapper.insertFillByCustomMethod8(h2Users);
        Arrays.stream(h2Users).forEach(user -> assertThat(user.getTestType()).isNotNull());

        h2Users = new H2User[]{new H2User("insertFillByCustomMethod9-1", AgeEnum.ONE),
                new H2User("insertFillByCustomMethod9-2", AgeEnum.ONE)};
        h2StudentMapper.insertFillByCustomMethod9(h2Users);
        Arrays.stream(h2Users).forEach(user -> assertThat(user.getTestType()).isNotNull());

        Map<String, Object> map;
        h2User = new H2User("insertFillByCustomMethod10", AgeEnum.ONE);
        map = new HashMap<>();
        map.put("et", h2User);
        h2StudentMapper.insertFillByCustomMethod10(map);
        assertThat(h2User.getTestType()).isNotNull();

        list = Arrays.asList(
                new H2User("insertFillByCustomMethod11-1", AgeEnum.ONE),
                new H2User("insertFillByCustomMethod11-2", AgeEnum.ONE));
        map = new HashMap<>();
        map.put("list", list);
        h2StudentMapper.insertFillByCustomMethod11(map);
        list.forEach(user -> assertThat(user.getTestType()).isNotNull());

        list = Arrays.asList(
                new H2User("insertFillByCustomMethod12-1", AgeEnum.ONE),
                new H2User("insertFillByCustomMethod12-2", AgeEnum.ONE));
        map = new HashMap<>();
        map.put("coll", list);
        h2StudentMapper.insertFillByCustomMethod12(map);
        list.forEach(user -> assertThat(user.getTestType()).isNotNull());

        h2Users = new H2User[]{new H2User("insertFillByCustomMethod13-1", AgeEnum.ONE),
                new H2User("insertFillByCustomMethod13-2", AgeEnum.ONE)};
        map = new HashMap<>();
        map.put("array", h2Users);
        h2StudentMapper.insertFillByCustomMethod13(map);
        list.forEach(user -> assertThat(user.getTestType()).isNotNull());
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
        assertThat(h2User.getLastUpdatedDt()).isNotNull();

        h2User = new H2User();
        h2StudentMapper.updateFillByCustomMethod2(Arrays.asList(1L, 2L, 3L), h2User);
        assertThat(h2User.getLastUpdatedDt()).isNotNull();

        h2User = new H2User();
        h2StudentMapper.updateFillByCustomMethod3(Arrays.asList(1L, 2L, 3L), h2User);
        assertThat(h2User.getLastUpdatedDt()).isNotNull();

        h2User = new H2User();
        h2StudentMapper.updateFillByCustomMethod4(Arrays.asList(1L, 2L, 3L), h2User);
        assertThat(h2User.getLastUpdatedDt()).isNotNull();

    }

    @Test
    void testListMapsByPage() {
        assertThat(userService.count()).isEqualTo(userService.listMaps().size());
        assertThat(userService.page(new Page<>(1, 2)).getRecords().size())
                .isEqualTo(userService.listMaps(new Page<>(1, 2)).size());
        assertThat(userService.page(new Page<>(2, 2)).getRecords().size())
                .isEqualTo(userService.listMaps(new Page<>(2, 2)).size());

        assertThat(userService.listMaps(new Page<>(1, 2, false)).size())
                .isEqualTo(userService.pageMaps(new Page<>(1, 2, false)).getRecords().size());
        assertThat(userService.listMaps(new Page<>(2, 2, false)).size())
                .isEqualTo(userService.pageMaps(new Page<>(2, 2, false)).getRecords().size());

        assertThat(userService.listMaps(new Page<>(1, 2, false), Wrappers.emptyWrapper()).size())
                .isEqualTo(userService.pageMaps(new Page<>(1, 2, false), Wrappers.emptyWrapper()).getRecords().size());
        assertThat(userService.listMaps(new Page<>(2, 2, false), Wrappers.emptyWrapper()).size())
                .isEqualTo(userService.pageMaps(new Page<>(2, 2, false), Wrappers.emptyWrapper()).getRecords().size());
    }

    @Test
    void testListByPage() {
        assertThat(userService.count()).isEqualTo(userService.list().size());
        assertThat(userService.page(new Page<>(1, 2)).getRecords().size())
                .isEqualTo(userService.list(new Page<>(1, 2)).size());
        assertThat(userService.page(new Page<>(2, 2)).getRecords().size())
                .isEqualTo(userService.list(new Page<>(2, 2)).size());
        assertThat(userService.page(new Page<>(1, 2, false), Wrappers.emptyWrapper()).getRecords().size())
                .isEqualTo(userService.list(new Page<>(1, 2, false), Wrappers.emptyWrapper()).size());

        List<H2User> list = userService.list(new Page<>(2, 2, false));

        assertThat(userService.page(new Page<>(2, 2, false), Wrappers.emptyWrapper()).getRecords().size())
                .isEqualTo(userService.list(new Page<>(2, 2, false), Wrappers.emptyWrapper()).size());
    }

    @Test
    void testUnchecked() {
        // 特征化：重复列名不去重，group by 拼在 order by 前面
        String segment = " GROUP BY age,test_type,age,test_type ORDER BY age ASC,age ASC,age ASC,deleted ASC,"
                + "age ASC,test_type ASC,deleted DESC,price DESC,deleted DESC,test_type DESC";

        assertThat(
                Wrappers.<H2User>lambdaQuery().select(H2User::getAge, H2User::getAge)
                        .select(true, H2User::getDeleted, H2User::getDeleted)
                        .orderBy(true, true, H2User::getAge, H2User::getAge)
                        .orderByAsc(H2User::getAge, H2User::getDeleted)
                        .orderByAsc(true, H2User::getAge, H2User::getTestType)
                        .orderByDesc(H2User::getDeleted, H2User::getPrice)
                        .orderByDesc(true, H2User::getDeleted, H2User::getTestType)
                        .groupBy(H2User::getAge, H2User::getTestType).groupBy(true, H2User::getAge, H2User::getTestType)
                        .getTargetSql())
                .isEqualTo(segment);

        // 链式包装自己不给 SQL，得从它包着的 wrapper 上取
        assertThat(
                new LambdaQueryChainWrapper<>(H2User.class).select(H2User::getAge)
                        .select(true, H2User::getDeleted, H2User::getDeleted)
                        .orderBy(true, true, H2User::getAge, H2User::getAge)
                        .orderByAsc(H2User::getAge, H2User::getDeleted)
                        .orderByAsc(true, H2User::getAge, H2User::getTestType)
                        .orderByDesc(H2User::getDeleted, H2User::getPrice)
                        .orderByDesc(true, H2User::getDeleted, H2User::getTestType)
                        .groupBy(H2User::getAge, H2User::getTestType).groupBy(true, H2User::getAge, H2User::getTestType)
                        .getWrapper().getTargetSql())
                .isEqualTo(segment);

        // 重写方法保留支持.
        int[] called = new int[5];
        LambdaQueryChainWrapper<H2User> overridden = new LambdaQueryChainWrapper<>(H2User.class) {

            @Override
            protected LambdaQueryChainWrapper<H2User> doOrderByDesc(boolean condition, SFunction<H2User, ?> column,
                    List<SFunction<H2User, ?>> columns) {
                called[0]++;
                return super.doOrderByDesc(condition, column, columns);
            }

            @Override
            protected LambdaQueryChainWrapper<H2User> doOrderByAsc(boolean condition, SFunction<H2User, ?> column,
                    List<SFunction<H2User, ?>> columns) {
                called[1]++;
                return super.doOrderByAsc(condition, column, columns);
            }

            @Override
            protected LambdaQueryChainWrapper<H2User> doOrderBy(boolean condition, boolean isAsc,
                    SFunction<H2User, ?> column, List<SFunction<H2User, ?>> columns) {
                called[2]++;
                return super.doOrderBy(condition, isAsc, column, columns);
            }

            @Override
            protected LambdaQueryChainWrapper<H2User> doGroupBy(boolean condition, SFunction<H2User, ?> column,
                    List<SFunction<H2User, ?>> columns) {
                called[3]++;
                return super.doGroupBy(condition, column, columns);
            }

            @Override
            protected LambdaQueryChainWrapper<H2User> doSelect(boolean condition, List<SFunction<H2User, ?>> columns) {
                called[4]++;
                return super.doSelect(condition, columns);
            }
        };
        overridden.select(H2User::getAge).select(true, H2User::getDeleted, H2User::getDeleted)
                .orderBy(true, true, H2User::getAge, H2User::getAge).orderByAsc(H2User::getAge, H2User::getDeleted)
                .orderByAsc(true, H2User::getAge, H2User::getTestType).orderByDesc(H2User::getDeleted, H2User::getPrice)
                .orderByDesc(true, H2User::getDeleted, H2User::getTestType).groupBy(H2User::getAge, H2User::getTestType)
                .groupBy(true, H2User::getAge, H2User::getTestType);
        assertThat(called).as("五个受保护的钩子方法都得被子类重写接住").doesNotContain(0);
        assertThat(overridden.getWrapper().getTargetSql()).isEqualTo(segment);
    }

    @Test
    void testSelectObjs() {
        for (Object o : userService.listObjs()) {
            assertThat(o.getClass()).isEqualTo(Long.class);
        }
        for (Long id : userService.<Long>listObjs()) {
            System.out.println(id);
        }
    }

    @Test
    void testResultSet() {
        BaseMapper<H2User> baseMapper = userService.getBaseMapper();
        Page<H2User> page = new Page<>(1, 1000000);
        List<H2User> rows = baseMapper.selectList(page, Wrappers.emptyWrapper());
        List<Long> ids = new ArrayList<>();
        // 流式回调逐行收集的结果必须和普通分页查出来的一模一样
        baseMapper.selectList(
                page, Wrappers.emptyWrapper(), resultContext -> ids.add(resultContext.getResultObject().getTestId()));
        assertThat(ids).containsExactlyElementsOf(rows.stream().map(H2User::getTestId).toList());

        int[] visited = new int[1];
        baseMapper.selectByIds(ids, resultContext -> visited[0]++);
        assertThat(visited[0]).isEqualTo(ids.size());

        visited[0] = 0;
        baseMapper.selectObjs(Wrappers.emptyWrapper(), (ResultHandler<Long>) resultContext -> visited[0]++);
        assertThat(visited[0]).isEqualTo(rows.size());

        visited[0] = 0;
        baseMapper.selectByMap(new HashMap<>(), resultContext -> visited[0]++);
        assertThat(visited[0]).isEqualTo(rows.size());

        visited[0] = 0;
        baseMapper.selectMaps(Page.of(1, 100000), Wrappers.emptyWrapper(), resultContext -> visited[0]++);
        assertThat(visited[0]).isEqualTo(rows.size());

        visited[0] = 0;
        baseMapper.selectMaps(Wrappers.emptyWrapper(), resultContext -> visited[0]++);
        assertThat(visited[0]).isEqualTo(rows.size());
    }

    @Test
    void testSelectOne() {
        assertThat(userService.list().size() > 2).isTrue();
        assertThatThrownBy(() -> userService.getBaseMapper().selectOne(Wrappers.emptyWrapper()))
                .isInstanceOf(TooManyResultsException.class);
        assertThat(userService.getBaseMapper().selectOne(Wrappers.emptyWrapper(), false)).isNotNull();
    }

    @Test
    void testSaveOrUpdateTransactional1() {
        var id = IdWorker.getId();
        var userList = List
                .of(new H2User(id, "test-1"), new H2User(IdWorker.getId(), "test-2"), new H2User(id, "test-3"));
        assertThatThrownBy(() -> userService.testSaveOrUpdateTransactional1(userList))
                .isExactlyInstanceOf(PersistenceException.class);
    }

    @Test
    void testSaveOrUpdateTransactional2() {
        var id = IdWorker.getId();
        var userList = List
                .of(new H2User(id, "test-1"), new H2User(IdWorker.getId(), "test-2"), new H2User(id, "test-3"));
        userService.testSaveOrUpdateTransactional2(userList);
        assertThat(userService.getById(id).getName()).isEqualTo("test-3");
    }

    @Test
    void testOrderByExpression() {
        Page<H2User> page = new Page<>();
        page.addOrder(OrderItem.withExpression("""
                CASE
                  WHEN age > 1 THEN 2
                  WHEN age < 1 THEN 1
                END
                """));
        page.addOrder(OrderItem.withExpression("""
                CASE
                  WHEN name IS NOT NULL THEN 0
                  ELSE 1
                END
                """, false));
        assertThatCode(() -> userService.page(page)).doesNotThrowAnyException();
    }

    @Test
    @Transactional
    void selectUsersWithCursor() {
        // 使用 try-with-resources 确保 Cursor 被正确关闭
        // 注意：必须添加 @Transactional 注解，保证 SqlSession 在迭代期间保持打开状态
        LambdaQueryWrapper<H2User> lqw = Wrappers.<H2User>lambdaQuery().eq(H2User::getAge, 3);
        try (Cursor<H2User> cursor = userService.getBaseMapper().selectWithCursor(lqw)) {

            // 遍历游标，逐条处理数据
            Iterator<H2User> iterator = cursor.iterator();
            while (iterator.hasNext()) {
                // 每次读取一条数据
                H2User user = iterator.next();

                // 处理单条记录
                System.out.println("Processing user: " + user.getName());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertThatThrownBy(() -> userService.getBaseMapper().selectOne(lqw))
                .isInstanceOf(TooManyResultsException.class);
    }

}
