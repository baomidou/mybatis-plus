package com.baomidou.mybatisplus.core.test.LambdaTest;

/**
 * @author ming
 * @Date 2018/5/10
 * @T 实体类
 */
public class EntityWrappers<T> extends Wrappers<EntityWrappers<T>, String, T> {

    @Override
    String getColumn(String s) {
        return s;
    }
}
