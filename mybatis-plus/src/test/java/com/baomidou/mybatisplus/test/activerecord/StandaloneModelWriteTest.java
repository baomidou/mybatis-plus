package com.baomidou.mybatisplus.test.activerecord;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.MybatisSqlSessionFactoryBuilder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.baomidou.mybatisplus.spring.activerecord.Model;
import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mybatis.spring.SqlSessionUtils;
import org.mybatis.spring.transaction.SpringManagedTransactionFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("integration")
class StandaloneModelWriteTest {

    private UnpooledDataSource dataSource;
    private Connection connection;
    private SqlSessionFactory factory;

    @BeforeEach
    void setUp() throws SQLException {
        dataSource = new UnpooledDataSource("org.h2.Driver", "jdbc:h2:mem:" + UUID.randomUUID(), "sa", "");
        dataSource.setAutoCommit(false);
        connection = dataSource.getConnection();
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE standalone_note (id BIGINT PRIMARY KEY, note VARCHAR(40))");
        }
        MybatisConfiguration configuration = new MybatisConfiguration(
                new Environment("test", new SpringManagedTransactionFactory(), dataSource));
        configuration.addMapper(NoteMapper.class);
        factory = new MybatisSqlSessionFactoryBuilder().build(configuration);
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.close();
    }

    @Test
    void activeRecordWritesSurviveSessionClose() {
        var note = new Note();
        note.setId(1L);
        note.setNote("first");
        assertThat(note.insert()).isTrue();
        assertThat(note.selectById()).extracting(Note::getNote).isEqualTo("first");

        note.setNote("second");
        assertThat(note.updateById()).isTrue();
        assertThat(note.selectById()).extracting(Note::getNote).isEqualTo("second");

        assertThat(note.deleteById()).isTrue();
        assertThat(note.selectById()).isNull();
    }

    @Test
    void dbWritesSurviveSessionCloseWithoutAMapperBean() {
        var note = new Note();
        note.setId(1L);
        note.setNote("saved");
        assertThat(Db.save(note)).isTrue();
        assertThat(Db.getById(1L, Note.class)).extracting(Note::getNote).isEqualTo("saved");
    }

    @Test
    void failedCallbackDiscardsPendingWrites() {
        var note = new Note();
        note.setId(1L);
        assertThatThrownBy(() -> SqlHelper.execute(Note.class, mapper -> {
            mapper.insert(note);
            throw new IllegalStateException("cancel write");
        })).isInstanceOf(IllegalStateException.class).hasMessage("cancel write");
        assertThat(note.selectById()).isNull();
    }

    @Test
    void activeTransactionKeepsBatchWritesPendingUntilFlush() {
        factory.getConfiguration().setDefaultExecutorType(ExecutorType.BATCH);
        var jdbc = new JdbcTemplate(dataSource);
        new TransactionTemplate(new DataSourceTransactionManager(dataSource)).executeWithoutResult(status -> {
            var note = new Note();
            note.setId(1L);
            note.insert();
            assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM standalone_note", Long.class)).isZero();
            var session = SqlSessionUtils.getSqlSession(factory);
            try {
                session.flushStatements();
            } finally {
                SqlSessionUtils.closeSqlSession(session, factory);
            }
            assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM standalone_note", Long.class)).isOne();
            status.setRollbackOnly();
        });
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM standalone_note", Long.class)).isZero();
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void closesThePrivateBatchCursorWithoutCommittingTheOuterTransaction(boolean fail) {
        var jdbc = new JdbcTemplate(dataSource);
        var cursor = new AtomicReference<Cursor<Note>>();
        new TransactionTemplate(new DataSourceTransactionManager(dataSource)).executeWithoutResult(status -> {
            Runnable batch = () -> SqlHelper.executeBatch(factory, LogFactory.getLog(getClass()), session -> {
                var mapper = session.getMapper(NoteMapper.class);
                var note = new Note();
                note.setId(1L);
                mapper.insert(note);
                session.flushStatements();
                cursor.set(mapper.selectWithCursor(Wrappers.emptyWrapper()));
                assertThat(cursor.get().iterator().hasNext()).isTrue();
                if (fail) {
                    throw new IllegalStateException("batch callback failed");
                }
                return 1;
            });
            if (fail) {
                assertThatThrownBy(batch::run).hasRootCauseMessage("batch callback failed");
            } else {
                batch.run();
            }
            assertThat(cursor.get().isOpen()).isFalse();
            assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM standalone_note", Long.class)).isOne();
            status.setRollbackOnly();
        });
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM standalone_note", Long.class)).isZero();
    }

    @Test
    void rollbackDiscardsActiveRecordAndDbWrites() {
        new TransactionTemplate(new DataSourceTransactionManager(dataSource)).executeWithoutResult(status -> {
            var first = new Note();
            first.setId(1L);
            var second = new Note();
            second.setId(2L);
            assertThat(first.insert()).isTrue();
            assertThat(Db.save(second)).isTrue();
            assertThat(first.selectCount(Wrappers.emptyWrapper())).isEqualTo(2L);
            status.setRollbackOnly();
        });
        assertThat(new Note().selectCount(Wrappers.emptyWrapper())).isZero();
    }

    interface NoteMapper extends BaseMapper<Note> {
    }

    @Getter
    @Setter
    @TableName("standalone_note")
    static class Note extends Model<Note> {

        private Long id;
        private String note;
    }
}
