package com.baomidou.mybatisplus.activerecord;

import java.lang.reflect.Method;

import com.baomidou.mybatisplus.activerecord.exception.IllegalFieldNameException;

final class Lambda {
	private final Object o;
	private final Method fn;

	Lambda(Object o, Method fn) {
		this.o = o;
		this.fn = fn;
	}

	Object call(Record record, Object value) {
		try {
			return fn.invoke(o, record, value);
		} catch (Exception e) {
			throw new IllegalFieldNameException(fn.getName(), e);
		}
	}
}
