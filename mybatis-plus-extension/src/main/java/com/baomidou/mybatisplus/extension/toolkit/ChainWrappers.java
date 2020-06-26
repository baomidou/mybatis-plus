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
package com.baomidou.mybatisplus.extension.toolkit;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;

/**
 * 快捷构造 chain 式调用的工具类
 *
 * @author miemie
 * @since 2019-11-28
 * @since 3.3.0
 */
public final class ChainWrappers {

    private ChainWrappers() {
        // ignore
    }

    /**
     * 链式查询 普通
     *
     * @return QueryWrapper 的包装类
     */
    public static <T> QueryChainWrapper<T> queryChain(BaseMapper<T> mapper) {
        return new QueryChainWrapper<>(mapper);
    }

    /**
     * 链式查询 lambda 式
     * <p>注意：不支持 Kotlin </p>
     *
     * @return LambdaQueryWrapper 的包装类
     */
    public static <T> LambdaQueryChainWrapper<T> lambdaQueryChain(BaseMapper<T> mapper) {
        return new LambdaQueryChainWrapper<>(mapper);
    }

    /**
     * 链式更改 普通
     *
     * @return UpdateWrapper 的包装类
     */
    public static <T> UpdateChainWrapper<T> updateChain(BaseMapper<T> mapper) {
        return new UpdateChainWrapper<>(mapper);
    }

    /**
     * 链式更改 lambda 式
     * <p>注意：不支持 Kotlin </p>
     *
     * @return LambdaUpdateWrapper 的包装类
     */
    public static <T> LambdaUpdateChainWrapper<T> lambdaUpdateChain(BaseMapper<T> mapper) {
        return new LambdaUpdateChainWrapper<>(mapper);
    }
}
