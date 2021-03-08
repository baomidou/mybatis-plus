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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

/**
 * 反射工具类，提供反射相关的快捷操作
 *
 * @author Caratacus
 * @author hcl
 * @since 2016-09-22
 */
public final class ReflectionKit {
    private static final Log logger = LogFactory.getLog(ReflectionKit.class);
    /**
     * class field cache
     */
    private static final Map<Class<?>, List<Field>> CLASS_FIELD_CACHE = new ConcurrentHashMap<>();

    private static final Map<Class<?>, Class<?>> PRIMITIVE_WRAPPER_TYPE_MAP = new IdentityHashMap<>(8);

    private static final Map<Class<?>, Class<?>> PRIMITIVE_TYPE_TO_WRAPPER_MAP = new IdentityHashMap<>(8);

    static {
        PRIMITIVE_WRAPPER_TYPE_MAP.put(Boolean.class, boolean.class);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(Byte.class, byte.class);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(Character.class, char.class);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(Double.class, double.class);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(Float.class, float.class);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(Integer.class, int.class);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(Long.class, long.class);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(Short.class, short.class);
        for (Map.Entry<Class<?>, Class<?>> entry : PRIMITIVE_WRAPPER_TYPE_MAP.entrySet()) {
            PRIMITIVE_TYPE_TO_WRAPPER_MAP.put(entry.getValue(), entry.getKey());
        }
    }

    /**
     * <p>
     * 反射 method 方法名，例如 setVersion
     * </p>
     *
     * @param field Field
     * @param str   String JavaBean类的version属性名
     * @return version属性的setter方法名称，e.g. setVersion
     * @deprecated 3.0.8
     */
    @Deprecated
    public static String setMethodCapitalize(Field field, final String str) {
        return StringUtils.concatCapitalize("set", str);
    }

    /**
     * 获取字段值
     *
     * @param entity    实体
     * @param fieldName 字段名称
     * @return 属性值
     */
    public static Object getFieldValue(Object entity, String fieldName) {
        Class<?> cls = entity.getClass();
        Map<String, Field> fieldMaps = getFieldMap(cls);
        try {
            Field field = fieldMaps.get(fieldName);
            Assert.notNull(field, "Error: NoSuchField in %s for %s.  Cause:", cls.getSimpleName(), fieldName);
            field.setAccessible(true);
            return field.get(entity);
        } catch (ReflectiveOperationException e) {
            throw ExceptionUtils.mpe("Error: Cannot read field in %s.  Cause:", e, cls.getSimpleName());
        }
    }

    /**
     * <p>
     * 反射对象获取泛型
     * </p>
     *
     * @param clazz 对象
     * @param index 泛型所在位置
     * @return Class
     */
    public static Class<?> getSuperClassGenericType(final Class<?> clazz, final int index) {
        Type genType = clazz.getGenericSuperclass();
        if (!(genType instanceof ParameterizedType)) {
            logger.warn(String.format("Warn: %s's superclass not ParameterizedType", clazz.getSimpleName()));
            return Object.class;
        }
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        if (index >= params.length || index < 0) {
            logger.warn(String.format("Warn: Index: %s, Size of %s's Parameterized Type: %s .", index,
                    clazz.getSimpleName(), params.length));
            return Object.class;
        }
        if (!(params[index] instanceof Class)) {
            logger.warn(String.format("Warn: %s not set the actual class on superclass generic parameter",
                    clazz.getSimpleName()));
            return Object.class;
        }
        return (Class<?>) params[index];
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GenericTypeInfo {
        private String name;
        private Class clazz;
    }

    /**
     * 获取type所实现的指定接口targetType的index位置泛型类型
     * @param type 当前类型
     * @param targetType type类型实现的某一接口类型
     * @param index 泛型位置
     * @return type所实现的指定接口targetType的指定位置index的泛型类型
     * @author lichangfeng
     * @since 2021-03-08
     */
    public static Class<?> getGenericInterfaces(Class type, Class targetType, int index) {
        return getGenericInterfaces(type, targetType).get(index).getClazz();
    }
    /**
     * 获取type所实现的指定接口targetType的泛型类型信息
     * @param type 当前类型
     * @param targetType type类型实现的某一接口类型
     * @return type所实现的指定接口targetType的泛型类型信息，list保持了泛型的顺序
     * @author lichangfeng
     * @since 2021-03-08
     */
    public static List<GenericTypeInfo> getGenericInterfaces(Class type, Class targetType) {
        // 初始化 targetInfos
        TypeVariable[] tvs = targetType.getTypeParameters();
        List<GenericTypeInfo> targetInfos = new ArrayList<>(tvs.length);
        for (TypeVariable tv : tvs) {
            targetInfos.add(new GenericTypeInfo(tv.getName(), null));
        }
        // 如果type不是targetType的子类型，直接返回
        if (!targetType.isAssignableFrom(type)) {
            logger.warn(targetType+" is not assignable from "+type);
            return targetInfos;
        }
        // 递归调用获取泛型信息
        getGenericInterfaces(Collections.EMPTY_LIST, type, targetType, targetInfos);
        return targetInfos;
    }

    /**
     * 通过递归调用，向上遍历currType所有接口类型的泛型信息，当找到targetType时，设置targetInfos
     * @param currInfos 当前类型的泛型信息
     * @param currType 当前类型
     * @param targetType 目标类型
     * @param targetInfos 目标类型泛型信息
     */
    private static void getGenericInterfaces(List<GenericTypeInfo> currInfos, Class currType, Class targetType, List<GenericTypeInfo> targetInfos) {
        // 当前类型为目标类型，设置targetInfos
        if (currType.equals(targetType)) {
            for (int i = 0; i < targetInfos.size(); i++) {
                Class tc = currInfos.get(i).getClazz();
                if (tc==null) {
                    continue;
                }
                /*
                向上遍历currType所有接口类型树时，可能targetType会出现多次，我们取其泛型类型的最具体类型，
                如同一位置泛型类型获取到两次，分别为type1和type2，如果type1是type2的子类，就取type1，用isAssignableFrom方法
                 */
                if (targetInfos.get(i).getClazz()==null || targetInfos.get(i).getClazz().isAssignableFrom(tc)) {
                    targetInfos.get(i).setClazz(tc);
                }
            }
            return;
        }

        // currType 所有直接实现的类型
        Type[] impleTypes = currType.getGenericInterfaces();
        for (Type impleType : impleTypes) {
            if (impleType instanceof ParameterizedType) {
                // 有标明泛型
                ParameterizedType parameterizedType = (ParameterizedType) impleType;
                // 类型impleType声明的泛型信息
                TypeVariable[] tvs = ((Class) parameterizedType.getRawType()).getTypeParameters();
                // 子类型中明确的泛型信息
                Type[] ts = ((ParameterizedType) impleType).getActualTypeArguments();
                // 将声明的泛型信息和子类型中标明的泛型信息组合为info list
                List<GenericTypeInfo> impleTypeInfos = new ArrayList<>(ts.length);
                for (int i = 0; i < ts.length; i++) {
                    if (ts[i] instanceof Class) {
                        GenericTypeInfo info = new GenericTypeInfo(tvs[i].getName(), (Class) ts[i]);
                        impleTypeInfos.add(info);
                    } else {
                        String typeName = ts[i].getTypeName();
                        /*
                        向上获取其具体类型
                        如Inter3 extends Inter2<Obj>， Inter2<T> extends Inter1<T>，则我们可以根据Inter2的T为Obj，得出Inter1的T也为Obj
                         */
                        Class infoClass = currInfos.stream().filter(info -> info.getName().equals(typeName)).map(info -> info.getClazz()).findFirst().orElse(null);
                        GenericTypeInfo info = new GenericTypeInfo(tvs[i].getName(), infoClass);
                        impleTypeInfos.add(info);
                    }
                }
                getGenericInterfaces(impleTypeInfos, (Class) parameterizedType.getRawType(), targetType, targetInfos);

            } else {
                // 无标明泛型
                // 类型impleType声明的泛型信息
                TypeVariable[] tvs = ((Class) impleType).getTypeParameters();
                List<GenericTypeInfo> impleTypeInfos = new ArrayList<>(tvs.length);
                for (int i = 0; i < tvs.length; i++) {
                    GenericTypeInfo info = new GenericTypeInfo(tvs[i].getName(), null);
                    impleTypeInfos.add(info);
                }
                getGenericInterfaces(impleTypeInfos, (Class)impleType, targetType, targetInfos);
            }
        }


    }

    /**
     * <p>
     * 获取该类的所有属性列表
     * </p>
     *
     * @param clazz 反射类
     */
    public static Map<String, Field> getFieldMap(Class<?> clazz) {
        List<Field> fieldList = getFieldList(clazz);
        return CollectionUtils.isNotEmpty(fieldList) ? fieldList.stream().collect(Collectors.toMap(Field::getName, field -> field)) : Collections.emptyMap();
    }

    /**
     * <p>
     * 获取该类的所有属性列表
     * </p>
     *
     * @param clazz 反射类
     */
    public static List<Field> getFieldList(Class<?> clazz) {
        if (Objects.isNull(clazz)) {
            return Collections.emptyList();
        }
        return CollectionUtils.computeIfAbsent(CLASS_FIELD_CACHE, clazz, k -> {
            Field[] fields = k.getDeclaredFields();
            List<Field> superFields = new ArrayList<>();
            Class<?> currentClass = k.getSuperclass();
            while (currentClass != null) {
                Field[] declaredFields = currentClass.getDeclaredFields();
                Collections.addAll(superFields, declaredFields);
                currentClass = currentClass.getSuperclass();
            }
            /* 排除重载属性 */
            Map<String, Field> fieldMap = excludeOverrideSuperField(fields, superFields);
            /*
             * 重写父类属性过滤后处理忽略部分，支持过滤父类属性功能
             * 场景：中间表不需要记录创建时间，忽略父类 createTime 公共属性
             * 中间表实体重写父类属性 ` private transient Date createTime; `
             */
            return fieldMap.values().stream()
                    /* 过滤静态属性 */
                    .filter(f -> !Modifier.isStatic(f.getModifiers()))
                    /* 过滤 transient关键字修饰的属性 */
                    .filter(f -> !Modifier.isTransient(f.getModifiers()))
                    .collect(Collectors.toList());
        });
    }

    /**
     * <p>
     * 获取该类的所有属性列表
     * </p>
     *
     * @param clazz 反射类
     * @deprecated 3.4.0
     */
    @Deprecated
    public static List<Field> doGetFieldList(Class<?> clazz) {
        if (clazz.getSuperclass() != null) {
            /* 排除重载属性 */
            Map<String, Field> fieldMap = excludeOverrideSuperField(clazz.getDeclaredFields(),
                    /* 处理父类字段 */
                    getFieldList(clazz.getSuperclass()));
            /*
             * 重写父类属性过滤后处理忽略部分，支持过滤父类属性功能
             * 场景：中间表不需要记录创建时间，忽略父类 createTime 公共属性
             * 中间表实体重写父类属性 ` private transient Date createTime; `
             */
            return fieldMap.values().stream()
                    /* 过滤静态属性 */
                    .filter(f -> !Modifier.isStatic(f.getModifiers()))
                    /* 过滤 transient关键字修饰的属性 */
                    .filter(f -> !Modifier.isTransient(f.getModifiers()))
                    .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * <p>
     * 排序重置父类属性
     * </p>
     *
     * @param fields         子类属性
     * @param superFieldList 父类属性
     */
    public static Map<String, Field> excludeOverrideSuperField(Field[] fields, List<Field> superFieldList) {
        // 子类属性
        Map<String, Field> fieldMap = Stream.of(fields).collect(toMap(Field::getName, identity(),
                (u, v) -> {
                    throw new IllegalStateException(String.format("Duplicate key %s", u));
                },
                LinkedHashMap::new));
        superFieldList.stream().filter(field -> !fieldMap.containsKey(field.getName()))
                .forEach(f -> fieldMap.put(f.getName(), f));
        return fieldMap;
    }

    /**
     * 判断是否为基本类型或基本包装类型
     *
     * @param clazz class
     * @return 是否基本类型或基本包装类型
     */
    public static boolean isPrimitiveOrWrapper(Class<?> clazz) {
        Assert.notNull(clazz, "Class must not be null");
        return (clazz.isPrimitive() || PRIMITIVE_WRAPPER_TYPE_MAP.containsKey(clazz));
    }

    public static Class<?> resolvePrimitiveIfNecessary(Class<?> clazz) {
        return (clazz.isPrimitive() && clazz != void.class ? PRIMITIVE_TYPE_TO_WRAPPER_MAP.get(clazz) : clazz);
    }
}
