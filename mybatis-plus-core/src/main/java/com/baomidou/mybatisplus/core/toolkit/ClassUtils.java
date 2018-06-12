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

import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;

/**
 * ClassUtils
 *
 * @author Caratacus
 * @since 2017/07/08
 */
public class ClassUtils {

    /**
     * 判断是否为代理对象
     *
     * @param clazz
     * @return
     */
    public static boolean isProxy(Class<?> clazz) {
        if (clazz != null) {
            for (Class<?> cls : clazz.getInterfaces()) {
                String interfaceName = cls.getName();
                if ("net.sf.cglib.proxy.Factory".equals(interfaceName) //cglib
                    || "org.springframework.cglib.proxy.Factory".equals(interfaceName)
                    || "javassist.util.proxy.ProxyObject".equals(interfaceName) //javassist
                    || "org.apache.ibatis.javassist.util.proxy.ProxyObject".equals(interfaceName)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取当前对象的class
     *
     * @param clazz
     * @return
     */
    public static Class<?> getUserClass(Class<?> clazz) {
        return isProxy(clazz) ? clazz.getSuperclass() : clazz;
    }

    /**
     * 获取当前对象的class
     *
     * @param object
     * @return
     */
    public static Class<?> getUserClass(Object object) {
        if (object == null) {
            throw new MybatisPlusException("Error: Instance must not be null");
        }
        return getUserClass(object.getClass());
    }

}
