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
package com.baomidou.mybatisplus.core.conditions;

import com.baomidou.mybatisplus.core.toolkit.*;
import com.baomidou.mybatisplus.core.toolkit.support.ColumnCache;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.joining;

/**
 * <p>
 * Lambda 语法使用 Wrapper
 * 统一处理解析 lambda 获取 column
 * </p>
 *
 * @author hubin miemie HCL
 * @since 2017-05-26
 */
@SuppressWarnings("serial")
public abstract class AbstractLambdaWrapper<T, Children extends AbstractLambdaWrapper<T, Children>>
    extends AbstractWrapper<T, SFunction<T, ?>, Children> {

    private Map<String, ColumnCache> columnMap = null;
    private boolean initColumnMap = false;

    @Override
    protected void initEntityClass() {
        super.initEntityClass();
        if (entityClass != null) {
            columnMap = LambdaUtils.getColumnMap(entityClass.getName());
            initColumnMap = true;
        }
    }

    @Override
    protected String columnsToString(SFunction<T, ?>... columns) {
        return columnsToString(true, columns);
    }

    protected String columnsToString(boolean onlyColumn, SFunction<T, ?>... columns) {
        return Arrays.stream(columns).map(i -> columnToString(i, onlyColumn)).collect(joining(StringPool.COMMA));
    }

    @Override
    protected String columnToString(SFunction<T, ?> column) {
        return columnToString(column, true);
    }

    protected String columnToString(SFunction<T, ?> column, boolean onlyColumn) {
        return getColumn(LambdaUtils.resolve(column), onlyColumn);
    }

    private String getColumn(SerializedLambda lambda, boolean onlyColumn) {
        String fieldName = StringUtils.resolveFieldName(lambda.getImplMethodName());
        if (!initColumnMap || !columnMap.containsKey(fieldName.toUpperCase(Locale.ENGLISH))) {
            String entityClassName = lambda.getImplClassName();
            columnMap = LambdaUtils.getColumnMap(entityClassName);
            Assert.notEmpty(columnMap, "cannot find column's cache for \"%s\", so you cannot used \"%s\"!",
                entityClassName, typedThis.getClass());
            initColumnMap = true;
        }
        return Optional.ofNullable(columnMap.get(fieldName.toUpperCase(Locale.ENGLISH)))
            .map(onlyColumn ? ColumnCache::getColumn : ColumnCache::getColumnSelect)
            .orElseThrow(() -> ExceptionUtils.mpe("your property named \"%s\" cannot find the corresponding database column name!", fieldName));
    }
}
