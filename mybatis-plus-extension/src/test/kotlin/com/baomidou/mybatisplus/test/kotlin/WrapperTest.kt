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
package com.baomidou.mybatisplus.test.kotlin

import com.baomidou.mybatisplus.core.MybatisConfiguration
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.kotlin.KtUpdateWrapper
import org.apache.ibatis.builder.MapperBuilderAssistant
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class WrapperTest {

    @BeforeEach
    fun beforeInit() {
        TableInfoHelper.initTableInfo(MapperBuilderAssistant(MybatisConfiguration(), ""), User::class.java)
    }

    @Test
    fun testLambdaQuery() {
        logSqlSegment("测试1.1 LambdaKt", KtQueryWrapper(User()).eq(User::name, "sss").eq(User::roleId, "sss2")
            .apply("a=1").apply("b={0}", 3).apply("c={0} and d={1}", 4, 5))
    }

    @Test
    fun testLambdaUpdate() {
        logSqlSegmentAndSetSql("测试2.1 LambdaKt", KtUpdateWrapper(User()).eq(User::name, "sss").eq(User::roleId, "sss2")
            .setSql("a=1").setSql("b={0}", 3).setSql("c={0},d={1}", 4, 5))
    }

    private fun logSqlSegment(explain: String, wp: KtQueryWrapper<*>) {
        println(String.format(" ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓   ->(%s)<-   ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓", explain))
        println(wp.sqlSegment)
        wp.paramNameValuePairs.forEach(::println)
    }

    private fun logSqlSegmentAndSetSql(explain: String, wp: KtUpdateWrapper<*>) {
        println(String.format(" ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓   ->(%s)<-   ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓", explain))
        println(wp.sqlSegment)
        println(wp.sqlSet)
        wp.paramNameValuePairs.forEach(::println)
    }
}
