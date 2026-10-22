/*
 * Copyright (c) 2011-2025, baomidou (jobob@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baomidou.mybatisplus.extension.toolkit;

import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.logging.Log;
import org.junit.jupiter.api.Test;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * 钉住 {@link SqlHelper} 中不依赖数据库的结果解释语义。
 *
 * @author TaoYu
 */
class SqlHelperResultSemanticsTest {

    @Test
    void retBoolOfIntegerTreatsNullAndNonPositiveAsFalse() {
        assertThat(SqlHelper.retBool((Integer) null)).isFalse();
        assertThat(SqlHelper.retBool(0)).isFalse();
        assertThat(SqlHelper.retBool(-1)).isFalse();
        assertThat(SqlHelper.retBool(1)).isTrue();
        assertThat(SqlHelper.retBool(2)).isTrue();
    }

    @Test
    void retBoolOfLongUsesTheSameBoundary() {
        assertThat(SqlHelper.retBool((Long) null)).isFalse();
        assertThat(SqlHelper.retBool(0L)).isFalse();
        assertThat(SqlHelper.retBool(-1L)).isFalse();
        assertThat(SqlHelper.retBool(1L)).isTrue();
    }

    @Test
    void retBoolOfBatchResultsAcceptsSuccessNoInfoAndRejectsExecuteFailed() {
        assertThat(SqlHelper.retBool((List<BatchResult>) null)).isFalse();
        assertThat(SqlHelper.retBool(Collections.emptyList())).isTrue();
        assertThat(SqlHelper.retBool(List.of(batchResult(1, 2)))).isTrue();
        assertThat(SqlHelper.retBool(List.of(batchResult(0)))).isFalse();
        assertThat(SqlHelper.retBool(List.of(batchResult(Statement.SUCCESS_NO_INFO)))).isTrue();
        assertThat(SqlHelper.retBool(List.of(batchResult(Statement.EXECUTE_FAILED)))).isFalse();
        assertThat(SqlHelper.retBool(List.of(batchResult(1), batchResult(2, Statement.SUCCESS_NO_INFO)))).isTrue();
        assertThat(SqlHelper.retBool(List.of(batchResult(1), batchResult(2, Statement.EXECUTE_FAILED)))).isFalse();
    }

    @Test
    void retCountMapsNullToZero() {
        assertThat(SqlHelper.retCount(null)).isZero();
        assertThat(SqlHelper.retCount(0L)).isZero();
        assertThat(SqlHelper.retCount(7L)).isEqualTo(7L);
    }

    @Test
    void getObjectSkipsLogSupplierWhenListHoldsAtMostOneElement() {
        AtomicInteger supplierCalls = new AtomicInteger();
        RecordingLog log = new RecordingLog();
        Supplier<Log> supplier = () -> {
            supplierCalls.incrementAndGet();
            return log;
        };
        assertThat(SqlHelper.<String>getObject(supplier, null)).isNull();
        assertThat(SqlHelper.<String>getObject(supplier, Collections.emptyList())).isNull();
        assertThat(SqlHelper.getObject(supplier, List.of("only"))).isEqualTo("only");
        assertThat(supplierCalls).hasValue(0);
        assertThat(log.warnings).isEmpty();
    }

    @Test
    void getObjectReturnsFirstElementAndWarnsOnceWhenListHoldsSeveral() {
        RecordingLog log = new RecordingLog();
        assertThat(SqlHelper.getObject(log, Arrays.asList("first", "second", "third"))).isEqualTo("first");
        assertThat(log.warnings).containsExactly("Warn: execute Method There are  3 results.");
    }

    @Test
    void getSqlStatementQualifiesTheSqlMethodWithTheMapperName() {
        assertThat(SqlHelper.getSqlStatement(BaseMapper.class, SqlMethod.INSERT_ONE))
                .isEqualTo("com.baomidou.mybatisplus.core.mapper.BaseMapper.insert");
        assertThat(SqlHelper.getSqlStatement(BaseMapper.class, SqlMethod.SELECT_LIST))
                .isEqualTo("com.baomidou.mybatisplus.core.mapper.BaseMapper.selectList");
    }

    private static BatchResult batchResult(int... updateCounts) {
        BatchResult result = mock(BatchResult.class);
        when(result.getUpdateCounts()).thenReturn(updateCounts);
        return result;
    }

    private static final class RecordingLog implements Log {

        private final List<String> warnings = new ArrayList<>();

        @Override
        public boolean isDebugEnabled() {
            return false;
        }

        @Override
        public boolean isTraceEnabled() {
            return false;
        }

        @Override
        public void error(String s, Throwable e) {
        }

        @Override
        public void error(String s) {
        }

        @Override
        public void debug(String s) {
        }

        @Override
        public void trace(String s) {
        }

        @Override
        public void warn(String s) {
            warnings.add(s);
        }
    }
}
