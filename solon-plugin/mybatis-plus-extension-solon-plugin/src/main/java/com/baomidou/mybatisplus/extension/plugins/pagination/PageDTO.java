/*
 * Copyright (c) 2011-2024, baomidou (jobob@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baomidou.mybatisplus.extension.plugins.pagination;

import com.baomidou.mybatisplus.core.metadata.OrderItem;

import java.util.List;

/**
 * 简单分页模型 DTO 用于解决跨服务数据传输问题，不影响 Page 作为返回对象序列化 JSON 产生不必要的数据
 *
 * @author hubin
 * @since 2021-05-20
 */
public class PageDTO<T> extends Page<T> {

    private static final long serialVersionUID = 1L;

    public PageDTO() {
    }

    public PageDTO(long current, long size) {
        this(current, size, 0);
    }

    public PageDTO(long current, long size, long total) {
        this(current, size, total, true);
    }

    public PageDTO(long current, long size, boolean searchCount) {
        this(current, size, 0, searchCount);
    }

    public PageDTO(long current, long size, long total, boolean searchCount) {
        super(current, size, total, searchCount);
    }

    /* --------------- 以下为静态构造方式 --------------- */
    public static <T> Page<T> of(long current, long size) {
        return of(current, size, 0);
    }

    public static <T> Page<T> of(long current, long size, long total) {
        return of(current, size, total, true);
    }

    public static <T> Page<T> of(long current, long size, boolean searchCount) {
        return of(current, size, 0, searchCount);
    }

    public static <T> Page<T> of(long current, long size, long total, boolean searchCount) {
        return new PageDTO<>(current, size, total, searchCount);
    }

    public String getCountId() {
        return super.countId();
    }

    public Long getMaxLimit() {
        return super.maxLimit();
    }

    public List<OrderItem> getOrders() {
        return super.orders();
    }

    public boolean isOptimizeCountSql() {
        return super.optimizeCountSql();
    }

    public boolean isSearchCount() {
        return super.searchCount();
    }

    public boolean isOptimizeJoinOfCountSql() {
        return super.optimizeJoinOfCountSql();
    }

    @Override
    public String toString() {
        return "PageDTO{} " + super.toString();
    }
}
