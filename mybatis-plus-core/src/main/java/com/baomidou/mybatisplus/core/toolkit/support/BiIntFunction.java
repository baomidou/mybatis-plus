package com.baomidou.mybatisplus.core.toolkit.support;

/**
 * 接受 Int 小类型的处理函数，使用小类型来避免 Java 自动装箱
 *
 * @author HCL
 * Create at 2018/11/19
 */
@FunctionalInterface
public interface BiIntFunction<T, R> {

    /**
     * 函数主接口
     *
     * @param t 被执行类型 T
     * @param i 参数
     * @return 返回
     */
    R apply(T t, int i);

}
