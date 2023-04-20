/*
 * Copyright (c) 2011-2022, baomidou (jobob@qq.com).
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
import org.apache.ibatis.executor.keygen.SelectKeyGenerator;

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
    private static final Map<String, IgnoreStrategy> IGNORE_STRATEGY_CACHE = new ConcurrentHashMap<>();
    /**
     *  本地线程拦截器忽略策略缓存
     */
    private static final ThreadLocal<IgnoreStrategy> IGNORE_STRATEGY_LOCAL = new ThreadLocal<>();

    /**
     * 手动设置拦截器忽略执行策略，权限大于注解权限
     * <p>
     * InterceptorIgnoreHelper.handle(IgnoreStrategy.builder().tenantLine(true).build());
     * </p>
     * <p>
     * 注意，需要手动关闭调用方法 InterceptorIgnoreHelper.clearIgnoreStrategy();
     * </p>
     *
     * @param ignoreStrategy {@link IgnoreStrategy}
     */
    public static void handle(IgnoreStrategy ignoreStrategy) {
        IGNORE_STRATEGY_LOCAL.set(ignoreStrategy);
    }

    /**
     * 清空本地忽略策略
     */
    public static void clearIgnoreStrategy() {
        IGNORE_STRATEGY_LOCAL.remove();
    }

    /**
     * 初始化缓存
     * <p>
     * Mapper 上 InterceptorIgnore 注解信息
     *
     * @param mapperClass Mapper Class
     */
    public synchronized static IgnoreStrategy initSqlParserInfoCache(Class<?> mapperClass) {
        InterceptorIgnore ignore = mapperClass.getAnnotation(InterceptorIgnore.class);
        if (ignore != null) {
            String key = mapperClass.getName();
            IgnoreStrategy cache = buildIgnoreStrategy(key, ignore);
            IGNORE_STRATEGY_CACHE.put(key, cache);
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
    public static void initSqlParserInfoCache(IgnoreStrategy mapperAnnotation, String mapperClassName, Method method) {
        InterceptorIgnore ignoreStrategy = method.getAnnotation(InterceptorIgnore.class);
        String key = mapperClassName.concat(StringPool.DOT).concat(method.getName());
        String name = mapperClassName.concat(StringPool.HASH).concat(method.getName());
        if (ignoreStrategy != null) {
            IgnoreStrategy methodCache = buildIgnoreStrategy(name, ignoreStrategy);
            if (mapperAnnotation == null) {
                IGNORE_STRATEGY_CACHE.put(key, methodCache);
                return;
            }
            IGNORE_STRATEGY_CACHE.put(key, chooseCache(mapperAnnotation, methodCache));
        }
    }

    public static boolean willIgnoreTenantLine(String id) {
        return willIgnore(id, IgnoreStrategy::getTenantLine);
    }

    public static boolean willIgnoreDynamicTableName(String id) {
        return willIgnore(id, IgnoreStrategy::getDynamicTableName);
    }

    public static boolean willIgnoreBlockAttack(String id) {
        return willIgnore(id, IgnoreStrategy::getBlockAttack);
    }

    public static boolean willIgnoreIllegalSql(String id) {
        return willIgnore(id, IgnoreStrategy::getIllegalSql);
    }

    public static boolean willIgnoreDataPermission(String id) {
        return willIgnore(id, IgnoreStrategy::getDataPermission);
    }

    public static boolean willIgnoreOthersByKey(String id, String key) {
        return willIgnore(id, i -> CollectionUtils.isNotEmpty(i.getOthers()) && i.getOthers().getOrDefault(key, false));
    }

    public static boolean willIgnore(String id, Function<IgnoreStrategy, Boolean> function) {
        // 1，优化获取本地忽略策略
        IgnoreStrategy ignoreStrategy = IGNORE_STRATEGY_LOCAL.get();
        if (null == ignoreStrategy) {
            // 2，不存在取注解策略
            ignoreStrategy = IGNORE_STRATEGY_CACHE.get(id);
        }
        if (ignoreStrategy == null && id.endsWith(SelectKeyGenerator.SELECT_KEY_SUFFIX)) {
            // 支持一下 selectKey
            ignoreStrategy = IGNORE_STRATEGY_CACHE.get(id.substring(0, id.length() - SelectKeyGenerator.SELECT_KEY_SUFFIX.length()));
        }
        if (ignoreStrategy == null) {
            ignoreStrategy = IGNORE_STRATEGY_CACHE.get(id.substring(0, id.lastIndexOf(StringPool.DOT)));
        }
        if (ignoreStrategy != null) {
            Boolean apply = function.apply(ignoreStrategy);
            return apply != null && apply;
        }
        return false;
    }

    private static IgnoreStrategy chooseCache(IgnoreStrategy mapper, IgnoreStrategy method) {
        return IgnoreStrategy.builder()
            .tenantLine(chooseBoolean(mapper.getTenantLine(), method.getTenantLine()))
            .dynamicTableName(chooseBoolean(mapper.getDynamicTableName(), method.getDynamicTableName()))
            .blockAttack(chooseBoolean(mapper.getBlockAttack(), method.getBlockAttack()))
            .illegalSql(chooseBoolean(mapper.getIllegalSql(), method.getIllegalSql()))
            .dataPermission(chooseBoolean(mapper.getDataPermission(), method.getDataPermission()))
            .others(chooseOthers(mapper.getOthers(), method.getOthers()))
            .build();
    }

    private static IgnoreStrategy buildIgnoreStrategy(String name, InterceptorIgnore ignore) {
        return IgnoreStrategy.builder()
            .tenantLine(getBoolean("tenantLine", name, ignore.tenantLine()))
            .dynamicTableName(getBoolean("dynamicTableName", name, ignore.dynamicTableName()))
            .blockAttack(getBoolean("blockAttack", name, ignore.blockAttack()))
            .illegalSql(getBoolean("illegalSql", name, ignore.illegalSql()))
            .dataPermission(getBoolean("dataPermission", name, ignore.dataPermission()))
            .others(getOthers(name, ignore.others()))
            .build();
    }

    private static Boolean getBoolean(String node, String name, String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        if (StringPool.ONE.equals(value) || StringPool.TRUE.equals(value) || StringPool.ON.equals(value)) {
            return true;
        }
        if (StringPool.ZERO.equals(value) || StringPool.FALSE.equals(value) || StringPool.OFF.equals(value)) {
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
}
