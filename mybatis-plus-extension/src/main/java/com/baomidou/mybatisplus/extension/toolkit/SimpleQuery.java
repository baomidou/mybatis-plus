/*
 * Copyright (c) 2011-2024, baomidou (jobob@qq.com).
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
package com.baomidou.mybatisplus.extension.toolkit;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;

/**
 * simple-query 让简单的查询更简单
 *
 * @author VampireAchao
 * @since 2021/11/9 18:27
 */
public class SimpleQuery {
    private SimpleQuery() {
        /* Do not new me! */
    }

    /**
     * 通过lambda获取Class
     *
     * @param sFunction 可序列化的lambda
     * @param <E>       Class类型
     * @return 对应的Class
     */
    @SuppressWarnings("unchecked")
    public static <E> Class<E> getType(SFunction<E, ?> sFunction) {
        return (Class<E>) LambdaUtils.extract(sFunction).getInstantiatedClass();
    }

    /**
     * ignore
     */
    @SafeVarargs
    public static <E, A> Map<A, E> keyMap(LambdaQueryWrapper<E> wrapper, SFunction<E, A> sFunction, Consumer<E>... peeks) {
        return list2Map(Db.list(wrapper.setEntityClass(getType(sFunction))), sFunction, Function.identity(), peeks);
    }

    /**
     * 传入Wrappers和key，从数据库中根据条件查询出对应的列表，封装成Map
     *
     * @param wrapper    条件构造器
     * @param sFunction  key
     * @param isParallel 是否并行流
     * @param peeks      封装成map时可能需要的后续操作，不需要可以不传
     * @param <E>        实体类型
     * @param <A>        实体中的属性类型
     * @return Map<实体中的属性, 实体>
     */
    @SafeVarargs
    public static <E, A> Map<A, E> keyMap(LambdaQueryWrapper<E> wrapper, SFunction<E, A> sFunction, boolean isParallel, Consumer<E>... peeks) {
        return list2Map(Db.list(wrapper.setEntityClass(getType(sFunction))), sFunction, Function.identity(), isParallel, peeks);
    }

    /**
     * ignore
     */
    @SafeVarargs
    public static <E, A, P> Map<A, P> map(LambdaQueryWrapper<E> wrapper, SFunction<E, A> keyFunc, SFunction<E, P> valueFunc, Consumer<E>... peeks) {
        return list2Map(Db.list(wrapper.setEntityClass(getType(keyFunc))), keyFunc, valueFunc, peeks);
    }

    /**
     * 传入Wrappers和key，从数据库中根据条件查询出对应的列表，封装成Map
     *
     * @param wrapper    条件构造器
     * @param keyFunc    key
     * @param valueFunc  value
     * @param isParallel 是否并行流
     * @param peeks      封装成map时可能需要的后续操作，不需要可以不传
     * @param <E>        实体类型
     * @param <A>        实体中的属性类型
     * @param <P>        实体中的属性类型
     * @return Map<实体中的属性, 实体>
     */
    @SafeVarargs
    public static <E, A, P> Map<A, P> map(LambdaQueryWrapper<E> wrapper, SFunction<E, A> keyFunc, SFunction<E, P> valueFunc, boolean isParallel, Consumer<E>... peeks) {
        return list2Map(Db.list(wrapper.setEntityClass(getType(keyFunc))), keyFunc, valueFunc, isParallel, peeks);
    }

    /**
     * ignore
     */
    @SafeVarargs
    public static <E, A> Map<A, List<E>> group(LambdaQueryWrapper<E> wrapper, SFunction<E, A> sFunction, Consumer<E>... peeks) {
        return listGroupBy(Db.list(wrapper.setEntityClass(getType(sFunction))), sFunction, peeks);
    }

    /**
     * ignore
     */
    @SafeVarargs
    public static <T, K> Map<K, List<T>> group(LambdaQueryWrapper<T> wrapper, SFunction<T, K> sFunction, boolean isParallel, Consumer<T>... peeks) {
        return listGroupBy(Db.list(wrapper.setEntityClass(getType(sFunction))), sFunction, isParallel, peeks);
    }

    /**
     * ignore
     */
    @SafeVarargs
    public static <T, K, D, A> Map<K, D> group(LambdaQueryWrapper<T> wrapper, SFunction<T, K> sFunction, Collector<T, A, D> downstream, Consumer<T>... peeks) {
        return listGroupBy(Db.list(wrapper.setEntityClass(getType(sFunction))), sFunction, downstream, false, peeks);
    }

    /**
     * 传入Wrappers和key，从数据库中根据条件查询出对应的列表，封装成Map
     *
     * @param wrapper    条件构造器
     * @param sFunction  分组依据
     * @param downstream 下游操作
     * @param isParallel 是否并行流
     * @param peeks      后续操作
     * @param <T>        实体类型
     * @param <K>        实体中的分组依据对应类型，也是Map中key的类型
     * @param <D>        下游操作对应返回类型，也是Map中value的类型
     * @param <A>        下游操作在进行中间操作时对应类型
     * @return Map<实体中的属性, List < 实体>>
     */
    @SafeVarargs
    public static <T, K, D, A> Map<K, D> group(LambdaQueryWrapper<T> wrapper, SFunction<T, K> sFunction, Collector<T, A, D> downstream, boolean isParallel, Consumer<T>... peeks) {
        return listGroupBy(Db.list(wrapper.setEntityClass(getType(sFunction))), sFunction, downstream, isParallel, peeks);
    }

    /**
     * ignore
     */
    @SafeVarargs
    public static <E, A> List<A> list(LambdaQueryWrapper<E> wrapper, SFunction<E, A> sFunction, Consumer<E>... peeks) {
        return list2List(Db.list(wrapper.setEntityClass(getType(sFunction))), sFunction, peeks);
    }

    /**
     * 传入wrappers和需要的某一列，从数据中根据条件查询出对应的列，转换成list
     *
     * @param wrapper    条件构造器
     * @param sFunction  需要的列
     * @param isParallel 是否并行流
     * @param peeks      后续操作
     * @return java.util.List<A>
     * @since 2021/11/9 17:59
     */
    @SafeVarargs
    public static <E, A> List<A> list(LambdaQueryWrapper<E> wrapper, SFunction<E, A> sFunction, boolean isParallel, Consumer<E>... peeks) {
        return list2List(Db.list(wrapper.setEntityClass(getType(sFunction))), sFunction, isParallel, peeks);
    }

    /**
     * ignore
     */
    @SafeVarargs
    public static <A, E> List<A> list2List(List<E> list, SFunction<E, A> sFunction, Consumer<E>... peeks) {
        return list2List(list, sFunction, false, peeks);
    }

    /**
     * 对list进行map、peek操作
     *
     * @param list       数据
     * @param sFunction  需要的列
     * @param isParallel 是否并行流
     * @param peeks      后续操作
     * @return java.util.List<A>
     * @since 2021/11/9 18:01
     */
    @SafeVarargs
    public static <A, E> List<A> list2List(List<E> list, SFunction<E, A> sFunction, boolean isParallel, Consumer<E>... peeks) {
        return peekStream(list, isParallel, peeks).map(sFunction).collect(Collectors.toList());
    }

    /**
     * ignore
     */
    @SafeVarargs
    public static <K, T> Map<K, List<T>> listGroupBy(List<T> list, SFunction<T, K> sFunction, Consumer<T>... peeks) {
        return listGroupBy(list, sFunction, false, peeks);
    }

    /**
     * ignore
     */
    @SafeVarargs
    public static <K, T> Map<K, List<T>> listGroupBy(List<T> list, SFunction<T, K> sFunction, boolean isParallel, Consumer<T>... peeks) {
        return listGroupBy(list, sFunction, Collectors.toList(), isParallel, peeks);
    }

    /**
     * ignore
     */
    @SafeVarargs
    public static <T, K, D, A> Map<K, D> listGroupBy(List<T> list, SFunction<T, K> sFunction, Collector<T, A, D> downstream, Consumer<T>... peeks) {
        return listGroupBy(list, sFunction, downstream, false, peeks);
    }

    /**
     * 对list进行groupBy操作
     *
     * @param list       数据
     * @param sFunction  分组的key，依据
     * @param downstream 下游操作
     * @param isParallel 是否并行流
     * @param peeks      封装成map时可能需要的后续操作，不需要可以不传
     * @param <T>        实体类型
     * @param <K>        实体中的分组依据对应类型，也是Map中key的类型
     * @param <D>        下游操作对应返回类型，也是Map中value的类型
     * @param <A>        下游操作在进行中间操作时对应类型
     * @return Map<实体中的属性, List < 实体>>
     */
    @SafeVarargs
    @SuppressWarnings("unchecked")
    public static <T, K, D, A> Map<K, D> listGroupBy(List<T> list, SFunction<T, K> sFunction, Collector<T, A, D> downstream, boolean isParallel, Consumer<T>... peeks) {
        boolean hasFinished = downstream.characteristics().contains(Collector.Characteristics.IDENTITY_FINISH);
        return peekStream(list, isParallel, peeks).collect(new Collector<T, HashMap<K, A>, Map<K, D>>() {
            @Override
            public Supplier<HashMap<K, A>> supplier() {
                return HashMap::new;
            }

            @Override
            public BiConsumer<HashMap<K, A>, T> accumulator() {
                return (m, t) -> {
                    // 只此一处，和原版groupingBy修改只此一处，成功在支持下游操作的情况下支持null值
                    K key = Optional.ofNullable(t).map(sFunction).orElse(null);
                    A container = m.computeIfAbsent(key, k -> downstream.supplier().get());
                    downstream.accumulator().accept(container, t);
                };
            }

            @Override
            public BinaryOperator<HashMap<K, A>> combiner() {
                return (m1, m2) -> {
                    for (Map.Entry<K, A> e : m2.entrySet()) {
                        m1.merge(e.getKey(), e.getValue(), downstream.combiner());
                    }
                    return m1;
                };
            }

            @Override
            public Function<HashMap<K, A>, Map<K, D>> finisher() {
                return hasFinished ? i -> (Map<K, D>) i : intermediate -> {
                    intermediate.replaceAll((k, v) -> (A) downstream.finisher().apply(v));
                    @SuppressWarnings("unchecked")
                    Map<K, D> castResult = (Map<K, D>) intermediate;
                    return castResult;
                };
            }

            @Override
            public Set<Characteristics> characteristics() {
                return hasFinished ? Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.IDENTITY_FINISH)) : Collections.emptySet();
            }
        });
    }


    /**
     * ignore
     */
    @SafeVarargs
    public static <E, A, P> Map<A, P> list2Map(List<E> list, SFunction<E, A> keyFunc, Function<E, P> valueFunc, Consumer<E>... peeks) {
        return list2Map(list, keyFunc, valueFunc, false, peeks);
    }

    /**
     * list转换为map
     *
     * @param <E>        实体类型
     * @param <A>        实体中的属性类型
     * @param <P>        实体中的属性类型
     * @param list       数据
     * @param keyFunc    key
     * @param isParallel 是否并行流
     * @param peeks      封装成map时可能需要的后续操作，不需要可以不传
     * @return Map<实体中的属性, 实体>
     */
    @SafeVarargs
    public static <E, A, P> Map<A, P> list2Map(List<E> list, SFunction<E, A> keyFunc, Function<E, P> valueFunc, boolean isParallel, Consumer<E>... peeks) {
        return peekStream(list, isParallel, peeks).collect(HashMap::new, (m, v) -> m.put(keyFunc.apply(v), valueFunc.apply(v)), HashMap::putAll);
    }

    /**
     * 将list转为Stream流，然后再叠加peek操作
     *
     * @param list       数据
     * @param isParallel 是否并行流
     * @param peeks      叠加的peek操作
     * @param <E>        数据元素类型
     * @return 转换后的流
     */
    @SafeVarargs
    public static <E> Stream<E> peekStream(List<E> list, boolean isParallel, Consumer<E>... peeks) {
        if (CollectionUtils.isEmpty(list)) {
            return Stream.empty();
        }
        return Stream.of(peeks).reduce(StreamSupport.stream(list.spliterator(), isParallel), Stream::peek, Stream::concat);
    }

}
