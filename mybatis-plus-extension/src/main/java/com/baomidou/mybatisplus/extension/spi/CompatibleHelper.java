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
package com.baomidou.mybatisplus.extension.spi;

import com.baomidou.mybatisplus.core.toolkit.Assert;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

import java.util.ServiceLoader;

/**
 * 兼容处理辅助类
 * <p>
 * 默认加载使用SPI实现,需要手动指定请使用{@link #setCompatibleSet(CompatibleSet)}
 * </p>
 *
 * @see com.baomidou.mybatisplus.core.spi.CompatibleHelper
 * @deprecated 3.5.12
 */
@Deprecated
public class CompatibleHelper {

    private static final Log LOG = LogFactory.getLog(CompatibleHelper.class);

    private static CompatibleSet COMPATIBLE_SET = null;

    static {
        ServiceLoader<CompatibleSet> loader = ServiceLoader
                .load(CompatibleSet.class, CompatibleSet.class.getClassLoader());
        int size = 0;
        for (CompatibleSet compatibleSet : loader) {
            size++;
            LOG.debug("Load compatibleSet: " + compatibleSet);
            COMPATIBLE_SET = compatibleSet;
        }
        if (size > 1) {
            LOG.warn("There are currently multiple implementations, and the last one is used " + COMPATIBLE_SET);
        }
    }

    /**
     * 判断是否存在 {@link com.baomidou.mybatisplus.extension.spi.CompatibleSet} 实例
     *
     * @return 是否存在 (存在返回true,为空返回false)
     * @since 3.5.12
     */
    public static boolean hasCompatibleSet() {
        return COMPATIBLE_SET != null;
    }

    /**
     * 手动指定 {@link com.baomidou.mybatisplus.extension.spi.CompatibleSet} 实例
     *
     * @param compatibleSet {@link com.baomidou.mybatisplus.extension.spi.CompatibleSet} 实例
     * @since 3.5.12
     */
    public static void setCompatibleSet(CompatibleSet compatibleSet) {
        COMPATIBLE_SET = compatibleSet;
    }

    /**
     * 获取{@link com.baomidou.mybatisplus.extension.spi.CompatibleSet}实例
     * <p>
     * 当为空时会抛出异常,需要检查是否为空请使用{@link #hasCompatibleSet()}
     * </p>
     *
     * @return {@link com.baomidou.mybatisplus.extension.spi.CompatibleSet}
     * @see #setCompatibleSet(CompatibleSet)
     */
    public static CompatibleSet getCompatibleSet() {
        Assert.isTrue(
                hasCompatibleSet(),
                "Please add specific implementation dependencies or use the setCompatibleSet method to specify");
        return COMPATIBLE_SET;
    }

}
