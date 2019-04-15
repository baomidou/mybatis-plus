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
package com.baomidou.mybatisplus.extension.plugins.pagination.dialects;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.extension.plugins.pagination.DialectModel;

/**
 * 数据库 分页语句组装接口
 *
 * @author hubin
 * @since 2016-01-23
 */
public interface IDialect {
    /**
     * 这俩没什么特殊意义
     * 只是为了实现类方便使用,以及区分分页 sql 的参数
     */
    String FIRST_MARK = StringPool.QUESTION_MARK;
    String SECOND_MARK = StringPool.QUESTION_MARK;

    /**
     * 组装分页语句
     *
     * @param originalSql 原始语句
     * @param offset      偏移量
     * @param limit       界限
     * @return 分页模型
     */
    DialectModel buildPaginationSql(String originalSql, long offset, long limit);
}
