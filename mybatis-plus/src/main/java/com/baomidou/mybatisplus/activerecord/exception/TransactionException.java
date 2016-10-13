package com.baomidou.mybatisplus.activerecord.exception;

/**
 * 在处理事务时遇到任何异常抛出此异常。
 * 
 * @since 2.0
 * @author redraiment
 */
public class TransactionException extends RuntimeException {
  public TransactionException(String message, Throwable cause) {
    super(message, cause);
  }
}
