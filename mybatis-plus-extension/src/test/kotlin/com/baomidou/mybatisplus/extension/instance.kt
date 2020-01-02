package com.baomidou.mybatisplus.extension

import com.baomidou.mybatisplus.core.MybatisConfiguration
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper
import org.apache.ibatis.builder.MapperBuilderAssistant
import kotlin.reflect.KClass

/**
 * @author hcl
 * Create at 2019/12/25
 */
object TestMybatisConfiguration : MybatisConfiguration()

/**
 * 初始化表信息
 */
fun initTableInfo(kClass: KClass<*>) {
    TableInfoHelper.initTableInfo(MapperBuilderAssistant(TestMybatisConfiguration, ""), kClass.java)
}

