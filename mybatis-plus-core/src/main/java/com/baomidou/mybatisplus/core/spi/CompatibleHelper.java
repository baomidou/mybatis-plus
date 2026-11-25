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
package com.baomidou.mybatisplus.core.spi;

import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.ExceptionUtils;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

import java.util.ServiceLoader;

/**
 * 兼容处理辅助类
 * <p>
 * SPI 实现按 order 选择，最高优先级并列时抛出异常。没有 SPI 实现时使用 {@link DefaultCompatibleSet}。 手动指定实现请使用
 * {@link #setCompatibleSet(CompatibleSet)}。
 * </p>
 */
public class CompatibleHelper {

    private static final Log LOG = LogFactory.getLog(CompatibleHelper.class);

    private static CompatibleSet COMPATIBLE_SET;

    static {
        ServiceLoader<CompatibleSet> loader = ServiceLoader
                .load(CompatibleSet.class, CompatibleSet.class.getClassLoader());
        COMPATIBLE_SET = selectCompatibleSet(loader);
        LOG.debug("Load compatibleSet: " + COMPATIBLE_SET);
    }

    static CompatibleSet selectCompatibleSet(Iterable<CompatibleSet> candidates) {
        CompatibleSet selected = null;
        CompatibleSet tied = null;
        for (CompatibleSet candidate : candidates) {
            int comparison = selected == null ? 1 : Integer.compare(candidate.order(), selected.order());
            if (comparison > 0) {
                selected = candidate;
                tied = null;
            } else if (comparison == 0) {
                tied = candidate;
            }
        }
        if (tied != null) {
            throw ExceptionUtils.mpe(
                    "Multiple CompatibleSet implementations share the highest priority: %s and %s",
                    selected.getClass().getName(), tied.getClass().getName());
        }
        return selected == null ? new DefaultCompatibleSet() : selected;
    }

    /**
     * 判断是否存在 {@link com.baomidou.mybatisplus.core.spi.CompatibleSet} 实例
     *
     * @return 是否存在 (存在返回true,为空返回false)
     * @since 3.5.12
     * @deprecated 3.5.19 已内置默认实现，直接使用 {@link #getCompatibleSet()}。
     */
    @Deprecated
    public static boolean hasCompatibleSet() {
        return COMPATIBLE_SET != null;
    }

    /**
     * 手动指定 {@link com.baomidou.mybatisplus.core.spi.CompatibleSet} 实例
     *
     * @param compatibleSet {@link com.baomidou.mybatisplus.core.spi.CompatibleSet} 实例，不可为 null
     * @since 3.5.12
     */
    public static void setCompatibleSet(CompatibleSet compatibleSet) {
        Assert.notNull(compatibleSet, "CompatibleSet must not be null");
        COMPATIBLE_SET = compatibleSet;
    }

    /**
     * 获取{@link com.baomidou.mybatisplus.core.spi.CompatibleSet}实例
     * <p>
     * 返回选中的 SPI 实现、默认实现或手动指定的实现。
     * </p>
     *
     * @return {@link com.baomidou.mybatisplus.core.spi.CompatibleSet}
     * @see #setCompatibleSet(CompatibleSet)
     */
    public static CompatibleSet getCompatibleSet() {
        Assert.notNull(
                COMPATIBLE_SET,
                "Please add specific implementation dependencies or use the setCompatibleSet method to specify");
        return COMPATIBLE_SET;
    }

}
