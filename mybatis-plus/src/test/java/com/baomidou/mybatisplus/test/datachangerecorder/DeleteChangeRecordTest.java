package com.baomidou.mybatisplus.test.datachangerecorder;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DataChangeRecorderInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DataChangeRecorderInnerInterceptor.OperationResult;
import com.baomidou.mybatisplus.test.DbTestSupport;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.apache.ibatis.plugin.Interceptor;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@Tag("integration")
class DeleteChangeRecordTest extends DbTestSupport<DeleteChangeRecordTest.DeliveryMapper> {

    private OperationResult recorded;

    @ParameterizedTest
    @ValueSource(strings = {"Left at reception", "Left at \"reception\"\nFloor 2\\desk"})
    void recordsTheOriginalRowsOfABatchDelete(String note) throws Exception {
        jdbcTemplate.update(
                "INSERT INTO delivery_note (id, note) VALUES (1, ?), (2, ?), (3, ?)", note, "Delivered", "In transit");

        doTestAutoCommit(mapper -> assertThat(mapper.deleteByIds(List.of(1L, 2L))).isEqualTo(2));

        assertThat(recorded.isRecordStatus()).isTrue();
        JsonNode rows = new ObjectMapper().readTree(recorded.toString()).get("changedData");
        assertThat(rows.isArray()).isTrue();
        assertThat((Iterable<JsonNode>) rows)
                .extracting(row -> row.get("ID").textValue(), row -> row.get("NOTE").textValue())
                .containsExactlyInAnyOrder(tuple("1", note), tuple("2", "Delivered"));
    }

    @Override
    protected List<String> tableSql() {
        return List.of(
                "DROP TABLE IF EXISTS delivery_note",
                "CREATE TABLE delivery_note (id BIGINT PRIMARY KEY, note VARCHAR(100))");
    }

    @Override
    protected List<Interceptor> interceptors() {
        var interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new DataChangeRecorderInnerInterceptor() {

            @Override
            protected void dealOperationResult(OperationResult result) {
                recorded = result;
            }
        });
        return List.of(interceptor);
    }

    interface DeliveryMapper extends BaseMapper<DeliveryNote> {
    }

    @Data
    static class DeliveryNote {

        private Long id;
        private String note;
    }
}
