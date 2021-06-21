/*
 * Copyright (c) 2011-2021, baomidou (jobob@qq.com).
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
package com.baomidou.mybatisplus.core.toolkit;

import static java.util.Locale.ENGLISH;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.metadata.AliasFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.support.ColumnCache;
import com.baomidou.mybatisplus.core.toolkit.support.LambdaMeta;
import com.baomidou.mybatisplus.core.toolkit.support.ProxyLambdaMeta;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.core.toolkit.support.SerializedLambdaMeta;

/**
 * Lambda 解析工具类
 *
 * @author HCL, MieMie
 * @since 2018-05-10
 */
public final class LambdaUtils {

    /**
     * 字段映射
     */
    private static final Map<String, Map<String, ColumnCache>> COLUMN_CACHE_MAP = new ConcurrentHashMap<>();

    /**
     * 别名扩展字段
     */
    private static final Map<String, Map<String, ColumnCache>> ALIAS_COLUMN_CACHE_MAP = new ConcurrentHashMap<>();

    /**
     * 该缓存可能会在任意不定的时间被清除
     *
     * @param func 需要解析的 lambda 对象
     * @param <T>  类型，被调用的 Function 对象的目标类型
     * @return 返回解析后的结果
     */
    public static <T> LambdaMeta extract(SFunction<T, ?> func) {
        try {
            Method method = func.getClass().getDeclaredMethod("writeReplace");
            return new SerializedLambdaMeta((SerializedLambda) ReflectionKit.setAccessible(method).invoke(func));
        } catch (NoSuchMethodException e) {
            if (func instanceof Proxy) return new ProxyLambdaMeta((Proxy) func);
            String message = "Cannot find method writeReplace, please make sure that the lambda composite class is currently passed in";
            throw new MybatisPlusException(message);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new MybatisPlusException(e);
        }
    }

    /**
     * 格式化 key 将传入的 key 变更为大写格式
     *
     * <pre>
     *     Assert.assertEquals("USERID", formatKey("userId"))
     * </pre>
     *
     * @param key key
     * @return 大写的 key
     */
    public static String formatKey(String key) {
        return key.toUpperCase(ENGLISH);
    }

    /**
     * 将传入的表信息加入缓存
     *
     * @param tableInfo 表信息
     */
    public static void installCache(TableInfo tableInfo) {
        String name = tableInfo.getEntityType().getName();
        COLUMN_CACHE_MAP.put(name, createColumnCacheMap(tableInfo));
        Map<String, ColumnCache> aliasColumnCacheMap = createAliasColumnCacheMap(tableInfo);
        if (ObjectUtils.isNotEmpty(aliasColumnCacheMap)) {
            ALIAS_COLUMN_CACHE_MAP.put(name, aliasColumnCacheMap);
        }
    }

    /**
     * 缓存实体字段 MAP 信息
     *
     * @param info 表信息
     * @return 缓存 map
     */
    private static Map<String, ColumnCache> createColumnCacheMap(TableInfo info) {
        Map<String, ColumnCache> map;

        if (info.havePK()) {
            map = CollectionUtils.newHashMapWithExpectedSize(info.getFieldList().size() + 1);
            map.put(formatKey(info.getKeyProperty()), new ColumnCache(info.getKeyColumn(), info.getKeySqlSelect()));
        } else {
            map = CollectionUtils.newHashMapWithExpectedSize(info.getFieldList().size());
        }

        info.getFieldList().forEach(i ->
            map.put(formatKey(i.getProperty()), new ColumnCache(i.getColumn(), i.getSqlSelect(), i.getMapping()))
        );
        return map;
    }

    /**
     * 获取实体对应字段 MAP
     *
     * @param clazz 实体类
     * @return 缓存 map
     */
    public static Map<String, ColumnCache> getColumnMap(Class<?> clazz) {
        return CollectionUtils.computeIfAbsent(COLUMN_CACHE_MAP, clazz.getName(), key -> {
            TableInfo info = TableInfoHelper.getTableInfo(clazz);
            return info == null ? null : createColumnCacheMap(info);
        });
    }

    /**
     * 获取别名扩展字段 MAP
     *
     * @param clazz 实体类
     * @return 缓存 map
     */
    public static Map<String, ColumnCache> getAliasColumnMap(Class<?> clazz) {
        String className = clazz.getName();
        Map<String, ColumnCache> aliasColumnMap = ALIAS_COLUMN_CACHE_MAP.get(className);
        if (aliasColumnMap != null) {
            return aliasColumnMap;
        }
        aliasColumnMap = Optional.ofNullable(TableInfoHelper.getTableInfo(clazz))
            .map(LambdaUtils::createAliasColumnCacheMap)
            .filter(ObjectUtils::isNotEmpty).orElse(null);
        if (aliasColumnMap != null) {
            ALIAS_COLUMN_CACHE_MAP.putIfAbsent(className, aliasColumnMap);
        }
        return aliasColumnMap;
    }

    /**
     * 缓存别名扩展字段 MAP
     *
     * @param info 表信息
     * @return 缓存 map
     */
    private static Map<String, ColumnCache> createAliasColumnCacheMap(TableInfo info) {
        List<AliasFieldInfo> aliasFieldList = info.getAliasFieldList();
        if (CollectionUtils.isEmpty(aliasFieldList)) {
            return null;
        }
        return aliasFieldList.stream().collect(Collectors.toMap(field -> formatKey(field.getProperty()),
            field -> new ColumnCache(field.getColumn(), field.getColumn())));
    }

}
