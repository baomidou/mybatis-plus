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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ArrayUtils;

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
     * 查询数据列表
     */
    private List<T> records = Collections.emptyList();

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
    /**
     * <p>
     * SQL 排序 ASC 集合
     * </p>
     */
    private List<String> ascs;
    /**
     * <p>
     * SQL 排序 DESC 集合
     * </p>
     */
    private List<String> descs;


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

    @Override
    public List getRecords() {
        return this.records;
    }

    @Override
    public IPage setRecords(List records) {
        this.records = records;
        return this;
    }

    @Override
    public Pagination setTotal(long total) {
        this.total = total;
        return this;
    }

    @Override
    public long getTotal() {
        return this.total;
    }

    public void setSize(long size) {
        this.size = size;
    }

    @Override
    public long getSize() {
        return this.size;
    }

    @Override
    public Pagination setCurrent(long current) {
        this.current = current;
        return this;
    }

    @Override
    public long getCurrent() {
        return this.current;
    }

    @Override
    public boolean searchCount() {
        return searchCount;
    }

    public void setSearchCount(boolean searchCount) {
        this.searchCount = searchCount;
    }

    @Override
    public List<String> ascs() {
        return ascs;
    }

    public Pagination setAscs(List<String> ascs) {
        this.ascs = ascs;
        return this;
    }

    public Pagination setAscs(String... ascs) {
        if (ArrayUtils.isNotEmpty(ascs)) {
            this.ascs = Arrays.asList(ascs);
        }
        return this;
    }

    @Override
    public List<String> descs() {
        return descs;
    }

    public Pagination setDescs(List<String> descs) {
        this.descs = descs;
        return this;
    }

    public Pagination setDescs(String... descs) {
        if (ArrayUtils.isNotEmpty(descs)) {
            this.descs = Arrays.asList(descs);
        }
        return this;
    }

}
