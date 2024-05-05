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
