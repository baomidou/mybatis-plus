/*
 * Copyright (c) 2011-2021, baomidou (jobob@qq.com).
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
package com.baomidou.mybatisplus.extension.toolkit;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author miemie
 * @since 2020-06-15
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PropertyMapper {
    private final Properties delegate;

    public static PropertyMapper newInstance(Properties properties) {
        return new PropertyMapper(properties);
    }

    public Set<String> keys() {
        return delegate.stringPropertyNames();
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

    /**
     * mp 内部规则分组
     *
     * @return 分组
     */
    public Map<String, Properties> group(String group) {
        final Set<String> keys = keys();
        Set<String> inner = keys.stream().filter(i -> i.startsWith(group)).collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(inner)) {
            return Collections.emptyMap();
        }
        Map<String, Properties> map = CollectionUtils.newHashMap();
        inner.forEach(i -> {
            Properties p = new Properties();
            String key = i.substring(group.length()) + StringPool.COLON;
            int keyIndex = key.length();
            keys.stream().filter(j -> j.startsWith(key)).forEach(j -> p.setProperty(j.substring(keyIndex), delegate.getProperty(j)));
            map.put(delegate.getProperty(i), p);
        });
        return map;
    }
}
