package com.baomidou.mybatisplus.core.test.LambdaTest;

import com.baomidou.mybatisplus.core.test.User;

/**
 * @author ming
 * @Date 2018/5/10
 */
public class JustTestIt {

    public static void main(String[] args) {
        LambdaEntityWrapper<User> lambdaEntityWrapper = new LambdaEntityWrapper<User>().eq(User::getId, 1);
        EntityWrappers<User> entityWrappers = new EntityWrappers<User>().eq("11", 1);
        //下面啥都没有
        System.out.println(lambdaEntityWrapper.getSqlSegment());
        System.out.println(entityWrappers.getSqlSegment());
    }
}
