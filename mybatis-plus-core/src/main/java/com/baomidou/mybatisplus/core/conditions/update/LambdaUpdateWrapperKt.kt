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
package com.baomidou.mybatisplus.core.conditions.update

import com.baomidou.mybatisplus.core.conditions.AbstractLambdaWrapperKt
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils
import com.baomidou.mybatisplus.core.toolkit.StringPool
import com.baomidou.mybatisplus.core.toolkit.sql.SqlUtils
import java.util.concurrent.atomic.AtomicInteger
import java.util.stream.Collectors.joining
import kotlin.reflect.KProperty


/**
 *
 *
 * Kotlin Lambda 更新封装
 *
 *
 * @author yangyuhan
 * @since 2018-11-02
 */
class LambdaUpdateWrapperKt<T> internal constructor(entity: T, paramNameSeq: AtomicInteger, paramNameValuePairs: Map<String, Any>,
                                                    mergeSegments: MergeSegments) : AbstractLambdaWrapperKt<T, LambdaUpdateWrapperKt<T>>() {

    /**
     * SQL 更新字段内容，例如：name='1',age=2
     */
    private val sqlSet = ArrayList<String>()

    init {
        this.entity = entity
        this.paramNameSeq = paramNameSeq
        this.paramNameValuePairs = paramNameValuePairs
        this.expression = mergeSegments
    }

    override fun getSqlSet(): String? {
        return if (CollectionUtils.isEmpty(sqlSet)) {
            null
        } else SqlUtils.stripSqlInjection(sqlSet.stream().collect(joining(StringPool.COMMA)))
    }

    operator fun set(column: KProperty<*>, `val`: Any): LambdaUpdateWrapperKt<T> {
        return this.set(true, column, `val`)
    }

    operator fun set(condition: Boolean, column: KProperty<*>, `val`: Any): LambdaUpdateWrapperKt<T> {
        if (condition) {
            sqlSet.add(String.format("%s=%s", columnToString(column), formatSql("{0}", `val`)))
        }
        return typedThis
    }

    override fun instance(paramNameSeq: AtomicInteger, paramNameValuePairs: Map<String, Any>): LambdaUpdateWrapperKt<T> {
        return LambdaUpdateWrapperKt(entity, paramNameSeq, paramNameValuePairs, MergeSegments())
    }
}
