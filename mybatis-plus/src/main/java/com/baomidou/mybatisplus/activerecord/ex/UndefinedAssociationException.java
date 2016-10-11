package com.baomidou.mybatisplus.activerecord.ex;

/**
 * 遇到未定义的关联时抛出此异常。
 * 
 * @since 1.0
 * @author redraiment
 */
public class UndefinedAssociationException extends RuntimeException {
  public UndefinedAssociationException(String name) {
    super(String.format("undefined association name: %s", name));
  }
}
