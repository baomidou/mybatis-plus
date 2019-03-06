/*
 * Copyright (c) 2011-2019, hubin (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.extension.kotlin

import com.baomidou.mybatisplus.extension.kotlin.chain.KtQueryChainWrapper
import com.baomidou.mybatisplus.extension.kotlin.chain.KtUpdateChainWrapper
import com.baomidou.mybatisplus.extension.service.IService
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import kotlin.reflect.jvm.javaType

/**
 * 每个 IService 对于实现类 ServiceImpl 上的第二个范型参数的 Class 的缓存
 *
 * 一般而言 Service 的实现类不会太多，也不会出现类卸载的情况，因此无需考虑清除的问题
 */
private val entityClassCache: MutableMap<IService<*>, Class<*>> = mutableMapOf()

/**
 * 获取 IService 的实现类 ServiceImpl 上的第二个范型参数的 Class
 *
 * @return 获取到的 Class
 */
@Suppress("UNCHECKED_CAST")
private fun <T : Any> IService<T>.entityClass() =
        (entityClassCache.getOrPut(this) {
            // 理论上不可能找不到，除非用户的代码直接就没继承 ServiceImpl
            // 理论上 as 转换时不可能出现异常，除非用的不知道什么鬼家的 JVM？
            (this::class.supertypes.first { it == ServiceImpl::class }.arguments[1].type?.javaType) as Class<*>
        }) as Class<T>

/**
 * 构建一个 Kotlin 用的 Query Lambda Wrapper，且这个 Wrapper 支持直接以一些常见的查询结束
 *
 * 使用示例：
 *
 * ```kotlin
 *
 * @Service
 * class UserServiceImpl : ServiceImpl<UserMapper, User>(), IUserService {
 *     override fun selectByUsername(username: String): User? {
 *         return this.ktQuery()
 *                 .eq(User::username, username)
 *                 .one()
 *     }
 * }
 * ```
 *
 * @return Wrapper 的实例
 */
fun <T : Any> IService<T>.ktQuery() = KtQueryChainWrapper(this.baseMapper, this.entityClass())

/**
 * 构建一个 Kotlin 用的 Update Lambda Wrapper，且这个 Wrapper 支持直接以一些常见的更新结束
 *
 * 使用示例：
 *
 * ```kotlin
 *
 * @Service
 * class UserServiceImpl : ServiceImpl<UserMapper, User>(), IUserService {
 *     override fun removeByUsername(username: String): User? {
 *         return this.ktUpdate()
 *                 .eq(User::username, username)
 *                 .remove()
 *     }
 * }
 * ```
 *
 * @return Wrapper 的实例
 */
fun <T : Any> IService<T>.ktUpdate() = KtUpdateChainWrapper(this.baseMapper, this.entityClass())

/**
 * 构建一个给 Kotlin 用的 Query Lambda Wrapper
 *
 * 使用示例：
 *
 * ```kotlin
 *
 * @Service
 * class UserServiceImpl : ServiceImpl<UserMapper, User>(), IUserService {
 *     override fun removeByUsername(username: String): User? {
 *         var wrapper = this.ktQueryWrapper();
 *         wrapper.eq(User::username, username)
 *         this.getOne(wrapper)
 *     }
 * }
 * ```
 * @return Wrapper 的实例
 */
fun <T : Any> IService<T>.ktQueryWrapper() = KtQueryWrapper(this.entityClass())

/**
 * 构建一个给 Kotlin 用的 Update Lambda Wrapper
 *
 * 使用示例：
 *
 * ```kotlin
 *
 * @Service
 * class UserServiceImpl : ServiceImpl<UserMapper, User>(), IUserService {
 *     override fun removeByUsername(username: String): User? {
 *         var wrapper = this.ktUpdateWrapper();
 *         wrapper.eq(User::username, username)
 *         this.remove(wrapper)
 *     }
 * }
 * ```
 * @return Wrapper 的实例
 */
fun <T : Any> IService<T>.ktUpdateWrapper() = KtUpdateWrapper(this.entityClass())
