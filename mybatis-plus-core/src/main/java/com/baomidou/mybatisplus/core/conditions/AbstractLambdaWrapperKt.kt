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
package com.baomidou.mybatisplus.core.conditions

import com.baomidou.mybatisplus.annotation.TableField
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.javaField

/**
 *
 *
 * Lambda 语法使用 Wrapper
 * 统一处理解析 lambda 获取 column
 *
 *
 * @author yangyuhan
 * @since 2018-11-02
 */
abstract class AbstractLambdaWrapperKt<T, This : AbstractLambdaWrapperKt<T, This>> : AbstractWrapper<T, KProperty<*>, This>() {

    private var columnMap: Map<String, String>? = null
    private var initColumnMap = false

    override fun columnToString(kProperty: KProperty<*>): String {
        return kProperty.javaField?.getDeclaredAnnotation(TableField::class.java)?.value!!
    }

}
