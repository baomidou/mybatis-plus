package com.baomidou.mybatisplus.extension.kotlin

import com.baomidou.mybatisplus.core.conditions.query.Query
import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo
import com.baomidou.mybatisplus.extension.conditions.AbstractChainWrapper
import com.baomidou.mybatisplus.extension.conditions.query.ChainQuery
import java.util.function.Predicate
import kotlin.reflect.KProperty

/**
 * @author FlyInWind
 * @since 2020-10-18
 */
class KtQueryChainWrapper<T : Any>(
    internal val baseMapper: BaseMapper<T>
) : AbstractChainWrapper<T, KProperty<*>, KtQueryChainWrapper<T>, KtQueryWrapper<T>>(),
    ChainQuery<T>, Query<KtQueryChainWrapper<T>, T, KProperty<*>> {


    constructor(baseMapper: BaseMapper<T>, entityClass: Class<T>) : this(baseMapper) {
        super.wrapperChildren = KtQueryWrapper(entityClass)
    }

    constructor(baseMapper: BaseMapper<T>, entity: T) : this(baseMapper) {
        super.wrapperChildren = KtQueryWrapper(entity)
    }

    override fun select(vararg columns: KProperty<*>): KtQueryChainWrapper<T> {
        wrapperChildren.select(*columns)
        return typedThis
    }

    override fun select(entityClass: Class<T>, predicate: Predicate<TableFieldInfo>): KtQueryChainWrapper<T> {
        wrapperChildren.select(entityClass, predicate)
        return typedThis
    }

    override fun getBaseMapper(): BaseMapper<T> {
        return baseMapper
    }

}
