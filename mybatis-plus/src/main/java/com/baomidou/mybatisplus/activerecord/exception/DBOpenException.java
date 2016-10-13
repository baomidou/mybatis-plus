package com.baomidou.mybatisplus.activerecord.exception;

/**
 * 连接数据库时遇到任何问题抛出此异常。
 * 
 * @since 1.0
 * @author redraiment
 */
public class DBOpenException extends RuntimeException {
  public DBOpenException(Throwable cause) {
    super(cause);
  }
}
