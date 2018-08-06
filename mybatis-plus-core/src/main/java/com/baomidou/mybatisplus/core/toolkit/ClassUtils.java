/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baomidou.mybatisplus.core.toolkit;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

/**
 * ClassUtils
 *
 * @author Caratacus
 * @author HCL
 * @since 2017/07/08
 */
public final class ClassUtils {

    /**
     * 代理 class 的名称
     */
    private static final List<String> PROXY_CLASS_NAMES = Arrays.asList("net.sf.cglib.proxy.Factory"
        // cglib
        , "org.springframework.cglib.proxy.Factory"
        , "javassist.util.proxy.ProxyObject"
        // javassist
        , "org.apache.ibatis.javassist.util.proxy.ProxyObject");

    private ClassUtils() {
    }

    /**
     * 判断是否为代理对象
     *
     * @param clazz 传入 class 对象
     * @return 如果对象class是代理 class，返回 true
     */
    public static boolean isProxy(Class<?> clazz) {
        if (clazz != null) {
            for (Class<?> cls : clazz.getInterfaces()) {
                if (PROXY_CLASS_NAMES.contains(cls.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取当前对象的 class
     *
     * @param clazz 传入
     * @return 如果是代理的class，返回父 class，否则返回自身
     */
    public static Class<?> getUserClass(Class<?> clazz) {
        return isProxy(clazz) ? clazz.getSuperclass() : clazz;
    }

    /**
     * 获取当前对象的class
     *
     * @param object 对象
     * @return 返回对象的 user class
     */
    public static Class<?> getUserClass(Object object) {
        Assert.notNull(object, "Error: Instance must not be null");
        return getUserClass(object.getClass());
    }

    /**
     * 根据指定的 class ， 实例化一个对象，根据构造参数来实例化
     * <p>
     * 在 java9 及其之后的版本 Class.newInstance() 方法已被废弃
     *
     * @param clazz 需要实例化的对象
     * @param <T>   类型，由输入类型决定
     * @return 返回新的实例
     */
    public static <T> T newInstance(Class<T> clazz) {
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw ExceptionUtils.mpe("实例化对象时出现错误,请尝试给 " + clazz.getName() + "添加无参的构造方法", e);
        }
    }

}
