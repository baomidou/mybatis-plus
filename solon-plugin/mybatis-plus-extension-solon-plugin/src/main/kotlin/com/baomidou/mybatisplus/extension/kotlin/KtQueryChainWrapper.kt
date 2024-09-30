/*
 * Copyright (c) 2011-2024, baomidou (jobob@qq.com).
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
package com.baomidou.mybatisplus.extension.kotlin

import com.baomidou.mybatisplus.core.conditions.query.Query
import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo
import com.baomidou.mybatisplus.extension.conditions.AbstractChainWrapper
import com.baomidou.mybatisplus.extension.conditions.query.ChainQuery
import java.util.function.Predicate
import kotlin.reflect.KProperty1

/**
 * @author FlyInWind
 * @since 2020-10-18
 */
@Suppress("serial")
open class KtQueryChainWrapper<T : Any>(
    internal val baseMapper: BaseMapper<T>?
) : AbstractChainWrapper<T, KProperty1<in T, *>, KtQueryChainWrapper<T>, KtQueryWrapper<T>>(),
    ChainQuery<T>, Query<KtQueryChainWrapper<T>, T, KProperty1<in T, *>> {

    constructor(baseMapper: BaseMapper<T>, entityClass: Class<T>) : this(baseMapper) {
        super.wrapperChildren = KtQueryWrapper(entityClass)
    }

    constructor(baseMapper: BaseMapper<T>, entity: T) : this(baseMapper) {
        super.wrapperChildren = KtQueryWrapper(entity)
    }

    constructor(entityClass: Class<T>) : this(null) {
        super.wrapperChildren = KtQueryWrapper(entityClass)
    }

    constructor(entity: T) : this(null) {
        super.wrapperChildren = KtQueryWrapper(entity)
        super.setEntityClass(entity.javaClass)
    }

    override fun select(condition: Boolean, columns: MutableList<KProperty1<in T, *>>): KtQueryChainWrapper<T> {
        wrapperChildren.select(condition, columns)
        return typedThis
    }

    override fun select(entityClass: Class<T>, predicate: Predicate<TableFieldInfo>): KtQueryChainWrapper<T> {
        wrapperChildren.select(entityClass, predicate)
        return typedThis
    }

    override fun getBaseMapper(): BaseMapper<T>? {
        return baseMapper
    }

    override fun getEntityClass(): Class<T> {
        return super.wrapperChildren.entityClass
    }
}
