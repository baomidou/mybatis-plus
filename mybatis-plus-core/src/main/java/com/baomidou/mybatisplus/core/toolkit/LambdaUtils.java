/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baomidou.mybatisplus.core.toolkit;

import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.support.ColumnCache;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Locale.ENGLISH;

/**
 * <p>
 * Lambda 解析工具类
 * </p>
 *
 * @author HCL
 * @since 2018-05-10
 */
public final class LambdaUtils {

    private static final Map<String, Map<String, ColumnCache>> LAMBDA_CACHE = new ConcurrentHashMap<>();

    /**
     * SerializedLambda 反序列化缓存
     */
    private static final Map<Class, WeakReference<SerializedLambda>> FUNC_CACHE = new ConcurrentHashMap<>();

    /**
     * <p>
     * 解析 lambda 表达式
     * </p>
     *
     * @param func 需要解析的 lambda 对象
     * @param <T>  类型，被调用的 Function 对象的目标类型
     * @return 返回解析后的结果
     */
    public static <T> SerializedLambda resolve(SFunction<T, ?> func) {
        Class clazz = func.getClass();
        return Optional.ofNullable(FUNC_CACHE.get(clazz))
            .map(WeakReference::get)
            .orElseGet(() -> {
                SerializedLambda lambda = SerializedLambda.resolve(func);
                FUNC_CACHE.put(clazz, new WeakReference<>(lambda));
                return lambda;
            });
    }

    /**
     * <p>
     * 缓存实体类名与表字段映射关系
     * </p>
     *
     * @param clazz     实体
     * @param tableInfo 表信息
     */
    public static void createCache(Class clazz, TableInfo tableInfo) {
        LAMBDA_CACHE.put(clazz.getName(), createLambdaMap(tableInfo, clazz));
    }

    /**
     * <p>
     * 缓存实体字段 MAP 信息
     * </p>
     *
     * @param tableInfo 表信息
     * @return 缓存 map
     */
    private static Map<String, ColumnCache> createLambdaMap(TableInfo tableInfo, Class clazz) {
        Map<String, ColumnCache> map = new HashMap<>();
        String keyProperty = tableInfo.getKeyProperty();
        if (StringUtils.isNotEmpty(keyProperty)) {
            saveCacheAndPut(tableInfo.getKeyColumn(), tableInfo.getKeySqlSelect(), keyProperty.toUpperCase(ENGLISH),
                clazz, tableInfo.getClazz(), map);
        }
        tableInfo.getFieldList().forEach(i -> saveCacheAndPut(i.getColumn(), i.getSqlSelect(tableInfo.getDbType()),
            i.getProperty().toUpperCase(ENGLISH), clazz, i.getClazz(), map));
        return map;
    }

    private static void saveCacheAndPut(String column, String select, String property, Class<?> entityClass,
                                        Class<?> propertyAffiliationClass, Map<String, ColumnCache> map) {
        ColumnCache cache = new ColumnCache(column, select);
        if (propertyAffiliationClass != entityClass) {
            saveCache(propertyAffiliationClass.getName(), property, cache);
        }
        map.put(property, cache);
    }

    /**
     * 保存缓存信息
     *
     * @param className   类名
     * @param property    属性
     * @param columnCache 字段信息
     */
    private static void saveCache(String className, String property, ColumnCache columnCache) {
        Map<String, ColumnCache> cacheMap = LAMBDA_CACHE.getOrDefault(className, new HashMap<>());
        cacheMap.put(property, columnCache);
        LAMBDA_CACHE.put(className, cacheMap);
    }

    /**
     * <p>
     * 获取实体对应字段 MAP
     * </p>
     *
     * @param entityClassName 实体类名
     * @return 缓存 map
     */
    public static Map<String, ColumnCache> getColumnMap(String entityClassName) {
        return LAMBDA_CACHE.getOrDefault(entityClassName, Collections.emptyMap());
    }
}
