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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 分库上下文
 * <p>
 * 上下文基于 {@link ThreadLocal} 保存当前数据源 KEY，拦截器在每条语句执行前都会重设； 但框架无法在语句结束后自动清理，线程池复用时上一请求的 KEY 可能残留。 典型受影响场景：Spring
 * 声明式事务在首条语句执行前即获取连接，此时会读到残留 KEY， 建议在请求入口/出口处调用 {@link #clear()} 兜底。
 *
 * @author hubin
 * @since 3.5.18
 */
public final class ShardingContext {

    /**
     * 写路由标记，追加在数据源 KEY 末尾（group#key#IUD），用于写后读绑定及事务提交识别
     */
    public static final String WRITE_FLAG = "#IUD";
    private static final ThreadLocal<String> CURRENT = new ThreadLocal<>();
    private static final Map<String, ShardingGroupProperty> GROUPS = new ConcurrentHashMap<>();
    private static volatile String primary;

    private ShardingContext() {
    }

    public static void initialize(ShardingProperties properties) {
        GROUPS.clear();
        primary = properties.getPrimary();
        for (Map.Entry<String, List<DataSourceProperty>> entry : properties.getDatasource().entrySet()) {
            ShardingGroupProperty group = new ShardingGroupProperty();
            List<String> masters = new ArrayList<>();
            List<String> slaves = new ArrayList<>();
            for (DataSourceProperty property : entry.getValue()) {
                if (property.getKey() == null || property.getKey().trim().isEmpty()) {
                    throw new IllegalArgumentException("datasource key cannot be empty");
                }
                if (property.getCluster() == DbCluster.slave) {
                    slaves.add(property.getKey());
                } else {
                    masters.add(property.getKey());
                }
            }
            if (masters.isEmpty()) {
                throw new IllegalArgumentException("master datasource is required for group " + entry.getKey());
            }
            group.setMasterKeys(masters);
            if (!slaves.isEmpty()) {
                group.setSlaveKeys(slaves);
            }
            GROUPS.put(entry.getKey(), group);
        }
        CURRENT.remove();
    }

    public static String databaseKey(String group, String key) {
        return group + "#" + key;
    }

    /**
     * 判断数据源 KEY 是否携带写路由标记
     */
    public static boolean isWriteKey(String key) {
        return key != null && key.endsWith(WRITE_FLAG);
    }

    /**
     * 去除数据源 KEY 末尾的写路由标记
     */
    public static String stripWriteFlag(String key) {
        return isWriteKey(key) ? key.substring(0, key.length() - WRITE_FLAG.length()) : key;
    }

    public static String get() {
        return CURRENT.get();
    }

    public static void set(String key) {
        if (key == null) {
            CURRENT.remove();
        } else {
            CURRENT.set(key);
        }
    }

    public static void clear() {
        CURRENT.remove();
    }

    public static String primaryGroup() {
        return primary;
    }

    public static String primaryKey() {
        ShardingGroupProperty group = GROUPS.get(primary);
        if (group == null || group.getMasterKeys() == null || group.getMasterKeys().isEmpty()) {
            return null;
        }
        return databaseKey(primary, group.getMasterKeys().get(0));
    }

    public static String currentOrPrimary() {
        String key = CURRENT.get();
        return key == null ? primaryKey() : key;
    }

    public static ShardingGroupProperty group(String name) {
        return GROUPS.get(name);
    }

    public static Map<String, ShardingGroupProperty> groups() {
        return Collections.unmodifiableMap(GROUPS);
    }
}
