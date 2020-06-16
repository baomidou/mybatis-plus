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
package com.baomidou.mybatisplus.core.toolkit;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * 插件工具类
 *
 * @author TaoYu , hubin
 * @since 2017-06-20
 */
public abstract class PluginUtils {
    public static final String DELEGATE_BOUNDSQL_SQL = "delegate.boundSql.sql";

    private final static Field additionalParametersField = initBoundSqlAdditionalParametersField();

    private static Field initBoundSqlAdditionalParametersField() {
        try {
            Field field = BoundSql.class.getDeclaredField("additionalParameters");
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            throw ExceptionUtils.mpe("can not find field['additionalParameters'] from BoundSql, why?", e);
        }
    }

    /**
     * 获得真正的处理对象,可能多层代理.
     */
    @SuppressWarnings("unchecked")
    public static <T> T realTarget(Object target) {
        if (Proxy.isProxyClass(target.getClass())) {
            MetaObject metaObject = SystemMetaObject.forObject(target);
            return realTarget(metaObject.getValue("h.target"));
        }
        return (T) target;
    }

    /**
     * 获取 BoundSql 属性值 additionalParameters
     *
     * @param boundSql BoundSql
     * @return additionalParameters
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> getAdditionalParameter(BoundSql boundSql) {
        try {
            return (Map<String, Object>) additionalParametersField.get(boundSql);
        } catch (IllegalAccessException e) {
            throw ExceptionUtils.mpe("获取 BoundSql 属性值 additionalParameters 失败: " + e, e);
        }
    }

    /**
     * 给 BoundSql 设置 additionalParameters
     *
     * @param boundSql             BoundSql
     * @param additionalParameters additionalParameters
     */
    public static void setAdditionalParameter(BoundSql boundSql, Map<String, Object> additionalParameters) {
        additionalParameters.forEach(boundSql::setAdditionalParameter);
    }
}
