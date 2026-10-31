package com.baomidou.mybatisplus.extension.kotlin

/**
 * @Author lidiwei
 * @CreateTime 2025/7/31 18:09
 */

inline fun <reified T : Any> buildKtQueryWrapper(): KtQueryWrapper<T> = KtQueryWrapper(T::class)

inline fun <reified T : Any> buildKtUpdateWrapper(): KtUpdateWrapper<T> = KtUpdateWrapper(T::class)
