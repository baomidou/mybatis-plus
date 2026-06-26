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
package com.baomidou.mybatisplus.core.toolkit.support;

import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.toolkit.ClassUtils;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.core.toolkit.StringPool;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;

/**
 * 针对 Kotlin 可调用引用（KProperty / KFunction）适配为 Java {@link SFunction} 时的元信息。
 * <p>
 * Kotlin 把形如 {@code User::name}（属性引用）或 {@code UserDO::getName}（getter 方法引用）作为 Java 函数式接口
 * 参数传递时，{@link SerializedLambda#getImplMethodName()} 往往是合成方法名（如 {@code xxx$lambda$0}、
 * {@code xxx$getName}），无法直接解析出字段名。该类改为从被引用对象本身解析：
 * <ul>
 *     <li>属性引用（KProperty）：{@code getName()} 返回属性名，补成 getter 形式交给 PropertyNamer 还原；</li>
 *     <li>getter 方法引用（KFunction）：{@code getName()} 已是方法名（如 {@code getName}），直接交给 PropertyNamer；</li>
 *     <li>归属类取自 {@code getOwner().getJClass()}，或在 invokedynamic 场景下取自 {@code instantiatedMethodType}。</li>
 * </ul>
 * <p>
 * 兼容性：属性引用（{@code Entity::prop}）在 Kotlin 1.2 至 2.4 全部可用；getter 方法引用（{@code JavaDO::getName}）
 * 在 Kotlin 1.2–1.4 与 1.6+ 可用。<b>已知限制</b>：Kotlin 1.5.x 会把 getter 方法引用优化为直接实现函数式接口的单例类，
 * 既无 KFunction 元数据也无 {@link SerializedLambda}（字段名仅存在于 {@code apply} 方法的字节码中），因此无法解析；
 * 此时请改用属性引用（{@code Entity::prop}）或 Kotlin 原生的 KtQueryWrapper。
 */
public class KotlinLambdaMeta implements LambdaMeta {

    private static final String KOTLIN_KPROPERTY = "kotlin.reflect.KProperty";

    private final String implMethodName;
    private final Class<?> instantiatedClass;

    private KotlinLambdaMeta(String implMethodName, Class<?> instantiatedClass) {
        this.implMethodName = implMethodName;
        this.instantiatedClass = instantiatedClass;
    }

    /**
     * invokedynamic 场景下被捕获的属性引用（KProperty）：字段名来自 KProperty，归属类来自 instantiatedMethodType。
     */
    public static KotlinLambdaMeta ofCapturedProperty(Object kProperty, SerializedLambda lambda) {
        return new KotlinLambdaMeta(getterName(callableName(kProperty), true), classFromLambda(lambda, classLoaderOf(kProperty)));
    }

    /**
     * class-based SAM 包装类场景：包装类字段持有的 Kotlin 可调用引用（KProperty 或 KFunction），归属类来自其 owner。
     */
    public static KotlinLambdaMeta ofCallableReference(Object callable) {
        return new KotlinLambdaMeta(getterName(callableName(callable), isKotlinProperty(callable)), ownerClass(callable));
    }

    /**
     * invokedynamic 场景下引用 Java getter 时编译出的合成适配方法（形如 {@code enclosing$getName}），
     * getter 名取自方法名的末段，归属类来自 instantiatedMethodType。
     */
    public static KotlinLambdaMeta ofGetterMethodName(String getterName, SerializedLambda lambda, ClassLoader classLoader) {
        return new KotlinLambdaMeta(getterName, classFromLambda(lambda, classLoader));
    }

    @Override
    public String getImplMethodName() {
        return implMethodName;
    }

    @Override
    public Class<?> getInstantiatedClass() {
        return instantiatedClass;
    }

    /**
     * 属性引用的 name 是属性名，需补成 getter 形式（{@code name -> getName}），以便 PropertyNamer.methodToProperty 还原；
     * 方法引用的 name 已是 getter 方法名（{@code getName}），原样返回。
     */
    private static String getterName(String name, boolean isProperty) {
        if (!isProperty) {
            return name;
        }
        return "get" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }

    private static String callableName(Object callable) {
        try {
            // setAccessible: Kotlin (<1.4) 生成的引用类是非 public 的，反射调用其 public 方法仍需放开访问
            Method getName = callable.getClass().getMethod("getName");
            return (String) ReflectionKit.setAccessible(getName).invoke(callable);
        } catch (ReflectiveOperationException e) {
            throw new MybatisPlusException(e);
        }
    }

    /**
     * 通过 {@code getOwner().getJClass()} 取得引用归属的实体类。
     * {@code getJClass()} 在有无 kotlin-reflect 时分别由 KClassImpl / ClassReference 提供，均可用。
     */
    private static Class<?> ownerClass(Object callable) {
        try {
            Method getOwner = callable.getClass().getMethod("getOwner");
            Object owner = ReflectionKit.setAccessible(getOwner).invoke(callable);
            Method getJClass = owner.getClass().getMethod("getJClass");
            return (Class<?>) ReflectionKit.setAccessible(getJClass).invoke(owner);
        } catch (ReflectiveOperationException e) {
            throw new MybatisPlusException(e);
        }
    }

    private static Class<?> classFromLambda(SerializedLambda lambda, ClassLoader classLoader) {
        String instantiatedMethodType = lambda.getInstantiatedMethodType();
        String instantiatedType = instantiatedMethodType.substring(2, instantiatedMethodType.indexOf(StringPool.SEMICOLON)).replace(StringPool.SLASH, StringPool.DOT);
        return ClassUtils.toClassConfident(instantiatedType, classLoader);
    }

    private static ClassLoader classLoaderOf(Object o) {
        return o.getClass().getClassLoader();
    }

    private static boolean isKotlinProperty(Object callable) {
        for (Class<?> clazz = callable.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
            if (implementsInterface(clazz, KOTLIN_KPROPERTY)) {
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

}
