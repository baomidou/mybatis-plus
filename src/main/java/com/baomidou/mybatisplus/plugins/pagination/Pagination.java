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

import java.io.Serializable;

import org.apache.ibatis.session.RowBounds;

import com.baomidou.mybatisplus.toolkit.StringUtils;

/**
 * <p>
 * 简单分页模型
 * </p>
 * 用户可以通过继承 org.apache.ibatis.session.RowBounds实现自己的分页模型<br>
 * 注意：插件仅支持RowBounds及其子类作为分页参数
 *
 * @author hubin
 * @Date 2016-01-23
 */
public class Pagination extends RowBounds implements Serializable {

    private static final long serialVersionUID = 1L;

    /* 总数 */
    private int total;

    /* 每页显示条数，默认 10 */
    private int size = 10;

    /* 总页数 */
    private int pages;

    /* 当前页 */
    private int current = 1;

    /* 查询总记录数（默认 true） */
    private boolean searchCount = true;

    /**
     * 开启排序（默认 true） 只在代码逻辑判断 并不截取sql分析
     *
     * @see com.baomidou.mybatisplus.mapper.SqlHelper fillWrapper
     **/
    private boolean openSort = true;

    /**
     * <p>
     * SQL 排序 ORDER BY 字段，例如： id DESC（根据id倒序查询）
     * </p>
     * <p>
     * DESC 表示按倒序排序(即：从大到小排序)<br>
     * ASC 表示按正序排序(即：从小到大排序)
     * </p>
     */
    private String orderByField;

    /**
     * 是否为升序 ASC（ 默认： true ）
     */
    private boolean isAsc = true;

    public Pagination() {
        super();
    }

    /**
     * <p>
     * 分页构造函数
     * </p>
     *
     * @param current 当前页
     * @param size    每页显示条数
     */
    public Pagination(int current, int size) {
        this(current, size, true);
    }

    public Pagination(int current, int size, boolean searchCount) {
        this(current, size, searchCount, true);
    }

    public Pagination(int current, int size, boolean searchCount, boolean openSort) {
        super(offsetCurrent(current, size), size);
        if (current > 1) {
            this.current = current;
        }
        this.size = size;
        this.searchCount = searchCount;
        this.openSort = openSort;
    }

    protected static int offsetCurrent(int current, int size) {
        if (current > 0) {
            return (current - 1) * size;
        }
        return 0;
    }

    public int getOffsetCurrent() {
        return offsetCurrent(this.current, this.size);
    }

    public boolean hasPrevious() {
        return this.current > 1;
    }

    public boolean hasNext() {
        return this.current < this.pages;
    }

    public int getTotal() {
        return total;
    }

    public Pagination setTotal(int total) {
        this.total = total;
        return this;
    }

    public int getSize() {
        return size;
    }

    public Pagination setSize(int size) {
        this.size = size;
        return this;
    }

    public int getPages() {
        if (this.size == 0) {
            return 0;
        }
        this.pages = this.total / this.size;
        if (this.total % this.size != 0) {
            this.pages++;
        }
        return this.pages;
    }

    public int getCurrent() {
        return current;
    }

    public Pagination setCurrent(int current) {
        this.current = current;
        return this;
    }

    public boolean isSearchCount() {
        return searchCount;
    }

    public Pagination setSearchCount(boolean searchCount) {
        this.searchCount = searchCount;
        return this;
    }

    public String getOrderByField() {
        return orderByField;
    }

    public Pagination setOrderByField(String orderByField) {
        if (StringUtils.isNotEmpty(orderByField)) {
            this.orderByField = orderByField;
        }
        return this;
    }

    public boolean isOpenSort() {
        return openSort;
    }

    public Pagination setOpenSort(boolean openSort) {
        this.openSort = openSort;
        return this;
    }

    public boolean isAsc() {
        return isAsc;
    }

    public Pagination setAsc(boolean isAsc) {
        this.isAsc = isAsc;
        return this;
    }

    @Override
    public String toString() {
        return "Pagination { total=" + total + " ,size=" + size + " ,pages=" + pages + " ,current=" + current + " }";
    }

}
