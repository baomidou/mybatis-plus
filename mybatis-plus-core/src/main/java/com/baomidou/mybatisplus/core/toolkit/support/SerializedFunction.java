package com.baomidou.mybatisplus.core.toolkit.support;

import java.io.Serializable;
import java.util.function.Function;

/**
 * 主要是为了实现序列化接口
 * <p>
 *
 * @author HCL
 * @Date 2018/05/10
 */
@FunctionalInterface
public interface SerializedFunction<T, R> extends Function<T, R>, Serializable {

}
