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

import java.io.Serializable;
import java.util.Map;
import java.util.Optional;

import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.support.Property;
import com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda;

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
public abstract class AbstractLambdaWrapper<T, This extends AbstractLambdaWrapper<T, This>>
    extends AbstractWrapper<T, Property<T, ?>, This> implements Serializable {

    private Map<String, String> columnMap = null;
    private boolean initColumnMap = false;

    @Override
    protected String columnToString(Property<T, ?> column) {
        return getColumn(LambdaUtils.resolve(column));
    }

    private String getColumn(SerializedLambda lambda) {
        if (!initColumnMap) {
            String entityClassName = lambda.getImplClass().replace("/", ".");
            columnMap = LambdaUtils.getColumnMap(entityClassName);
            if (CollectionUtils.isEmpty(columnMap)) {
                throw new MybatisPlusException("该模式不能应用于非 baseMapper 的泛型 entity 之外的 entity!");
            }
            initColumnMap = true;
        }
        return Optional.ofNullable(columnMap.get(StringUtils.resolveFieldName(lambda.getImplMethodName())))
            .orElseThrow(() -> new MybatisPlusException("该模式不能应用于非数据库字段!"));
    }
}
