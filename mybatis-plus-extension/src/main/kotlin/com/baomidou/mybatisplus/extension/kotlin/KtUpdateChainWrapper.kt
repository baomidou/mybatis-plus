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

    override fun set(condition: Boolean, column: KProperty<*>, value: Any?): KtUpdateChainWrapper<T> {
        wrapperChildren.set(condition, column, value)
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
