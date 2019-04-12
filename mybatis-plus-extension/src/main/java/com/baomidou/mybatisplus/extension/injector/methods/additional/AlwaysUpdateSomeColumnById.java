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

import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.injector.ChooseTableField;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlScriptUtils;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

import static java.util.stream.Collectors.joining;

/**
 * 根据 ID 更新固定的那几个字段(但是不包含逻辑删除)
 *
 * <p>
 * 自己的通用 mapper 如下使用:
 * <pre>
 * int alwaysUpdateSomeColumnById(@Param(Constants.ENTITY) T entity);
 * </pre>
 * </p>
 *
 * <p> 如何筛选字段参考请 {@link InsertBatchSomeColumn} 里面的注释 </p>
 *
 * @author hubin
 * @since 2019-04-12
 */
public class AlwaysUpdateSomeColumnById extends ChooseTableField<AlwaysUpdateSomeColumnById> {

    /**
     * mapper 对应的方法名
     */
    private static final String MAPPER_METHOD = "alwaysUpdateSomeColumnById";

    public AlwaysUpdateSomeColumnById() {
        this.addPredicate(t -> !t.isLogicDelete());
    }

    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        SqlMethod sqlMethod = SqlMethod.UPDATE_BY_ID;
        final String additional = optlockVersion() + tableInfo.getLogicDeleteSql(true, false);
        String sqlSet = this.filters(tableInfo.getFieldList())
            .map(i -> i.getSqlSet(true, ENTITY_DOT)).collect(joining(NEWLINE));
        sqlSet = SqlScriptUtils.convertSet(sqlSet);
        String sql = String.format(sqlMethod.getSql(), tableInfo.getTableName(), sqlSet,
            tableInfo.getKeyColumn(), ENTITY_DOT + tableInfo.getKeyProperty(), additional);
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        return addUpdateMappedStatement(mapperClass, modelClass, MAPPER_METHOD, sqlSource);
    }
}
