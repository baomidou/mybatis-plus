/*
 * Copyright (c) 2011-2024, baomidou (jobob@qq.com).
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
package com.baomidou.mybatisplus.core.injector.methods;

import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

import java.util.Map;

/**
 * 根据columnMap 查询一条数据
 *
 * @author hubin
 * @since 2018-04-06
 */
@Deprecated
public class SelectByMap extends AbstractMethod {

    public SelectByMap() {
        this(SqlMethod.SELECT_BY_MAP.getMethod());
    }

    /**
     * @param name 方法名
     * @since 3.5.0
     */
    public SelectByMap(String name) {
        super(name);
    }

    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        SqlMethod sqlMethod = SqlMethod.SELECT_BY_MAP;
        String sql = String.format(sqlMethod.getSql(), sqlSelectColumns(tableInfo, false),
            tableInfo.getTableName(), sqlWhereByMap(tableInfo));
        SqlSource sqlSource = super.createSqlSource(configuration, sql, Map.class);
        return this.addSelectMappedStatementForTable(mapperClass, methodName, sqlSource, tableInfo);
    }
}
