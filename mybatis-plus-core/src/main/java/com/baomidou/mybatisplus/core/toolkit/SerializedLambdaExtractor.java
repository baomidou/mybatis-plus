/*
 * Copyright (c) 2011-2022, baomidou (jobob@qq.com).
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
package com.baomidou.mybatisplus.core.toolkit;

import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.invoke.SerializedLambda;

/**
 * 继承 {@link java.io.ObjectInputStream} 并开启对象替换功能，
 * 进而从 {@link #replaceObject(Object)} 中直接获得 {@link SerializedLambda} 对象。
 */
public class SerializedLambdaExtractor extends ObjectOutputStream {
    private SerializedLambda serializedLambda;
    private static final OutputStream NULL_OUT = new OutputStream() {
        @Override
        public void write(int b) {
        }

        @Override
        public void write(@NotNull byte[] b, int off, int len) {
        }
    };

    public SerializedLambdaExtractor() throws IOException {
        super(NULL_OUT);
        enableReplaceObject(true);
    }

    public static SerializedLambda extract(Serializable lambda) {
        try (SerializedLambdaExtractor extractor = new SerializedLambdaExtractor()) {
            extractor.writeObject(lambda);
            return extractor.serializedLambda;
        } catch (IOException e) {
            throw new MybatisPlusException(e);
        }
    }

    /**
     * 在此方法中直接返回 null 尽快结束后面的序列化行为。
     */
    @Override
    protected Object replaceObject(Object obj) {
        serializedLambda = (SerializedLambda) obj;
        return null;
    }

}

