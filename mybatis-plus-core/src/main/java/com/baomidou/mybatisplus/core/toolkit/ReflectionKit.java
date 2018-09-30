/*
 * Copyright (c) 2011-2020, hubin (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.core.toolkit;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toMap;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

/**
 * <p>
 * 反射工具类
 * </p>
 *
 * @author Caratacus
 * @since 2016-09-22
 */
public class ReflectionKit {

    private static final Log logger = LogFactory.getLog(ReflectionKit.class);
    /**
     * class field cache
     */
    private static final Map<Class, List<Field>> classFieldCache = new ConcurrentHashMap<>();

    /**
     * <p>
     * 反射 method 方法名，例如 getId
     * </p>
     *
     * @param field
     * @param str   属性字符串内容
     */
    public static String getMethodCapitalize(Field field, final String str) {
        Class<?> fieldType = field.getType();
        // fix #176
        return StringUtils.concatCapitalize(boolean.class.equals(fieldType) ? "is" : "get", str);
    }

    /**
     * <p>
     * 反射 method 方法名，例如 setVersion
     * </p>
     *
     * @param field Field
     * @param str   String JavaBean类的version属性名
     * @return version属性的setter方法名称，e.g. setVersion
     */
    public static String setMethodCapitalize(Field field, final String str) {
        Class<?> fieldType = field.getType();
        // type of boolean's field, getter methodname is isGood(),
        // setter methodname is setGood(boolean)
        return StringUtils.concatCapitalize("set", str);
    }

    /**
     * <p>
     * 获取 public get方法的值
     * </p>
     *
     * @param cls
     * @param entity 实体
     * @param str    属性字符串内容
     * @return Object
     */
    public static Object getMethodValue(Class<?> cls, Object entity, String str) {
        Map<String, Field> fieldMaps = getFieldMap(cls);
        try {
            if (CollectionUtils.isEmpty(fieldMaps)) {
                throw ExceptionUtils.mpe(String.format("Error: NoSuchField in %s for %s.  Cause:", cls.getSimpleName(), str));
            }
            Method method = cls.getMethod(getMethodCapitalize(fieldMaps.get(str), str));
            return method.invoke(entity);
        } catch (NoSuchMethodException e) {
            throw ExceptionUtils.mpe(String.format("Error: NoSuchMethod in %s.  Cause:", cls.getSimpleName()) + e);
        } catch (IllegalAccessException e) {
            throw ExceptionUtils.mpe(String.format("Error: Cannot execute a private method. in %s.  Cause:",
                cls.getSimpleName()) + e);
        } catch (InvocationTargetException e) {
            throw ExceptionUtils.mpe("Error: InvocationTargetException on getMethodValue.  Cause:" + e);
        }
    }

    /**
     * <p>
     * 获取 public get方法的值
     * </p>
     *
     * @param entity 实体
     * @param str    属性字符串内容
     * @return Object
     */
    public static Object getMethodValue(Object entity, String str) {
        if (null == entity) {
            return null;
        }
        return getMethodValue(entity.getClass(), entity, str);
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
    public static Class getSuperClassGenericType(final Class clazz, final int index) {
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
        return (Class) params[index];
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
        if (CollectionUtils.isNotEmpty(fieldList)) {
            Map<String, Field> fieldMap = new LinkedHashMap<>();
            fieldList.forEach(field -> fieldMap.put(field.getName(), field));
            return fieldMap;
        }
        return Collections.emptyMap();
    }

    /**
     * <p>
     * 获取该类的所有属性列表
     * </p>
     *
     * @param clazz 反射类
     */
    public static List<Field> getFieldList(Class<?> clazz) {
        if (null == clazz) {
            return null;
        }
        List<Field> fields = classFieldCache.get(clazz);
        if (CollectionUtils.isEmpty(fields)) {
            synchronized (classFieldCache) {
                fields = doGetFieldList(clazz);
                classFieldCache.put(clazz, fields);
            }
        }
        return fields;
    }

    /**
     * <p>
     * 获取该类的所有属性列表
     * </p>
     *
     * @param clazz 反射类
     */
    public static List<Field> doGetFieldList(Class<?> clazz) {
        List<Field> fieldList = Stream.of(clazz.getDeclaredFields())
            /* 过滤静态属性 */
            .filter(field -> !Modifier.isStatic(field.getModifiers()))
            /* 过滤 transient关键字修饰的属性 */
            .filter(field -> !Modifier.isTransient(field.getModifiers()))
            .collect(toCollection(LinkedList::new));
        /* 处理父类字段 */
        Class<?> superClass = clazz.getSuperclass();
        if (superClass.equals(Object.class)) {
            return fieldList;
        }
        /* 排除重载属性 */
        return excludeOverrideSuperField(fieldList, getFieldList(superClass));
    }

    /**
     * <p>
     * 排序重置父类属性
     * </p>
     *
     * @param fieldList      子类属性
     * @param superFieldList 父类属性
     */
    public static List<Field> excludeOverrideSuperField(List<Field> fieldList, List<Field> superFieldList) {
        // 子类属性
        Map<String, Field> fieldMap = fieldList.stream().collect(toMap(Field::getName, identity()));
        superFieldList.stream().filter(field -> fieldMap.get(field.getName()) == null).forEach(fieldList::add);
        return fieldList;
    }

}
