package com.baomidou.mybatisplus.core.toolkit;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;

import com.baomidou.mybatisplus.core.toolkit.support.SerializedFunction;
import com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda;

/**
 * Lambda 工具类
 *
 * @author HCL
 * @Date 2018/05/10
 */
public final class LambdaUtils {

    // 做个小小的缓存
    private static final Map<Class, WeakReference<SerializedLambda>> CACHE = new WeakHashMap<>();

    private LambdaUtils() {
    }

    /**
     * 解析 lambda 表达式
     *
     * @param func 需要解析的 lambda 对象
     * @param <T>  类型，被调用的 Function 对象的目标类型
     * @return 返回解析后的结果
     */
    public static <T> SerializedLambda resolve(SerializedFunction<T, ?> func) {
        Class clazz = func.getClass();
        return Optional.ofNullable(CACHE.get(clazz))
            .map(WeakReference::get)
            .orElseGet(() -> {
                SerializedLambda lambda = SerializedLambda.convert(func);
                CACHE.put(clazz, new WeakReference<>(lambda));
                return lambda;
            });
    }

}
