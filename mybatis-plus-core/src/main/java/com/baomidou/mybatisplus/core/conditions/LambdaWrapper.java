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

import com.baomidou.mybatisplus.core.toolkit.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.support.Property;

/**
 * <p>
 * 查询条件封装
 * </p>
 *
 * @author hubin
 * @since 2017-05-26
 */
public class LambdaWrapper<T> extends AbstractWrapper<T, Property<T, ?>,  LambdaWrapper<T>> {

    @Override
    public String columnToString(Property<T, ?> column) {
        return TableInfoHelper.toColumn(column);
    }
}
