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
package com.baomidou.mybatisplus.core.toolkit;

import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.support.*;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Locale.ENGLISH;

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
     * 该缓存可能会在任意不定的时间被清除
     *
     * @param func 需要解析的 lambda 对象
     * @param <T>  类型，被调用的 Function 对象的目标类型
     * @return 返回解析后的结果
     */
    public static <T> LambdaMeta extract(SFunction<T, ?> func) {
        // 1. IDEA 调试模式下 lambda 表达式是一个代理
        if (func instanceof Proxy) {
            return new IdeaProxyLambdaMeta((Proxy) func);
        }
        // 2. 反射读取
        try {
            Method method = func.getClass().getDeclaredMethod("writeReplace");
            method.setAccessible(true);
            SerializedLambda lambda = (SerializedLambda) method.invoke(func);
            // 2.1 Kotlin (>=1.6) 的属性引用会被编译为捕获了 KProperty 的 lambda，
            //     其 implMethodName 为合成方法名，需直接从被捕获的 KProperty 解析字段名
            Object kProperty = findKotlinProperty(lambda);
            if (kProperty != null) {
                return KotlinLambdaMeta.ofCapturedProperty(kProperty, lambda);
            }
            // 2.2 Kotlin 引用 Java getter（如 UserDO::getName）时会编译出形如 enclosing$getName 的合成适配方法，
            //     此时没有捕获 KProperty，从合成方法名末段还原出 getter 名
            String kotlinGetter = kotlinGetterAdapterName(lambda.getImplMethodName());
            if (kotlinGetter != null) {
                return KotlinLambdaMeta.ofGetterMethodName(kotlinGetter, lambda, func.getClass().getClassLoader());
            }
            return new ReflectLambdaMeta(lambda, func.getClass().getClassLoader());
        } catch (NoSuchMethodException e) {
            // 2.3 Kotlin (<1.6) 将引用编译为 class-based 的 SAM 包装类（没有 writeReplace），
            //     被引用的 Kotlin 可调用引用（KProperty / KFunction）作为包装类的字段保存，直接读取解析字段名
            Object callable = findKotlinCallableInFields(func);
            if (callable != null) {
                return KotlinLambdaMeta.ofCallableReference(callable);
            }
            return new ShadowLambdaMeta(com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda.extract(func));
        } catch (Throwable e) {
            // 3. 反射失败使用序列化的方式读取
            return new ShadowLambdaMeta(com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda.extract(func));
        }
    }

    /**
     * Kotlin 标准库中可调用引用接口的全限定名，使用名称匹配以避免 core 模块对 Kotlin 产生编译期依赖
     */
    private static final String KOTLIN_KPROPERTY = "kotlin.reflect.KProperty";
    private static final String KOTLIN_KFUNCTION = "kotlin.reflect.KFunction";

    /**
     * 在 lambda 捕获的参数中查找 Kotlin 的属性引用（KProperty）
     */
    private static Object findKotlinProperty(SerializedLambda lambda) {
        for (int i = 0; i < lambda.getCapturedArgCount(); i++) {
            Object arg = lambda.getCapturedArg(i);
            if (isKotlinProperty(arg)) {
                return arg;
            }
        }
        return null;
    }

    /**
     * 在对象自身或其字段中查找 Kotlin 的可调用引用（KProperty 或 KFunction）。
     * 用于 class-based 的 SAM 包装类：Kotlin 将被引用的 KProperty / KFunction 作为包装类的字段保存。
     */
    private static Object findKotlinCallableInFields(Object func) {
        if (isKotlinCallableReference(func)) {
            return func;
        }
        for (Class<?> clazz = func.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
            for (Field field : clazz.getDeclaredFields()) {
                Object value;
                try {
                    value = ReflectionKit.setAccessible(field).get(func);
                } catch (Throwable e) {
                    continue;
                }
                if (isKotlinCallableReference(value)) {
                    return value;
                }
            }
        }
        return null;
    }

    /**
     * 从 invokedynamic 合成适配方法名中还原 getter 名。
     * <p>
     * Kotlin 引用 Java getter（如 {@code UserDO::getName}）时会编译出形如 {@code enclosing$getName} 的合成方法，
     * 其末段即真实的 getter 名。仅当方法名含 {@code $} 且末段形如 getXxx/isXxx/setXxx 时才识别，
     * 以免影响普通 Java 方法引用（其 implMethodName 直接就是 getName，无 {@code $}）。
     */
    private static String kotlinGetterAdapterName(String implMethodName) {
        int idx = implMethodName.lastIndexOf('$');
        if (idx <= 0) {
            return null;
        }
        String candidate = implMethodName.substring(idx + 1);
        return isGetterName(candidate) ? candidate : null;
    }

    private static boolean isGetterName(String name) {
        if ((name.startsWith("get") || name.startsWith("set")) && name.length() > 3) {
            return Character.isUpperCase(name.charAt(3));
        }
        if (name.startsWith("is") && name.length() > 2) {
            return Character.isUpperCase(name.charAt(2));
        }
        return false;
    }

    private static boolean isKotlinProperty(Object arg) {
        return isKotlinReference(arg, KOTLIN_KPROPERTY);
    }

    private static boolean isKotlinCallableReference(Object arg) {
        return isKotlinReference(arg, KOTLIN_KPROPERTY) || isKotlinReference(arg, KOTLIN_KFUNCTION);
    }

    private static boolean isKotlinReference(Object arg, String interfaceName) {
        if (arg == null) {
            return false;
        }
        for (Class<?> clazz = arg.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
            if (implementsInterface(clazz, interfaceName)) {
                return true;
            }
        }
        return false;
    }

    private static boolean implementsInterface(Class<?> clazz, String interfaceName) {
        for (Class<?> anInterface : clazz.getInterfaces()) {
            if (interfaceName.equals(anInterface.getName()) || implementsInterface(anInterface, interfaceName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 格式化 key 将传入的 key 变更为大写格式
     * 为了支持首字母是大写的字段
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
        COLUMN_CACHE_MAP.put(tableInfo.getEntityType().getName(), createColumnCacheMap(tableInfo));
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

}
