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
package com.baomidou.mybatisplus.test.extension.plugins.inner;

import com.baomidou.mybatisplus.extension.plugins.inner.IllegalSQLInnerInterceptor;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
class IllegalSQLInnerInterceptorCatalogTest {

    @ParameterizedTest
    @CsvSource({"target_catalog.target_schema.catalog_probe, target_catalog, target_schema",
            "target_schema.catalog_probe, default_catalog, target_schema",
            "catalog_probe, default_catalog, default_schema"})
    void shouldUseTableQualifiersForIndexLookup(String tableName, String expectedCatalog, String expectedSchema)
            throws SQLException {
        Connection connection = mock(Connection.class);
        DatabaseMetaData metadata = mock(DatabaseMetaData.class);
        ResultSet indexInfo = mock(ResultSet.class);
        when(connection.getMetaData()).thenReturn(metadata);
        when(connection.getCatalog()).thenReturn("default_catalog");
        when(connection.getSchema()).thenReturn("default_schema");
        when(metadata.getIndexInfo(anyString(), anyString(), eq("catalog_probe"), eq(false), eq(true)))
                .thenReturn(indexInfo);
        when(indexInfo.next()).thenReturn(true, false);
        when(indexInfo.getString(8)).thenReturn("1");
        when(indexInfo.getString(9)).thenReturn("id");

        IllegalSQLInnerInterceptor interceptor = new IllegalSQLInnerInterceptor();
        assertThat(interceptor.parserSingle("select id from " + tableName + " where id = ?", connection))
                .contains("WHERE id = ?");
        verify(metadata).getIndexInfo(expectedCatalog, expectedSchema, "catalog_probe", false, true);
    }
}
