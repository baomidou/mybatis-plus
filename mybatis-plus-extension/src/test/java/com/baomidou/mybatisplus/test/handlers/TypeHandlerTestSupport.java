package com.baomidou.mybatisplus.test.handlers;

import org.mockito.Mock;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * 类型处理器测试的模板基类：备好三个 JDBC mock，再规定子类必须实现读写四件事 （{@code setParameter} 与三种取回结果的方式）。子类各自给这四个方法挂 {@code @Test}，
 * 所以它本身不含用例，名字也不该带 {@code Test}。
 */
public abstract class TypeHandlerTestSupport {

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
