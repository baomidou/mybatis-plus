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

import java.util.function.Function;

import com.baomidou.mybatisplus.core.enums.SqlKeyword;


/**
 * <p>
 * 查询条件封装
 * </p>
 *
 * @author hubin
 * @since 2017-05-26
 */
public class QueryWrapper<T> extends Wrapper<T, QueryWrapper<T>> {

    /**
     * <p>
     * 多重嵌套查询条件
     * </p>
     *
     * @param condition  查询条件值
     * @param sqlKeyword SQL 关键词
     * @return this
     */
    protected QueryWrapper addNestedCondition(Function<QueryWrapper, QueryWrapper> condition, SqlKeyword sqlKeyword) {
        return doIt(true, sqlKeyword, () -> "(", condition.apply(new QueryWrapper<T>()), () -> ")");
    }

}
