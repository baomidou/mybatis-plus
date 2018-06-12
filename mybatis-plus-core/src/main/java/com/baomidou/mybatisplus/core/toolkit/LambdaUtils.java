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
import com.baomidou.mybatisplus.core.toolkit.support.Property;
import com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * Lambda 工具类
 * </p>
 *
 * @author HCL
 * @since 2018-05-10
 */
public final class LambdaUtils {

    private static final Map<String, Map<String, String>> LAMBDA_CACHE = new ConcurrentHashMap<>();
    private static final Map<Class, WeakReference<SerializedLambda>> FUNC_CACHE = new WeakHashMap<>();

    /**
     * <p>
     * 解析 lambda 表达式
     * </p>
     *
     * @param func 需要解析的 lambda 对象
     * @param <T>  类型，被调用的 Function 对象的目标类型
     * @return 返回解析后的结果
     */
    public static <T> SerializedLambda resolve(Property<T, ?> func) {
        Class clazz = func.getClass();
        return Optional.ofNullable(FUNC_CACHE.get(clazz))
            .map(WeakReference::get)
            .orElseGet(() -> {
                SerializedLambda lambda = SerializedLambda.convert(func);
                FUNC_CACHE.put(clazz, new WeakReference<>(lambda));
                return lambda;
            });
    }

    /**
     * <p>
     * 缓存实体类名与表字段映射关系
     * </p>
     *
     * @param className 实体类名
     * @param tableInfo 表信息
     */
    public static void createCache(String className, TableInfo tableInfo) {
        LAMBDA_CACHE.put(className, createLambdaMap(tableInfo));
    }

    /**
     * <p>
     * 缓存实体字段 MAP 信息
     * </p>
     *
     * @param tableInfo 表信息
     * @return
     */
    private static Map<String, String> createLambdaMap(TableInfo tableInfo) {
        Map<String, String> map = new HashMap<>(16);
        if (StringUtils.isNotEmpty(tableInfo.getKeyProperty())) {
            map.put(tableInfo.getKeyProperty(), tableInfo.getKeyColumn());
        }
        tableInfo.getFieldList().forEach(i -> map.put(i.getProperty(), i.getColumn()));
        return map;
    }

    /**
     * <p>
     * 获取实体对应字段 MAP
     * </p>
     *
     * @param entityClassName 实体类名
     * @return
     */
    public static Map<String, String> getColumnMap(String entityClassName) {
        return LAMBDA_CACHE.get(entityClassName);
    }
}
