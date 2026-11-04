package com.baomidou.mybatisplus.extension.kotlin

import com.baomidou.mybatisplus.core.conditions.interfaces.Func
import kotlin.reflect.KProperty1

/**
 * @Author lidiwei
 * @CreateTime 2025/7/31 16:20
 */
interface FuncDsl<Children, T> : Func<Children, KProperty1<in T, *>> {
    val KProperty1<in T, *>.isNull: Children?
        get() = this@FuncDsl.isNull(this)

    val KProperty1<in T, *>.isNotNull: Children?
        get() = this@FuncDsl.isNotNull(this)

    infix fun KProperty1<in T, *>.isIn(coll: Collection<*>?) = this@FuncDsl.`in`(this, coll)
    infix fun KProperty1<in T, *>.isIn(array: Array<*>) = this@FuncDsl.`in`(this, array)

    infix fun KProperty1<in T, *>.isNotIn(coll: Collection<*>?) = this@FuncDsl.notIn(this, coll)
    infix fun KProperty1<in T, *>.isNotIn(array: Array<*>) = this@FuncDsl.notIn(this, array)

}
