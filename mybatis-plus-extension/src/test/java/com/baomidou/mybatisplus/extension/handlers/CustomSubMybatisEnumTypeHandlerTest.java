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

import org.apache.ibatis.type.JdbcType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomSubMybatisEnumTypeHandlerTest extends BaseTypeHandlerTest {

    private static final SubEnumValueTypeHandler<SubEnum> TYPE_HANDLER = new SubEnumValueTypeHandler<>(SubEnum.class);

    @Test
    @Override
    public void setParameter() throws Exception {
        TYPE_HANDLER.setParameter(preparedStatement, 1, SubEnum.ME, null);
        verify(preparedStatement).setObject(1, "我");
        TYPE_HANDLER.setParameter(preparedStatement, 2, SubEnum.YOU, null);
        verify(preparedStatement).setObject(2, "你");
        TYPE_HANDLER.setParameter(preparedStatement, 3, null, JdbcType.VARCHAR);
        verify(preparedStatement).setNull(3, JdbcType.VARCHAR.TYPE_CODE);
    }

    @Test
    @Override
    public void getResultFromResultSetByColumnName() throws Exception {
        assertNull(TYPE_HANDLER.getResult(resultSet, "column"));
        when(resultSet.getObject("column")).thenReturn("我");
        assertEquals(SubEnum.ME, TYPE_HANDLER.getResult(resultSet, "column"));
        when(resultSet.getObject("column")).thenReturn("你");
        assertEquals(SubEnum.YOU, TYPE_HANDLER.getResult(resultSet, "column"));
    }

    @Test
    @Override
    public void getResultFromResultSetByColumnIndex() throws Exception {
        when(resultSet.getObject(1)).thenReturn("我");
        assertEquals(SubEnum.ME, TYPE_HANDLER.getResult(resultSet, 1));
        when(resultSet.getObject(2)).thenReturn("你");
        assertEquals(SubEnum.YOU, TYPE_HANDLER.getResult(resultSet, 2));
        when(resultSet.getObject(3)).thenReturn(null);
        assertNull(TYPE_HANDLER.getResult(resultSet, 3));
    }

    @Test
    @Override
    public void getResultFromCallableStatement() throws Exception {
        when(callableStatement.getObject(1)).thenReturn("我");
        assertEquals(SubEnum.ME, TYPE_HANDLER.getResult(callableStatement, 1));
        when(callableStatement.getObject(2)).thenReturn("你");
        assertEquals(SubEnum.YOU, TYPE_HANDLER.getResult(callableStatement, 2));
        when(callableStatement.getObject(3)).thenReturn(null);
        assertNull(TYPE_HANDLER.getResult(callableStatement, 3));
    }

}
