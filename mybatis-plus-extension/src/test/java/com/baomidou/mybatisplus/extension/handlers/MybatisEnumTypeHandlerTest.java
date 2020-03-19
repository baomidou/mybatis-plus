/*
 * Copyright (c) 2011-2019, hubin (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.extension.handlers;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.core.enums.IEnum;
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

    @Test
    void dealEnumType(){
        Assertions.assertFalse(MybatisEnumTypeHandler.dealEnumType(String.class).isPresent());
        Assertions.assertTrue(MybatisEnumTypeHandler.dealEnumType(GradeEnum.class).isPresent());
        Assertions.assertFalse(MybatisEnumTypeHandler.dealEnumType(SexEnum.class).isPresent());
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
    public void getResultFromResultSetByColumnName() throws Exception {
        when(resultSet.getObject("column")).thenReturn(null);
        assertNull(SEX_ENUM_ENUM_TYPE_HANDLER.getResult(resultSet, "column"));
        when(resultSet.getObject("column")).thenReturn(1);
        assertEquals(SexEnum.MAN, SEX_ENUM_ENUM_TYPE_HANDLER.getResult(resultSet, "column"));
        when(resultSet.getObject("column")).thenReturn(2);
        assertEquals(SexEnum.WO_MAN, SEX_ENUM_ENUM_TYPE_HANDLER.getResult(resultSet, "column"));
        when(resultSet.getObject("column")).thenReturn(null);

        assertNull(GRADE_ENUM_ENUM_TYPE_HANDLER.getResult(resultSet, "column"));
        when(resultSet.getObject("column")).thenReturn(1);
        assertEquals(GradeEnum.PRIMARY, GRADE_ENUM_ENUM_TYPE_HANDLER.getResult(resultSet, "column"));
        when(resultSet.getObject("column")).thenReturn(2);
        assertEquals(GradeEnum.SECONDARY, GRADE_ENUM_ENUM_TYPE_HANDLER.getResult(resultSet, "column"));
    }

    @Test
    @Override
    public void getResultFromResultSetByColumnIndex() throws Exception {
        when(resultSet.getObject(1)).thenReturn(1);
        assertEquals(SexEnum.MAN, SEX_ENUM_ENUM_TYPE_HANDLER.getResult(resultSet, 1));
        when(resultSet.getObject(2)).thenReturn(2);
        assertEquals(SexEnum.WO_MAN, SEX_ENUM_ENUM_TYPE_HANDLER.getResult(resultSet, 2));
        when(resultSet.getObject(3)).thenReturn(null);
        assertNull(SEX_ENUM_ENUM_TYPE_HANDLER.getResult(resultSet, 3));

        when(resultSet.getObject(4)).thenReturn(1);
        assertEquals(GradeEnum.PRIMARY, GRADE_ENUM_ENUM_TYPE_HANDLER.getResult(resultSet, 4));
        when(resultSet.getObject(5)).thenReturn(2);
        assertEquals(GradeEnum.SECONDARY, GRADE_ENUM_ENUM_TYPE_HANDLER.getResult(resultSet, 5));
        when(resultSet.getObject(6)).thenReturn(null);
        assertNull(GRADE_ENUM_ENUM_TYPE_HANDLER.getResult(resultSet, 6));
    }

    @Test
    @Override
    public void getResultFromCallableStatement() throws Exception {
        when(callableStatement.getObject(1)).thenReturn(1);
        assertEquals(SexEnum.MAN, SEX_ENUM_ENUM_TYPE_HANDLER.getResult(callableStatement, 1));
        when(callableStatement.getObject(2)).thenReturn(2);
        assertEquals(SexEnum.WO_MAN, SEX_ENUM_ENUM_TYPE_HANDLER.getResult(callableStatement, 2));
        when(callableStatement.getObject(3)).thenReturn(null);
        assertNull(SEX_ENUM_ENUM_TYPE_HANDLER.getResult(callableStatement, 3));

        when(callableStatement.getObject(4)).thenReturn(1);
        assertEquals(GradeEnum.PRIMARY, GRADE_ENUM_ENUM_TYPE_HANDLER.getResult(callableStatement, 4));
        when(callableStatement.getObject(5)).thenReturn(2);
        assertEquals(GradeEnum.SECONDARY, GRADE_ENUM_ENUM_TYPE_HANDLER.getResult(callableStatement, 5));
        when(callableStatement.getObject(6)).thenReturn(null);
        assertNull(GRADE_ENUM_ENUM_TYPE_HANDLER.getResult(callableStatement, 6));
    }

}
