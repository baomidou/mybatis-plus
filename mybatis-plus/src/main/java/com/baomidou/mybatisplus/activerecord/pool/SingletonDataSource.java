package com.baomidou.mybatisplus.activerecord.pool;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;
import javax.sql.DataSource;

/**
 * <p>jActiveRecord默认DataSource实现，永远只返回同一个数据库连接。
 * 因此，内存型的数据库也能正常工作。</p>
 * 
 * @since 2.0
 * @author redraiment
 */
public final class SingletonDataSource implements DataSource {
  private final Connection instance;

  /**
   * 提供连接数据库基本信息。
   * 
   * @param url 数据库连接地址
   * @param info 包含用户名、密码等登入信息。
   * @throws java.sql.SQLException 连接数据库失败
   */
  public SingletonDataSource(String url, Properties info) throws SQLException {
    final Connection c = DriverManager.getConnection(url, info);
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        try {
          c.close();
        } catch (SQLException e) {
          throw new RuntimeException("close database fail");
        }
      }
    });
    instance = new SingletonConnection(c);
  }

  /**
   * 每次调用时均返回一个新的数据库连接。
   * 
   * @return 一个新的数据库连接。
   * @throws SQLException 连接数据库失败。
   */
  @Override
  public Connection getConnection() throws SQLException {
    return instance;
  }

  /**
   * 每次调用时均返回一个新的数据库连接。
   * 
   * @return 一个新的数据库连接。
   * @throws SQLException 连接数据库失败。
   */
  @Override
  public Connection getConnection(String username, String password) throws SQLException {
    return instance;
  }

  /**
   * 不支持，永远不会被调用。
   * 
   * @return 无
   * @throws SQLException 从不
   */
  @Override
  public PrintWriter getLogWriter() throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  /**
   * 不支持，永远不会被调用。
   * 
   * @throws SQLException 从不
   */
  @Override
  public void setLogWriter(PrintWriter out) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  /**
   * 不支持，永远不会被调用。
   * 
   * @throws SQLException 从不
   */
  @Override
  public void setLoginTimeout(int seconds) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  /**
   * 不支持，永远不会被调用。
   * 
   * @return 无
   * @throws SQLException 从不
   */
  @Override
  public int getLoginTimeout() throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  /**
   * 不支持，永远不会被调用。
   * 
   * @return 无
   * @throws SQLFeatureNotSupportedException 从不
   */
  @Override
  public Logger getParentLogger() throws SQLFeatureNotSupportedException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  /**
   * 不支持，永远不会被调用。
   * 
   * @param <T> 类型
   * @return 无
   * @throws SQLException 从不
   */
  @Override
  public <T> T unwrap(Class<T> iface) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  /**
   * 不支持，永远不会被调用。
   * 
   * @return 无
   * @throws SQLException 从不
   */
  @Override
  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
