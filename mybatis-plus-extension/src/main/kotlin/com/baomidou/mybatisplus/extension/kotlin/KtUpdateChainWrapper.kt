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

import com.baomidou.mybatisplus.core.conditions.update.Update
import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.baomidou.mybatisplus.extension.conditions.AbstractChainWrapper
import com.baomidou.mybatisplus.extension.conditions.update.ChainUpdate
import kotlin.reflect.KProperty1

/**
 * @author FlyInWind
 * @since 2020-10-18
 */
@Suppress("serial")
open class KtUpdateChainWrapper<T : Any>(
    internal val baseMapper: BaseMapper<T>?
) : AbstractChainWrapper<T, KProperty1<in T, *>, KtUpdateChainWrapper<T>, KtUpdateWrapper<T>>(),
    ChainUpdate<T>, Update<KtUpdateChainWrapper<T>, KProperty1<in T, *>> {

    constructor(baseMapper: BaseMapper<T>, entityClass: Class<T>) : this(baseMapper) {
        super.wrapperChildren = KtUpdateWrapper(entityClass)
    }

    constructor(baseMapper: BaseMapper<T>, entity: T) : this(baseMapper) {
        super.wrapperChildren = KtUpdateWrapper(entity)
    }

    constructor(entityClass: Class<T>) : this(null) {
        super.wrapperChildren = KtUpdateWrapper(entityClass)
    }

    constructor(entity: T) : this(null) {
        super.wrapperChildren = KtUpdateWrapper(entity)
        super.setEntityClass(entity.javaClass)
    }

    override fun set(condition: Boolean, column: KProperty1<in T, *>, value: Any?, mapping: String?): KtUpdateChainWrapper<T> {
        wrapperChildren.set(condition, column, value, mapping)
        return typedThis
    }

    override fun setSql(condition: Boolean, setSql: String, vararg params: Any): KtUpdateChainWrapper<T> {
        wrapperChildren.setSql(condition, setSql, *params)
        return typedThis
    }

    override fun setDecrBy(condition: Boolean, column: KProperty1<in T, *>, `val`: Number): KtUpdateChainWrapper<T> {
        wrapperChildren.setDecrBy(condition, column, `val`)
        return typedThis
    }

    override fun setIncrBy(condition: Boolean, column: KProperty1<in T, *>, `val`: Number): KtUpdateChainWrapper<T> {
        wrapperChildren.setIncrBy(condition, column, `val`)
        return typedThis
    }

    override fun getBaseMapper(): BaseMapper<T>? {
        return baseMapper
    }

    override fun getEntityClass(): Class<T> {
        return super.wrapperChildren.entityClass
    }
}
