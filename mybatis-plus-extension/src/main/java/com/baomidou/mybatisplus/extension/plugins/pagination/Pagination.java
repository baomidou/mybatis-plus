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

import java.io.Serializable;

/**
 * <p>
 * 简单分页模型
 * </p>
 *
 * @author hubin
 * @since 2018-06-09
 */
public class Pagination<T> implements IPage, Serializable {

    /**
     * 总数
     */
    private long total;

    /**
     * 每页显示条数，默认 10
     */
    private long size = 10;

    /**
     * 当前页
     */
    private long current = 1;

    /**
     * 查询总记录数（默认 true）
     */
    private boolean searchCount = true;

    public Pagination() {
        // to do nothing
    }

    /**
     * <p>
     * 分页构造函数
     * </p>
     *
     * @param current 当前页
     * @param size    每页显示条数
     */
    public Pagination(long current, long size) {
        this(current, size, true);
    }

    public Pagination(long current, long size, boolean searchCount) {
        if (current > 1) {
            this.current = current;
        }
        this.size = size;
        this.searchCount = searchCount;
    }

    /**
     * <p>
     * 存在上一页
     * </p>
     *
     * @return true / false
     */
    public boolean hasPrevious() {
        return this.current > 1;
    }

    /**
     * <p>
     * 存在下一页
     * </p>
     *
     * @return true / false
     */
    public boolean hasNext() {
        return this.current < this.getPages();
    }

    public void setTotal(long total) {
        this.total = total;
    }

    @Override
    public long total() {
        return this.total;
    }

    public void setSize(long size) {
        this.size = size;
    }

    @Override
    public long size() {
        return this.size;
    }

    public void setCurrent(long current) {
        this.current = current;
    }


    @Override
    public long current() {
        return this.current;
    }

    public boolean isSearchCount() {
        return searchCount;
    }

    public void setSearchCount(boolean searchCount) {
        this.searchCount = searchCount;
    }
}
