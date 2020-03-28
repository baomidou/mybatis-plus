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
package com.baomidou.mybatisplus.core.toolkit.sql;

import com.baomidou.mybatisplus.core.enums.SqlLike;
import com.baomidou.mybatisplus.core.enums.SqlWildcard;
import com.baomidou.mybatisplus.core.toolkit.StringPool;

/**
 * SqlUtils工具类
 *
 * @author Caratacus
 * @since 2016-11-13
 */
public class SqlUtils {

    /**
     * 用%连接like
     *
     * @param str 原字符串
     * @return like 的值
     */
    public static String concatLike(Object str, SqlLike type) {
        switch (type) {
            case LEFT:
                return StringPool.PERCENT + str;
            case RIGHT:
                return str + StringPool.PERCENT;
            default:
                return StringPool.PERCENT + str + StringPool.PERCENT;
        }
    }

    /**
     * 防止SQL注入
     *
     * @param str 原参数
     * @return like 的值
     */
    public static Object concatLike(Object str, SqlLike type, SqlWildcard wildcard) {
        if (wildcard == null) {
            wildcard = SqlWildcard.PERCENT;
        }
        switch (type) {
            case LEFT:
                return wildcard.getWildcard() + str;
            case RIGHT:
                return str + wildcard.getWildcard();
            default:
                return wildcard.getWildcard() + str + wildcard.getWildcard();
        }
    }
}
