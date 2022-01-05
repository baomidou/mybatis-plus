package com.baomidou.mybatisplus.extension.toolkit;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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
        return list2Map(SqlHelper.getMapper(getType(sFunction)).selectList(wrapper), sFunction, Function.identity(), peeks);
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
        return list2Map(SqlHelper.getMapper(getType(sFunction)).selectList(wrapper), sFunction, Function.identity(), isParallel, peeks);
    }

    /**
     * ignore
     */
    @SafeVarargs
    public static <E, A, P> Map<A, P> map(LambdaQueryWrapper<E> wrapper, SFunction<E, A> keyFunc, SFunction<E, P> valueFunc, Consumer<E>... peeks) {
        return list2Map(SqlHelper.getMapper(getType(keyFunc)).selectList(wrapper), keyFunc, valueFunc, peeks);
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
        return list2Map(SqlHelper.getMapper(getType(keyFunc)).selectList(wrapper), keyFunc, valueFunc, isParallel, peeks);
    }

    /**
     * ignore
     */
    @SafeVarargs
    public static <E, A> Map<A, List<E>> group(LambdaQueryWrapper<E> wrapper, SFunction<E, A> sFunction, Consumer<E>... peeks) {
        return listGroupBy(SqlHelper.getMapper(getType(sFunction)).selectList(wrapper), sFunction, peeks);
    }

    /**
     * 传入Wrappers和key，从数据库中根据条件查询出对应的列表，封装成Map
     *
     * @param wrapper    条件构造器
     * @param sFunction  分组依据
     * @param isParallel 是否并行流
     * @param peeks      后续操作
     * @param <E>        实体类型
     * @param <A>        实体中的属性类型
     * @return Map<实体中的属性, List < 实体>>
     */
    @SafeVarargs
    public static <E, A> Map<A, List<E>> group(LambdaQueryWrapper<E> wrapper, SFunction<E, A> sFunction, boolean isParallel, Consumer<E>... peeks) {
        return listGroupBy(SqlHelper.getMapper(getType(sFunction)).selectList(wrapper), sFunction, isParallel, peeks);
    }

    /**
     * ignore
     */
    @SafeVarargs
    public static <E, A> List<A> list(LambdaQueryWrapper<E> wrapper, SFunction<E, A> sFunction, Consumer<E>... peeks) {
        return list2List(SqlHelper.getMapper(getType(sFunction)).selectList(wrapper), sFunction, peeks);
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
        return list2List(SqlHelper.getMapper(getType(sFunction)).selectList(wrapper), sFunction, isParallel, peeks);
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
    public static <A, E> Map<A, List<E>> listGroupBy(List<E> list, SFunction<E, A> sFunction, Consumer<E>... peeks) {
        return listGroupBy(list, sFunction, false, peeks);
    }

    /**
     * 对list进行groupBy操作
     *
     * @param list       数据
     * @param sFunction  分组的key，依据
     * @param isParallel 是否并行流
     * @param peeks      封装成map时可能需要的后续操作，不需要可以不传
     * @param <E>        实体类型
     * @param <A>        实体中的属性类型
     * @return Map<实体中的属性, List < 实体>>
     */
    @SafeVarargs
    public static <A, E> Map<A, List<E>> listGroupBy(List<E> list, SFunction<E, A> sFunction, boolean isParallel, Consumer<E>... peeks) {
        return peekStream(list, isParallel, peeks).collect(HashMap::new, (m, v) -> {
            A key = Optional.ofNullable(v).map(sFunction).orElse(null);
            List<E> values = m.computeIfAbsent(key, k -> new ArrayList<>(list.size()));
            values.add(v);
        }, (totalMap, nowMap) -> nowMap.forEach((key, v) -> {
            List<E> values = totalMap.computeIfAbsent(key, k -> new ArrayList<>(list.size()));
            values.addAll(v);
        }));
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
