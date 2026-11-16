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

import com.baomidou.mybatisplus.extension.parser.JsqlParserCacheTypes;

import org.apache.fury.Fury;
import org.apache.fury.ThreadSafeFury;

/**
 * Fury Factory.
 *
 * @author laokou
 * @deprecated Apache Fury 已更名为 Apache Fory，将在未来版本中移除，请迁移至 {@link ForyFactory}。 注意：Fory 与 Fury 的序列化格式不兼容，Fory 无法读取 Fury
 *             序列化的数据，若序列化结果存放在外部存储中，迁移前需清空旧数据。
 */
@Deprecated
public final class FuryFactory {

    private static final FuryFactory FACTORY = new FuryFactory();

    private final ThreadSafeFury FURY = Fury.builder()
            // 开启异步编译
            .withAsyncCompilation(true).buildThreadSafeFury();

    public FuryFactory() {
        JsqlParserCacheTypes.registerFuryAndFory(FURY::register);
    }

    public static FuryFactory getFuryFactory() {
        return FACTORY;
    }

    public byte[] serialize(Object object) {
        if (object == null) {
            return new byte[0];
        }
        return FURY.serialize(object);
    }

    public Object deserialize(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        return FURY.deserialize(bytes);
    }

}
