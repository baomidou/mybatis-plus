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

import org.mockito.Mock;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public abstract class BaseTypeHandlerTest {

    @Mock
    protected ResultSet resultSet;
    @Mock
    protected PreparedStatement preparedStatement;
    @Mock
    protected CallableStatement callableStatement;

    public abstract void setParameter() throws Exception;

    public abstract void getResultFromResultSetByColumnName() throws Exception;

    public abstract void getResultFromResultSetByColumnIndex() throws Exception;

    public abstract void getResultFromCallableStatement() throws Exception;
}
