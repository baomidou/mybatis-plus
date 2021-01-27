/*
 * Copyright (c) 2011-2021, baomidou (jobob@qq.com).
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
import kotlin.reflect.KProperty

/**
 * @author FlyInWind
 * @since 2020-10-18
 */
class KtUpdateChainWrapper<T : Any>(
    internal val baseMapper: BaseMapper<T>
) : AbstractChainWrapper<T, KProperty<*>, KtUpdateChainWrapper<T>, KtUpdateWrapper<T>>(),
    ChainUpdate<T>, Update<KtUpdateChainWrapper<T>, KProperty<*>> {


    constructor(baseMapper: BaseMapper<T>, entityClass: Class<T>) : this(baseMapper) {
        super.wrapperChildren = KtUpdateWrapper(entityClass)
    }

    constructor(baseMapper: BaseMapper<T>, entity: T) : this(baseMapper) {
        super.wrapperChildren = KtUpdateWrapper(entity)
    }

    override fun set(condition: Boolean, column: KProperty<*>, value: Any?, mapping: String?): KtUpdateChainWrapper<T> {
        wrapperChildren.set(condition, column, value, mapping)
        return typedThis
    }

    override fun setSql(condition: Boolean, sql: String): KtUpdateChainWrapper<T> {
        wrapperChildren.setSql(condition, sql)
        return typedThis
    }

    override fun getBaseMapper(): BaseMapper<T> {
        return baseMapper
    }

}
