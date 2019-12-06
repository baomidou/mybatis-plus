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
package com.baomidou.mybatisplus.core.handlers;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.function.Supplier;

/**
 * 严格填充模式 model
 *
 * @author miemie
 * @since 2019-11-26
 */
@Data
@AllArgsConstructor
public class StrictFill {
    /**
     * 字段名
     */
    private String fieldName;
    /**
     * 字段类型
     */
    private Class<?> fieldType;
    /**
     * 获取字段值的函数
     */
    private Supplier<Object> fieldVal;

    public static StrictFill of(String fieldName, Class<?> fieldType, Object fieldVal) {
        return new StrictFill(fieldName, fieldType, () -> fieldVal);
    }

    public static StrictFill of(String fieldName, Class<?> fieldType, Supplier<Object> fieldVal) {
        return new StrictFill(fieldName, fieldType, fieldVal);
    }
}
