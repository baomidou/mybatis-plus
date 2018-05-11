package com.baomidou.mybatisplus.core.test.lambda1;

import java.io.Serializable;

/**
 * @author ming
 * @Date 2018/5/11
 */
@FunctionalInterface
public interface Property<T, R> extends Serializable {

    R apply(T r);
}
