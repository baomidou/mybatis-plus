/*
 * Copyright (c) 2011-2020, hubin (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.core.conditions;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

/**
 * <p>
 * Wrapper 条件辅助类
 * </p>
 *
 * @author hubin
 * @since 2018-09-01
 */
public class Condition {

    /**
     * <p>
     * 获取 QueryWrapper 实例
     * </p>
     * <p>
     * 示例：Condition.<User>create().eq("id", 1)
     * </p>
     */
    public static <T> QueryWrapper<T> create() {
        return new QueryWrapper<>();
    }

    public static <T> QueryWrapper<T> create(T entity) {
        return new QueryWrapper<>(entity);
    }

    /**
     * <p>
     * 获取 LambdaQueryWrapper 实例
     * </p>
     * <p>
     * 示例：Condition.<User>lambda().eq(User::getId, 1)
     * </p>
     */
    public static <T> LambdaQueryWrapper<T> lambda() {
        return new LambdaQueryWrapper<>();
    }

    public static <T> LambdaQueryWrapper<T> lambda(T entity) {
        return new LambdaQueryWrapper<>(entity);
    }
}
