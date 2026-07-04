package com.baomidou.mybatisplus.core.executor.keygen;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.injector.methods.Insert;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.executor.BatchExecutor;
import org.apache.ibatis.mapping.MappedStatement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.Types;
import java.util.concurrent.atomic.AtomicInteger;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AutoIdKeyGeneratorTest {

    @Test
    void shouldKeepManuallyAssignedAutoId() throws Exception {
        MappedStatement mappedStatement = mappedStatement();
        AutoEntity entity = new AutoEntity();
        entity.setId(300000L);

        mappedStatement.getKeyGenerator().processAfter(null, mappedStatement, statementWithGeneratedKeys(1L), entity);

        Assertions.assertEquals(300000L, entity.getId());
    }

    @Test
    void shouldConsumeGeneratedKeysAndOnlyFillNullAutoId() throws Exception {
        MappedStatement mappedStatement = mappedStatement();
        AutoEntity manuallyAssigned = new AutoEntity();
        manuallyAssigned.setId(300000L);
        AutoEntity generated = new AutoEntity();
        Statement statement = statementWithGeneratedKeys(1L, 2L);

        mappedStatement.getKeyGenerator().processAfter(mock(BatchExecutor.class), mappedStatement, statement, manuallyAssigned);
        mappedStatement.getKeyGenerator().processAfter(mock(BatchExecutor.class), mappedStatement, statement, generated);

        Assertions.assertEquals(300000L, manuallyAssigned.getId());
        Assertions.assertEquals(2L, generated.getId());
    }

    @Test
    void shouldReadGeneratedKeysEachTimeForNonBatchExecutor() throws Exception {
        MappedStatement mappedStatement = mappedStatement();
        AutoEntity first = new AutoEntity();
        AutoEntity second = new AutoEntity();
        Statement statement = mock(Statement.class);
        ResultSet firstResultSet = resultSetWithGeneratedKeys(1L);
        ResultSet secondResultSet = resultSetWithGeneratedKeys(2L);
        when(statement.getGeneratedKeys()).thenReturn(firstResultSet, secondResultSet);

        mappedStatement.getKeyGenerator().processAfter(null, mappedStatement, statement, first);
        mappedStatement.getKeyGenerator().processAfter(null, mappedStatement, statement, second);

        Assertions.assertEquals(1L, first.getId());
        Assertions.assertEquals(2L, second.getId());
    }

    private MappedStatement mappedStatement() {
        MybatisConfiguration configuration = new MybatisConfiguration();
        MapperBuilderAssistant assistant = new MapperBuilderAssistant(configuration, "");
        assistant.setCurrentNamespace(AutoMapper.class.getName());
        TableInfo tableInfo = TableInfoHelper.initTableInfo(assistant, AutoEntity.class);
        new Insert().inject(assistant, AutoMapper.class, AutoEntity.class, tableInfo);
        return configuration.getMappedStatement(AutoMapper.class.getName() + ".insert");
    }

    private Statement statementWithGeneratedKeys(Long... keys) throws Exception {
        Statement statement = mock(Statement.class);
        ResultSet resultSet = resultSetWithGeneratedKeys(keys);
        when(statement.getGeneratedKeys()).thenReturn(resultSet);
        return statement;
    }

    private ResultSet resultSetWithGeneratedKeys(Long... keys) throws Exception {
        ResultSet resultSet = mock(ResultSet.class);
        ResultSetMetaData metaData = mock(ResultSetMetaData.class);
        AtomicInteger row = new AtomicInteger(-1);
        when(resultSet.getMetaData()).thenReturn(metaData);
        when(resultSet.next()).thenAnswer(invocation -> row.incrementAndGet() < keys.length);
        when(resultSet.getLong(1)).thenAnswer(invocation -> keys[row.get()]);
        when(resultSet.wasNull()).thenReturn(false);
        when(metaData.getColumnCount()).thenReturn(1);
        when(metaData.getColumnType(1)).thenReturn(Types.BIGINT);
        return resultSet;
    }

    private interface AutoMapper extends BaseMapper<AutoEntity> {
    }

    @TableName("auto_entity")
    private static class AutoEntity {

        @TableId(type = IdType.AUTO)
        private Long id;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
    }
}
