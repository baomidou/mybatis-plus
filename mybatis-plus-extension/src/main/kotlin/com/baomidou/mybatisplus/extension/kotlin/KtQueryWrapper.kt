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

import com.baomidou.mybatisplus.core.conditions.query.Query
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo
import com.baomidou.mybatisplus.core.toolkit.ArrayUtils
import com.baomidou.mybatisplus.core.toolkit.TableInfoHelper
import java.util.concurrent.atomic.AtomicInteger
import java.util.function.Predicate
import kotlin.reflect.KProperty

/**
 * Kotlin Lambda 语法使用 Wrapper
 *
 * @author yangyuhan
 * @since 2018-11-02
 */
class KtQueryWrapper<T : Any> : AbstractKtWrapper<T, KtQueryWrapper<T>>, Query<KtQueryWrapper<T>, T, KProperty<*>> {

    /**
     * 查询字段
     */
    private var sqlSelect: String? = null

    constructor(entity: T) {
        this.setEntity(entity)
        this.initNeed()
    }

    constructor(entityClass: Class<T>) {
        this.entityClass = entityClass
        this.initEntityClass()
        this.initNeed()
    }

    internal constructor(entity: T, entityClass: Class<T>?, sqlSelect: String?, paramNameSeq: AtomicInteger,
                         paramNameValuePairs: Map<String, Any>, mergeSegments: MergeSegments) {
        this.entity = entity
        this.entityClass = entityClass
        this.paramNameSeq = paramNameSeq
        this.paramNameValuePairs = paramNameValuePairs
        this.expression = mergeSegments
        this.sqlSelect = sqlSelect
    }

    /**
     * SELECT 部分 SQL 设置
     *
     * @param columns 查询字段
     */
    @SafeVarargs
    override fun select(vararg columns: KProperty<*>): KtQueryWrapper<T> {
        if (ArrayUtils.isNotEmpty(columns)) {
            this.sqlSelect = this.columnsToString(false, *columns)
        }
        return typedThis
    }

    override fun select(predicate: Predicate<TableFieldInfo>): KtQueryWrapper<T> {
        return select(entityClass, predicate)
    }

    /**
     * 过滤查询的字段信息(主键除外!)
     *
     * 例1: 只要 java 字段名以 "test" 开头的              -> select(i -> i.getProperty().startsWith("test"))
     *
     * 例2: 只要 java 字段属性是 CharSequence 类型的       -> select(TableFieldInfo::isCharSequence)
     *
     * 例3: 只要 java 字段没有填充策略的                   -> select(i -> i.getFieldFill == FieldFill.DEFAULT)
     *
     * 例4: 要全部字段                                   -> select(i -> true)
     *
     * 例5: 只要主键字段                                 -> select(i -> false)
     *
     *
     * @param predicate 过滤方式
     * @return this
     */
    override fun select(entityClass: Class<T>, predicate: Predicate<TableFieldInfo>): KtQueryWrapper<T> {
        this.entityClass = entityClass
        this.sqlSelect = TableInfoHelper.getTableInfo(checkEntityClass).chooseSelect(predicate)
        return typedThis
    }

    override fun getSqlSelect(): String? {
        return sqlSelect
    }

    /**
     * 用于生成嵌套 sql
     *
     * 故 sqlSelect 不向下传递
     */
    override fun instance(): KtQueryWrapper<T> {
        return KtQueryWrapper(entity, entityClass, null, paramNameSeq, paramNameValuePairs, MergeSegments())
    }
}
