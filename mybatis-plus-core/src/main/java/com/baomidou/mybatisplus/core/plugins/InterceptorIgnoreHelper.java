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
package com.baomidou.mybatisplus.core.plugins;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.toolkit.*;
import lombok.Builder;
import lombok.Data;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * @author miemie
 * @since 2020-07-31
 */
public abstract class InterceptorIgnoreHelper {

    /**
     * SQL 解析缓存
     * key 可能是 mappedStatement 的 ID,也可能是 class 的 name
     */
    private static final Map<String, InterceptorIgnoreCache> INTERCEPTOR_IGNORE_CACHE = new ConcurrentHashMap<>();

    /**
     * 初始化缓存
     * <p>
     * Mapper 上 InterceptorIgnore 注解信息
     *
     * @param mapperClass Mapper Class
     */
    public synchronized static InterceptorIgnoreCache initSqlParserInfoCache(Class<?> mapperClass) {
        InterceptorIgnore ignore = mapperClass.getAnnotation(InterceptorIgnore.class);
        if (ignore != null) {
            String key = mapperClass.getName();
            InterceptorIgnoreCache cache = buildInterceptorIgnoreCache(key, ignore);
            INTERCEPTOR_IGNORE_CACHE.put(key, cache);
            return cache;
        }
        return null;
    }

    /**
     * 初始化缓存
     * <p>
     * Mapper#method 上 InterceptorIgnore 注解信息
     *
     * @param mapperAnnotation Mapper Class Name
     * @param method           Method
     */
    public static void initSqlParserInfoCache(InterceptorIgnoreCache mapperAnnotation, String mapperClassName, Method method) {
        InterceptorIgnore ignore = method.getAnnotation(InterceptorIgnore.class);
        String key = mapperClassName.concat(StringPool.DOT).concat(method.getName());
        String name = mapperClassName.concat(StringPool.HASH).concat(method.getName());
        if (ignore != null) {
            InterceptorIgnoreCache methodCache = buildInterceptorIgnoreCache(name, ignore);
            if (mapperAnnotation == null) {
                INTERCEPTOR_IGNORE_CACHE.put(key, methodCache);
                return;
            }
            INTERCEPTOR_IGNORE_CACHE.put(key, chooseCache(mapperAnnotation, methodCache));
        }
    }

    public static boolean willIgnoreTenantLine(String id) {
        return willIgnore(id, InterceptorIgnoreCache::getTenantLine);
    }

    public static boolean willIgnoreDynamicTableName(String id) {
        return willIgnore(id, InterceptorIgnoreCache::getDynamicTableName);
    }

    public static boolean willIgnoreBlockAttack(String id) {
        return willIgnore(id, InterceptorIgnoreCache::getBlockAttack);
    }

    public static boolean willIgnoreIllegalSql(String id) {
        return willIgnore(id, InterceptorIgnoreCache::getIllegalSql);
    }

    public static boolean willIgnoreDataPermission(String id) {
        return willIgnore(id, InterceptorIgnoreCache::getDataPermission);
    }

    public static boolean willIgnoreSharding(String id) {
        return willIgnore(id, InterceptorIgnoreCache::getSharding);
    }

    public static boolean willIgnoreOthersByKey(String id, String key) {
        return willIgnore(id, i -> CollectionUtils.isNotEmpty(i.getOthers()) && i.getOthers().getOrDefault(key, false));
    }

    public static boolean willIgnore(String id, Function<InterceptorIgnoreCache, Boolean> function) {
        InterceptorIgnoreCache cache = INTERCEPTOR_IGNORE_CACHE.get(id);
        if (cache == null) {
            cache = INTERCEPTOR_IGNORE_CACHE.get(id.substring(0, id.lastIndexOf(StringPool.DOT)));
        }
        if (cache != null) {
            Boolean apply = function.apply(cache);
            return apply != null && apply;
        }
        return false;
    }

    private static InterceptorIgnoreCache chooseCache(InterceptorIgnoreCache mapper, InterceptorIgnoreCache method) {
        return InterceptorIgnoreCache.builder()
            .tenantLine(chooseBoolean(mapper.getTenantLine(), method.getTenantLine()))
            .dynamicTableName(chooseBoolean(mapper.getDynamicTableName(), method.getDynamicTableName()))
            .blockAttack(chooseBoolean(mapper.getBlockAttack(), method.getBlockAttack()))
            .illegalSql(chooseBoolean(mapper.getIllegalSql(), method.getIllegalSql()))
            .dataPermission(chooseBoolean(mapper.getDataPermission(), method.getDataPermission()))
            .others(chooseOthers(mapper.getOthers(), method.getOthers()))
            .build();
    }

    private static InterceptorIgnoreCache buildInterceptorIgnoreCache(String name, InterceptorIgnore ignore) {
        return InterceptorIgnoreCache.builder()
            .tenantLine(getBoolean("tenantLine", name, ignore.tenantLine()))
            .dynamicTableName(getBoolean("dynamicTableName", name, ignore.dynamicTableName()))
            .blockAttack(getBoolean("blockAttack", name, ignore.blockAttack()))
            .illegalSql(getBoolean("illegalSql", name, ignore.illegalSql()))
            .dataPermission(getBoolean("dataPermission", name, ignore.dataPermission()))
            .sharding(getBoolean("sharding", name, ignore.sharding()))
            .others(getOthers(name, ignore.others()))
            .build();
    }

    private static Boolean getBoolean(String node, String name, String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        if ("1".equals(value) || "true".equals(value) || "on".equals(value)) {
            return true;
        }
        if ("0".equals(value) || "false".equals(value) || "off".equals(value)) {
            return false;
        }
        throw ExceptionUtils.mpe("unsupported value \"%s\" by `@InterceptorIgnore#%s` on top of \"%s\"", value, node, name);
    }

    private static Map<String, Boolean> getOthers(String name, String[] values) {
        if (ArrayUtils.isEmpty(values)) {
            return null;
        }
        Map<String, Boolean> map = CollectionUtils.newHashMapWithExpectedSize(values.length);
        for (String s : values) {
            int index = s.indexOf(StringPool.AT);
            Assert.isTrue(index > 0, "unsupported value \"%s\" by `@InterceptorIgnore#others` on top of \"%s\"", s, name);
            String key = s.substring(0, index);
            Boolean value = getBoolean("others", name, s.substring(index + 1));
            map.put(key, value);
        }
        return map;
    }

    /**
     * mapper#method 上的注解 优先级大于 mapper 上的注解
     */
    private static Boolean chooseBoolean(Boolean mapper, Boolean method) {
        if (mapper == null && method == null) {
            return null;
        }
        if (method != null) {
            return method;
        }
        return mapper;
    }

    private static Map<String, Boolean> chooseOthers(Map<String, Boolean> mapper, Map<String, Boolean> method) {
        boolean emptyMapper = CollectionUtils.isEmpty(mapper);
        boolean emptyMethod = CollectionUtils.isEmpty(method);
        if (emptyMapper && emptyMethod) {
            return null;
        }
        if (emptyMapper) {
            return method;
        }
        if (emptyMethod) {
            return mapper;
        }
        Set<String> mapperKeys = mapper.keySet();
        Set<String> methodKeys = method.keySet();
        Set<String> keys = new HashSet<>(mapperKeys.size() + methodKeys.size());
        keys.addAll(methodKeys);
        keys.addAll(mapperKeys);
        Map<String, Boolean> map = CollectionUtils.newHashMapWithExpectedSize(keys.size());
        methodKeys.forEach(k -> map.put(k, chooseBoolean(mapper.get(k), method.get(k))));
        return map;
    }

    @Data
    @Builder
    public static class InterceptorIgnoreCache {
        private Boolean tenantLine;
        private Boolean dynamicTableName;
        private Boolean blockAttack;
        private Boolean illegalSql;
        private Boolean dataPermission;
        private Boolean sharding;
        private Map<String, Boolean> others;
    }
}
