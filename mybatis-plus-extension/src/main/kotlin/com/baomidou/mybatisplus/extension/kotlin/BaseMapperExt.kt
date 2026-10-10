package com.baomidou.mybatisplus.extension.kotlin

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.baomidou.mybatisplus.core.metadata.IPage
import org.apache.ibatis.session.ResultHandler
import kotlin.apply

/**
 * @Author lidiwei
 * @CreateTime 2025/7/31 10:13
 */

inline fun <reified T : Any> BaseMapper<T>.delete(noinline build: (KtQueryWrapper<T>.() -> Unit)? = null): Int =
    this.delete(build.toKtQueryWrapper())

inline fun <reified T : Any> BaseMapper<T>.update(
    entity: T? = null,
    noinline build: (KtUpdateWrapper<T>.() -> Unit)? = null
): Int =
    this.update(entity, build.toKtUpdateWrapper())

inline fun <reified T : Any> BaseMapper<T>.update(noinline build: (KtUpdateWrapper<T>.() -> Unit)? = null): Int =
    this.update(build.toKtUpdateWrapper())

inline fun <reified T : Any> BaseMapper<T>.selectOne(
    throwEx: Boolean = true,
    noinline build: (KtQueryWrapper<T>.() -> Unit)? = null,
): T? =
    this.selectOne(build.toKtQueryWrapper(), throwEx)

inline fun <reified T : Any> BaseMapper<T>.exists(noinline build: (KtQueryWrapper<T>.() -> Unit)? = null): Boolean =
    this.exists(build.toKtQueryWrapper())

inline fun <reified T : Any> BaseMapper<T>.selectCount(noinline build: (KtQueryWrapper<T>.() -> Unit)? = null): Long =
    this.selectCount(build.toKtQueryWrapper())

inline fun <reified T : Any> BaseMapper<T>.selectList(noinline build: (KtQueryWrapper<T>.() -> Unit)? = null): List<T> =
    this.selectList(build.toKtQueryWrapper())


inline fun <reified T : Any> BaseMapper<T>.selectList(
    page: IPage<T>,
    noinline build: (KtQueryWrapper<T>.() -> Unit)? = null,
): List<T> =
    this.selectList(page, build.toKtQueryWrapper())

inline fun <reified T : Any> BaseMapper<T>.selectList(
    noinline build: (KtQueryWrapper<T>.() -> Unit)? = null,
    resultHandler: ResultHandler<T>
) = this.selectList(build.toKtQueryWrapper(), resultHandler)

inline fun <reified T : Any> BaseMapper<T>.selectList(
    page: IPage<T>,
    noinline build: (KtQueryWrapper<T>.() -> Unit)? = null,
    resultHandler: ResultHandler<T>
) = this.selectList(page, build.toKtQueryWrapper(), resultHandler)

inline fun <reified T : Any> BaseMapper<T>.selectMaps(
    noinline build: (KtQueryWrapper<T>.() -> Unit)? = null,
): List<Map<String, Any>> = this.selectMaps(build.toKtQueryWrapper())

inline fun <reified T : Any> BaseMapper<T>.selectMaps(
    page: IPage<Map<String, Any>>? = null,
    noinline build: (KtQueryWrapper<T>.() -> Unit)? = null,
): List<Map<String, Any>> = this.selectMaps(page, build.toKtQueryWrapper())

inline fun <reified T : Any> BaseMapper<T>.selectMaps(
    noinline build: (KtQueryWrapper<T>.() -> Unit)? = null,
    resultHandler: ResultHandler<Map<String, Any>>
) = this.selectMaps(build.toKtQueryWrapper(), resultHandler)

inline fun <reified T : Any> BaseMapper<T>.selectMaps(
    page: IPage<Map<String, Any>>? = null,
    noinline build: (KtQueryWrapper<T>.() -> Unit)? = null,
    resultHandler: ResultHandler<Map<String, Any>>
) = this.selectMaps(page, build.toKtQueryWrapper(), resultHandler)

inline fun <reified T : Any, P : IPage<Map<String, Any>>> BaseMapper<T>.selectMapsPage(
    page: P? = null,
    noinline build: (KtQueryWrapper<T>.() -> Unit)? = null,
) = this.selectMapsPage(page, build.toKtQueryWrapper())

inline fun <reified T : Any> BaseMapper<T>.selectPage(
    page: IPage<T>,
    noinline build: (KtQueryWrapper<T>.() -> Unit)? = null,
): IPage<T> =
    this.selectPage(page, build.toKtQueryWrapper())

inline fun <reified T : Any> (KtQueryWrapper<T>.() -> Unit)?.toKtQueryWrapper() =
    this?.let { buildKtQueryWrapper<T>().apply(it) }

inline fun <reified T : Any> (KtUpdateWrapper<T>.() -> Unit)?.toKtUpdateWrapper() =
    this?.let { buildKtUpdateWrapper<T>().apply(it) }
