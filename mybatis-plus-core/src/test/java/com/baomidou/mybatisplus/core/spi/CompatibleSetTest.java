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
package com.baomidou.mybatisplus.core.spi;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.MybatisSqlSessionFactoryBuilder;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@Tag("unit")
class CompatibleSetTest {

    @Test
    void resolvesEntityConfigurationFactoryAndSession() {
        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.addMapper(TestMapper.class);
        try {
            SqlSessionFactory factory = new MybatisSqlSessionFactoryBuilder().build(configuration);
            CompatibleSet compatibleSet = mock(CompatibleSet.class, CALLS_REAL_METHODS);
            SqlSession session = mock(SqlSession.class);
            doReturn(session).when(compatibleSet).getSqlSession(factory);

            assertThat(compatibleSet.getConfiguration(TestEntity.class)).isSameAs(configuration);
            assertThat(compatibleSet.getSqlSessionFactory(TestEntity.class)).isSameAs(factory);
            assertThat(compatibleSet.getSqlSession(TestEntity.class)).isSameAs(session);
        } finally {
            TableInfoHelper.remove(TestEntity.class);
        }
    }

    @Test
    void rejectsEntityWithoutTableMetadata() {
        CompatibleSet compatibleSet = mock(CompatibleSet.class, CALLS_REAL_METHODS);
        assertThatThrownBy(() -> compatibleSet.getConfiguration(UnregisteredEntity.class))
                .isInstanceOf(MybatisPlusException.class).hasMessageContaining("Not Found TableInfoCache");
    }

    @Test
    void rejectsNullCompatibleSetWithoutReplacingCurrentProvider() {
        CompatibleSet current = CompatibleHelper.getCompatibleSet();

        assertThatThrownBy(() -> CompatibleHelper.setCompatibleSet(null)).isInstanceOf(MybatisPlusException.class)
                .hasMessage("CompatibleSet must not be null");
        assertThat(CompatibleHelper.getCompatibleSet()).isSameAs(current);
    }

    @Test
    void defaultProviderOpensCommitsAndClosesStandaloneSessions() {
        CompatibleSet compatibleSet = CompatibleHelper.getCompatibleSet();
        assertThat(compatibleSet).isInstanceOf(DefaultCompatibleSet.class);
        SqlSessionFactory factory = mock(SqlSessionFactory.class);
        SqlSession session = mock(SqlSession.class);
        doReturn(session).when(factory).openSession();

        assertThat(compatibleSet.getSqlSession(factory)).isSameAs(session);
        compatibleSet.commitSqlSession(session, factory);
        compatibleSet.closeSqlSession(session, factory);

        verify(session).commit();
        verify(session).close();
    }

    @Test
    void defaultProviderCommitsSuccessfulBatchesAndClosesFailedBatchesWithoutCommit() {
        CompatibleSet compatibleSet = new DefaultCompatibleSet();
        SqlSessionFactory factory = mock(SqlSessionFactory.class);
        SqlSession successful = mock(SqlSession.class);
        SqlSession failed = mock(SqlSession.class);
        doReturn(successful, failed).when(factory).openSession(ExecutorType.BATCH);
        Log log = mock(Log.class);

        assertThat(compatibleSet.executeBatch(factory, log, session -> 1)).isTrue();
        assertThatThrownBy(() -> compatibleSet.executeBatch(factory, log, session -> {
            throw new IllegalStateException("batch failed");
        })).isInstanceOf(IllegalStateException.class).hasMessage("batch failed");

        verify(successful).commit();
        verify(successful).close();
        verify(failed, never()).commit();
        verify(failed).close();
    }

    @Test
    void selectsProviderEvenWhenOrderIsNegative() {
        CompatibleSet provider = provider(Integer.MIN_VALUE);

        assertThat(CompatibleHelper.selectCompatibleSet(List.of(provider))).isSameAs(provider);
    }

    @Test
    void selectsHighestOrder() {
        CompatibleSet first = provider(0);
        CompatibleSet second = provider(0);
        CompatibleSet highest = provider(1);

        assertThat(CompatibleHelper.selectCompatibleSet(List.of(first, second, highest))).isSameAs(highest);
        assertThat(CompatibleHelper.selectCompatibleSet(List.of(highest, first, second))).isSameAs(highest);
    }

    @Test
    void rejectsTiesAtTheHighestPriority() {
        CompatibleSet first = mock(CompatibleSet.class, CALLS_REAL_METHODS);
        CompatibleSet second = mock(CompatibleSet.class, CALLS_REAL_METHODS);

        assertThat(first.order()).isZero();
        assertThatThrownBy(() -> CompatibleHelper.selectCompatibleSet(List.of(first, second)))
                .isInstanceOf(MybatisPlusException.class).hasMessageContaining("share the highest priority");
        assertThat(CompatibleHelper.selectCompatibleSet(List.of())).isInstanceOf(DefaultCompatibleSet.class);
    }

    private CompatibleSet provider(int order) {
        CompatibleSet compatibleSet = mock(CompatibleSet.class);
        doReturn(order).when(compatibleSet).order();
        return compatibleSet;
    }

    interface TestMapper extends BaseMapper<TestEntity> {
    }

    static class TestEntity {

        private Long id;
    }

    static class UnregisteredEntity {
    }
}
