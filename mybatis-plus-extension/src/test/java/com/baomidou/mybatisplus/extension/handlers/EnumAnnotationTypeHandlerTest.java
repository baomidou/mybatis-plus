package com.baomidou.mybatisplus.extension.handlers;

import com.baomidou.mybatisplus.annotation.EnumValue;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EnumAnnotationTypeHandlerTest extends BaseTypeHandlerTest {

    private static final TypeHandler<SexEnum> HANDLER = new EnumAnnotationTypeHandler<>(SexEnum.class);

    @Getter
    @AllArgsConstructor
    enum SexEnum {
        MAN(1, "男人"), WO_MAN(2, "女人");
        @EnumValue
        Integer code;
        String desc;
    }

    @Test
    @Override
    public void setParameter() throws Exception {
        HANDLER.setParameter(preparedStatement, 1, null, JdbcType.INTEGER);
        verify(preparedStatement).setNull(1, JdbcType.INTEGER.TYPE_CODE);
        HANDLER.setParameter(preparedStatement, 2, SexEnum.MAN, null);
        verify(preparedStatement).setObject(2, 1);
        HANDLER.setParameter(preparedStatement, 3, SexEnum.WO_MAN, null);
        verify(preparedStatement).setObject(3, 2);
    }

    @Test
    @Override
    public void getResultFromResultSetByColumnName() throws Exception {
        when(resultSet.getObject("column")).thenReturn(null);
        assertNull(HANDLER.getResult(resultSet, "column"));
        when(resultSet.getObject("column")).thenReturn(1);
        assertEquals(SexEnum.MAN, HANDLER.getResult(resultSet, "column"));
        when(resultSet.getObject("column")).thenReturn(2);
        assertEquals(SexEnum.WO_MAN, HANDLER.getResult(resultSet, "column"));
    }

    @Test
    @Override
    public void getResultFromResultSetByColumnIndex() throws Exception {
        when(resultSet.getObject(1)).thenReturn(1);
        assertEquals(SexEnum.MAN, HANDLER.getResult(resultSet, 1));
        when(resultSet.getObject(2)).thenReturn(2);
        assertEquals(SexEnum.WO_MAN, HANDLER.getResult(resultSet, 2));
        when(resultSet.getObject(3)).thenReturn(null);
        assertNull(HANDLER.getResult(resultSet, 3));
    }

    @Test
    @Override
    public void getResultFromCallableStatement() throws Exception {
        when(callableStatement.getObject(1)).thenReturn(1);
        assertEquals(SexEnum.MAN, HANDLER.getResult(callableStatement, 1));
        when(callableStatement.getObject(2)).thenReturn(2);
        assertEquals(SexEnum.WO_MAN, HANDLER.getResult(callableStatement, 2));
        when(callableStatement.getObject(3)).thenReturn(null);
        assertNull(HANDLER.getResult(callableStatement, 3));

    }

}
