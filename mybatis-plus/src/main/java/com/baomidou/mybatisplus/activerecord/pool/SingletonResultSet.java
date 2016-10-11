package com.baomidou.mybatisplus.activerecord.pool;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

/**
 *
 * @author redraiment
 * @since 2.2
 */
final class SingletonResultSet implements ResultSet {
  private final Statement s;
  private final ResultSet rs;

  SingletonResultSet(Statement s, ResultSet rs) {
    this.s = s;
    this.rs = rs;
  }

  @Override
  public boolean next() throws SQLException {
    return rs.next();
  }

  @Override
  public void close() throws SQLException {
    rs.close();
  }

  @Override
  public boolean wasNull() throws SQLException {
    return rs.wasNull();
  }

  @Override
  public String getString(int columnIndex) throws SQLException {
    return rs.getString(columnIndex);
  }

  @Override
  public boolean getBoolean(int columnIndex) throws SQLException {
    return rs.getBoolean(columnIndex);
  }

  @Override
  public byte getByte(int columnIndex) throws SQLException {
    return rs.getByte(columnIndex);
  }

  @Override
  public short getShort(int columnIndex) throws SQLException {
    return rs.getShort(columnIndex);
  }

  @Override
  public int getInt(int columnIndex) throws SQLException {
    return rs.getInt(columnIndex);
  }

  @Override
  public long getLong(int columnIndex) throws SQLException {
    return rs.getLong(columnIndex);
  }

  @Override
  public float getFloat(int columnIndex) throws SQLException {
    return rs.getFloat(columnIndex);
  }

  @Override
  public double getDouble(int columnIndex) throws SQLException {
    return rs.getDouble(columnIndex);
  }

  @Override
  public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
    return rs.getBigDecimal(columnIndex, scale);
  }

  @Override
  public byte[] getBytes(int columnIndex) throws SQLException {
    return rs.getBytes(columnIndex);
  }

  @Override
  public Date getDate(int columnIndex) throws SQLException {
    return rs.getDate(columnIndex);
  }

  @Override
  public Time getTime(int columnIndex) throws SQLException {
    return rs.getTime(columnIndex);
  }

  @Override
  public Timestamp getTimestamp(int columnIndex) throws SQLException {
    return rs.getTimestamp(columnIndex);
  }

  @Override
  public InputStream getAsciiStream(int columnIndex) throws SQLException {
    return rs.getAsciiStream(columnIndex);
  }

  @Override
  public InputStream getUnicodeStream(int columnIndex) throws SQLException {
    return rs.getUnicodeStream(columnIndex);
  }

  @Override
  public InputStream getBinaryStream(int columnIndex) throws SQLException {
    return rs.getBinaryStream(columnIndex);
  }

  @Override
  public String getString(String columnLabel) throws SQLException {
    return rs.getString(columnLabel);
  }

  @Override
  public boolean getBoolean(String columnLabel) throws SQLException {
    return rs.getBoolean(columnLabel);
  }

  @Override
  public byte getByte(String columnLabel) throws SQLException {
    return rs.getByte(columnLabel);
  }

  @Override
  public short getShort(String columnLabel) throws SQLException {
    return rs.getShort(columnLabel);
  }

  @Override
  public int getInt(String columnLabel) throws SQLException {
    return rs.getInt(columnLabel);
  }

  @Override
  public long getLong(String columnLabel) throws SQLException {
    return rs.getLong(columnLabel);
  }

  @Override
  public float getFloat(String columnLabel) throws SQLException {
    return rs.getFloat(columnLabel);
  }

  @Override
  public double getDouble(String columnLabel) throws SQLException {
    return rs.getDouble(columnLabel);
  }

  @Override
  public BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
    return rs.getBigDecimal(columnLabel, scale);
  }

  @Override
  public byte[] getBytes(String columnLabel) throws SQLException {
    return rs.getBytes(columnLabel);
  }

  @Override
  public Date getDate(String columnLabel) throws SQLException {
    return rs.getDate(columnLabel);
  }

  @Override
  public Time getTime(String columnLabel) throws SQLException {
    return rs.getTime(columnLabel);
  }

  @Override
  public Timestamp getTimestamp(String columnLabel) throws SQLException {
    return rs.getTimestamp(columnLabel);
  }

  @Override
  public InputStream getAsciiStream(String columnLabel) throws SQLException {
    return rs.getAsciiStream(columnLabel);
  }

  @Override
  public InputStream getUnicodeStream(String columnLabel) throws SQLException {
    return rs.getUnicodeStream(columnLabel);
  }

  @Override
  public InputStream getBinaryStream(String columnLabel) throws SQLException {
    return rs.getBinaryStream(columnLabel);
  }

  @Override
  public SQLWarning getWarnings() throws SQLException {
    return rs.getWarnings();
  }

  @Override
  public void clearWarnings() throws SQLException {
    rs.clearWarnings();
  }

  @Override
  public String getCursorName() throws SQLException {
    return rs.getCursorName();
  }

  @Override
  public ResultSetMetaData getMetaData() throws SQLException {
    return rs.getMetaData();
  }

  @Override
  public Object getObject(int columnIndex) throws SQLException {
    return rs.getObject(columnIndex);
  }

  @Override
  public Object getObject(String columnLabel) throws SQLException {
    return rs.getObject(columnLabel);
  }

  @Override
  public int findColumn(String columnLabel) throws SQLException {
    return rs.findColumn(columnLabel);
  }

  @Override
  public Reader getCharacterStream(int columnIndex) throws SQLException {
    return rs.getCharacterStream(columnIndex);
  }

  @Override
  public Reader getCharacterStream(String columnLabel) throws SQLException {
    return rs.getCharacterStream(columnLabel);
  }

  @Override
  public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
    return rs.getBigDecimal(columnIndex);
  }

  @Override
  public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
    return rs.getBigDecimal(columnLabel);
  }

  @Override
  public boolean isBeforeFirst() throws SQLException {
    return rs.isBeforeFirst();
  }

  @Override
  public boolean isAfterLast() throws SQLException {
    return rs.isAfterLast();
  }

  @Override
  public boolean isFirst() throws SQLException {
    return rs.isFirst();
  }

  @Override
  public boolean isLast() throws SQLException {
    return rs.isLast();
  }

  @Override
  public void beforeFirst() throws SQLException {
    rs.beforeFirst();
  }

  @Override
  public void afterLast() throws SQLException {
    rs.afterLast();
  }

  @Override
  public boolean first() throws SQLException {
    return rs.first();
  }

  @Override
  public boolean last() throws SQLException {
    return rs.last();
  }

  @Override
  public int getRow() throws SQLException {
    return rs.getRow();
  }

  @Override
  public boolean absolute(int row) throws SQLException {
    return rs.absolute(row);
  }

  @Override
  public boolean relative(int rows) throws SQLException {
    return rs.relative(rows);
  }

  @Override
  public boolean previous() throws SQLException {
    return rs.previous();
  }

  @Override
  public void setFetchDirection(int direction) throws SQLException {
    rs.setFetchDirection(direction);
  }

  @Override
  public int getFetchDirection() throws SQLException {
    return rs.getFetchDirection();
  }

  @Override
  public void setFetchSize(int rows) throws SQLException {
    rs.setFetchSize(rows);
  }

  @Override
  public int getFetchSize() throws SQLException {
    return rs.getFetchSize();
  }

  @Override
  public int getType() throws SQLException {
    return rs.getType();
  }

  @Override
  public int getConcurrency() throws SQLException {
    return rs.getConcurrency();
  }

  @Override
  public boolean rowUpdated() throws SQLException {
    return rs.rowUpdated();
  }

  @Override
  public boolean rowInserted() throws SQLException {
    return rs.rowInserted();
  }

  @Override
  public boolean rowDeleted() throws SQLException {
    return rs.rowDeleted();
  }

  @Override
  public void updateNull(int columnIndex) throws SQLException {
    rs.updateNull(columnIndex);
  }

  @Override
  public void updateBoolean(int columnIndex, boolean x) throws SQLException {
    rs.updateBoolean(columnIndex, x);
  }

  @Override
  public void updateByte(int columnIndex, byte x) throws SQLException {
    rs.updateByte(columnIndex, x);
  }

  @Override
  public void updateShort(int columnIndex, short x) throws SQLException {
    rs.updateShort(columnIndex, x);
  }

  @Override
  public void updateInt(int columnIndex, int x) throws SQLException {
    rs.updateInt(columnIndex, x);
  }

  @Override
  public void updateLong(int columnIndex, long x) throws SQLException {
    rs.updateLong(columnIndex, x);
  }

  @Override
  public void updateFloat(int columnIndex, float x) throws SQLException {
    rs.updateFloat(columnIndex, x);
  }

  @Override
  public void updateDouble(int columnIndex, double x) throws SQLException {
    rs.updateDouble(columnIndex, x);
  }

  @Override
  public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
    rs.updateBigDecimal(columnIndex, x);
  }

  @Override
  public void updateString(int columnIndex, String x) throws SQLException {
    rs.updateString(columnIndex, x);
  }

  @Override
  public void updateBytes(int columnIndex, byte[] x) throws SQLException {
    rs.updateBytes(columnIndex, x);
  }

  @Override
  public void updateDate(int columnIndex, Date x) throws SQLException {
    rs.updateDate(columnIndex, x);
  }

  @Override
  public void updateTime(int columnIndex, Time x) throws SQLException {
    rs.updateTime(columnIndex, x);
  }

  @Override
  public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
    rs.updateTimestamp(columnIndex, x);
  }

  @Override
  public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
    rs.updateAsciiStream(columnIndex, x, length);
  }

  @Override
  public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
    rs.updateBinaryStream(columnIndex, x, length);
  }

  @Override
  public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
    rs.updateCharacterStream(columnIndex, x, length);
  }

  @Override
  public void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {
    rs.updateObject(columnIndex, x, scaleOrLength);
  }

  @Override
  public void updateObject(int columnIndex, Object x) throws SQLException {
    rs.updateObject(columnIndex, x);
  }

  @Override
  public void updateNull(String columnLabel) throws SQLException {
    rs.updateNull(columnLabel);
  }

  @Override
  public void updateBoolean(String columnLabel, boolean x) throws SQLException {
    rs.updateBoolean(columnLabel, x);
  }

  @Override
  public void updateByte(String columnLabel, byte x) throws SQLException {
    rs.updateByte(columnLabel, x);
  }

  @Override
  public void updateShort(String columnLabel, short x) throws SQLException {
    rs.updateShort(columnLabel, x);
  }

  @Override
  public void updateInt(String columnLabel, int x) throws SQLException {
    rs.updateInt(columnLabel, x);
  }

  @Override
  public void updateLong(String columnLabel, long x) throws SQLException {
    rs.updateLong(columnLabel, x);
  }

  @Override
  public void updateFloat(String columnLabel, float x) throws SQLException {
    rs.updateFloat(columnLabel, x);
  }

  @Override
  public void updateDouble(String columnLabel, double x) throws SQLException {
    rs.updateDouble(columnLabel, x);
  }

  @Override
  public void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException {
    rs.updateBigDecimal(columnLabel, x);
  }

  @Override
  public void updateString(String columnLabel, String x) throws SQLException {
    rs.updateString(columnLabel, x);
  }

  @Override
  public void updateBytes(String columnLabel, byte[] x) throws SQLException {
    rs.updateBytes(columnLabel, x);
  }

  @Override
  public void updateDate(String columnLabel, Date x) throws SQLException {
    rs.updateDate(columnLabel, x);
  }

  @Override
  public void updateTime(String columnLabel, Time x) throws SQLException {
    rs.updateTime(columnLabel, x);
  }

  @Override
  public void updateTimestamp(String columnLabel, Timestamp x) throws SQLException {
    rs.updateTimestamp(columnLabel, x);
  }

  @Override
  public void updateAsciiStream(String columnLabel, InputStream x, int length) throws SQLException {
    rs.updateAsciiStream(columnLabel, x, length);
  }

  @Override
  public void updateBinaryStream(String columnLabel, InputStream x, int length) throws SQLException {
    rs.updateBinaryStream(columnLabel, x, length);
  }

  @Override
  public void updateCharacterStream(String columnLabel, Reader reader, int length) throws SQLException {
    rs.updateCharacterStream(columnLabel, reader, length);
  }

  @Override
  public void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {
    rs.updateObject(columnLabel, x, scaleOrLength);
  }

  @Override
  public void updateObject(String columnLabel, Object x) throws SQLException {
    rs.updateObject(columnLabel, x);
  }

  @Override
  public void insertRow() throws SQLException {
    rs.insertRow();
  }

  @Override
  public void updateRow() throws SQLException {
    rs.updateRow();
  }

  @Override
  public void deleteRow() throws SQLException {
    rs.deleteRow();
  }

  @Override
  public void refreshRow() throws SQLException {
    rs.refreshRow();
  }

  @Override
  public void cancelRowUpdates() throws SQLException {
    rs.cancelRowUpdates();
  }

  @Override
  public void moveToInsertRow() throws SQLException {
    rs.moveToInsertRow();
  }

  @Override
  public void moveToCurrentRow() throws SQLException {
    rs.moveToCurrentRow();
  }

  @Override
  public Statement getStatement() throws SQLException {
    return s;
  }

  @Override
  public Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {
    return rs.getObject(columnIndex, map);
  }

  @Override
  public Ref getRef(int columnIndex) throws SQLException {
    return rs.getRef(columnIndex);
  }

  @Override
  public Blob getBlob(int columnIndex) throws SQLException {
    return rs.getBlob(columnIndex);
  }

  @Override
  public Clob getClob(int columnIndex) throws SQLException {
    return rs.getClob(columnIndex);
  }

  @Override
  public Array getArray(int columnIndex) throws SQLException {
    return rs.getArray(columnIndex);
  }

  @Override
  public Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException {
    return rs.getObject(columnLabel, map);
  }

  @Override
  public Ref getRef(String columnLabel) throws SQLException {
    return rs.getRef(columnLabel);
  }

  @Override
  public Blob getBlob(String columnLabel) throws SQLException {
    return rs.getBlob(columnLabel);
  }

  @Override
  public Clob getClob(String columnLabel) throws SQLException {
    return rs.getClob(columnLabel);
  }

  @Override
  public Array getArray(String columnLabel) throws SQLException {
    return rs.getArray(columnLabel);
  }

  @Override
  public Date getDate(int columnIndex, Calendar cal) throws SQLException {
    return rs.getDate(columnIndex, cal);
  }

  @Override
  public Date getDate(String columnLabel, Calendar cal) throws SQLException {
    return rs.getDate(columnLabel, cal);
  }

  @Override
  public Time getTime(int columnIndex, Calendar cal) throws SQLException {
    return rs.getTime(columnIndex, cal);
  }

  @Override
  public Time getTime(String columnLabel, Calendar cal) throws SQLException {
    return rs.getTime(columnLabel, cal);
  }

  @Override
  public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
    return rs.getTimestamp(columnIndex, cal);
  }

  @Override
  public Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
    return rs.getTimestamp(columnLabel, cal);
  }

  @Override
  public URL getURL(int columnIndex) throws SQLException {
    return rs.getURL(columnIndex);
  }

  @Override
  public URL getURL(String columnLabel) throws SQLException {
    return rs.getURL(columnLabel);
  }

  @Override
  public void updateRef(int columnIndex, Ref x) throws SQLException {
    rs.updateRef(columnIndex, x);
  }

  @Override
  public void updateRef(String columnLabel, Ref x) throws SQLException {
    rs.updateRef(columnLabel, x);
  }

  @Override
  public void updateBlob(int columnIndex, Blob x) throws SQLException {
    rs.updateBlob(columnIndex, x);
  }

  @Override
  public void updateBlob(String columnLabel, Blob x) throws SQLException {
    rs.updateBlob(columnLabel, x);
  }

  @Override
  public void updateClob(int columnIndex, Clob x) throws SQLException {
    rs.updateClob(columnIndex, x);
  }

  @Override
  public void updateClob(String columnLabel, Clob x) throws SQLException {
    rs.updateClob(columnLabel, x);
  }

  @Override
  public void updateArray(int columnIndex, Array x) throws SQLException {
    rs.updateArray(columnIndex, x);
  }

  @Override
  public void updateArray(String columnLabel, Array x) throws SQLException {
    rs.updateArray(columnLabel, x);
  }

  @Override
  public RowId getRowId(int columnIndex) throws SQLException {
    return rs.getRowId(columnIndex);
  }

  @Override
  public RowId getRowId(String columnLabel) throws SQLException {
    return rs.getRowId(columnLabel);
  }

  @Override
  public void updateRowId(int columnIndex, RowId x) throws SQLException {
    rs.updateRowId(columnIndex, x);
  }

  @Override
  public void updateRowId(String columnLabel, RowId x) throws SQLException {
    rs.updateRowId(columnLabel, x);
  }

  @Override
  public int getHoldability() throws SQLException {
    return rs.getHoldability();
  }

  @Override
  public boolean isClosed() throws SQLException {
    return rs.isClosed();
  }

  @Override
  public void updateNString(int columnIndex, String nString) throws SQLException {
    rs.updateNString(columnIndex, nString);
  }

  @Override
  public void updateNString(String columnLabel, String nString) throws SQLException {
    rs.updateNString(columnLabel, nString);
  }

  @Override
  public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
    rs.updateNClob(columnIndex, nClob);
  }

  @Override
  public void updateNClob(String columnLabel, NClob nClob) throws SQLException {
    rs.updateNClob(columnLabel, nClob);
  }

  @Override
  public NClob getNClob(int columnIndex) throws SQLException {
    return rs.getNClob(columnIndex);
  }

  @Override
  public NClob getNClob(String columnLabel) throws SQLException {
    return rs.getNClob(columnLabel);
  }

  @Override
  public SQLXML getSQLXML(int columnIndex) throws SQLException {
    return rs.getSQLXML(columnIndex);
  }

  @Override
  public SQLXML getSQLXML(String columnLabel) throws SQLException {
    return rs.getSQLXML(columnLabel);
  }

  @Override
  public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
    rs.updateSQLXML(columnIndex, xmlObject);
  }

  @Override
  public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
    rs.updateSQLXML(columnLabel, xmlObject);
  }

  @Override
  public String getNString(int columnIndex) throws SQLException {
    return rs.getNString(columnIndex);
  }

  @Override
  public String getNString(String columnLabel) throws SQLException {
    return rs.getNString(columnLabel);
  }

  @Override
  public Reader getNCharacterStream(int columnIndex) throws SQLException {
    return rs.getNCharacterStream(columnIndex);
  }

  @Override
  public Reader getNCharacterStream(String columnLabel) throws SQLException {
    return rs.getNCharacterStream(columnLabel);
  }

  @Override
  public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
    rs.updateNCharacterStream(columnIndex, x, length);
  }

  @Override
  public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
    rs.updateNCharacterStream(columnLabel, reader, length);
  }

  @Override
  public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
    rs.updateAsciiStream(columnIndex, x, length);
  }

  @Override
  public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
    rs.updateBinaryStream(columnIndex, x, length);
  }

  @Override
  public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
    rs.updateCharacterStream(columnIndex, x, length);
  }

  @Override
  public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
    rs.updateAsciiStream(columnLabel, x, length);
  }

  @Override
  public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
    rs.updateBinaryStream(columnLabel, x, length);
  }

  @Override
  public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
    rs.updateCharacterStream(columnLabel, reader, length);
  }

  @Override
  public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
    rs.updateBlob(columnIndex, inputStream, length);
  }

  @Override
  public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
    rs.updateBlob(columnLabel, inputStream, length);
  }

  @Override
  public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
    rs.updateClob(columnIndex, reader, length);
  }

  @Override
  public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
    rs.updateClob(columnLabel, reader, length);
  }

  @Override
  public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
    rs.updateNClob(columnIndex, reader, length);
  }

  @Override
  public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
    rs.updateNClob(columnLabel, reader, length);
  }

  @Override
  public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
    rs.updateNCharacterStream(columnIndex, x);
  }

  @Override
  public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
    rs.updateNCharacterStream(columnLabel, reader);
  }

  @Override
  public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
    rs.updateAsciiStream(columnIndex, x);
  }

  @Override
  public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
    rs.updateBinaryStream(columnIndex, x);
  }

  @Override
  public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
    rs.updateCharacterStream(columnIndex, x);
  }

  @Override
  public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
    rs.updateAsciiStream(columnLabel, x);
  }

  @Override
  public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
    rs.updateBinaryStream(columnLabel, x);
  }

  @Override
  public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
    rs.updateCharacterStream(columnLabel, reader);
  }

  @Override
  public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
    rs.updateBlob(columnIndex, inputStream);
  }

  @Override
  public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
    rs.updateBlob(columnLabel, inputStream);
  }

  @Override
  public void updateClob(int columnIndex, Reader reader) throws SQLException {
    rs.updateClob(columnIndex, reader);
  }

  @Override
  public void updateClob(String columnLabel, Reader reader) throws SQLException {
    rs.updateClob(columnLabel, reader);
  }

  @Override
  public void updateNClob(int columnIndex, Reader reader) throws SQLException {
    rs.updateNClob(columnIndex, reader);
  }

  @Override
  public void updateNClob(String columnLabel, Reader reader) throws SQLException {
    rs.updateNClob(columnLabel, reader);
  }

  @Override
  public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
    return rs.getObject(columnIndex, type);
  }

  @Override
  public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
    return rs.getObject(columnLabel, type);
  }

  @Override
  public <T> T unwrap(Class<T> iface) throws SQLException {
    return rs.unwrap(iface);
  }

  @Override
  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    return rs.isWrapperFor(iface);
  }
}
