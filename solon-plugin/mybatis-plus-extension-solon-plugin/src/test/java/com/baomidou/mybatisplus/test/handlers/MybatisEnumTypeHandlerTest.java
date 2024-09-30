package com.baomidou.mybatisplus.test.handlers;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;
import com.baomidou.mybatisplus.core.handlers.MybatisEnumTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.ibatis.type.JdbcType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MybatisEnumTypeHandlerTest extends BaseTypeHandlerTest {

    private static final MybatisEnumTypeHandler<SexEnum> SEX_ENUM_ENUM_TYPE_HANDLER = new MybatisEnumTypeHandler<>(SexEnum.class);

    private static final MybatisEnumTypeHandler<GradeEnum> GRADE_ENUM_ENUM_TYPE_HANDLER = new MybatisEnumTypeHandler<>(GradeEnum.class);

    private static final MybatisEnumTypeHandler<CharacterEnum> CHARACTER_ENUM_MYBATIS_ENUM_TYPE_HANDLER = new MybatisEnumTypeHandler<>(CharacterEnum.class);

    @Test
    @Override
    public void getResultFromResultSetByColumnName() throws Exception {
        when(resultSet.getObject("column", Integer.class)).thenReturn(null);
        assertNull(SEX_ENUM_ENUM_TYPE_HANDLER.getResult(resultSet, "column"));
        when(resultSet.getObject("column", Integer.class)).thenReturn(1);
        assertEquals(SexEnum.MAN, SEX_ENUM_ENUM_TYPE_HANDLER.getResult(resultSet, "column"));
        when(resultSet.getObject("column", Integer.class)).thenReturn(2);
        assertEquals(SexEnum.WO_MAN, SEX_ENUM_ENUM_TYPE_HANDLER.getResult(resultSet, "column"));

        when(resultSet.getObject("column", Integer.class)).thenReturn(null);
        assertNull(GRADE_ENUM_ENUM_TYPE_HANDLER.getResult(resultSet, "column"));
        when(resultSet.getObject("column", Integer.class)).thenReturn(1);
        assertEquals(GradeEnum.PRIMARY, GRADE_ENUM_ENUM_TYPE_HANDLER.getResult(resultSet, "column"));
        when(resultSet.getObject("column", Integer.class)).thenReturn(2);
        assertEquals(GradeEnum.SECONDARY, GRADE_ENUM_ENUM_TYPE_HANDLER.getResult(resultSet, "column"));

        when(resultSet.getObject("column", Character.class)).thenReturn(null);
        assertNull(CHARACTER_ENUM_MYBATIS_ENUM_TYPE_HANDLER.getResult(resultSet, "column"));
        when(resultSet.getObject("column", Character.class)).thenReturn('1');
        assertEquals(CharacterEnum.MAN, CHARACTER_ENUM_MYBATIS_ENUM_TYPE_HANDLER.getResult(resultSet, "column"));
        when(resultSet.getObject("column", Character.class)).thenReturn('2');
        assertEquals(CharacterEnum.WO_MAN, CHARACTER_ENUM_MYBATIS_ENUM_TYPE_HANDLER.getResult(resultSet, "column"));
    }

    @Test
    void dealEnumType() {
        Assertions.assertFalse(MybatisEnumTypeHandler.findEnumValueFieldName(String.class).isPresent());
        Assertions.assertTrue(MybatisEnumTypeHandler.findEnumValueFieldName(GradeEnum.class).isPresent());
        Assertions.assertFalse(MybatisEnumTypeHandler.findEnumValueFieldName(SexEnum.class).isPresent());
        Assertions.assertFalse(MybatisEnumTypeHandler.findEnumValueFieldName(String.class).isPresent());
        Assertions.assertTrue(MybatisEnumTypeHandler.findEnumValueFieldName(GradeEnum.class).isPresent());
        Assertions.assertFalse(MybatisEnumTypeHandler.findEnumValueFieldName(SexEnum.class).isPresent());
    }

    @Test
    @Override
    public void setParameter() throws Exception {
        SEX_ENUM_ENUM_TYPE_HANDLER.setParameter(preparedStatement, 1, SexEnum.MAN, null);
        verify(preparedStatement).setObject(1, 1);
        SEX_ENUM_ENUM_TYPE_HANDLER.setParameter(preparedStatement, 2, SexEnum.WO_MAN, null);
        verify(preparedStatement).setObject(2, 2);
        SEX_ENUM_ENUM_TYPE_HANDLER.setParameter(preparedStatement, 3, null, JdbcType.INTEGER);
        verify(preparedStatement).setNull(3, JdbcType.INTEGER.TYPE_CODE);

        GRADE_ENUM_ENUM_TYPE_HANDLER.setParameter(preparedStatement, 4, GradeEnum.PRIMARY, null);
        verify(preparedStatement).setObject(4, 1);
        GRADE_ENUM_ENUM_TYPE_HANDLER.setParameter(preparedStatement, 5, GradeEnum.SECONDARY, null);
        verify(preparedStatement).setObject(5, 2);
        GRADE_ENUM_ENUM_TYPE_HANDLER.setParameter(preparedStatement, 6, null, JdbcType.INTEGER);
        verify(preparedStatement).setNull(6, JdbcType.INTEGER.TYPE_CODE);
    }

    @Test
    @Override
    public void getResultFromResultSetByColumnIndex() throws Exception {
        when(resultSet.getObject(1, Integer.class)).thenReturn(1);
        assertEquals(SexEnum.MAN, SEX_ENUM_ENUM_TYPE_HANDLER.getResult(resultSet, 1));
        when(resultSet.getObject(2, Integer.class)).thenReturn(2);
        assertEquals(SexEnum.WO_MAN, SEX_ENUM_ENUM_TYPE_HANDLER.getResult(resultSet, 2));
        when(resultSet.getObject(3, Integer.class)).thenReturn(null);
        assertNull(SEX_ENUM_ENUM_TYPE_HANDLER.getResult(resultSet, 3));

        when(resultSet.getObject(4, Integer.class)).thenReturn(1);
        assertEquals(GradeEnum.PRIMARY, GRADE_ENUM_ENUM_TYPE_HANDLER.getResult(resultSet, 4));
        when(resultSet.getObject(5, Integer.class)).thenReturn(2);
        assertEquals(GradeEnum.SECONDARY, GRADE_ENUM_ENUM_TYPE_HANDLER.getResult(resultSet, 5));
        when(resultSet.getObject(6, Integer.class)).thenReturn(null);
        assertNull(GRADE_ENUM_ENUM_TYPE_HANDLER.getResult(resultSet, 6));
    }

    @Test
    @Override
    public void getResultFromCallableStatement() throws Exception {
        when(callableStatement.getObject(1, Integer.class)).thenReturn(1);
        assertEquals(SexEnum.MAN, SEX_ENUM_ENUM_TYPE_HANDLER.getResult(callableStatement, 1));
        when(callableStatement.getObject(2, Integer.class)).thenReturn(2);
        assertEquals(SexEnum.WO_MAN, SEX_ENUM_ENUM_TYPE_HANDLER.getResult(callableStatement, 2));
        when(callableStatement.getObject(3, Integer.class)).thenReturn(null);
        assertNull(SEX_ENUM_ENUM_TYPE_HANDLER.getResult(callableStatement, 3));

        when(callableStatement.getObject(4, Integer.class)).thenReturn(1);
        assertEquals(GradeEnum.PRIMARY, GRADE_ENUM_ENUM_TYPE_HANDLER.getResult(callableStatement, 4));
        when(callableStatement.getObject(5, Integer.class)).thenReturn(2);
        assertEquals(GradeEnum.SECONDARY, GRADE_ENUM_ENUM_TYPE_HANDLER.getResult(callableStatement, 5));
        when(callableStatement.getObject(6, Integer.class)).thenReturn(null);
        assertNull(GRADE_ENUM_ENUM_TYPE_HANDLER.getResult(callableStatement, 6));
    }

    @Test
    public void testNullButReturnZero() throws Exception {
        when(callableStatement.getObject(1, Integer.class)).thenReturn(0);
        when(callableStatement.wasNull()).thenReturn(true);
        assertNull(SEX_ENUM_ENUM_TYPE_HANDLER.getResult(callableStatement, 1));
    }

    @Getter
    @AllArgsConstructor
    enum SexEnum implements IEnum<Integer> {

        MAN(1, "1"),
        WO_MAN(2, "2");
        Integer code;
        String desc;

        @Override
        public Integer getValue() {
            return this.code;
        }
    }

    @Getter
    @AllArgsConstructor
    enum GradeEnum {

        PRIMARY(1, "小学"),
        SECONDARY(2, "中学"),
        HIGH(3, "高中");

        @EnumValue
        private final int code;

        private final String desc;
    }

    @Getter
    @AllArgsConstructor
    enum CharacterEnum {
        MAN('1', "男"),
        WO_MAN('2', "女");

        @EnumValue
        char code;
        String desc;
    }
}
