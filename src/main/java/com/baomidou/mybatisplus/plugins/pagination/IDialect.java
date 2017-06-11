/**
 * Copyright (c) 2011-2014, hubin (jobob@qq.com).
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
package com.baomidou.mybatisplus.plugins.pagination;

/**
 * <p>
 * 数据库 分页语句组装接口
 * </p>
 *
 * @author hubin
 * @Date 2016-01-23
 */
public interface IDialect {

    /**
     * 组装分页语句
     *
     * @param originalSql
     *            原始语句
     * @param offset
     *            偏移量
     * @param limit
     *            界限
     * @return 分页语句
     */
    String buildPaginationSql(String originalSql, int offset, int limit);
}
