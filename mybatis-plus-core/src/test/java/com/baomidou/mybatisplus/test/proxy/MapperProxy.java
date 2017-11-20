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

import com.baomidou.mybatisplus.test.proxy.mapper.IUserMapper;
import com.baomidou.mybatisplus.test.proxy.mapper.UserMapperImpl;

/**
 * <p>
 * 类似 org.apache.ibatis.binding.MapperProxy<T>
 * </p>
 *
 * @author hubin
 * @Date 2016-07-06
 */
public class MapperProxy<T> implements InvocationHandler {

    private final Class<T> methodInterface;

    public MapperProxy(Class<T> methodInterface) {
        this.methodInterface = methodInterface;
    }

    /**
     * <p>
     * MyBatis 实现原理，在该方法中通过 Method 获取接口和方法名，
     * 接口的全名相当于MyBatis XML中的namespace，方法名相当于具体一个方法中的id。
     * </p>
     */
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Object.class.equals(method.getDeclaringClass())) {
            return method.invoke(this, args);
        }

		/* 执行 SQL */
        return execute(method, args);
    }


    /**
     * <p>
     * 这里测试模拟已经注入了实现 SQL
     * </p>
     * <p>
     * 通过动态代理 SqlSession 根据 namespace.id 调用 org.apache.ibatis.binding.MapperMethod 对应方法执行。
     * </p>
     */
    public Object execute(Method method, Object[] args) {
        if (IUserMapper.class.isAssignableFrom(methodInterface)) {
            System.out.println("类名: " + methodInterface.getName() + " , 方法名: " + method.getName());
        }
        return new UserMapperImpl().selectById((Long) args[0]);
    }
}
