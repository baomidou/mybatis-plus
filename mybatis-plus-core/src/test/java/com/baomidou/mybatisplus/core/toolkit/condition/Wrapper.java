package com.baomidou.mybatisplus.core.toolkit.condition;

/**
 * Create by HCL at 2018/05/29
 */
public interface Wrapper<T> extends IStrCondition<T, Wrapper<T>> {

    String getSqlSeq();

}
