/*
 * Copyright (c) 2011-2014, hubin (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baomidou.mybatisplus.annotation;

import lombok.Getter;

/**
 * <p>
 * 字段策略枚举类
 * </p>
 *
 * @author hubin
 * @since 2016-09-09
 */
@Getter
public enum FieldStrategy {
    /**
     * 忽略判断
     */
    IGNORED(0, "忽略判断"),
    /**
     * 非NULL判断
     */
    NOT_NULL(1, "非 NULL 判断"),
    /**
     * 非空判断
     */
    NOT_EMPTY(2, "非空判断");

    /**
     * 主键
     */
    private final int key;

    /**
     * 描述
     */
    private final String desc;

    FieldStrategy(final int key, final String desc) {
        this.key = key;
        this.desc = desc;
    }

}
