package com.baomidou.mybatisplus.extension.kotlin

import com.baomidou.mybatisplus.core.conditions.interfaces.Compare
import kotlin.reflect.KProperty1

/**
 * @Author lidiwei
 * @CreateTime 2025/7/31 16:18
 */
interface CompareDsl<Children, T> : Compare<Children, KProperty1<in T, *>> {

    infix fun <V> KProperty1<in T, V>.eq(value: V?) = this@CompareDsl.eq(this, value)
    infix fun <V> KProperty1<in T, V>.ne(value: V?) = this@CompareDsl.ne(this, value)
    infix fun <V> KProperty1<in T, V>.gt(value: V?) = this@CompareDsl.gt(this, value)
    infix fun <V> KProperty1<in T, V>.ge(value: V?) = this@CompareDsl.ge(this, value)
    infix fun <V> KProperty1<in T, V>.lt(value: V?) = this@CompareDsl.lt(this, value)
    infix fun <V> KProperty1<in T, V>.le(value: V?) = this@CompareDsl.le(this, value)
    infix fun <A, B> KProperty1<in T, *>.between(value: Pair<A, B>) =
        this@CompareDsl.between(this, value.first, value.second)

    infix fun <A, B> KProperty1<in T, *>.notBetween(value: Pair<A, B>) =
        this@CompareDsl.notBetween(this, value.first, value.second)

    infix fun <V> KProperty1<in T, V>.like(value: V?) = this@CompareDsl.like(this, value)
    infix fun <V> KProperty1<in T, V>.notLike(value: V?) = this@CompareDsl.notLike(this, value)
    infix fun <V> KProperty1<in T, V>.likeLeft(value: V?) = this@CompareDsl.likeLeft(this, value)
    infix fun <V> KProperty1<in T, V>.notLikeLeft(value: V?) = this@CompareDsl.notLikeLeft(this, value)
    infix fun <V> KProperty1<in T, V>.likeRight(value: V?) = this@CompareDsl.likeRight(this, value)
    infix fun <V> KProperty1<in T, V>.notLikeRight(value: V?) = this@CompareDsl.notLikeRight(this, value)
}
