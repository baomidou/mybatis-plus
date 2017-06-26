/**
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
package com.baomidou.mybatisplus.plugins.pagination;

import com.baomidou.mybatisplus.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;

/**
 * <p>
 * 分页辅助类
 * </p>
 *
 * @author liutao , hubin
 * @since 2017-06-24
 */

public class PageHelper {

    // 分页本地线程变量
    private static final ThreadLocal<Pagination> LOCAL_PAGE = new ThreadLocal<>();

    /**
     * <p>
     * 获取总条数
     * </p>
     *
     * @return
     */
    public static int getTotal() {
        if (isPageable()) {
            return LOCAL_PAGE.get().getTotal();
        } else {
            throw new MybatisPlusException("The current thread does not start paging. Please call before PageHelper.startPage");
        }
    }

    /**
     * <p>
     * 获取分页
     * </p>
     *
     * @return
     */
    public static Pagination getPagination() {
        return LOCAL_PAGE.get();
    }

    /**
     * <p>
     * 设置分页
     * </p>
     *
     * @param page
     */
    public static void setPagination(Pagination page) {
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
    public static void startPage(int current, int size) {
        LOCAL_PAGE.set(new Pagination(current, size));
    }

    /**
     * <p>
     * 是否存在分页
     * </p>
     *
     * @return
     */
    public static boolean isPageable() {
        return LOCAL_PAGE.get() != null ? true : false;
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
