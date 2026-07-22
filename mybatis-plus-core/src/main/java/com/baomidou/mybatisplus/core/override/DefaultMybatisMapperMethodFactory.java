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
 * 默认的 MybatisMapperMethod 工厂实现。
 * <p>
 * 保持与原有行为完全一致，确保向后兼容。
 * 当用户未自定义 {@link MybatisMapperMethodFactory} 时使用此默认实现。
 * </p>
 *
 * @author xzxiaoshan
 * @since 3.5.18
 */
public class DefaultMybatisMapperMethodFactory implements MybatisMapperMethodFactory {

    @Override
    public MybatisMapperMethod create(Class<?> mapperInterface, Method method, Configuration config) {
        return new MybatisMapperMethod(mapperInterface, method, config);
    }
}
