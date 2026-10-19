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
package com.baomidou.mybatisplus.core.toolkit.sql;

/**
 * Sql 模板工具类（性能一般能达到 StringBuilder 的 90%~100% 保留了代码的可读性提升性能）
 *
 * @author hubin
 * @since 2026-06-29
 */
@FunctionalInterface
public interface SqlTemplate {

    String format(Object... args);

    static SqlTemplate of(SqlTemplate template) {
        return template;
    }

    static <A, B> SqlTemplate2<A, B> of2(SqlTemplate2<A, B> fn) {
        return fn;
    }

    static <A, B, C> SqlTemplate3<A, B, C> of3(SqlTemplate3<A, B, C> fn) {
        return fn;
    }

    static <A, B, C, D> SqlTemplate4<A, B, C, D> of4(SqlTemplate4<A, B, C, D> fn) {
        return fn;
    }

    static <A, B, C, D, E> SqlTemplate5<A, B, C, D, E> of5(SqlTemplate5<A, B, C, D, E> fn) {
        return fn;
    }

    static <A, B, C, D, E, F> SqlTemplate6<A, B, C, D, E, F> of6(SqlTemplate6<A, B, C, D, E, F> fn) {
        return fn;
    }

    @FunctionalInterface
    interface SqlTemplate2<A, B> {

        String format(A a, B b);
    }

    @FunctionalInterface
    interface SqlTemplate3<A, B, C> {

        String format(A a, B b, C c);
    }

    @FunctionalInterface
    interface SqlTemplate4<A, B, C, D> {

        String format(A a, B b, C c, D d);
    }

    @FunctionalInterface
    interface SqlTemplate5<A, B, C, D, E> {

        String format(A a, B b, C c, D d, E e);
    }

    @FunctionalInterface
    interface SqlTemplate6<A, B, C, D, E, F> {

        String format(A a, B b, C c, D d, E e, F f);
    }
}
