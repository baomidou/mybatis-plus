/**
 * Copyright (c) 2011-2020, hubin (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.toolkit;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import com.baomidou.mybatisplus.annotations.SqlParser;
import com.baomidou.mybatisplus.entity.SqlParserInfo;
import com.baomidou.mybatisplus.exceptions.MybatisPlusException;

/**
 * <p>
 * 插件工具类
 * </p>
 *
 * @author TaoYu , hubin
 * @since 2017-06-20
 */
public final class PluginUtils {

    public static final String DELEGATE_BOUNDSQL_SQL = "delegate.boundSql.sql";
    public static final String DELEGATE_MAPPEDSTATEMENT = "delegate.mappedStatement";

    /**
     * SQL 解析缓存
     */
    private static final Map<String, SqlParserInfo> sqlParserInfoCache = new ConcurrentHashMap<>();


    private PluginUtils() {
        // to do nothing
    }


    /**
     * <p>
     * 获取 SqlParser 注解信息
     * </p>
     *
     * @param metaObject 元数据对象
     * @return
     */
    public static SqlParserInfo getSqlParserInfo(MetaObject metaObject) {
        try {
            MappedStatement ms = getMappedStatement(metaObject);
            String statementId = ms.getId();
            SqlParserInfo sqlParserInfo = sqlParserInfoCache.get(statementId);
            if (null != sqlParserInfo) {
                // 存在直接返回
                return sqlParserInfo;
            }
            // 初始化缓存 SqlParser 注解信息
            String namespace = statementId.substring(0, statementId.lastIndexOf("."));
            Method[] methods = Class.forName(namespace).getDeclaredMethods();
            for (Method method : methods) {
                SqlParser sqlParser = method.getAnnotation(SqlParser.class);
                sqlParserInfo = new SqlParserInfo();
                if (null != sqlParser) {
                    sqlParserInfo.setFilter(sqlParser.filter());
                }
                StringBuilder sid = new StringBuilder();
                sid.append(namespace).append(".").append(method.getName());
                sqlParserInfoCache.put(sid.toString(), sqlParserInfo);
            }
            // 获取缓存结果
            return sqlParserInfoCache.get(statementId);
        } catch (Exception e) {
            throw new MybatisPlusException("获取 SqlParser 注解信息异常", e);
        }
    }

    /**
     * <p>
     * 获取当前执行 MappedStatement
     * </p>
     *
     * @param metaObject 元对象
     * @return
     */
    public static MappedStatement getMappedStatement(MetaObject metaObject) {
        return (MappedStatement) metaObject.getValue(DELEGATE_MAPPEDSTATEMENT);
    }

    /**
     * <p>
     * 获得真正的处理对象,可能多层代理.
     * </p>
     */
    public static Object realTarget(Object target) {
        if (Proxy.isProxyClass(target.getClass())) {
            MetaObject metaObject = SystemMetaObject.forObject(target);
            return realTarget(metaObject.getValue("h.target"));
        }
        return target;
    }

    /**
     * <p>
     * 根据 key 获取 Properties 的值
     * </p>
     */
    public static String getProperty(Properties properties, String key) {
        String value = properties.getProperty(key);
        return StringUtils.isEmpty(value) ? null : value;
    }
}
