/*
 * Copyright (c) 2011-2021, baomidou (jobob@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baomidou.mybatisplus.extension.parsers;

import com.baomidou.mybatisplus.core.parser.ISqlParser;
import com.baomidou.mybatisplus.core.parser.SqlInfo;
import com.baomidou.mybatisplus.core.toolkit.TableNameParser;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.ibatis.reflection.MetaObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 动态表名 SQL 解析器
 *
 * @author jobob
 * @since 2019-04-23
 * @deprecated 3.4.0 @2020-07-30 use {@link MybatisPlusInterceptor} {@link DynamicTableNameInnerInterceptor}
 */
@Data
@Accessors(chain = true)
@Deprecated
public class DynamicTableNameParser implements ISqlParser {
    private Map<String, ITableNameHandler> tableNameHandlerMap;

    /**
     * 进行 SQL 表名名替换
     *
     * @param metaObject 元对象
     * @param sql        SQL 语句
     * @return 返回解析后的 SQL 信息
     */
    @Override
    public SqlInfo parser(MetaObject metaObject, String sql) {
        // fix-issue:https://gitee.com/baomidou/mybatis-plus/issues/I1K7Q1
        // Assert.isFalse(CollectionUtils.isEmpty(tableNameHandlerMap), "tableNameHandlerMap is empty.");
        if (allowProcess(metaObject)) {
            TableNameParser parser = new TableNameParser(sql);
            List<TableNameParser.SqlToken> names = new ArrayList<>();
            parser.accept(names::add);
            StringBuilder builder = new StringBuilder();
            int last = 0;
            for (TableNameParser.SqlToken name : names) {
                int start = name.getStart();
                if (start != last) {
                    builder.append(sql, last, start);
                    String value = name.getValue();
                    ITableNameHandler handler = tableNameHandlerMap.get(value);
                    if (handler != null) {
                        builder.append(handler.dynamicTableName(metaObject, sql, value));
                    } else {
                        builder.append(value);
                    }
                }
                last = name.getEnd();
            }
            if (last != sql.length()) {
                builder.append(sql.substring(last));
            }
            return SqlInfo.of(builder.toString());
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
