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
package com.baomidou.mybatisplus.extension.plugins.datasource;

import org.apache.ibatis.mapping.SqlCommandType;

import java.util.function.Function;

/**
 * 数据源分片设置类
 *
 * @author hubin
 * @since 3.5.18
 */
public final class ShardingKey {

    private ShardingKey() {
    }

    /**
     * 获取数据库关键字，规则 group#key
     *
     * @param group 数据库分组
     * @param key 数据库关键字
     * @return group#key
     */
    public static String getDatabaseKey(String group, String key) {
        return ShardingContext.databaseKey(group, key);
    }

    /**
     * 设置数据源 KEY
     *
     * @param key 数据源 KEY
     */
    public static void change(String key) {
        ShardingContext.set(key);
    }

    /**
     * 切换数据源 KEY
     *
     * @param group 数据源组
     * @param type {@link SqlCommandType}
     * @param selector 动态数据库组信息函数
     */
    public static void change(String group, SqlCommandType type, Function<ShardingGroupProperty, String> selector) {
        String key = selector.apply(ShardingContext.group(group));
        if (key == null)
            throw new IllegalStateException("No healthy datasource for group " + group);
        String databaseKey = getDatabaseKey(group, key);
        if (type == SqlCommandType.INSERT || type == SqlCommandType.UPDATE || type == SqlCommandType.DELETE) {
            databaseKey += ShardingContext.WRITE_FLAG;
        }
        change(databaseKey);
    }

    /**
     * 获取数据源 KEY 不存在返回默认
     */
    public static String getDatabaseKeyOrElsePrimaryKey() {
        return ShardingContext.currentOrPrimary();
    }
}
