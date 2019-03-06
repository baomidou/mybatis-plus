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
package com.baomidou.mybatisplus.extension.kotlin.chain

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.service.additional.AbstractChainWrapper
import com.baomidou.mybatisplus.extension.service.additional.update.ChainUpdate
import kotlin.reflect.KProperty

/**
 * Kotlin Lambda 语法使用 Wrapper，并定义了更新方法
 *
 * @author Cat73
 * @since 2019-02-28
 */
class KtUpdateChainWrapper<T : Any>(private val mapper: BaseMapper<T>, clazz: Class<T>) :
        AbstractChainWrapper<T, KProperty<*>, KtUpdateChainWrapper<T>, KtQueryWrapper<T>>(),
        ChainUpdate<T> {
    init {
        super.wrapperChildren = KtQueryWrapper(clazz)
    }

    override fun getWrapper() = super.wrapperChildren

    override fun getBaseMapper() = this.mapper
}
