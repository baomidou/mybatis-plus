package com.baomidou.mybatisplus.extension.plugins.inner;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.MybatisSqlSessionFactoryBuilder;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import lombok.Data;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("integration")
class OptimisticLockerInnerInterceptorTest {

    @Test
    void shouldNotUseAnotherColumnsEqualityAfterVersionInequality() throws Exception {
        assertUpdatedVersion(new UpdateWrapper<LockerPredicateEntity>().ne("version", 0).eq("id", 7), 5);
    }

    @Test
    void shouldNotUseAnotherColumnsEqualityAfterVersionRange() throws Exception {
        assertUpdatedVersion(new UpdateWrapper<LockerPredicateEntity>().gt("version", 0).eq("age", 40), 5);
    }

    @Test
    void shouldNotCarryVersionMatchIntoNestedCondition() throws Exception {
        assertUpdatedVersion(
                new UpdateWrapper<LockerPredicateEntity>().ge("version", 0).and(nested -> nested.eq("id", 7)), 5);
    }

    @Test
    void shouldStillFindVersionEqualityAfterOtherPredicates() throws Exception {
        assertUpdatedVersion(
                new UpdateWrapper<LockerPredicateEntity>().ne("version", 0).eq("age", 40).eq("version", 5), 6);
    }

    @Test
    void shouldStillFindNestedVersionEquality() throws Exception {
        assertUpdatedVersion(
                new UpdateWrapper<LockerPredicateEntity>().eq("id", 7).and(nested -> nested.eq("version", 5)), 6);
    }

    @Test
    void shouldRejectStaleVersionEquality() throws Exception {
        assertUpdatedVersion(new UpdateWrapper<LockerPredicateEntity>().eq("id", 7).eq("version", 4), 5, 0);
    }

    private void assertUpdatedVersion(UpdateWrapper<LockerPredicateEntity> wrapper, int expected) throws Exception {
        assertUpdatedVersion(wrapper, expected, 1);
    }

    private void assertUpdatedVersion(UpdateWrapper<LockerPredicateEntity> wrapper, int expected, int updatedRows)
            throws Exception {
        UnpooledDataSource dataSource = new UnpooledDataSource("org.h2.Driver", "jdbc:h2:mem:" + UUID.randomUUID(),
                "sa", "");
        try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {
            statement.execute(
                    "CREATE TABLE zz_locker_predicate (id BIGINT PRIMARY KEY, age INT, name VARCHAR(30), version INT)");
            statement.execute("INSERT INTO zz_locker_predicate VALUES (7, 40, 'before', 5)");
            MybatisConfiguration configuration = new MybatisConfiguration();
            configuration.setEnvironment(new Environment("test", new JdbcTransactionFactory(), dataSource));
            MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
            interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor(true));
            configuration.addInterceptor(interceptor);
            configuration.addMapper(LockerPredicateMapper.class);
            try (SqlSession session = new MybatisSqlSessionFactoryBuilder().build(configuration).openSession(true)) {
                LockerPredicateMapper mapper = session.getMapper(LockerPredicateMapper.class);
                assertThat(mapper.update(wrapper.set("name", "after"))).isEqualTo(updatedRows);
                LockerPredicateEntity result = mapper.selectById(7);
                assertThat(result.getName()).isEqualTo(updatedRows == 1 ? "after" : "before");
                assertThat(result.getVersion()).isEqualTo(expected);
            }
        }
    }

    interface LockerPredicateMapper extends BaseMapper<LockerPredicateEntity> {
    }

    @Data
    @TableName("zz_locker_predicate")
    static class LockerPredicateEntity {

        private Long id;
        private Integer age;
        private String name;
        @Version
        private Integer version;
    }
}
