package com.baomidou.mybatisplus.test;

import com.baomidou.mybatisplus.core.MybatisSqlSessionFactoryBuilder;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.handlers.MybatisEnumTypeHandler;
import com.baomidou.mybatisplus.core.toolkit.MybatisBatchUtils;
import com.baomidou.mybatisplus.test.h2.entity.H2User;
import com.baomidou.mybatisplus.test.h2.enums.AgeEnum;
import com.baomidou.mybatisplus.test.h2.mapper.H2UserMapper;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.UUID;
import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 原生Mybatis测试
 *
 * @author nieqiurong 2019/2/27.
 */
@ExtendWith(MockitoExtension.class)
@Tag("integration")
class MybatisTest {

    private static SqlSessionFactory sqlSessionFactory;

    private static DataSource dataSource;

    @BeforeAll
    public static void init() throws IOException, SQLException {
        Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
        sqlSessionFactory = new MybatisSqlSessionFactoryBuilder().build(reader);
        dataSource = sqlSessionFactory.getConfiguration().getEnvironment().getDataSource();
        Configuration configuration = sqlSessionFactory.getConfiguration();
        TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
        /*
         *  如果是将defaultEnumTypeHandler设置成MP的处理器,
         *  请自行注册处理非MP枚举处理类的原生枚举类型
         */
        typeHandlerRegistry.register(AgeEnum.class, MybatisEnumTypeHandler.class); //这里我举起了个栗子
        Connection connection = dataSource.getConnection();
        ScriptRunner scriptRunner = new ScriptRunner(connection);
        scriptRunner.runScript(Resources.getResourceAsReader("h2/user.ddl.sql"));
    }

    /**
     * 这里连的是和 Spring 用例同一个 {@code jdbc:h2:mem:test}，别的类插入的行会一直留在库里， 所以用例进出都要把自己那批固定主键物理删掉：
     * 不然"回滚后查不到""插入前不存在"这类断言就成了看谁先跑。
     */
    @BeforeEach
    @AfterEach
    void purgeOwnRows() throws SQLException {
        try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {
            statement.executeUpdate(
                    "DELETE FROM h2user WHERE test_id IN (66, 1000, 1001, 2000, 2001, 3000, 3001, "
                            + "4000, 4010, 4011, 4015, 4016, 4020, 11010, 11011)");
        }
    }

    @Test
    void shouldPerformCrudWithNativeMybatis() {
        // 名字每次换一个，count/delete 的条数才只跟自己插的两行有关（NAME 列只有 30 个字符，取 uuid 前段）
        String name = "mt-" + UUID.randomUUID().toString().substring(0, 12);
        try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
            H2UserMapper mapper = sqlSession.getMapper(H2UserMapper.class);
            assertThat(mapper.myInsertWithNameVersion(name, 2)).isEqualTo(1);
            assertThat(mapper.insert(new H2User(name))).isEqualTo(1);
            assertThat(mapper.selectCount(new QueryWrapper<H2User>().lambda().eq(H2User::getName, name))).isEqualTo(2);
            assertThat(mapper.delete(new QueryWrapper<H2User>().lambda().eq(H2User::getName, name))).isEqualTo(2);
            H2User h2User = new H2User(66L, "66666", AgeEnum.THREE, 666);
            assertThat(mapper.insert(h2User)).isEqualTo(1);
            h2User.setName("7777777777");
            H2User user = mapper.selectById(66L);
            assertThat(user).isNotNull();
            assertThat(user.getAge()).isEqualTo(AgeEnum.THREE);
            assertThat(user.getTestType()).isNotNull();
            assertThat(mapper.updateById(new H2User(66L, "777777"))).isEqualTo(1);
            assertThat(mapper.deleteById(66L)).isEqualTo(1);
            assertThat(mapper.selectById(66L)).isNull();
        }
    }

    @Test
    void shouldBatchInsertWithAutoCommitFalse() {
        var userList = List.of(new H2User(2000L, "测试"), new H2User(2001L, "测试"));
        MybatisBatchUtils.execute(sqlSessionFactory, userList, H2UserMapper.class.getName() + ".insert");
        try (var sqlSession = sqlSessionFactory.openSession()) {
            var mapper = sqlSession.getMapper(H2UserMapper.class);
            for (H2User u : userList) {
                assertThat(mapper.selectById(u.getTestId())).isNotNull();
            }
        }
    }

    @Test
    void shouldRollbackBatchOnDuplicateKeyException() {
        assertThatThrownBy(
                () -> MybatisBatchUtils.execute(
                        sqlSessionFactory, List.of(new H2User(1000L, "测试"), new H2User(1000L, "测试")),
                        H2UserMapper.class.getName() + ".insert"))
                .isExactlyInstanceOf(PersistenceException.class);
        try (var sqlSession = sqlSessionFactory.openSession()) {
            var mapper = sqlSession.getMapper(H2UserMapper.class);
            assertThat(mapper.selectById(1000L)).isNull();
        }
        assertThatThrownBy(
                () -> MybatisBatchUtils.execute(
                        sqlSessionFactory, List.of(new H2User(1001L, "测试"), new H2User(1001L, "测试")),
                        H2UserMapper.class.getName() + ".insert", 1))
                .isExactlyInstanceOf(PersistenceException.class);
        try (var sqlSession = sqlSessionFactory.openSession()) {
            var mapper = sqlSession.getMapper(H2UserMapper.class);
            assertThat(mapper.selectById(1001L)).isNotNull();
        }
    }

    @Test
    void shouldRollbackBatchOnProcessorException() {
        // 1010/1011 是 user.insert.sql 里的种子行，换成没被占用的主键才能断言"回滚后确实查不到"
        List<H2User> userList = List.of(new H2User(11010L, "测试"), new H2User(11011L, "测试"));
        assertThatThrownBy(
                () -> MybatisBatchUtils
                        .execute(sqlSessionFactory, userList, H2UserMapper.class.getName() + ".insert", parameter -> {
                            if (parameter.getTestId() == 11011L) {
                                throw new RuntimeException("出异常了");
                            }
                            return parameter;
                        }))
                .isExactlyInstanceOf(RuntimeException.class);
        try (var sqlSession = sqlSessionFactory.openSession()) {
            var mapper = sqlSession.getMapper(H2UserMapper.class);
            assertThat(mapper.selectById(11010L)).isNull();
        }
    }

    @Test
    void shouldBatchInsertWithAutoCommitTrue() {
        var userList = List.of(new H2User(3000L, "测试"), new H2User(3001L, "测试"));
        MybatisBatchUtils.execute(sqlSessionFactory, userList, true, H2UserMapper.class.getName() + ".insert");
        try (var sqlSession = sqlSessionFactory.openSession()) {
            var mapper = sqlSession.getMapper(H2UserMapper.class);
            for (H2User u : userList) {
                assertThat(mapper.selectById(u.getTestId())).isNotNull();
            }
        }
    }

    @Test
    void shouldRetainPartialResultsOnDuplicateKeyWithAutoCommit() {
        assertThatThrownBy(
                () -> MybatisBatchUtils.execute(
                        sqlSessionFactory, List.of(new H2User(4000L, "测试"), new H2User(4000L, "测试")), true,
                        H2UserMapper.class.getName() + ".insert"))
                .isExactlyInstanceOf(PersistenceException.class);
        try (var sqlSession = sqlSessionFactory.openSession()) {
            var mapper = sqlSession.getMapper(H2UserMapper.class);
            assertThat(mapper.selectById(4000L)).isNotNull();
        }
        assertThatThrownBy(
                () -> MybatisBatchUtils.execute(
                        sqlSessionFactory, List.of(new H2User(4020L, "测试"), new H2User(4020L, "测试"), 1), true,
                        H2UserMapper.class.getName() + ".insert"))
                .isExactlyInstanceOf(PersistenceException.class);
        try (var sqlSession = sqlSessionFactory.openSession()) {
            var mapper = sqlSession.getMapper(H2UserMapper.class);
            assertThat(mapper.selectById(4020L)).isNotNull();
        }
    }

    @Test
    void shouldRollbackBatchOnProcessorExceptionWithAutoCommit() {
        assertThatThrownBy(
                () -> MybatisBatchUtils.execute(
                        sqlSessionFactory, List.of(new H2User(4010L, "测试"), new H2User(4011L, "测试")), true,
                        H2UserMapper.class.getName() + ".insert", parameter -> {
                            if (parameter.getTestId() == 4011L) {
                                throw new RuntimeException("出异常了");
                            }
                            return parameter;
                        }))
                .isExactlyInstanceOf(RuntimeException.class);
        try (var sqlSession = sqlSessionFactory.openSession()) {
            var mapper = sqlSession.getMapper(H2UserMapper.class);
            assertThat(mapper.selectById(4010L)).isNull();
            assertThat(mapper.selectById(4011L)).isNull();
        }
        assertThatThrownBy(
                () -> MybatisBatchUtils.execute(
                        sqlSessionFactory, List.of(new H2User(4015L, "测试"), new H2User(4016L, "测试")), true,
                        H2UserMapper.class.getName() + ".insert", parameter -> {
                            if (parameter.getTestId() == 4016L) {
                                throw new RuntimeException("出异常了");
                            }
                            return parameter;
                        }, 1))
                .isExactlyInstanceOf(RuntimeException.class);
        try (var sqlSession = sqlSessionFactory.openSession()) {
            var mapper = sqlSession.getMapper(H2UserMapper.class);
            assertThat(mapper.selectById(4015L)).isNotNull();
            assertThat(mapper.selectById(4016L)).isNull();
        }
    }

}
