package com.baomidou.mybatisplus.test.kotlin

import com.baomidou.mybatisplus.core.MybatisConfiguration
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.kotlin.KtUpdateWrapper
import org.apache.ibatis.builder.MapperBuilderAssistant
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class WrapperTest {

    @BeforeEach
    fun beforeInit() {
        TableInfoHelper.initTableInfo(MapperBuilderAssistant(MybatisConfiguration(), ""), User::class.java)
    }

    /**
     * 特征化 KtQueryWrapper：属性引用解析成列名，apply 原样拼进 WHERE，占位符按调用顺序编号。
     */
    @Test
    fun testLambdaQuery() {
        val wrapper = KtQueryWrapper(User()).eq(User::name, "sss").eq(User::roleId, "sss2")
            .apply("a=1").apply("b={0}", 3).apply("c={0} and d={1}", 4, 5)
        assertThat(wrapper.sqlSegment).isEqualTo(
            "(username = #{ew.paramNameValuePairs.MPGENVAL1} AND role_id = #{ew.paramNameValuePairs.MPGENVAL2}" +
                " AND a=1 AND b=#{ew.paramNameValuePairs.MPGENVAL3}" +
                " AND c=#{ew.paramNameValuePairs.MPGENVAL4} and d=#{ew.paramNameValuePairs.MPGENVAL5})"
        )
        assertThat(wrapper.targetSql).isEqualTo("(username = ? AND role_id = ? AND a=1 AND b=? AND c=? and d=?)")
        assertThat(wrapper.paramNameValuePairs).hasSize(5).containsAllEntriesOf(
            mapOf<String, Any>(
                "MPGENVAL1" to "sss", "MPGENVAL2" to "sss2",
                "MPGENVAL3" to 3, "MPGENVAL4" to 4, "MPGENVAL5" to 5
            )
        )
    }

    /**
     * 特征化 KtUpdateWrapper：setSql 的参数先占号，所以条件里的 eq 从 MPGENVAL4 开始编号。
     */
    @Test
    fun testLambdaUpdate() {
        val wrapper = KtUpdateWrapper(User()).eq(User::name, "sss").eq(User::roleId, "sss2")
            .setSql("a=1").setSql("b={0}", 3).setSql("c={0},d={1}", 4, 5)
        assertThat(wrapper.sqlSet).isEqualTo(
            "a=1,b=#{ew.paramNameValuePairs.MPGENVAL1},c=#{ew.paramNameValuePairs.MPGENVAL2}," +
                "d=#{ew.paramNameValuePairs.MPGENVAL3}"
        )
        assertThat(wrapper.sqlSegment).isEqualTo(
            "(username = #{ew.paramNameValuePairs.MPGENVAL4} AND role_id = #{ew.paramNameValuePairs.MPGENVAL5})"
        )
        assertThat(wrapper.targetSql).isEqualTo("(username = ? AND role_id = ?)")
        assertThat(wrapper.paramNameValuePairs).hasSize(5).containsAllEntriesOf(
            mapOf<String, Any>(
                "MPGENVAL1" to 3, "MPGENVAL2" to 4, "MPGENVAL3" to 5,
                "MPGENVAL4" to "sss", "MPGENVAL5" to "sss2"
            )
        )
    }
}
