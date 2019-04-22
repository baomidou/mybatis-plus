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

import com.baomidou.mybatisplus.core.parser.ISqlParser;
import com.baomidou.mybatisplus.core.parser.SqlInfo;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.ExceptionUtils;
import com.baomidou.mybatisplus.core.toolkit.TableNameParser;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.ibatis.reflection.MetaObject;

import java.util.Collection;
import java.util.List;

/**
 * 动态表名 SQL 解析器
 *
 * @author jobob
 * @since 2019-04-23
 */
@Data
@Accessors(chain = true)
public class DynamicTableNameParser implements ISqlParser {
    private List<ITableNameHandler> tableNameHandlers;

    @Override
    public SqlInfo parser(MetaObject metaObject, String sql) {
        Assert.isFalse(CollectionUtils.isEmpty(tableNameHandlers), "tableNameHandlers is empty.");
        Collection<String> tables = new TableNameParser(sql).tables();
        // 待写完
        return null;
    }
}
