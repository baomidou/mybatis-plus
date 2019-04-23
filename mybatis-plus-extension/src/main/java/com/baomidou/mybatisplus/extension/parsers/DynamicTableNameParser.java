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

import java.util.Collection;
import java.util.Map;

import org.apache.ibatis.reflection.MetaObject;

import com.baomidou.mybatisplus.core.parser.ISqlParser;
import com.baomidou.mybatisplus.core.parser.SqlInfo;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.TableNameParser;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 动态表名 SQL 解析器
 *
 * @author jobob
 * @since 2019-04-23
 */
@Data
@Accessors(chain = true)
public class DynamicTableNameParser implements ISqlParser {

    private Map<String, ITableNameHandler> tableNameHandlerMap;

    @Override
    public SqlInfo parser(MetaObject metaObject, String sql) {
        Assert.isFalse(CollectionUtils.isEmpty(tableNameHandlerMap), "tableNameHandlerMap is empty.");
        if (allowProcess(metaObject)) {
            Collection<String> tables = new TableNameParser(sql).tables();
            if (CollectionUtils.isNotEmpty(tables)) {
                boolean sqlParsed = false;
                String parsedSql = sql;
                for (final String table : tables) {
                    ITableNameHandler tableNameHandler = tableNameHandlerMap.get(table);
                    if (null != tableNameHandler) {
                        parsedSql = tableNameHandler.process(metaObject, parsedSql, table);
                        sqlParsed = true;
                    }
                }
                if (sqlParsed) {
                    return SqlInfo.newInstance().setSql(parsedSql);
                }
            }
        }
        return null;
    }


    /**
     * 判断是否允许执行
     * <p>例如：逻辑删除只解析 delete , update 操作</p>
     *
     * @param metaObject 元对象
     * @return true
     */
    public boolean allowProcess(MetaObject metaObject) {
        return true;
    }
}
