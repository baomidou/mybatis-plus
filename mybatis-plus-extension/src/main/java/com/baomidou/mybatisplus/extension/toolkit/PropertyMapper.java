/*
 * Copyright (c) 2011-2020, baomidou (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.extension.toolkit;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.RequiredArgsConstructor;

import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author miemie
 * @since 2020-06-15
 */
@RequiredArgsConstructor
public class PropertyMapper {
    private final Properties delegate;

    public static PropertyMapper getInstance(Properties properties) {
        return new PropertyMapper(properties);
    }

    public PropertyMapper whenNotBlack(String key, Consumer<String> consumer) {
        String value = delegate.getProperty(key);
        if (StringUtils.isNotBlank(value)) {
            consumer.accept(value);
        }
        return this;
    }

    public <T> PropertyMapper whenNotBlack(String key, Function<String, T> function, Consumer<T> consumer) {
        String value = delegate.getProperty(key);
        if (StringUtils.isNotBlank(value)) {
            consumer.accept(function.apply(value));
        }
        return this;
    }
}
