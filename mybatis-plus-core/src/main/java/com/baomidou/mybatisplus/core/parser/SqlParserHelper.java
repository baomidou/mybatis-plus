/*
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
package com.baomidou.mybatisplus.core.parser;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.reflection.MetaObject;

import com.baomidou.mybatisplus.annotation.SqlParser;

/**
 * <p>
 * SQL 解析辅助类
 * </p>
 *
 * @author hubin
 * @since 2018-07-22
 */
public class SqlParserHelper {

    public static final String DELEGATE_MAPPED_STATEMENT = "delegate.mappedStatement";
    /**
     * SQL 解析缓存
     */
    private static final Map<String, SqlParserInfo> SQL_PARSER_INFO_CACHE = new ConcurrentHashMap<>();


    /**
     * <p>
     * 初始化缓存 SqlParser 注解信息
     * </p>
     *
     * @param mapperClass Mapper Class
     */
    public synchronized static void initSqlParserInfoCache(Class<?> mapperClass) {
        Method[] methods = mapperClass.getDeclaredMethods();
        for (Method method : methods) {
            SqlParser sqlParser = method.getAnnotation(SqlParser.class);
            if (null != sqlParser) {
                StringBuilder sid = new StringBuilder();
                sid.append(mapperClass.getName()).append(".").append(method.getName());
                SQL_PARSER_INFO_CACHE.put(sid.toString(), new SqlParserInfo(sqlParser));
            }
        }
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
        return SQL_PARSER_INFO_CACHE.get(getMappedStatement(metaObject).getId());
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
        return (MappedStatement) metaObject.getValue(DELEGATE_MAPPED_STATEMENT);
    }
}
