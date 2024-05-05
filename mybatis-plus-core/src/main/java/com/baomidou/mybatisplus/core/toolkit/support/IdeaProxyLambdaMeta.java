/*
 * Copyright (c) 2011-2024, baomidou (jobob@qq.com).
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


import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandleProxies;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Executable;
import java.lang.reflect.Proxy;

/**
 * 在 IDEA 的 Evaluate 中执行的 Lambda 表达式元数据需要使用该类处理元数据
 * <p>
 * Create by hcl at 2021/5/17
 */
public class IdeaProxyLambdaMeta implements LambdaMeta {
    private final Class<?> clazz;
    private final String name;

    public IdeaProxyLambdaMeta(Proxy func) {
        MethodHandle dmh = MethodHandleProxies.wrapperInstanceTarget(func);
        Executable executable = MethodHandles.reflectAs(Executable.class, dmh);
        clazz = executable.getDeclaringClass();
        name = executable.getName();
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
