package com.baomidou.mybatisplus.core.plugins;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.toolkit.ExceptionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;

import lombok.Builder;
import lombok.Data;

/**
 * @author miemie
 * @since 2020-07-31
 */
public class InterceptorIgnoreHelper {

    /**
     * SQL 解析缓存
     * key 可能是 mappedStatement 的 ID,也可能是 class 的 name
     */
    private static final Map<String, InterceptorIgnoreCache> INTERCEPTOR_IGNORE_CACHE = new ConcurrentHashMap<>();

    /**
     * 初始化缓存 接口上 InterceptorIgnore 注解信息
     *
     * @param mapperClass Mapper Class
     */
    public synchronized static InterceptorIgnoreCache initSqlParserInfoCache(Class<?> mapperClass) {
        InterceptorIgnore ignore = mapperClass.getAnnotation(InterceptorIgnore.class);
        if (ignore != null) {
            return buildInterceptorIgnoreCache(ignore);
        }
        return null;
    }

    /**
     * 初始化缓存 方法上 InterceptorIgnore 注解信息
     *
     * @param mapperAnnotation Mapper Class Name
     * @param method           Method
     */
    public static void initSqlParserInfoCache(InterceptorIgnoreCache mapperAnnotation, String mapperClassName, Method method) {
        InterceptorIgnore ignore = method.getAnnotation(InterceptorIgnore.class);
        final String key = mapperClassName.concat(StringPool.DOT).concat(method.getName());
        if (ignore != null) {
            InterceptorIgnoreCache methodCache = buildInterceptorIgnoreCache(ignore);
            if (mapperAnnotation == null) {
                INTERCEPTOR_IGNORE_CACHE.put(key, methodCache);
                return;
            }
            INTERCEPTOR_IGNORE_CACHE.put(key, chooseCache(mapperAnnotation, methodCache));
        } else if (mapperAnnotation != null) {
            INTERCEPTOR_IGNORE_CACHE.put(key, mapperAnnotation);
        }
    }

    public static boolean willIgnoreTenantLine(String id) {
        return willIgnore(id, i -> i.getTenantLine() != null && i.getTenantLine());
    }

    public static boolean willIgnoreDynamicTableName(String id) {
        return willIgnore(id, i -> i.getDynamicTableName() != null && i.getDynamicTableName());
    }

    public static boolean willIgnoreBlockAttack(String id) {
        return willIgnore(id, i -> i.getBlockAttack() != null && i.getBlockAttack());
    }

    public static boolean willIgnoreIllegalSql(String id) {
        return willIgnore(id, i -> i.getIllegalSql() != null && i.getIllegalSql());
    }

    public static boolean willIgnore(String id, Function<InterceptorIgnoreCache, Boolean> function) {
        InterceptorIgnoreCache cache = INTERCEPTOR_IGNORE_CACHE.get(id);
        if (cache != null && !cache.ignoreAll) {
            return function.apply(cache);
        }
        return false;
    }

    public static boolean willIgnore(String id, Class<?> interceptor) {
        InterceptorIgnoreCache cache = INTERCEPTOR_IGNORE_CACHE.get(id);
        if (cache != null&& !cache.ignoreAll) {
            return cache.ignores.contains(interceptor);
        }
        return false;
    }

    private static InterceptorIgnoreCache chooseCache(InterceptorIgnoreCache mapper, InterceptorIgnoreCache method) {
        return InterceptorIgnoreCache.builder()
            .tenantLine(chooseBoolean(mapper.getTenantLine(), method.getTenantLine()))
            .dynamicTableName(chooseBoolean(mapper.getDynamicTableName(), method.getDynamicTableName()))
            .blockAttack(chooseBoolean(mapper.getBlockAttack(), method.getBlockAttack()))
            .illegalSql(chooseBoolean(mapper.getIllegalSql(), method.getIllegalSql()))
            .build();
    }

    private static InterceptorIgnoreCache buildInterceptorIgnoreCache(InterceptorIgnore ignore) {
        Set<Class<?>> ignoreInterceptors = new HashSet<>();
        Collections.addAll(ignoreInterceptors, ignore.ignores());

        return InterceptorIgnoreCache.builder()
            .tenantLine(getBoolean(ignore.tenantLine()))
            .dynamicTableName(getBoolean(ignore.dynamicTableName()))
            .blockAttack(getBoolean(ignore.blockAttack()))
            .illegalSql(getBoolean(ignore.illegalSql()))
            .ignoreAll(ignore.ignoreAll())
            .ignores(ignoreInterceptors)
            .build();
    }

    private static Boolean getBoolean(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        if ("1".equals(value) || "true".equals(value) || "on".equals(value)) {
            return true;
        }
        if ("0".equals(value) || "false".equals(value) || "off".equals(value)) {
            return false;
        }
        throw ExceptionUtils.mpe("not support this value of \"%s\"", value);
    }

    private static Boolean chooseBoolean(Boolean mapper, Boolean method) {
        if (mapper == null && method == null) {
            return null;
        }
        if (method != null) {
            return method;
        }
        return mapper;
    }

    @Data
    @Builder
    public static class InterceptorIgnoreCache {
        private Boolean tenantLine;
        private Boolean dynamicTableName;
        private Boolean blockAttack;
        private Boolean illegalSql;
        /**
         * 是否忽视所有插件
         */
        private Boolean ignoreAll;
        /**
         * 忽略的自定义interceptor集合
         */
        private Set<Class<?>> ignores;
    }
}
