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
import java.util.function.Supplier;

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
     * 注意，需要手动关闭调用方法 {@link #clearIgnoreStrategy()}
     * </p>
     * <p>简化操作可请使用{@link #execute(IgnoreStrategy, Supplier)}</p>
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
     * 判断当前线程是否有忽略策略
     *
     * @return 是否有忽略策略
     * @since 3.5.10
     */
    public static boolean hasIgnoreStrategy() {
        return IGNORE_STRATEGY_LOCAL.get() != null;
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
     * 获取忽略策略缓存信息
     *
     * @param key key
     * @return 策略信息
     * @since 3.5.10
     */
    public static IgnoreStrategy getIgnoreStrategy(String key) {
        return IGNORE_STRATEGY_CACHE.get(key);
    }

    /**
     * 按指定策略执行指定方法 (忽略线程级别,参数执行级使用最高)
     * 方法执行完成后后释放掉当前线程上的忽略策略.
     * <p>
     * 注意:
     * <li>1.不要和{@link #handle(IgnoreStrategy)}一起混合使用,此方法只是简化操作,防止未释放掉资源造成的错误<li/>
     * <li>2.不要和{@link InterceptorIgnore} 注解一起搭配使用,例如在mapper上的default方法里再调用此方法,最终优先级还是以此方法为准<li/>
     * <li>3.记住,一旦调用了此方法,开始会覆盖你当前执行线程上的策略,结束必定会释放掉当前线程上的策略</>
     * </p>
     *
     * @param ignoreStrategy 忽略策略
     * @param supplier       执行方法
     * @param <T>            T
     * @return 返回值
     * @since 3.5.10
     */
    public static <T> T execute(IgnoreStrategy ignoreStrategy, Supplier<T> supplier) {
        try {
            handle(ignoreStrategy);
            return supplier.get();
        } finally {
            clearIgnoreStrategy();
        }
    }

    /**
     * 按指定策略执行指定方法 (忽略线程级别,参数执行级使用最高)
     * 方法执行完成后后释放掉当前线程上的忽略策略.
     * <p>
     * 注意:
     * <li>1.不要和{@link #handle(IgnoreStrategy)}一起混合使用,此方法只是简化操作,防止未释放掉资源造成的错误<li/>
     * <li>2.不要和{@link InterceptorIgnore} 注解一起搭配使用,例如在mapper上的default方法里再调用此方法,最终优先级还是以此方法为准<li/>
     * <li>3.记住,一旦调用了此方法,开始会覆盖你当前执行线程上的策略,结束必定会释放掉当前线程上的策略</>
     * </p>
     *
     * @param ignoreStrategy 忽略策略
     * @param runnable       执行方法
     * @since 3.5.10
     */
    public static void execute(IgnoreStrategy ignoreStrategy, Runnable runnable) {
        try {
            handle(ignoreStrategy);
            runnable.run();
        } finally {
            clearIgnoreStrategy();
        }
    }

    /**
     * 通过方法获取策略信息(优先级方法注解>当前类注解)
     *
     * @param method 方法信息
     * @return 忽略策略信息
     * @see #initSqlParserInfoCache(Class)
     * @see #initSqlParserInfoCache(IgnoreStrategy, String, Method)
     * @since 3.5.10
     */
    public static IgnoreStrategy findIgnoreStrategy(Class<?> clz, Method method) {
        String className = clz.getName();
        IgnoreStrategy ignoreStrategy = getIgnoreStrategy(method.getDeclaringClass().getName() + StringPool.DOT + method.getName());
        if (ignoreStrategy == null) {
            ignoreStrategy = getIgnoreStrategy(className);
        }
        return ignoreStrategy;
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
            // fixed github issues/5342
            int index = id.lastIndexOf(StringPool.DOT);
            ignoreStrategy = IGNORE_STRATEGY_CACHE.get(index > 0 ? id.substring(0, index) : id);
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
        for (String k : methodKeys) {
            map.put(k, chooseBoolean(mapper.get(k), method.get(k)));
        }
        return map;
    }
}
