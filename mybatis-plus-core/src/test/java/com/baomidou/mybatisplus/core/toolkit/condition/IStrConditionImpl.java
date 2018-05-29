package com.baomidou.mybatisplus.core.toolkit.condition;

/**
 * Create by HCL at 2018/05/29
 */
public class IStrConditionImpl<T, SC> extends AbstractCondition<SC, T, String>
    implements IStrCondition<T, SC> {

    @Override
    protected String getColumn(String s) {
        return s;
    }

}
