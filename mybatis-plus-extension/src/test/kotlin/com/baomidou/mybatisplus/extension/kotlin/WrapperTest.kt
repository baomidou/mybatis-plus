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

import com.baomidou.mybatisplus.core.MybatisConfiguration
import com.baomidou.mybatisplus.core.conditions.ISqlSegment
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper
import org.apache.ibatis.builder.MapperBuilderAssistant
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class WrapperTest {

    @BeforeEach
    fun beforeInit() {
        TableInfoHelper.initTableInfo(MapperBuilderAssistant(MybatisConfiguration(), ""), User::class.java)
    }

    private fun logSqlSegment(explain: String, sqlSegment: ISqlSegment) {
        println(String.format(" ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓   ->(%s)<-   ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓", explain))
        println(sqlSegment.sqlSegment)
    }

    @Test
    fun testLambdaQuery() {
        logSqlSegment("测试1.1 LambdaKt", KtQueryWrapper(User()).eq(User::name, "sss").eq(User::roleId, "sss2"))
        logSqlSegment("测试1.2 LambdaKt", KtQueryWrapper(User::class.java).eq(User::name, "sss").eq(User::roleId, "sss2"))
    }

    @Test
    fun testLambdaUpdate() {
        logSqlSegment("测试2.1 LambdaKt", KtUpdateWrapper(User()).eq(User::name, "sss").eq(User::roleId, "sss2"))
        logSqlSegment("测试2.2 LambdaKt", KtUpdateWrapper(User::class.java).eq(User::name, "sss").eq(User::roleId, "sss2"))
    }
}
