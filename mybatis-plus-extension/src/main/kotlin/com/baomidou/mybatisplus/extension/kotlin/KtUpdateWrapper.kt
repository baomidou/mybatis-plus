/*
 * Copyright (c) 2011-2020, baomidou (jobob@qq.com).
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

import com.baomidou.mybatisplus.core.conditions.SharedString
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments
import com.baomidou.mybatisplus.core.conditions.update.Update
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils
import com.baomidou.mybatisplus.core.toolkit.StringPool
import com.baomidou.mybatisplus.core.toolkit.StringUtils
import com.baomidou.mybatisplus.core.toolkit.support.ColumnCache
import java.util.concurrent.atomic.AtomicInteger
import java.util.stream.Collectors.joining
import kotlin.reflect.KProperty

/**
 * Kotlin Lambda 更新封装
 *
 * @author yangyuhan
 * @since 2018-11-02
 */
class KtUpdateWrapper<T : Any> : AbstractKtWrapper<T, KtUpdateWrapper<T>>, Update<KtUpdateWrapper<T>, KProperty<*>> {

    /**
     * SQL 更新字段内容，例如：name='1', age=2
     */
    private val sqlSet = ArrayList<String>()

    constructor(entity: T) {
        this.entity = entity
        super.initNeed()
    }

    constructor(entityClass: Class<T>) {
        this.entityClass = entityClass
        super.initNeed()
    }

    internal constructor(entity: T?, paramNameSeq: AtomicInteger, paramNameValuePairs: Map<String, Any>,
                         columnMap: Map<String, ColumnCache>, lastSql: SharedString, sqlComment: SharedString,
                         sqlFirst: SharedString) {
        this.entity = entity
        this.paramNameSeq = paramNameSeq
        this.paramNameValuePairs = paramNameValuePairs
        this.expression = MergeSegments()
        this.columnMap = columnMap
        this.lastSql = lastSql
        this.sqlComment = sqlComment
        this.sqlFirst = sqlFirst
    }

    override fun getSqlSet(): String? {
        return if (CollectionUtils.isEmpty(sqlSet)) null
        else sqlSet.stream().collect(joining(StringPool.COMMA))
    }

    override fun setSql(condition: Boolean, sql: String): KtUpdateWrapper<T> {
        if (condition && StringUtils.isNotBlank(sql)) {
            sqlSet.add(sql)
        }
        return typedThis
    }

    override fun set(condition: Boolean, column: KProperty<*>, value: Any?): KtUpdateWrapper<T> {
        if (condition) {
            sqlSet.add(String.format("%s=%s", columnToString(column), formatSql("{0}", value)))
        }
        return typedThis
    }

    override fun instance(): KtUpdateWrapper<T> {
        return KtUpdateWrapper(entity, paramNameSeq, paramNameValuePairs, columnMap,
            SharedString.emptyString(), SharedString.emptyString(), SharedString.emptyString())
    }

    override fun clear() {
        super.clear()
        sqlSet.clear()
    }
}
