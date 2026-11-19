package com.baomidou.mybatisplus.test.h2;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.MybatisSqlSessionFactoryBuilder;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.datasource.*;
import com.baomidou.mybatisplus.extension.plugins.inner.ShardingInnerInterceptor;
import com.baomidou.mybatisplus.spring.transaction.ShardingManagedTransaction;
import com.baomidou.mybatisplus.spring.transaction.ShardingManagedTransactionFactory;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * Exercises statement reuse against distinct physical databases, with result caches disabled.
 */
@Tag("integration")
class ShardingStatementReuseTest {

    @AfterEach
    void clearRoute() {
        ShardingContext.clear();
    }

    @ParameterizedTest
    @EnumSource(ExecutorType.class)
    void readsFollowRouteChangesIncludingReturningToTheFirstShard(ExecutorType type) throws Exception {
        try (Shards shards = new Shards(); SqlSession session = shards.factory.openSession(type)) {
            ShardMapper mapper = session.getMapper(ShardMapper.class);
            assertThat(mapper.label(1)).isEqualTo("A");
            shards.route.set("b#writer");
            assertThat(mapper.label(1)).isEqualTo("B");
            shards.route.set("a#writer");
            assertThat(mapper.label(1)).isEqualTo("A");
        }
    }

    @ParameterizedTest
    @EnumSource(ExecutorType.class)
    void commitsWritesToEachSelectedShard(ExecutorType type) throws Exception {
        try (Shards shards = new Shards()) {
            try (SqlSession session = shards.factory.openSession(type)) {
                ShardMapper mapper = session.getMapper(ShardMapper.class);
                mapper.insert(2, "a2");
                shards.route.set("b#writer");
                mapper.insert(3, "b3");
                shards.route.set("a#writer");
                mapper.insert(4, "a4");
                session.commit();
            }
            assertThat(shards.labels(shards.a)).containsExactly("A", "a2", "a4");
            assertThat(shards.labels(shards.b)).containsExactly("B", "b3");
        }
    }

    @ParameterizedTest
    @EnumSource(ExecutorType.class)
    void rollingBackAlsoUndoesBatchesFlushedDuringRouteChanges(ExecutorType type) throws Exception {
        try (Shards shards = new Shards()) {
            try (SqlSession session = shards.factory.openSession(type)) {
                ShardMapper mapper = session.getMapper(ShardMapper.class);
                mapper.insert(2, "a2");
                shards.route.set("b#writer");
                mapper.insert(3, "b3");
                session.rollback();
            }
            assertThat(shards.labels(shards.a)).containsExactly("A");
            assertThat(shards.labels(shards.b)).containsExactly("B");
        }
    }

    @Test
    void sameRouteKeepsTheBatchIntactAndIgnoresTheWriteMarker() throws Exception {
        try (Shards shards = new Shards(); SqlSession session = shards.factory.openSession(ExecutorType.BATCH)) {
            ShardMapper mapper = session.getMapper(ShardMapper.class);
            mapper.insert(2, "a2");
            mapper.insert(3, "a3");
            assertThat(session.flushStatements()).singleElement().satisfies(result -> {
                assertThat(result.getParameterObjects()).hasSize(2);
                assertThat(result.getUpdateCounts()).containsExactly(1, 1);
            });
            session.rollback();
        }
    }

    @Test
    void sessionsSharingAnInterceptorTrackTheirOwnPreviousRoute() throws Exception {
        try (Shards shards = new Shards();
                SqlSession first = shards.factory.openSession(ExecutorType.REUSE);
                SqlSession second = shards.factory.openSession(ExecutorType.REUSE)) {
            assertThat(first.getMapper(ShardMapper.class).label(1)).isEqualTo("A");
            shards.route.set("b#writer");
            assertThat(second.getMapper(ShardMapper.class).label(1)).isEqualTo("B");
            assertThat(first.getMapper(ShardMapper.class).label(1)).isEqualTo("B");
            shards.route.set("a#writer");
            assertThat(second.getMapper(ShardMapper.class).label(1)).isEqualTo("A");
        }
    }

    @Test
    void batchAfterSelectKeysExecuteOnTheOriginalShard() throws Exception {
        try (Shards shards = new Shards()) {
            Map<String, Object> a = new HashMap<>();
            a.put("id", 2);
            a.put("label", "a2");
            Map<String, Object> b = new HashMap<>();
            b.put("id", 3);
            b.put("label", "b3");
            try (SqlSession session = shards.factory.openSession(ExecutorType.BATCH)) {
                session.getMapper(ShardMapper.class).insertWithKey(a);
                shards.route.set("b#writer");
                session.getMapper(ShardMapper.class).insertWithKey(b);
                assertThat(ShardingContext.stripWriteFlag(ShardingContext.get())).isEqualTo("b#writer");
                session.commit();
            }
            assertThat(a).containsEntry("key", "A");
            assertThat(b).containsEntry("key", "B");
        }
    }

    @Test
    void aFailedFlushDoesNotRunTheNextShardOrLoseTheRequestedContext() throws Exception {
        try (Shards shards = new Shards()) {
            try (SqlSession session = shards.factory.openSession(ExecutorType.BATCH)) {
                session.getMapper(ShardMapper.class).insert(1, "duplicate");
                shards.route.set("b#writer");
                assertThatThrownBy(() -> session.getMapper(ShardMapper.class).insert(3, "b3"))
                        .hasMessageContaining("Error updating database");
                assertThat(ShardingContext.stripWriteFlag(ShardingContext.get())).isEqualTo("b#writer");
                session.rollback();
            }
            assertThat(shards.labels(shards.a)).containsExactly("A");
            assertThat(shards.labels(shards.b)).containsExactly("B");
        }
    }

    @Test
    void routingChecksDoNotOpenConnectionsAndFlushUnderThePreviousContext() throws Exception {
        try (Shards shards = new Shards()) {
            ShardingManagedTransaction transaction = new ShardingManagedTransaction(shards.router);
            Executor executor = mock(Executor.class);
            ShardingContext.set("a#writer");
            transaction.prepareStatement(executor);
            ShardingContext.set("a#writer" + ShardingContext.WRITE_FLAG);
            transaction.prepareStatement(executor);
            assertThat(mockingDetails(executor).getInvocations()).isEmpty();
            ShardingContext.set("b#writer");
            doAnswer(invocation -> {
                assertThat(ShardingContext.get()).isEqualTo("a#writer");
                return Collections.emptyList();
            }).when(executor).flushStatements();
            transaction.prepareStatement(executor);
            verify(executor).flushStatements();
            verify(executor).clearLocalCache();
            verifyNoMoreInteractions(executor);
            assertThat(ShardingContext.get()).isEqualTo("b#writer");
            // No connection is cached: removing the routes must make a later acquisition fail.
            shards.router.removeDataSource("a", "writer").removeDataSource("b", "writer");
            assertThatThrownBy(transaction::getConnection).hasMessageContaining("Unknown datasource route");
            transaction.close();
        }
    }

    @Test
    void readsPinnedToTheExistingWriteConnectionDoNotFlush() throws Exception {
        try (Shards shards = new Shards()) {
            ShardingManagedTransaction transaction = new ShardingManagedTransaction(shards.router);
            Executor executor = mock(Executor.class);
            try {
                ShardingContext.set("a#writer" + ShardingContext.WRITE_FLAG);
                transaction.prepareStatement(executor);
                transaction.getConnection();
                ShardingContext.set("a#reader");
                transaction.prepareStatement(executor);
                assertThat(mockingDetails(executor).getInvocations()).isEmpty();
            } finally {
                transaction.close();
            }
        }
    }

    @Test
    void aPlainDatasourceDoesNotParticipateInRoutingChecks() throws Exception {
        ShardingManagedTransaction transaction = new ShardingManagedTransaction(new UnpooledDataSource());
        Executor executor = mock(Executor.class);
        ShardingContext.set("a#writer");
        transaction.prepareStatement(executor);
        ShardingContext.set("b#writer");
        transaction.prepareStatement(executor);
        assertThat(mockingDetails(executor).getInvocations()).isEmpty();
        transaction.close();
    }

    interface ShardMapper {

        @Select("SELECT label FROM shard_record WHERE id=#{id}")
        String label(int id);

        @Insert("INSERT INTO shard_record(id,label) VALUES(#{id},#{label})")
        int insert(@Param("id") int id, @Param("label") String label);

        @Insert("INSERT INTO shard_record(id,label) VALUES(#{id},#{label})")
        @SelectKey(statement = "SELECT label FROM shard_record WHERE id=1", keyProperty = "key", before = false, resultType = String.class)
        int insertWithKey(Map<String, Object> record);
    }

    private static class Shards implements AutoCloseable {

        final UnpooledDataSource a = database();
        final UnpooledDataSource b = database();
        final Connection keepA;
        final Connection keepB;
        final ShardingDatasource router;
        final AtomicReference<String> route = new AtomicReference<>("a#writer");
        final SqlSessionFactory factory;

        Shards() throws Exception {
            keepA = a.getConnection();
            keepB = b.getConnection();
            initialize(keepA, "A");
            initialize(keepB, "B");
            DataSourceProperty writer = new DataSourceProperty();
            writer.setKey("writer");
            writer.setCluster(DbCluster.master);
            ShardingProperties properties = new ShardingProperties();
            properties.setPrimary("a");
            Map<String, List<DataSourceProperty>> groups = new LinkedHashMap<>();
            groups.put("a", Collections.singletonList(writer));
            groups.put("b", Collections.singletonList(writer));
            properties.setDatasource(groups);
            router = new ShardingDatasource(new DriverManagerDataSourceProvider(), Map.of("a#writer", a, "b#writer", b),
                    Collections.emptyMap(), properties);
            MybatisConfiguration configuration = new MybatisConfiguration();
            configuration.setEnvironment(new Environment("shards", new ShardingManagedTransactionFactory(), router));
            configuration.setLocalCacheScope(LocalCacheScope.STATEMENT);
            configuration.setCacheEnabled(false);
            MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
            interceptor.addInnerInterceptor(new ShardingInnerInterceptor((ms, parameter, previous) -> {
                String selected = ms.getId().endsWith(SELECT_KEY_SUFFIX) ? ShardingContext.get() : route.get();
                if (ms.getSqlCommandType() != SqlCommandType.SELECT)
                    selected += ShardingContext.WRITE_FLAG;
                ShardingContext.set(selected);
                return true;
            }));
            configuration.addInterceptor(interceptor);
            configuration.addMapper(ShardMapper.class);
            factory = new MybatisSqlSessionFactoryBuilder().build(configuration);
        }

        private static final String SELECT_KEY_SUFFIX = "!selectKey";

        private static UnpooledDataSource database() {
            UnpooledDataSource ds = new UnpooledDataSource("org.h2.Driver",
                    "jdbc:h2:mem:" + UUID.randomUUID() + ";DATABASE_TO_LOWER=TRUE", "sa", "");
            ds.setAutoCommit(false);
            return ds;
        }

        private static void initialize(Connection c, String label) throws Exception {
            try (Statement s = c.createStatement()) {
                s.execute("CREATE TABLE shard_record(id INT PRIMARY KEY,label VARCHAR(40))");
                s.execute("INSERT INTO shard_record VALUES(1,'" + label + "')");
            }
            c.commit();
        }

        List<String> labels(UnpooledDataSource ds) throws Exception {
            try (Connection c = ds.getConnection();
                    Statement s = c.createStatement();
                    ResultSet rs = s.executeQuery("SELECT label FROM shard_record ORDER BY id")) {
                List<String> result = new ArrayList<>();
                while (rs.next())
                    result.add(rs.getString(1));
                return result;
            }
        }

        @Override
        public void close() throws Exception {
            keepA.close();
            keepB.close();
        }
    }
}
