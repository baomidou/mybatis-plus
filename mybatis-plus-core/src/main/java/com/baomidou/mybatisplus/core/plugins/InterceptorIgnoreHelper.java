package com.baomidou.mybatisplus.core.plugins;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.toolkit.ArrayUtils;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

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
    public synchronized static void initSqlParserInfoCache(Class<?> mapperClass) {
        InterceptorIgnore ignore = mapperClass.getAnnotation(InterceptorIgnore.class);
        if (ignore != null) {
            INTERCEPTOR_IGNORE_CACHE.put(mapperClass.getName(), buildInterceptorIgnoreCache(ignore));
        }
    }

    /**
     * 初始化缓存 方法上 InterceptorIgnore 注解信息
     *
     * @param mapperClassName Mapper Class Name
     * @param method          Method
     */
    public static void initSqlParserInfoCache(String mapperClassName, Method method) {
        InterceptorIgnore ignore = method.getAnnotation(InterceptorIgnore.class);
        if (ignore != null) {
            INTERCEPTOR_IGNORE_CACHE.computeIfAbsent(mapperClassName.concat(StringPool.DOT).concat(method.getName()),
                k -> buildInterceptorIgnoreCache(ignore));
        }
    }

    public static boolean willIgnore(String id, Function<InterceptorIgnoreCache, Boolean> function) {
        InterceptorIgnoreCache cache = INTERCEPTOR_IGNORE_CACHE.get(id);
        if (cache == null) {
            id = id.substring(0, id.lastIndexOf(StringPool.DOT));
            cache = INTERCEPTOR_IGNORE_CACHE.get(id);
        }
        if (cache != null) {
            return function.apply(cache);
        }
        return false;
    }

    private static InterceptorIgnoreCache buildInterceptorIgnoreCache(InterceptorIgnore ignore) {
        String tenantLine = ignore.tenantLine();
        String dynamicTableName = ignore.dynamicTableName();
        String blockAttack = ignore.blockAttack();
        String illegalSql = ignore.illegalSql();
        String[] additional = ignore.additional();
        return new InterceptorIgnoreCache(StringUtils.isBlank(tenantLine) ? null : Boolean.valueOf(tenantLine),
            StringUtils.isBlank(dynamicTableName) ? null : Boolean.valueOf(dynamicTableName),
            StringUtils.isBlank(blockAttack) ? null : Boolean.valueOf(blockAttack),
            StringUtils.isBlank(illegalSql) ? null : Boolean.valueOf(illegalSql),
            ArrayUtils.isEmpty(additional) ? Collections.emptyList() : Arrays.asList(additional)
        );
    }

    @Data
    @AllArgsConstructor
    public static class InterceptorIgnoreCache {
        private Boolean tenantLine;
        private Boolean dynamicTableName;
        private Boolean blockAttack;
        private Boolean illegalSql;
        private List<String> additional;
    }
}
