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

import org.apache.fory.Fory;
import org.apache.fory.ThreadSafeFory;

/**
 * Fory Factory.
 *
 * @author laokou
 */
public final class ForyFactory {

    private static final ForyFactory FACTORY = new ForyFactory();

    private final ThreadSafeFory FORY = Fory.builder()
            // Fory 1.x 默认跨语言模式,要求引用字段非空;JSqlParser AST 大量字段可为 null,需使用纯 Java 模式
            .withXlang(false)
            // 开启异步编译
            .withAsyncCompilation(true).buildThreadSafeFory();

    public ForyFactory() {
        JsqlParserCacheTypes.registerFuryAndFory(FORY::register);
    }

    public static ForyFactory getForyFactory() {
        return FACTORY;
    }

    public byte[] serialize(Object object) {
        if (object == null) {
            return new byte[0];
        }
        return FORY.serialize(object);
    }

    public Object deserialize(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        return FORY.deserialize(bytes);
    }

}
