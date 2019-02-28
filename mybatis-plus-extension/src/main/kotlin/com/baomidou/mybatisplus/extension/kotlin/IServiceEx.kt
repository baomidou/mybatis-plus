/*
 * Copyright (c) 2011-2016, hubin (jobob@qq.com).
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
import kotlin.reflect.jvm.javaType

/**
 * 获取 IService 的实现类 ServiceImpl 上的第二个范型参数的 Class
 *
 * @return 获取到的 Class
 */
@Suppress("UNCHECKED_CAST")
private fun <T : Any> IService<T>.entityClass(): Class<T> =
        // TODO cache
        // TODO ServiceImpl 不一定是直接超类
        (this::class.supertypes[0].arguments[1].type?.javaType) as Class<T>

/**
 * Kotlin 用的 Lambda 式链式查询
 *
 * **如实现类并未直接继承 ServiceImpl，本方法会报错，这个问题将在近期解决**
 */
fun <T : Any> IService<T>.ktQuery() = KtQueryChainWrapper(this.baseMapper, this.entityClass())

/**
 * Kotlin 用的 Lambda 式链式更改
 *
 * **如实现类并未直接继承 ServiceImpl，本方法会报错，这个问题将在近期解决**
 */
fun <T : Any> IService<T>.ktUpdate() = KtUpdateChainWrapper(this.baseMapper, this.entityClass())
