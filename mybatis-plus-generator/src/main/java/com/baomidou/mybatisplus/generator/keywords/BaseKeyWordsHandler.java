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
package com.baomidou.mybatisplus.generator.keywords;

import com.baomidou.mybatisplus.generator.config.IKeyWordsHandler;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * 基类关键字处理
 *
 * @author nieqiurong 2020/5/8.
 * @since 3.3.2
 */
public abstract class BaseKeyWordsHandler implements IKeyWordsHandler {

    public final Set<String> keyWords;

    public BaseKeyWordsHandler(@NotNull List<String> keyWords) {
        this.keyWords = new HashSet<>(keyWords);
    }

    public BaseKeyWordsHandler(@NotNull Set<String> keyWords) {
        this.keyWords = keyWords;
    }

    @Override
    public @NotNull Collection<String> getKeyWords() {
        return keyWords;
    }

    @Override
    public boolean isKeyWords(@NotNull String columnName) {
        return getKeyWords().contains(columnName.toUpperCase(Locale.ENGLISH));
    }

}
