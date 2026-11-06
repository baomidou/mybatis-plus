package com.baomidou.mybatisplus.spring.transaction;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.MybatisSqlSessionFactoryBuilder;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.datasource.DataSourceProperty;
import com.baomidou.mybatisplus.extension.plugins.datasource.DbCluster;
import com.baomidou.mybatisplus.extension.plugins.datasource.DriverManagerDataSourceProvider;
import com.baomidou.mybatisplus.extension.plugins.datasource.ShardingContext;
import com.baomidou.mybatisplus.extension.plugins.datasource.ShardingDatasource;
import com.baomidou.mybatisplus.extension.plugins.datasource.ShardingProperties;
import com.baomidou.mybatisplus.extension.plugins.inner.ShardingInnerInterceptor;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.One;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.LocalCacheScope;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("integration")
class ShardingManagedTransactionTest {

    @AfterEach
    void cleanup() {
        ShardingContext.clear();
    }

    @Test
    void firstLevelCacheIsIsolatedByDatabase() throws Exception {
        try (Shards shards = new Shards()) {
            SqlSessionFactory factory = shards.factory(LocalCacheScope.SESSION, false);
            try (SqlSession session = factory.openSession()) {
                ShardMapper mapper = session.getMapper(ShardMapper.class);
                assertThat(mapper.label(1)).isEqualTo("shard-A");
                shards.route.set("b#writer");
                assertThat(mapper.label(1)).isEqualTo("shard-B");
                shards.route.set("a#writer");
                assertThat(mapper.label(1)).isEqualTo("shard-A");
            }
        }
    }

    @Test
    void secondLevelCacheIsIsolatedWithoutOpeningConnectionsOnCacheHits() throws Exception {
        try (Shards shards = new Shards()) {
            SqlSessionFactory factory = shards.factory(LocalCacheScope.STATEMENT, true);
            assertThat(label(factory)).isEqualTo("shard-A");
            int connections = shards.a.acquired;
            assertThat(label(factory)).isEqualTo("shard-A");
            assertThat(shards.a.acquired).isEqualTo(connections);
            shards.route.set("b#writer");
            assertThat(label(factory)).isEqualTo("shard-B");
            shards.route.set("a#writer");
            assertThat(label(factory)).isEqualTo("shard-A");
            assertThat(shards.a.acquired).isEqualTo(connections);
        }
    }

    @Test
    void nestedSelectCacheIsIsolatedByDatabase() throws Exception {
        try (Shards shards = new Shards()) {
            try (SqlSession session = shards.factory(LocalCacheScope.SESSION, false).openSession()) {
                ShardMapper mapper = session.getMapper(ShardMapper.class);
                assertThat(mapper.row("first").label).isEqualTo("shard-A");
                shards.route.set("b#writer");
                // Different outer parameter forces execution; the nested label(1) key is unchanged.
                assertThat(mapper.row("second").label).isEqualTo("shard-B");
            }
        }
    }

    @Test
    void cacheKeyFollowsWriteConnectionWhenReadingReplica() throws Exception {
        try (Shards shards = new Shards()) {
            try (SqlSession session = shards.factory(LocalCacheScope.SESSION, false).openSession()) {
                ShardMapper mapper = session.getMapper(ShardMapper.class);
                shards.route.set("a#reader");
                assertThat(mapper.label(1)).isEqualTo("shard-B");
                shards.route.set("a#writer");
                assertThat(mapper.insert(2)).isEqualTo(1);
                shards.route.set("a#reader");
                assertThat(mapper.label(1)).isEqualTo("shard-A");
                assertThat(mapper.label(2)).isEqualTo("inserted");
                session.commit();
            }
        }
    }

    @Test
    void springRollbackUndoesMapperInsertAndReleasesConnection() throws Exception {
        try (Shards shards = new Shards()) {
            SqlSessionTemplate session = new SqlSessionTemplate(shards.factory(LocalCacheScope.SESSION, true));
            int acquired = shards.a.acquired;
            shards.transaction().execute(status -> {
                assertThat(session.getMapper(ShardMapper.class).insert(2)).isEqualTo(1);
                status.setRollbackOnly();
                return null;
            });
            assertThat(shards.a.acquired).isEqualTo(acquired + 1);
            assertThat(shards.a.lastConnection.isClosed()).isTrue();
            assertThat(TransactionSynchronizationManager.hasResource(shards.router)).isFalse();
            assertThat(count(shards.a)).isEqualTo(1);
        }
    }

    @Test
    void springCommitPersistsMapperInsert() throws Exception {
        try (Shards shards = new Shards()) {
            SqlSessionTemplate session = new SqlSessionTemplate(shards.factory(LocalCacheScope.SESSION, false));
            shards.transaction().execute(status -> session.getMapper(ShardMapper.class).insert(2));
            assertThat(count(shards.a)).isEqualTo(2);
        }
    }

    @Test
    void springExceptionRollsBackMapperInsert() throws Exception {
        try (Shards shards = new Shards()) {
            SqlSessionTemplate session = new SqlSessionTemplate(shards.factory(LocalCacheScope.SESSION, false));
            assertThatThrownBy(() -> shards.transaction().execute(status -> {
                session.getMapper(ShardMapper.class).insert(2);
                throw new IllegalArgumentException("abort");
            })).isInstanceOf(IllegalArgumentException.class).hasMessage("abort");
            assertThat(count(shards.a)).isEqualTo(1);
        }
    }

    @Test
    void springTransactionRejectsSwitchEvenWhenTargetResultIsCached() throws Exception {
        try (Shards shards = new Shards()) {
            SqlSessionFactory factory = shards.factory(LocalCacheScope.SESSION, true);
            shards.route.set("b#writer");
            assertThat(label(factory)).isEqualTo("shard-B");
            ShardingContext.set("a#writer");
            SqlSessionTemplate session = new SqlSessionTemplate(factory);
            shards.transaction().execute(status -> {
                assertThatThrownBy(() -> session.getMapper(ShardMapper.class).label(1))
                        .hasRootCauseInstanceOf(IllegalStateException.class)
                        .hasStackTraceContaining("Cannot switch datasource from a#writer to b#writer");
                status.setRollbackOnly();
                return null;
            });
        }
    }

    @ParameterizedTest
    @EnumSource(ExecutorType.class)
    void springTransactionRejectsWriteToAnotherDatabase(ExecutorType executorType) throws Exception {
        try (Shards shards = new Shards()) {
            SqlSessionFactory factory = shards.factory(LocalCacheScope.SESSION, false);
            SqlSessionTemplate session = new SqlSessionTemplate(factory, executorType);
            shards.transaction().execute(status -> {
                session.getMapper(ShardMapper.class).insert(2);
                shards.route.set("b#writer");
                assertThatThrownBy(() -> session.getMapper(ShardMapper.class).insert(3))
                        .hasStackTraceContaining("Cannot switch datasource from a#writer to b#writer");
                status.setRollbackOnly();
                return null;
            });
            assertThat(count(shards.a)).isEqualTo(1);
            assertThat(count(shards.b)).isEqualTo(1);
        }
    }

    @Test
    void springReadReplicaRouteUsesBoundWriterAndItsCacheIdentity() throws Exception {
        try (Shards shards = new Shards()) {
            SqlSessionFactory factory = shards.factory(LocalCacheScope.SESSION, true);
            shards.route.set("a#reader");
            assertThat(label(factory)).isEqualTo("shard-B");
            ShardingContext.set("a#writer");
            SqlSessionTemplate session = new SqlSessionTemplate(factory);
            shards.transaction().execute(status -> {
                assertThat(session.getMapper(ShardMapper.class).label(1)).isEqualTo("shard-A");
                return null;
            });
        }
    }

    @Test
    void springTimeoutAndConnectionOwnershipArePreserved() throws Exception {
        try (Shards shards = new Shards()) {
            TransactionTemplate template = shards.transaction();
            template.setTimeout(30);
            template.execute(status -> {
                ShardingManagedTransaction transaction = new ShardingManagedTransaction(shards.router);
                try {
                    Connection connection = transaction.getConnection();
                    assertThat(transaction.getTimeout()).isBetween(1, 30);
                    transaction.close();
                    assertThat(connection.isClosed()).isFalse();
                    assertThat(connection.getAutoCommit()).isFalse();
                } catch (SQLException e) {
                    throw new IllegalStateException(e);
                }
                return null;
            });
            assertThat(shards.a.lastConnection.isClosed()).isTrue();
        }
    }

    @Test
    void standaloneTransactionsStillCommitAndRollback() throws Exception {
        try (Shards shards = new Shards()) {
            shards.a.setAutoCommit(false);
            SqlSessionFactory factory = shards.factory(LocalCacheScope.SESSION, false);
            try (SqlSession session = factory.openSession()) {
                session.getMapper(ShardMapper.class).insert(2);
                session.rollback();
            }
            assertThat(count(shards.a)).isEqualTo(1);
            try (SqlSession session = factory.openSession()) {
                session.getMapper(ShardMapper.class).insert(2);
                session.commit();
            }
            assertThat(count(shards.a)).isEqualTo(2);
        }
    }

    @Test
    void plainDataSourceDelegatesCommitAndRollback() throws Exception {
        try (Shards shards = new Shards()) {
            shards.a.setAutoCommit(false);
            ShardingManagedTransaction transaction = new ShardingManagedTransaction(shards.a);
            try {
                execute(transaction.getConnection(), "INSERT INTO zz_sharding_transaction VALUES(2,'inserted')");
                transaction.rollback();
                assertThat(count(shards.a)).isEqualTo(1);
                execute(transaction.getConnection(), "INSERT INTO zz_sharding_transaction VALUES(2,'inserted')");
                transaction.commit();
                assertThat(count(shards.a)).isEqualTo(2);
            } finally {
                transaction.close();
            }
        }
    }

    private static String label(SqlSessionFactory factory) {
        try (SqlSession session = factory.openSession()) {
            return session.getMapper(ShardMapper.class).label(1);
        }
    }

    private static void execute(Connection connection, String... statements) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            for (String sql : statements) {
                statement.execute(sql);
            }
        }
    }

    private static int count(DataSource datasource) throws SQLException {
        try (Connection connection = datasource.getConnection();
                Statement statement = connection.createStatement();
                ResultSet result = statement.executeQuery("SELECT COUNT(*) FROM zz_sharding_transaction")) {
            result.next();
            return result.getInt(1);
        }
    }

    @CacheNamespace
    interface ShardMapper {

        @Select("SELECT label FROM zz_sharding_transaction WHERE id=#{id}")
        String label(int id);

        @Insert("INSERT INTO zz_sharding_transaction VALUES(#{id},'inserted')")
        int insert(int id);

        @Select("SELECT id FROM zz_sharding_transaction WHERE id=1 AND #{value} IS NOT NULL")
        @Results(@Result(property = "label", column = "id", one = @One(select = "label")))
        Row row(String value);
    }

    public static class Row {

        public String label;
    }

    private static class CountingDataSource extends UnpooledDataSource {

        int acquired;
        Connection lastConnection;

        CountingDataSource() {
            super("org.h2.Driver", "jdbc:h2:mem:" + UUID.randomUUID(), "sa", "");
        }

        @Override
        public Connection getConnection() throws SQLException {
            acquired++;
            lastConnection = super.getConnection();
            return lastConnection;
        }
    }

    private static class Shards implements AutoCloseable {

        final CountingDataSource a = new CountingDataSource();
        final CountingDataSource b = new CountingDataSource();
        final Connection keepA = a.getConnection();
        final Connection keepB = b.getConnection();
        final AtomicReference<String> route = new AtomicReference<>("a#writer");
        final ShardingDatasource router;

        Shards() throws Exception {
            execute(
                    keepA, "CREATE TABLE zz_sharding_transaction(id INT PRIMARY KEY,label VARCHAR(40))",
                    "INSERT INTO zz_sharding_transaction VALUES(1,'shard-A')");
            execute(
                    keepB, "CREATE TABLE zz_sharding_transaction(id INT PRIMARY KEY,label VARCHAR(40))",
                    "INSERT INTO zz_sharding_transaction VALUES(1,'shard-B')");
            DataSourceProperty writer = new DataSourceProperty();
            writer.setKey("writer");
            writer.setCluster(DbCluster.master);
            DataSourceProperty reader = new DataSourceProperty();
            reader.setKey("reader");
            reader.setCluster(DbCluster.slave);
            ShardingProperties properties = new ShardingProperties();
            properties.setPrimary("a");
            Map<String, List<DataSourceProperty>> groups = new LinkedHashMap<>();
            groups.put("a", Arrays.asList(writer, reader));
            groups.put("b", Collections.singletonList(writer));
            properties.setDatasource(groups);
            Map<String, DataSource> sources = new LinkedHashMap<>();
            sources.put("a#writer", a);
            sources.put("a#reader", b);
            sources.put("b#writer", b);
            router = new ShardingDatasource(new DriverManagerDataSourceProvider(), sources, Collections.emptyMap(),
                    properties);
        }

        SqlSessionFactory factory(LocalCacheScope scope, boolean secondLevel) {
            MybatisConfiguration configuration = new MybatisConfiguration();
            configuration.setEnvironment(new Environment("sharding", new ShardingManagedTransactionFactory(), router));
            configuration.setLocalCacheScope(scope);
            configuration.setCacheEnabled(secondLevel);
            MybatisPlusInterceptor plugin = new MybatisPlusInterceptor();
            plugin.addInnerInterceptor(new ShardingInnerInterceptor((ms, parameter, previousKey) -> {
                String selected = route.get();
                if (ms.getSqlCommandType() != SqlCommandType.SELECT) {
                    selected += ShardingContext.WRITE_FLAG;
                }
                ShardingContext.set(selected);
                return true;
            }));
            configuration.addInterceptor(plugin);
            configuration.addMapper(ShardMapper.class);
            return new MybatisSqlSessionFactoryBuilder().build(configuration);
        }

        TransactionTemplate transaction() {
            return new TransactionTemplate(new DataSourceTransactionManager(router));
        }

        @Override
        public void close() throws Exception {
            keepA.close();
            keepB.close();
        }
    }
}
