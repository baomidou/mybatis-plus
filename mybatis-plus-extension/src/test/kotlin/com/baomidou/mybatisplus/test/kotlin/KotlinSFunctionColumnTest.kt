/*
 * Copyright (c) 2011-2025, baomidou (jobob@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baomidou.mybatisplus.test.kotlin

import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableName
import com.baomidou.mybatisplus.core.MybatisConfiguration
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper
import org.apache.ibatis.builder.MapperBuilderAssistant
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@TableName("sys_user")
class User {
    var id: Int? = null

    @TableField("username")
    var name: String? = null

    var roleId: Int? = null
}

@TableName("account")
class Account {
    var id: Int? = null
    var isActive: Boolean = false
}

/**
 * 当 Kotlin (>= 1.5) 把属性引用 (User::name) 或 Java getter 方法引用 (PermissionDO::getBizType)
 * 作为 Java 的 SFunction 参数传递时，编译器会生成合成的 implMethodName，导致列名无法解析。
 * 该测试覆盖在 Kotlin 中直接使用 Java 版 Lambda Wrapper 的场景。
 */
class KotlinSFunctionColumnTest {

    @BeforeEach
    fun init() {
        TableInfoHelper.initTableInfo(MapperBuilderAssistant(MybatisConfiguration(), ""), User::class.java)
    }

    @Test
    fun `query wrapper resolves column from kotlin property reference`() {
        val sql = QueryWrapper(User::class.java)
            .eq(User::name, "sss")
            .eq(User::roleId, 1)
            .sqlSegment
        // name 通过 @TableField("username") 映射, roleId 映射为 role_id
        assertThat(sql).contains("username", "role_id")
    }

    @Test
    fun `update wrapper resolves column from kotlin property reference`() {
        val sql = UpdateWrapper(User::class.java)
            .eq(User::name, "sss")
            .sqlSegment
        assertThat(sql).contains("username")
    }

    @Test
    fun `query wrapper resolves column from kotlin getter method reference`() {
        TableInfoHelper.initTableInfo(MapperBuilderAssistant(MybatisConfiguration(), ""), PermissionDO::class.java)
        // PermissionDO::getBizType 是对 Java getter 的方法引用 (KFunction), 不同于属性引用 (KProperty)
        val sql = QueryWrapper(PermissionDO::class.java)
            .eq(PermissionDO::getBizType, 1)
            .eq(PermissionDO::getUserId, 2L)
            .sqlSegment
        assertThat(sql).contains("biz_type", "user_id")
    }

    @Test
    fun `resolves column from is-prefixed boolean property reference`() {
        TableInfoHelper.initTableInfo(MapperBuilderAssistant(MybatisConfiguration(), ""), Account::class.java)
        val sql = QueryWrapper(Account::class.java)
            .eq(Account::isActive, true)
            .sqlSegment
        // isActive 的属性名以 "is" 开头, getter 形式还原后须仍为 isActive -> is_active
        assertThat(sql).contains("is_active")
    }
}
