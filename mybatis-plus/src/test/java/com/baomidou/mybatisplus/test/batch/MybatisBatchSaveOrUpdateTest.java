package com.baomidou.mybatisplus.test.batch;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.batch.MybatisBatch;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.MybatisBatchUtils;
import com.baomidou.mybatisplus.test.DbTestSupport;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("integration")
class MybatisBatchSaveOrUpdateTest extends DbTestSupport<MybatisBatchSaveOrUpdateTest.BatchSizeMapper> {

    private final MybatisBatch.Method<BatchSizeRecord> method = new MybatisBatch.Method<>(BatchSizeMapper.class);

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void preclassifiedInsertsRespectBatchSize(boolean autoCommit) {
        List<BatchSizeRecord> records = List.of(record(1), record(2), record(3), record(4), record(5));
        List<BatchResult> results = MybatisBatchUtils.saveOrUpdate(
                sqlSessionFactory, records, autoCommit, method.insert(), (session, record) -> true, method.updateById(),
                2);
        assertThat(results).extracting(result -> result.getParameterObjects().size()).containsExactly(2, 2, 1);
        assertThat(results).flatExtracting(BatchResult::getParameterObjects).containsExactlyElementsOf(records);
        assertThat(results).allSatisfy(result -> assertThat(result.getUpdateCounts()).containsOnly(1));
        try (SqlSession session = sqlSessionFactory.openSession()) {
            assertThat(session.getMapper(BatchSizeMapper.class).selectCount(null)).isEqualTo(5);
        }
    }

    @Test
    void preclassifiedUpdatesRespectBatchSize() {
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            for (int id = 1; id <= 5; id++) {
                session.getMapper(BatchSizeMapper.class).insert(record(id));
            }
        }
        List<BatchSizeRecord> records = List.of(record(1), record(2), record(3), record(4), record(5));
        records.forEach(record -> record.setName("updated"));
        List<BatchResult> results = MybatisBatchUtils.saveOrUpdate(
                sqlSessionFactory, records, method.insert(), (session, record) -> false, method.updateById(), 2);
        assertThat(results).extracting(result -> result.getParameterObjects().size()).containsExactly(2, 2, 1);
        assertThat(results).allSatisfy(result -> assertThat(result.getUpdateCounts()).containsOnly(1));
        try (SqlSession session = sqlSessionFactory.openSession()) {
            assertThat(session.getMapper(BatchSizeMapper.class).selectList(null)).extracting(BatchSizeRecord::getName)
                    .containsOnly("updated").hasSize(5);
        }
    }

    @Test
    void sharedSessionQueriesPreserveResultsAcrossBatchBoundaries() {
        List<BatchSizeRecord> records = List.of(record(1), new BatchSizeRecord(1L, "updated"), record(2));
        List<BatchResult> results = MybatisBatchUtils.saveOrUpdate(
                sqlSessionFactory, records, method.insert(), (session, record) -> session
                        .selectList(method.get("selectById").getStatementId(), record.getId()).isEmpty(),
                method.updateById(), 2);
        assertThat(results).extracting(result -> result.getMappedStatement().getId()).containsExactly(
                method.insert().getStatementId(), method.updateById().getStatementId(),
                method.insert().getStatementId());
        assertThat(results).flatExtracting(BatchResult::getParameterObjects)
                .extracting(parameter -> parameter instanceof Map ? ((Map<?, ?>) parameter).get("et") : parameter)
                .containsExactlyElementsOf(records);
        assertThat(results).allSatisfy(result -> assertThat(result.getUpdateCounts()).containsExactly(1));
        try (SqlSession session = sqlSessionFactory.openSession()) {
            BatchSizeMapper mapper = session.getMapper(BatchSizeMapper.class);
            assertThat(mapper.selectCount(null)).isEqualTo(2);
            assertThat(mapper.selectById(1).getName()).isEqualTo("updated");
        }
    }

    @Test
    void failureInLaterBatchRollsBackPreviouslyFlushedWrites() {
        List<BatchSizeRecord> records = List.of(record(1), record(2), record(3), record(3));
        assertThatThrownBy(
                () -> MybatisBatchUtils.saveOrUpdate(
                        sqlSessionFactory, records, method.insert(), (session, record) -> true, method.updateById(), 2))
                .isInstanceOf(PersistenceException.class);
        try (SqlSession session = sqlSessionFactory.openSession()) {
            assertThat(session.getMapper(BatchSizeMapper.class).selectCount(null)).isZero();
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void invalidBatchSizeDoesNotWriteData(int batchSize) {
        assertThatThrownBy(
                () -> MybatisBatchUtils.saveOrUpdate(
                        sqlSessionFactory, List.of(record(1)), method.insert(), (session, record) -> true,
                        method.updateById(), batchSize))
                .isInstanceOf(MybatisPlusException.class).hasMessageContaining("batchSize");
        try (SqlSession session = sqlSessionFactory.openSession()) {
            assertThat(session.getMapper(BatchSizeMapper.class).selectCount(null)).isZero();
        }
    }

    @Test
    void emptyInputHasNoBatchResults() {
        assertThat(
                MybatisBatchUtils.saveOrUpdate(
                        sqlSessionFactory, Collections.<BatchSizeRecord>emptyList(), method.insert(),
                        (session, record) -> true, method.updateById(), 2))
                .isEmpty();
    }

    private BatchSizeRecord record(long id) {
        return new BatchSizeRecord(id, "record-" + id);
    }

    @Override
    protected TransactionFactory transactionFactory() {
        return new JdbcTransactionFactory();
    }

    @Override
    protected List<String> tableSql() {
        return List.of(
                "DROP TABLE IF EXISTS zz_save_or_update_batch_size",
                "CREATE TABLE zz_save_or_update_batch_size (id BIGINT PRIMARY KEY, name VARCHAR(30))");
    }

    interface BatchSizeMapper extends BaseMapper<BatchSizeRecord> {
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @TableName("zz_save_or_update_batch_size")
    static class BatchSizeRecord {

        private Long id;
        private String name;
    }
}
