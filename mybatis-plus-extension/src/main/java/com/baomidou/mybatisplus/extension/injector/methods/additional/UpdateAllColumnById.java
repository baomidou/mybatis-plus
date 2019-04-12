/*
 * Copyright (c) 2011-2019, hubin (jobob@qq.com).
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
package com.baomidou.mybatisplus.extension.injector.methods.additional;

import static java.util.stream.Collectors.joining;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.StringPool;

/**
 * 根据 ID 更新所有字段
 *
 * @author hubin
 * @since 2019-04-12
 */
public class UpdateAllColumnById extends AbstractMethod {

    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        boolean logicDelete = tableInfo.isLogicDelete();
        SqlMethod sqlMethod = SqlMethod.UPDATE_ALL_COLUMN_BY_ID;
        StringBuilder append = new StringBuilder("<if test=\"et instanceof java.util.Map\">")
            .append(" AND ${et.").append(Constants.MP_OPTLOCK_VERSION_COLUMN)
            .append("}=#{et.").append(Constants.MP_OPTLOCK_VERSION_ORIGINAL).append(StringPool.RIGHT_BRACE)
            .append("</if>");
        if (logicDelete) {
            append.append(tableInfo.getLogicDeleteSql(true, false));
        }
        String sql = String.format(sqlMethod.getSql(), tableInfo.getTableName(),
            tableInfo.getFieldList().stream().filter(i -> !(tableInfo.isLogicDelete() && i.isLogicDelete()))
                .map(i -> i.getSqlSet(true, ENTITY_DOT)).collect(joining(DOT_NEWLINE)),
            tableInfo.getKeyColumn(), ENTITY_DOT + tableInfo.getKeyProperty(),
            append);
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        return addUpdateMappedStatement(mapperClass, modelClass, sqlMethod.getMethod(), sqlSource);
    }
}
