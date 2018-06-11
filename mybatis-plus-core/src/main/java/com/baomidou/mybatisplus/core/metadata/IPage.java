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
package com.baomidou.mybatisplus.core.metadata;

import java.util.List;

/**
 * <p>
 * 分页 Page 对象接口
 * </p>
 *
 * @author hubin
 * @since 2018-06-09
 */
public interface IPage<T> {

    /**
     * <p>
     * 查询总页数
     * </p>
     *
     * @return true 是 / false 否
     */
    default boolean searchCount() {
        return true;
    }

    /**
     * <p>
     * 降序字段集合
     * </p>
     *
     * @return
     */
    default List<String> descs() {
        return null;
    }

    /**
     * <p>
     * 升序字段集合
     * </p>
     *
     * @return
     */
    default List<String> ascs() {
        return null;
    }

    /**
     * <p>
     * 自动优化 COUNT SQL
     * </p>
     *
     * @return true 是 / false 否
     */
    default boolean optimizeCountSql() {
        return true;
    }

    /**
     * <p>
     * 计算当前分页偏移量
     * </p>
     *
     * @return
     */
    default long offset() {
        return this.getCurrent() > 0 ? (this.getCurrent() - 1) * this.getSize() : 0;
    }

    /**
     * <p>
     * 当前分页总页数
     * </p>
     *
     * @return
     */
    default long getPages() {
        if (this.getSize() == 0) {
            return 0L;
        }
        long pages = this.getTotal() / this.getSize();
        if (this.getTotal() % this.getSize() != 0) {
            pages++;
        }
        return pages;
    }

    /**
     * <p>
     * 分页记录列表
     * </p>
     *
     * @return 分页对象记录列表
     */
    List<T> getRecords();

    /**
     * <p>
     * 设置分页记录列表
     * </p>
     *
     * @return 当前对象
     */
    IPage setRecords(List<T> records);

    /**
     * <p>
     * 当前满足条件总行数
     * </p>
     *
     * @return
     */
    long getTotal();

    /**
     * <p>
     * 设置当前满足条件总行数
     * </p>
     *
     * @return 当前对象
     */
    IPage setTotal(long total);

    /**
     * <p>
     * 当前分页总页数
     * </p>
     *
     * @return
     */
    long getSize();

    /**
     * <p>
     * 当前页，默认 1
     * </p>
     *
     * @return
     */
    long getCurrent();

    /**
     * <p>
     * 设置当前页
     * </p>
     *
     * @return 当前对象
     */
    IPage setCurrent(long current);

}
