package com.baomidou.mybatisplus.test.handlers;

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
