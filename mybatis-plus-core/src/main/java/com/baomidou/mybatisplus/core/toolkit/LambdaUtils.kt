package com.baomidou.mybatisplus.core.toolkit

import org.apache.commons.collections.MapUtils
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KProperty
import kotlin.reflect.full.memberProperties

val LAMBDA_KT_CACHE = ConcurrentHashMap<KProperty<*>, String>()


fun createCatch(clazz: Class<*>, source: Map<String, String>)  {
    for (memberProperty in clazz.kotlin.memberProperties) {
        LAMBDA_KT_CACHE[memberProperty]= MapUtils.getString(source, memberProperty.name)
    }
}

