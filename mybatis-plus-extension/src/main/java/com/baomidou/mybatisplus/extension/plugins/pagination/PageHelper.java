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

import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.metadata.IPage;


/**
 * <p>
 * 分页辅助类
 * </p>
 *
 * @author liutao , hubin
 * @since 2017-06-24
 */

public class PageHelper {

    /**
     * 分页本地线程变量
     */
    private static final ThreadLocal<IPage> LOCAL_PAGE = new ThreadLocal<>();

    /**
     * <p>
     * 获取总条数
     * </p>
     */
    public static Long getTotal() {
        if (isPageable()) {
            return LOCAL_PAGE.get().getTotal();
        }
        throw new MybatisPlusException("The current thread does not start paging. Please call before PageHelper.startPage");
    }

    /**
     * <p>
     * 释放资源并获取总条数
     * </p>
     */
    public static Long freeTotal() {
        Long total = getTotal();
        // 释放资源
        remove();
        return total;
    }

    /**
     * <p>
     * 计算当前分页偏移量
     * </p>
     *
     * @param current 当前页
     * @param size    每页显示数量
     * @return
     */
    public static long offsetCurrent(long current, long size) {
        if (current > 0) {
            return (current - 1) * size;
        }
        return 0;
    }

    /**
     * <p>
     * Page 分页偏移量
     * </p>
     */
    public static long offsetCurrent(IPage page) {
        if (null == page) {
            return 0;
        }
        return offsetCurrent(page.getCurrent(), page.getSize());
    }

    /**
     * <p>
     * 获取分页
     * </p>
     */
    public static IPage getPage() {
        return LOCAL_PAGE.get();
    }

    /**
     * <p>
     * 设置分页
     * </p>
     */
    public static void setPage(IPage page) {
        LOCAL_PAGE.set(page);
    }

    /**
     * <p>
     * 启动分页
     * </p>
     *
     * @param current 当前页
     * @param size    页大小
     */
    public static void startPage(long current, long size) {
        LOCAL_PAGE.set(new Page(current, size));
    }

    /**
     * <p>
     * 是否存在分页
     * </p>
     *
     * @return
     */
    public static boolean isPageable() {
        return LOCAL_PAGE.get() != null;
    }

    /**
     * <p>
     * 释放资源
     * </p>
     */
    public static void remove() {
        LOCAL_PAGE.remove();
    }
}
