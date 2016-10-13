package com.baomidou.mybatisplus.activerecord.exception;

/**
 * 未找到相应的方言。
 * 
 * @since 1.0
 * @author redraiment
 */
public class UnsupportedDatabaseException extends RuntimeException {
  public UnsupportedDatabaseException(String product) {
    super(String.format("Unsupported Database: %s", product));
  }
}
