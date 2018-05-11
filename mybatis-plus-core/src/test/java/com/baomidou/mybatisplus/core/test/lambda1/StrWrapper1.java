package com.baomidou.mybatisplus.core.test.lambda1;

/**
 * @author ming
 * @Date 2018/5/11
 */
public class StrWrapper1<T> extends AbstractWrapper1<StrWrapper1<T>, T, String> {

    @Override
    String getColumn(String s) {
        return s;
    }

    @Override
    public String getSqlSegment() {
        return "";
    }
}
