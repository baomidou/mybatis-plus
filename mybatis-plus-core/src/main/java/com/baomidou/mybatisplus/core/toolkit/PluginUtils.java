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

import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import java.lang.reflect.Proxy;
import java.util.Properties;

/**
 * 插件工具类
 *
 * @author TaoYu , hubin
 * @since 2017-06-20
 */
public final class PluginUtils {
    public static final String DELEGATE_BOUNDSQL_SQL = "delegate.boundSql.sql";

    private PluginUtils() {
        // to do nothing
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
     * 根据 key 获取 Properties 的值
     */
    public static String getProperty(Properties properties, String key) {
        String value = properties.getProperty(key);
        return StringUtils.isBlank(value) ? null : value;
    }
}
