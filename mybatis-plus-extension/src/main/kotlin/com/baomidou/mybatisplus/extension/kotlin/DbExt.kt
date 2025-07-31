package com.baomidou.mybatisplus.extension.kotlin

import com.baomidou.mybatisplus.extension.toolkit.Db
import kotlin.reflect.KClass

/**
 * @Author lidiwei
 * @CreateTime 2025/7/31 16:15
 */

inline fun <reified T : Any> ktQuery(query: KtQueryChainWrapper<T>.() -> Unit): KtQueryChainWrapper<T> =
    Db.ktQuery<T>(T::class.java).apply(query)

inline fun <reified T : Any, C : KClass<T>> C.ktQuery(query: KtQueryChainWrapper<T>.() -> Unit): KtQueryChainWrapper<T> =
    Db.ktQuery<T>(T::class.java).apply(query)
