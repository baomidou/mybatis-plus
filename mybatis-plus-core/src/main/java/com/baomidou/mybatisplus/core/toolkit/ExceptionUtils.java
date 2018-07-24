package com.baomidou.mybatisplus.core.toolkit;

import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;

/**
 * @author HCL
 * 2018/7/24 18:45
 */
public final class ExceptionUtils {

    private ExceptionUtils() {
    }

    /**
     * 返回一个新的异常，统一构建，方便统一处理
     *
     * @param msg 消息
     * @param t   异常信息
     * @return 返回异常
     */
    public static MybatisPlusException mpe(String msg, Throwable t) {
        return new MybatisPlusException(msg, t);
    }

    /**
     * 重载的方法
     *
     * @param msg 消息
     * @return 返回异常
     */
    public static MybatisPlusException mpe(String msg) {
        return new MybatisPlusException(msg);
    }

    /**
     * 重载的方法
     *
     * @param t 异常
     * @return 返回异常
     */
    public static MybatisPlusException mpe(Throwable t) {
        return new MybatisPlusException(t);
    }

}
