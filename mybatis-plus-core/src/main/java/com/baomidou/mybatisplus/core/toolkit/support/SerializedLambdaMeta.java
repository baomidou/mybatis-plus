package com.baomidou.mybatisplus.core.toolkit.support;

import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.toolkit.ClassUtils;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;

/**
 * Created by hcl at 2021/5/14
 */
public class SerializedLambdaMeta implements LambdaMeta {

    private final SerializedLambda lambda;

    public SerializedLambdaMeta(SerializedLambda lambda) {
        this.lambda = lambda;
    }

    @Override
    public String getImplMethodName() {
        return lambda.getImplMethodName();
    }

    @Override
    public Class<?> getInstantiatedClass() {
        String instantiatedMethodType = lambda.getInstantiatedMethodType();
        String instantiatedType = instantiatedMethodType.substring(2, instantiatedMethodType.indexOf(';')).replace('/', '.');
        return ClassUtils.toClassConfident(instantiatedType, getCapturingClass().getClassLoader());
    }

    public Class<?> getCapturingClass() {
        try {
            return Class.forName(lambda.getCapturingClass().replace('/', '.'));
        } catch (ClassNotFoundException e) {
            throw new MybatisPlusException(e);
        }
    }

}
