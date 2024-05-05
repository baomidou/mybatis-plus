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
 * 查询满足条件所有数据（并翻页）
 *
 * @author hubin
 * @deprecated 3.5.3.2 {@link  com.baomidou.mybatisplus.core.mapper.BaseMapper#selectMaps(com.baomidou.mybatisplus.core.metadata.IPage, com.baomidou.mybatisplus.core.conditions.Wrapper)}
 * @since 2018-04-06
 */
@Deprecated
public class SelectMapsPage extends AbstractMethod {

    public SelectMapsPage() {
        this(SqlMethod.SELECT_MAPS_PAGE.getMethod());
    }

    /**
     * @param name 方法名
     * @since 3.5.0
     */
    public SelectMapsPage(String name) {
        super(name);
    }

    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        SqlMethod sqlMethod = SqlMethod.SELECT_MAPS_PAGE;
        String sql = String.format(sqlMethod.getSql(), sqlFirst(), sqlSelectColumns(tableInfo, true),
            tableInfo.getTableName(), sqlWhereEntityWrapper(true, tableInfo), sqlOrderBy(tableInfo), sqlComment());
        SqlSource sqlSource = super.createSqlSource(configuration, sql, modelClass);
        return this.addSelectMappedStatementForOther(mapperClass, methodName, sqlSource, Map.class);
    }
}
