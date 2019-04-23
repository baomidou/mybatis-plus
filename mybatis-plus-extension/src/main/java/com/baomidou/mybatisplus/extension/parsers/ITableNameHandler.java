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
package com.baomidou.mybatisplus.extension.parsers;

import org.apache.ibatis.reflection.MetaObject;

/**
 * 动态表名处理器
 *
 * @author jobob
 * @since 2019-04-23
 */
public interface ITableNameHandler {

    /**
     * 表名 SQL 处理
     *
     * @param metaObject 元对象
     * @param sql        当前执行 SQL
     * @param tableName  表名
     * @return
     */
    default String process(MetaObject metaObject, String sql, String tableName) {
        String dynamicTableName = dynamicTableName(metaObject, sql, tableName);
        if (null != dynamicTableName && !dynamicTableName.equalsIgnoreCase(tableName)) {
            return sql.replaceAll(tableName, dynamicTableName);
        }
        return sql;
    }

    /**
     * 生成动态表名，无改变返回 NULL
     *
     * @param metaObject 元对象
     * @param sql        当前执行 SQL
     * @param tableName  表名
     * @return String
     */
    String dynamicTableName(MetaObject metaObject, String sql, String tableName);
}
