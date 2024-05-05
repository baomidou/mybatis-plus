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
package com.baomidou.mybatisplus.generator;

import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Map;

/**
 * 渲染模板接口
 *
 * @author nieqiurong 2020/11/9.
 * @since 3.5.0
 */
public interface ITemplate extends Serializable {

    @NotNull
    Map<String, Object> renderData(@NotNull TableInfo tableInfo);

}
