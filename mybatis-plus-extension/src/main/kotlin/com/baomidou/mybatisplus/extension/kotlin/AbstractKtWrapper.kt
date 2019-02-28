/*
 * Copyright (c) 2011-2020, hubin (jobob@qq.com).
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

import com.baomidou.mybatisplus.core.conditions.AbstractWrapper
import com.baomidou.mybatisplus.core.toolkit.LambdaUtils
import com.baomidou.mybatisplus.core.toolkit.StringPool
import com.baomidou.mybatisplus.core.toolkit.support.ColumnCache
import java.util.*
import kotlin.reflect.KProperty

/**
 * Lambda 语法使用 Wrapper
 *
 * 统一处理解析 lambda 获取 column
 *
 * @author yangyuhan,MieMie,HanChunLin
 * @since 2018-11-07
 */
abstract class AbstractKtWrapper<T, Children : AbstractKtWrapper<T, Children>> : AbstractWrapper<T, KProperty<*>, Children>() {

    /**
     * 列 Map
     */
    private lateinit var columnMap: Map<String, ColumnCache>

    /**
     * 为了兼容 [AbstractWrapper.columnToString] 的方法,重载该方法
     * 因为 Java 并不支持参数默认值，这里只能妥协
     */
    override fun columnToString(column: KProperty<*>?): String? {
        return column?.let { columnToString(column) }
    }

    /**
     * 将某一列转换为对应的数据库字符串, 将 DTO 中的字段 [kProperty] 转换为对应 SQL 语句中的形式
     * 如果 [onlyColumn] 为 true， 则会转换为 select body 的形式
     *
     * <pre>
     *     @TableField("user_id")
     *     var userId: String? = null
     *
     *     assert("user_id" == columnToString(::userId, true))
     *     assert("user_id AS "userId"" == columnToString(::userId, false))
     *</pre>
     *
     */
    fun columnToString(kProperty: KProperty<*>, onlyColumn: Boolean = true): String? {
        if (!::columnMap.isInitialized) {
            columnMap = LambdaUtils.getColumnMap(this.checkEntityClass.name)
        }
        return columnMap[kProperty.name.toUpperCase(Locale.ENGLISH)]?.let { if (onlyColumn) it.column else it.columnSelect }
    }

    /**
     * 批量处理传入的属性，正常情况下的输出就像：
     *
     * "user_id" AS "userId" , "user_name" AS "userName"
     */
    fun columnsToString(onlyColumn: Boolean = true, vararg columns: KProperty<*>): String {
        return columns.mapNotNull { columnToString(it, onlyColumn) }.joinToString(separator = StringPool.COMMA)
    }
}
