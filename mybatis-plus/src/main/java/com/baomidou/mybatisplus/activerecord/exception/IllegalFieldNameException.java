package com.baomidou.mybatisplus.activerecord.exception;

/**
 * 列不存在。
 * 
 * @since 1.0
 * @author redraiment
 */
public class IllegalFieldNameException extends RuntimeException {
	public IllegalFieldNameException(String fieldName) {
		super(String.format("illegal field %s", fieldName));
	}

	public IllegalFieldNameException(String fieldName, Throwable cause) {
		super(String.format("illegal field %s", fieldName), cause);
	}
}
