package com.baomidou.mybatisplus.activerecord.pool;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

/**
 *
 * @author redraiment
 * @since 2.2
 */
final class SingletonPreparedStatement implements PreparedStatement {
  private final Connection c;
  private final PreparedStatement s;

  SingletonPreparedStatement(Connection c, PreparedStatement s) {
    this.c = c;
    this.s = s;
  }

  @Override
  public ResultSet executeQuery() throws SQLException {
    return new SingletonResultSet(this, s.executeQuery());
  }

  @Override
  public int executeUpdate() throws SQLException {
    return s.executeUpdate();
  }

  @Override
  public void setNull(int parameterIndex, int sqlType) throws SQLException {
    s.setNull(parameterIndex, sqlType);
  }

  @Override
  public void setBoolean(int parameterIndex, boolean x) throws SQLException {
    s.setBoolean(parameterIndex, x);
  }

  @Override
  public void setByte(int parameterIndex, byte x) throws SQLException {
    s.setByte(parameterIndex, x);
  }

  @Override
  public void setShort(int parameterIndex, short x) throws SQLException {
    s.setShort(parameterIndex, x);
  }

  @Override
  public void setInt(int parameterIndex, int x) throws SQLException {
    s.setInt(parameterIndex, x);
  }

  @Override
  public void setLong(int parameterIndex, long x) throws SQLException {
    s.setLong(parameterIndex, x);
  }

  @Override
  public void setFloat(int parameterIndex, float x) throws SQLException {
    s.setFloat(parameterIndex, x);
  }

  @Override
  public void setDouble(int parameterIndex, double x) throws SQLException {
    s.setDouble(parameterIndex, x);
  }

  @Override
  public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
    s.setBigDecimal(parameterIndex, x);
  }

  @Override
  public void setString(int parameterIndex, String x) throws SQLException {
    s.setString(parameterIndex, x);
  }

  @Override
  public void setBytes(int parameterIndex, byte[] x) throws SQLException {
    s.setBytes(parameterIndex, x);
  }

  @Override
  public void setDate(int parameterIndex, Date x) throws SQLException {
    s.setDate(parameterIndex, x);
  }

  @Override
  public void setTime(int parameterIndex, Time x) throws SQLException {
    s.setTime(parameterIndex, x);
  }

  @Override
  public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
    s.setTimestamp(parameterIndex, x);
  }

  @Override
  public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
    s.setAsciiStream(parameterIndex, x, length);
  }

  @Override
  public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
    s.setUnicodeStream(parameterIndex, x, length);
  }

  @Override
  public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
    s.setBinaryStream(parameterIndex, x, length);
  }

  @Override
  public void clearParameters() throws SQLException {
    s.clearParameters();
  }

  @Override
  public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
    s.setObject(parameterIndex, x, targetSqlType);
  }

  @Override
  public void setObject(int parameterIndex, Object x) throws SQLException {
    s.setObject(parameterIndex, x);
  }

  @Override
  public boolean execute() throws SQLException {
    return s.execute();
  }

  @Override
  public void addBatch() throws SQLException {
    s.addBatch();
  }

  @Override
  public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
    s.setCharacterStream(parameterIndex, reader, length);
  }

  @Override
  public void setRef(int parameterIndex, Ref x) throws SQLException {
    s.setRef(parameterIndex, x);
  }

  @Override
  public void setBlob(int parameterIndex, Blob x) throws SQLException {
    s.setBlob(parameterIndex, x);
  }

  @Override
  public void setClob(int parameterIndex, Clob x) throws SQLException {
    s.setClob(parameterIndex, x);
  }

  @Override
  public void setArray(int parameterIndex, Array x) throws SQLException {
    s.setArray(parameterIndex, x);
  }

  @Override
  public ResultSetMetaData getMetaData() throws SQLException {
    return s.getMetaData();
  }

  @Override
  public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
    s.setDate(parameterIndex, x, cal);
  }

  @Override
  public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
    s.setTime(parameterIndex, x, cal);
  }

  @Override
  public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
    s.setTimestamp(parameterIndex, x, cal);
  }

  @Override
  public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
    s.setNull(parameterIndex, sqlType, typeName);
  }

  @Override
  public void setURL(int parameterIndex, URL x) throws SQLException {
    s.setURL(parameterIndex, x);
  }

  @Override
  public ParameterMetaData getParameterMetaData() throws SQLException {
    return s.getParameterMetaData();
  }

  @Override
  public void setRowId(int parameterIndex, RowId x) throws SQLException {
    s.setRowId(parameterIndex, x);
  }

  @Override
  public void setNString(int parameterIndex, String value) throws SQLException {
    s.setNString(parameterIndex, value);
  }

  @Override
  public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
    s.setNCharacterStream(parameterIndex, value, length);
  }

  @Override
  public void setNClob(int parameterIndex, NClob value) throws SQLException {
    s.setNClob(parameterIndex, value);
  }

  @Override
  public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
    s.setClob(parameterIndex, reader, length);
  }

  @Override
  public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
    s.setBlob(parameterIndex, inputStream, length);
  }

  @Override
  public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
    s.setNClob(parameterIndex, reader, length);
  }

  @Override
  public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
    s.setSQLXML(parameterIndex, xmlObject);
  }

  @Override
  public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {
    s.setObject(parameterIndex, x, targetSqlType, scaleOrLength);
  }

  @Override
  public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
    s.setAsciiStream(parameterIndex, x, length);
  }

  @Override
  public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
    s.setBinaryStream(parameterIndex, x, length);
  }

  @Override
  public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
    s.setCharacterStream(parameterIndex, reader, length);
  }

  @Override
  public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
    s.setAsciiStream(parameterIndex, x);
  }

  @Override
  public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
    s.setBinaryStream(parameterIndex, x);
  }

  @Override
  public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
    s.setCharacterStream(parameterIndex, reader);
  }

  @Override
  public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
    s.setNCharacterStream(parameterIndex, value);
  }

  @Override
  public void setClob(int parameterIndex, Reader reader) throws SQLException {
    s.setClob(parameterIndex, reader);
  }

  @Override
  public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
    s.setBlob(parameterIndex, inputStream);
  }

  @Override
  public void setNClob(int parameterIndex, Reader reader) throws SQLException {
    s.setNClob(parameterIndex, reader);
  }

  @Override
  public ResultSet executeQuery(String sql) throws SQLException {
    return new SingletonResultSet(this, s.executeQuery(sql));
  }

  @Override
  public int executeUpdate(String sql) throws SQLException {
    return s.executeUpdate(sql);
  }

  @Override
  public void close() throws SQLException {
    s.close();
  }

  @Override
  public int getMaxFieldSize() throws SQLException {
    return s.getMaxFieldSize();
  }

  @Override
  public void setMaxFieldSize(int max) throws SQLException {
    s.setMaxFieldSize(max);
  }

  @Override
  public int getMaxRows() throws SQLException {
    return s.getMaxRows();
  }

  @Override
  public void setMaxRows(int max) throws SQLException {
    s.setMaxRows(max);
  }

  @Override
  public void setEscapeProcessing(boolean enable) throws SQLException {
    s.setEscapeProcessing(enable);
  }

  @Override
  public int getQueryTimeout() throws SQLException {
    return s.getQueryTimeout();
  }

  @Override
  public void setQueryTimeout(int seconds) throws SQLException {
    s.setQueryTimeout(seconds);
  }

  @Override
  public void cancel() throws SQLException {
    s.cancel();
  }

  @Override
  public SQLWarning getWarnings() throws SQLException {
    return s.getWarnings();
  }

  @Override
  public void clearWarnings() throws SQLException {
    s.clearWarnings();
  }

  @Override
  public void setCursorName(String name) throws SQLException {
    s.setCursorName(name);
  }

  @Override
  public boolean execute(String sql) throws SQLException {
    return s.execute(sql);
  }

  @Override
  public ResultSet getResultSet() throws SQLException {
    return new SingletonResultSet(this, s.getResultSet());
  }

  @Override
  public int getUpdateCount() throws SQLException {
    return s.getUpdateCount();
  }

  @Override
  public boolean getMoreResults() throws SQLException {
    return s.getMoreResults();
  }

  @Override
  public void setFetchDirection(int direction) throws SQLException {
    s.setFetchDirection(direction);
  }

  @Override
  public int getFetchDirection() throws SQLException {
    return s.getFetchDirection();
  }

  @Override
  public void setFetchSize(int rows) throws SQLException {
    s.setFetchSize(rows);
  }

  @Override
  public int getFetchSize() throws SQLException {
    return s.getFetchSize();
  }

  @Override
  public int getResultSetConcurrency() throws SQLException {
    return s.getResultSetConcurrency();
  }

  @Override
  public int getResultSetType() throws SQLException {
    return s.getResultSetType();
  }

  @Override
  public void addBatch(String sql) throws SQLException {
    s.addBatch(sql);
  }

  @Override
  public void clearBatch() throws SQLException {
    s.clearBatch();
  }

  @Override
  public int[] executeBatch() throws SQLException {
    return s.executeBatch();
  }

  @Override
  public Connection getConnection() throws SQLException {
    return c;
  }

  @Override
  public boolean getMoreResults(int current) throws SQLException {
    return s.getMoreResults(current);
  }

  @Override
  public ResultSet getGeneratedKeys() throws SQLException {
    return new SingletonResultSet(this, s.getGeneratedKeys());
  }

  @Override
  public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
    return s.executeUpdate(sql, autoGeneratedKeys);
  }

  @Override
  public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
    return s.executeUpdate(sql, columnIndexes);
  }

  @Override
  public int executeUpdate(String sql, String[] columnNames) throws SQLException {
    return s.executeUpdate(sql, columnNames);
  }

  @Override
  public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
    return s.execute(sql, autoGeneratedKeys);
  }

  @Override
  public boolean execute(String sql, int[] columnIndexes) throws SQLException {
    return s.execute(sql, columnIndexes);
  }

  @Override
  public boolean execute(String sql, String[] columnNames) throws SQLException {
    return s.execute(sql, columnNames);
  }

  @Override
  public int getResultSetHoldability() throws SQLException {
    return s.getResultSetHoldability();
  }

  @Override
  public boolean isClosed() throws SQLException {
    return s.isClosed();
  }

  @Override
  public void setPoolable(boolean poolable) throws SQLException {
    s.setPoolable(poolable);
  }

  @Override
  public boolean isPoolable() throws SQLException {
    return s.isPoolable();
  }

  @Override
  public void closeOnCompletion() throws SQLException {
    s.closeOnCompletion();
  }

  @Override
  public boolean isCloseOnCompletion() throws SQLException {
    return s.isCloseOnCompletion();
  }

  @Override
  public <T> T unwrap(Class<T> iface) throws SQLException {
    return s.unwrap(iface);
  }

  @Override
  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    return s.isWrapperFor(iface);
  }
}
