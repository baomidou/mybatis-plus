package com.baomidou.mybatisplus.core.test.LambdaTest;

import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SerializedFunction;
import com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda;

/**
 * @author ming
 * @Date 2018/5/10
 */
public class LambdaEntityWrapper<T> extends Wrappers<LambdaEntityWrapper<T>, SerializedFunction<T, ?>, T> {

    @Override
    String getColumn(SerializedFunction<T, ?> tFunction) {
        //todo 能执行?
        SerializedLambda resolve = LambdaUtils.resolve(tFunction);
        return resolve.getImplMethodName();
    }
}
