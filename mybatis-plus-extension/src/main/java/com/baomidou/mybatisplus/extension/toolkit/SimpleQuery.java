package com.baomidou.mybatisplus.extension.toolkit;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * simple-query 让简单的查询更简单
 *
 * @author <achao1441470436@gmail.com>
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
     * 传入Wrappers和key，从数据库中根据条件查询出对应的列表，封装成Map
     *
     * @param wrapper   条件构造器
     * @param sFunction key
     * @param peeks     封装成map时可能需要的后续操作，不需要可以不传
     * @param <E>       实体类型
     * @param <A>       实体中的属性类型
     * @return Map<实体中的属性, 实体>
     */
    @SafeVarargs
    public static <E, A> Map<A, E> keyMap(LambdaQueryWrapper<E> wrapper, SFunction<E, A> sFunction, Consumer<E>... peeks) {
        return list2Map(SqlHelper.getMapper(getType(sFunction)).selectList(wrapper), sFunction, Function.identity(), peeks);
    }

    /**
     * 传入Wrappers和key，从数据库中根据条件查询出对应的列表，封装成Map
     *
     * @param wrapper   条件构造器
     * @param keyFunc   key
     * @param valueFunc value
     * @param peeks     封装成map时可能需要的后续操作，不需要可以不传
     * @param <E>       实体类型
     * @param <A>       实体中的属性类型
     * @param <P>       实体中的属性类型
     * @return Map<实体中的属性, 实体>
     */
    @SafeVarargs
    public static <E, A, P> Map<A, P> map(LambdaQueryWrapper<E> wrapper, SFunction<E, A> keyFunc, SFunction<E, P> valueFunc, Consumer<E>... peeks) {
        return list2Map(SqlHelper.getMapper(getType(keyFunc)).selectList(wrapper), keyFunc, valueFunc, peeks);
    }

    /**
     * 传入Wrappers和key，从数据库中根据条件查询出对应的列表，封装成Map
     *
     * @param wrapper   条件构造器
     * @param sFunction 分组依据
     * @param peeks     后续操作
     * @param <E>       实体类型
     * @param <A>       实体中的属性类型
     * @return Map<实体中的属性, List < 实体>>
     */
    @SafeVarargs
    public static <E, A> Map<A, List<E>> group(LambdaQueryWrapper<E> wrapper, SFunction<E, A> sFunction, Consumer<E>... peeks) {
        return listGroupBy(SqlHelper.getMapper(getType(sFunction)).selectList(wrapper), sFunction, peeks);
    }

    /**
     * 传入wrappers和需要的某一列，从数据中根据条件查询出对应的列，转换成list
     *
     * @param wrapper   条件构造器
     * @param sFunction 需要的列
     * @param peeks     后续操作
     * @return java.util.List<A>
     * @author <achao1441470436@gmail.com>
     * @since 2021/11/9 17:59
     */
    @SafeVarargs
    public static <E, A> List<A> list(LambdaQueryWrapper<E> wrapper, SFunction<E, A> sFunction, Consumer<E>... peeks) {
        return list2List(SqlHelper.getMapper(getType(sFunction)).selectList(wrapper), sFunction, peeks);
    }


    /**
     * 对list进行map、peek操作
     *
     * @param list      数据
     * @param sFunction 需要的列
     * @param peeks     后续操作
     * @return java.util.List<A>
     * @author <achao1441470436@gmail.com>
     * @since 2021/11/9 18:01
     */
    @SafeVarargs
    public static <A, E> List<A> list2List(List<E> list, SFunction<E, A> sFunction, Consumer<E>... peeks) {
        return Stream.of(peeks).reduce(list.parallelStream(), Stream::peek, Stream::concat).map(sFunction).collect(Collectors.toList());
    }

    /**
     * 对list进行groupBy操作
     *
     * @param list      数据
     * @param sFunction 分组的key，依据
     * @param peeks     封装成map时可能需要的后续操作，不需要可以不传
     * @param <E>       实体类型
     * @param <A>       实体中的属性类型
     * @return Map<实体中的属性, List < 实体>>
     */
    @SafeVarargs
    public static <A, E> Map<A, List<E>> listGroupBy(List<E> list, SFunction<E, A> sFunction, Consumer<E>... peeks) {
        return Stream.of(peeks).reduce(list.parallelStream(), Stream::peek, Stream::concat).collect(Collectors.groupingBy(sFunction));
    }


    /**
     * list转换为map
     *
     * @param <E>     实体类型
     * @param <A>     实体中的属性类型
     * @param <P>     实体中的属性类型
     * @param list    数据
     * @param keyFunc key
     * @param peeks   封装成map时可能需要的后续操作，不需要可以不传
     * @return Map<实体中的属性, 实体>
     */
    @SafeVarargs
    public static <E, A, P> Map<A, P> list2Map(List<E> list, SFunction<E, A> keyFunc, Function<E, P> valueFunc, Consumer<E>... peeks) {
        return Stream.of(peeks).reduce(list.parallelStream(), Stream::peek, Stream::concat).collect(Collectors.toMap(keyFunc, valueFunc, (l, r) -> l));
    }


}
