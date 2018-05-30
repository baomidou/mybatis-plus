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
 * Lambda 工具类
 *
 * @author HCL
 * @Date 2018/05/10
 */
public final class LambdaUtils {

    private static final Map<String, Map<String, String>> LAMBDA_CACHE = new ConcurrentHashMap<>();
    // 做个小小的缓存
    private static final Map<Class, WeakReference<SerializedLambda>> CACHE = new WeakHashMap<>();

    /**
     * 解析 lambda 表达式
     *
     * @param func 需要解析的 lambda 对象
     * @param <T>  类型，被调用的 Function 对象的目标类型
     * @return 返回解析后的结果
     */
    public static <T> SerializedLambda resolve(Property<T, ?> func) {
        Class clazz = func.getClass();
        return Optional.ofNullable(CACHE.get(clazz))
            .map(WeakReference::get)
            .orElseGet(() -> {
                SerializedLambda lambda = SerializedLambda.convert(func);
                CACHE.put(clazz, new WeakReference<>(lambda));
                return lambda;
            });
    }

    public static void createCache(String className, TableInfo tableInfo) {
        LAMBDA_CACHE.put(className, createLambdaMap(tableInfo));
    }

    private static Map<String, String> createLambdaMap(TableInfo tableInfo) {
        Map<String, String> map = new HashMap<>();
        if (StringUtils.isNotEmpty(tableInfo.getKeyProperty())) {
            map.put(tableInfo.getKeyProperty(), tableInfo.getKeyColumn());
        }
        tableInfo.getFieldList().forEach(i -> map.put(i.getProperty(), i.getColumn()));
        return map;
    }
}
