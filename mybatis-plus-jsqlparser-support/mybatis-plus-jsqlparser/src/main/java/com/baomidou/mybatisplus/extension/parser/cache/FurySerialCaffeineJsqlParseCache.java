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
package com.baomidou.mybatisplus.extension.parser.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.function.Consumer;

/**
 * jsqlparser 缓存 fury 序列化 Caffeine 缓存实现.
 *
 * @author laokou
 * @deprecated Apache Fury 已更名为 Apache Fory，将在未来版本中移除，请改用 {@link ForySerialCaffeineJsqlParseCache}。
 */
@Deprecated
public class FurySerialCaffeineJsqlParseCache extends ForySerialCaffeineJsqlParseCache {

    public FurySerialCaffeineJsqlParseCache(Cache<String, byte[]> cache) {
        super(cache);
    }

    public FurySerialCaffeineJsqlParseCache(Consumer<Caffeine<Object, Object>> consumer) {
        super(consumer);
    }

}
