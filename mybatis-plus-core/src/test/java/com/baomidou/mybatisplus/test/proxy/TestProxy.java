/**
 * Copyright (c) 2011-2014, hubin (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.test.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.baomidou.mybatisplus.test.proxy.mapper.IUserMapper;
import com.baomidou.mybatisplus.test.proxy.mapper.User;
import com.baomidou.mybatisplus.test.proxy.mapper.UserMapperImpl;

/**
 * <p>
 * mybatis 执行原理测试
 * </p>
 *
 * @author hubin
 * @Date 2016-07-06
 */
public class TestProxy {

    public static void main(String[] args) {
        /**
         * 代理方式一
         */
        IUserMapper userMapper = MapperProxyFactory.getMapper(IUserMapper.class);
        User user = userMapper.selectById(1L);
        System.err.println((user == null) ? "代理失败" : user.toString());
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            System.err.println("\n");
        }

        /**
         * 代理方式二
         */
        MyMapperProxy<IUserMapper> userDaoProxy = new MyMapperProxy<>();
        IUserMapper mapper = userDaoProxy.bind(new UserMapperImpl());
        User user1 = mapper.selectById(1L);
        System.err.println((user1 == null) ? "代理失败" : user1.toString());
    }

}

/**
 * 动态代理方式二
 */
class MyMapperProxy<T extends IUserMapper> implements InvocationHandler {

    private T t;

    @SuppressWarnings("unchecked")
    public T bind(T t) {
        this.t = t;
        return (T) Proxy.newProxyInstance(t.getClass().getClassLoader(), t.getClass().getInterfaces(), this);
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object rst;
        before();
        rst = method.invoke(t, args);
        after();
        return rst;
    }

    private void before() {
        System.out.println("before ...");
    }

    private void after() {
        System.out.println("after");
    }
}
