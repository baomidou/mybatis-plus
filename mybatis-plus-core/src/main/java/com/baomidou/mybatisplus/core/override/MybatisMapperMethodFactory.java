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
package com.baomidou.mybatisplus.core.override;

import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Method;

/**
 * MybatisMapperMethod 工厂接口。
 * <p>
 * 允许用户自定义 {@link MybatisMapperMethod} 的创建逻辑，
 * 实现自定义的 Mapper 方法执行行为（如审计日志、结果包装、参数预处理等）。
 * </p>
 *
 * @author xzxiaoshan
 * @since 3.5.18
 */
public interface MybatisMapperMethodFactory {

    /**
     * 创建 MybatisMapperMethod 实例。
     *
     * @param mapperInterface Mapper 接口类
     * @param method          Mapper 方法
     * @param config          MyBatis Configuration
     * @return MybatisMapperMethod 实例
     */
    MybatisMapperMethod create(Class<?> mapperInterface, Method method, Configuration config);
}
