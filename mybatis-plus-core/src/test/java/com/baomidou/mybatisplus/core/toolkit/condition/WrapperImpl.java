package com.baomidou.mybatisplus.core.toolkit.condition;

/**
 * Create by HCL at 2018/05/29
 */
public class WrapperImpl<T> extends IStrConditionImpl<T, Wrapper<T>> implements Wrapper<T> {

    @Override
    public String getSqlSeq() {
        return super.toString();
    }



}
