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

/**
 * Lambda 信息
 * <p>
 * Created by hcl at 2021/5/14
 */
public interface LambdaMeta {

    /**
     * 获取 lambda 表达式实现方法的名称
     *
     * @return lambda 表达式对应的实现方法名称
     */
    String getImplMethodName();

    /**
     * 实例化该方法的类
     *
     * @return 返回对应的类名称
     */
    Class<?> getInstantiatedClass();

}
