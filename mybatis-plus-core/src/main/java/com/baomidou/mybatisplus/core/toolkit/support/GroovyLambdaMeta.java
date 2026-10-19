/*
 * Copyright (c) 2011-2025, baomidou (jobob@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baomidou.mybatisplus.core.toolkit.support;

import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 在 Groovy 脚本中通过 {@code ::} 创建的方法引用（MethodClosure）的元信息解析。
 * <p>
 * Groovy 将 MethodClosure 包装为 {@link java.lang.reflect.Proxy}，其 {@link InvocationHandler} <br>
 * 提供 {@code getDelegate()} 方法可返回底层的 MethodClosure 对象，再从中通过 <br>
 * {@code getMethod()}（方法名）和 {@code getOwner()}（所属类/实例）解析出 Lambda 元信息。
 *
 * @author miemie
 * @since 3.5.12
 */
public class GroovyLambdaMeta implements LambdaMeta {

    private final Class<?> clazz;
    private final String name;

    public GroovyLambdaMeta(Proxy func) {
        InvocationHandler handler = Proxy.getInvocationHandler(func);
        try {
            Method getDelegate = handler.getClass().getMethod("getDelegate");
            Object delegate = getDelegate.invoke(handler);
            Method getMethod = delegate.getClass().getMethod("getMethod");
            this.name = (String) getMethod.invoke(delegate);
            Method getOwner = delegate.getClass().getMethod("getOwner");
            Object owner = getOwner.invoke(delegate);
            this.clazz = (owner instanceof Class) ? (Class<?>) owner : owner.getClass();
        } catch (NoSuchMethodException e) {
            throw new MybatisPlusException(
                    "Unsupported proxy type for Groovy lambda extraction: " + handler.getClass().getName()
                            + ". Expected a Groovy ConvertedClosure handler exposing getDelegate().",
                    e);
        } catch (Exception e) {
            throw new MybatisPlusException(
                    "Failed to extract Groovy lambda meta from proxy handler: " + handler.getClass().getName(), e);
        }
    }

    @Override
    public String getImplMethodName() {
        return name;
    }

    @Override
    public Class<?> getInstantiatedClass() {
        return clazz;
    }

    @Override
    public String toString() {
        return clazz.getSimpleName() + "::" + name;
    }

}
