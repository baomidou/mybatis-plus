package com.baomidou.mybatisplus.extension.kotlin

import com.baomidou.mybatisplus.extension.toolkit.Db
import kotlin.reflect.KClass

/**
 * @Author lidiwei
 * @CreateTime 2025/7/31 16:15
 */

inline fun <reified T : Any> ktQuery(noinline query: (KtQueryChainWrapper<T>.() -> Unit)? = null): KtQueryChainWrapper<T> =
    Db.ktQuery<T>(T::class.java).also { query?.invoke(it) }

inline fun <reified T : Any, C : KClass<T>> C.ktQuery(noinline query: (KtQueryChainWrapper<T>.() -> Unit)? = null): KtQueryChainWrapper<T> =
    Db.ktQuery<T>(T::class.java).also { query?.invoke(it) }
