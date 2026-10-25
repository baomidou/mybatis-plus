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

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.SqlCommandType;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据源分片辅助类
 *
 * @author hubin
 * @since 3.5.18
 */
@Slf4j
public final class ShardingHelper {
    private static final Map<String, MappedSharding> MAPPINGS = new ConcurrentHashMap<>();
    private static final Map<Class<? extends IShardingStrategy>, IShardingStrategy> STRATEGIES = new ConcurrentHashMap<>();
    private static volatile MappedSharding primary;

    private ShardingHelper() {
    }

    public static void initDbSharding(ShardingProperties properties) {
        ShardingContext.initialize(properties);
        IShardingStrategy strategy = properties.getShardingStrategy() == null ? new RandomShardingStrategy() : properties.getShardingStrategy();
        primary = new MappedSharding(properties.getPrimary(), strategy);
        STRATEGIES.put(strategy.getClass(), strategy);
        MAPPINGS.clear();
    }

    public static void clearDatabaseKey() {
        ShardingContext.clear();
    }

    public static String getDatabaseKey() {
        return ShardingContext.get();
    }

    public static String getDatabaseKeyOrElsePrimaryKey() {
        return ShardingContext.currentOrPrimary();
    }

    public static String getPrimaryKey() {
        return ShardingContext.primaryKey();
    }

    public static Map<String, ShardingGroupProperty> getDbGroupPropertyMap() {
        return ShardingContext.groups();
    }

    public static void changeDatasource(String statementId, Object parameter, SqlCommandType commandType) {
        mapping(statementId).changeDatasource(statementId, parameter, commandType);
    }

    private static MappedSharding mapping(String statementId) {
        if (primary == null) {
            throw new IllegalStateException("ShardingHelper has not been initialized");
        }
        MappedSharding mapped = MAPPINGS.get(statementId);
        if (mapped != null) {
            return mapped;
        }
        mapped = resolve(statementId);
        MAPPINGS.put(statementId, mapped);
        return mapped;
    }

    /**
     * 解析语句对应 Mapper 上的 {@link Sharding} 注解；方法级注解优先于类级注解，
     * 重载方法任一携带注解即生效；类级注解沿接口/父类层级查找。
     * 无法解析（如 XML 独立命名空间、反射失败）时回退主库组。
     */
    private static MappedSharding resolve(String statementId) {
        if (statementId == null) {
            return primary;
        }
        int separator = statementId.lastIndexOf('.');
        if (separator <= 0) {
            return primary;
        }
        String methodName = statementId.substring(separator + 1);
        try {
            Class<?> mapperClass = Resources.classForName(statementId.substring(0, separator));
            Sharding annotation = findAnnotation(mapperClass, methodName);
            if (annotation == null) {
                return primary;
            }
            String group = annotation.value().trim().isEmpty() ? ShardingContext.primaryGroup() : annotation.value().trim();
            return new MappedSharding(group, strategy(annotation.strategy()));
        } catch (ClassNotFoundException e) {
            log.debug("Statement [{}] namespace is not a mapper class, falling back to primary group", statementId);
            return primary;
        } catch (RuntimeException e) {
            log.warn("Failed to resolve sharding mapping for statement [{}], falling back to primary group", statementId, e);
            return primary;
        }
    }

    private static Sharding findAnnotation(Class<?> mapperClass, String methodName) {
        for (Method method : mapperClass.getMethods()) {
            if (!method.getName().equals(methodName)) {
                continue;
            }
            Sharding annotation = method.getAnnotation(Sharding.class);
            if (annotation != null) {
                return annotation;
            }
        }
        return findClassAnnotation(mapperClass);
    }

    private static Sharding findClassAnnotation(Class<?> type) {
        Sharding annotation = type.getAnnotation(Sharding.class);
        if (annotation != null) {
            return annotation;
        }
        for (Class<?> iface : type.getInterfaces()) {
            annotation = findClassAnnotation(iface);
            if (annotation != null) {
                return annotation;
            }
        }
        Class<?> superClass = type.getSuperclass();
        return superClass == null ? null : findClassAnnotation(superClass);
    }

    private static IShardingStrategy strategy(Class<? extends IShardingStrategy> type) {
        return STRATEGIES.computeIfAbsent(type, key -> {
            try {
                return key.getDeclaredConstructor().newInstance();
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException("Cannot create sharding strategy " + key.getName(), e);
            }
        });
    }
}
