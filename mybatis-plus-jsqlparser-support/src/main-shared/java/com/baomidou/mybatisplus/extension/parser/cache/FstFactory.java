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

import org.nustaq.serialization.FSTConfiguration;

/**
 * Fst Factory
 *
 * @author miemie
 * @since 2023-08-06
 */
public class FstFactory {

    private static final FstFactory FACTORY = new FstFactory();
    private final FSTConfiguration conf = FSTConfiguration.createDefaultConfiguration();

    public static FstFactory getDefaultFactory() {
        return FACTORY;
    }

    public FstFactory() {
        JsqlParserCacheTypes.registerFst(conf::registerClass);
    }

    public FSTConfiguration getConfig() {
        return conf;
    }

    public byte[] asByteArray(Object obj) {
        return conf.asByteArray(obj);
    }

    public Object asObject(byte[] bytes) {
        return conf.asObject(bytes);
    }
}
