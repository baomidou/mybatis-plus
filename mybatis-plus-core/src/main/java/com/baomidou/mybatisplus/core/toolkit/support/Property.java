package com.baomidou.mybatisplus.core.toolkit.support;

import java.io.Serializable;

/**
 * @author ming
 * @since 2018/5/12
 */
@FunctionalInterface
public interface Property<T, R> extends Serializable {

    R apply(T t);
}
