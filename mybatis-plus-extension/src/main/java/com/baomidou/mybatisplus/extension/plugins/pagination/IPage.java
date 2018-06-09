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
package com.baomidou.mybatisplus.extension.plugins.pagination;

/**
 * <p>
 * 分页 Page 对象接口
 * </p>
 *
 * @author hubin
 * @since 2018-06-09
 */
public interface IPage {

    /**
     * <p>
     * 计算当前分页偏移量
     * </p>
     *
     * @param current 当前页
     * @param size    每页显示数量
     * @return
     */
    default long offsetCurrent(long current, long size) {
        return current > 0 ? (current - 1) * size : 0;
    }

    /**
     * <p>
     * 当前分页总页数
     * </p>
     *
     * @return
     */
    default long getPages() {
        if (this.size() == 0) {
            return 0L;
        }
        long pages = this.total() / this.size();
        if (this.total() % this.size() != 0) {
            pages++;
        }
        return pages;
    }

    /**
     * 总数
     */
    /**
     * <p>
     * 当前分页总页数
     * </p>
     *
     * @return
     */
    long total();

    /**
     * 每页显示条数，默认 10
     */
    /**
     * <p>
     * 当前分页总页数
     * </p>
     *
     * @return
     */
    long size();

    /**
     * <p>
     * 当前页，默认 1
     * </p>
     *
     * @return
     */
    long current();

}
