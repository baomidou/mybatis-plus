/*
 * Copyright (c) 2011-2020, hubin (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.extension.kotlin

import com.baomidou.mybatisplus.core.conditions.AbstractWrapper
import com.baomidou.mybatisplus.core.toolkit.ExceptionUtils
import com.baomidou.mybatisplus.core.toolkit.LambdaUtils
import com.baomidou.mybatisplus.core.toolkit.StringPool
import com.baomidou.mybatisplus.core.toolkit.support.ColumnCache
import java.util.*
import java.util.stream.Collectors.joining
import kotlin.reflect.KProperty

/**
 * Lambda 语法使用 Wrapper
 * 统一处理解析 lambda 获取 column
 *
 * @author yangyuhan
 * @since 2018-11-07
 */
abstract class AbstractKtWrapper<T, This : AbstractKtWrapper<T, This>> : AbstractWrapper<T, KProperty<*>, This>() {

    private var columnMap: Map<String, ColumnCache>? = null

    override fun initEntityClass() {
        super.initEntityClass()
        columnMap = LambdaUtils.getColumnMap(this.entityClass.name)
    }

    override fun columnsToString(vararg columns: KProperty<*>): String {
        return columnsToString(true, *columns)
    }

    fun columnsToString(onlyColumn: Boolean, vararg columns: KProperty<*>): String {
        return Arrays.stream(columns).map { i -> columnToString(i, onlyColumn) }.collect(joining(StringPool.COMMA))
    }

    override fun columnToString(kProperty: KProperty<*>): String? {
        return columnToString(kProperty, true)
    }

    fun columnToString(kProperty: KProperty<*>, onlyColumn: Boolean): String? {
        return Optional.ofNullable(columnMap?.get(kProperty.name.toUpperCase(Locale.ENGLISH)))
            .map(if (onlyColumn) ColumnCache::getColumn else ColumnCache::getColumnSelect)
            .orElseThrow { ExceptionUtils.mpe("your property named %s cannot find the corresponding database column name!", kProperty.name) }
    }
}
