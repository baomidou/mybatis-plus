package com.baomidou.mybatisplus.core.test.lambda1;

import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SerializedFunction;
import com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda;

/**
 * @author ming
 * @Date 2018/5/11
 */
public class LambdaWrapper1<T> extends AbstractWrapper1<LambdaWrapper1<T>, T, Property<T, ?>> {

    @Override
    String getColumn(Property<T, ?> tProperty) {
        //todo 能执行?
        SerializedLambda resolve = LambdaUtils.resolve((SerializedFunction<T, ?>)tProperty);
        return resolve.getImplMethodName();
    }

    @Override
    public String getSqlSegment() {
        return null;
    }
}
