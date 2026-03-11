package com.baomidou.mybatisplus.core.handlers;

import org.apache.ibatis.type.JdbcType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.*;
import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

/**
 * LocalDateTimeTypeHandler 单元测试
 *
 * @author mybatis-plus
 */
@ExtendWith(MockitoExtension.class)
public class LocalDateTimeTypeHandlerTest {

    private static final LocalDateTimeTypeHandler HANDLER = new LocalDateTimeTypeHandler();

    @Test
    public void setParameterNull() throws SQLException {
        PreparedStatement ps = mock(PreparedStatement.class);
        HANDLER.setParameter(ps, 1, null, JdbcType.TIMESTAMP);
        verify(ps).setNull(1, Types.TIMESTAMP);
    }

    @Test
    public void setParameterWithLocalDateTime() throws SQLException {
        PreparedStatement ps = mock(PreparedStatement.class);
        LocalDateTime dateTime = LocalDateTime.of(2023, 12, 25, 10, 30, 45, 123456789);
        HANDLER.setParameter(ps, 1, dateTime, JdbcType.TIMESTAMP);
        Timestamp expectedTimestamp = Timestamp.valueOf(dateTime);
        verify(ps).setTimestamp(1, expectedTimestamp);
    }

    @Test
    public void setParameterWithMillisecondPrecision() throws SQLException {
        PreparedStatement ps = mock(PreparedStatement.class);
        // 测试毫秒精度
        LocalDateTime dateTime = LocalDateTime.of(2023, 12, 25, 10, 30, 45, 123000000);
        HANDLER.setParameter(ps, 1, dateTime, JdbcType.TIMESTAMP);
        verify(ps).setTimestamp(1, Timestamp.valueOf(dateTime));
    }

    @Test
    public void getResultFromResultSetByColumnNameNull() throws SQLException {
        ResultSet rs = mock(ResultSet.class);
        when(rs.getTimestamp("column")).thenReturn(null);
        LocalDateTime result = HANDLER.getResult(rs, "column");
        Assertions.assertNull(result);
    }

    @Test
    public void getResultFromResultSetByColumnName() throws SQLException {
        ResultSet rs = mock(ResultSet.class);
        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.of(2023, 12, 25, 10, 30, 45, 123000000));
        when(rs.getTimestamp("column")).thenReturn(timestamp);
        LocalDateTime result = HANDLER.getResult(rs, "column");
        Assertions.assertEquals(LocalDateTime.of(2023, 12, 25, 10, 30, 45, 123000000), result);
    }

    @Test
    public void getResultFromResultSetByColumnIndexNull() throws SQLException {
        ResultSet rs = mock(ResultSet.class);
        when(rs.getTimestamp(1)).thenReturn(null);
        LocalDateTime result = HANDLER.getResult(rs, 1);
        Assertions.assertNull(result);
    }

    @Test
    public void getResultFromResultSetByColumnIndex() throws SQLException {
        ResultSet rs = mock(ResultSet.class);
        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.of(2023, 12, 25, 10, 30, 45, 456000000));
        when(rs.getTimestamp(1)).thenReturn(timestamp);
        LocalDateTime result = HANDLER.getResult(rs, 1);
        Assertions.assertEquals(LocalDateTime.of(2023, 12, 25, 10, 30, 45, 456000000), result);
    }

    @Test
    public void getResultFromCallableStatementNull() throws SQLException {
        CallableStatement cs = mock(CallableStatement.class);
        when(cs.getTimestamp(1)).thenReturn(null);
        LocalDateTime result = HANDLER.getResult(cs, 1);
        Assertions.assertNull(result);
    }

    @Test
    public void getResultFromCallableStatement() throws SQLException {
        CallableStatement cs = mock(CallableStatement.class);
        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.of(2023, 12, 25, 10, 30, 45, 789000000));
        when(cs.getTimestamp(1)).thenReturn(timestamp);
        LocalDateTime result = HANDLER.getResult(cs, 1);
        Assertions.assertEquals(LocalDateTime.of(2023, 12, 25, 10, 30, 45, 789000000), result);
    }

}
