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
package com.baomidou.mybatisplus.core.conditions.interfaces;

/**
 * <p>
 * 查询条件封装
 * 比较值
 * </p>
 *
 * @author hubin miemie HCL
 * @since 2017-05-26
 */
public interface Compare<This, R> {

    /**
     * 等于 =
     */
    default This eq(R column, Object val) {
        return eq(true, column, val);
    }

    /**
     * 等于 =
     */
    This eq(boolean condition, R column, Object val);

    /**
     * 不等于 <>
     */
    default This ne(R column, Object val) {
        return ne(true, column, val);
    }

    /**
     * 不等于 <>
     */
    This ne(boolean condition, R column, Object val);

    /**
     * 大于 >
     */
    default This gt(R column, Object val) {
        return gt(true, column, val);
    }

    /**
     * 大于 >
     */
    This gt(boolean condition, R column, Object val);

    /**
     * 大于等于 >=
     */
    default This ge(R column, Object val) {
        return ge(true, column, val);
    }

    /**
     * 大于等于 >=
     */
    This ge(boolean condition, R column, Object val);

    /**
     * 小于 <
     */
    default This lt(R column, Object val) {
        return lt(true, column, val);
    }

    /**
     * 小于 <
     */
    This lt(boolean condition, R column, Object val);

    /**
     * 小于等于 <=
     */
    default This le(R column, Object val) {
        return le(true, column, val);
    }

    /**
     * 小于等于 <=
     */
    This le(boolean condition, R column, Object val);

    /**
     * LIKE '%值%'
     */
    default This like(R column, Object val) {
        return like(true, column, val);
    }

    /**
     * LIKE '%值%'
     */
    This like(boolean condition, R column, Object val);

    /**
     * NOT LIKE '%值%'
     */
    default This notLike(R column, Object val) {
        return notLike(true, column, val);
    }

    /**
     * NOT LIKE '%值%'
     */
    This notLike(boolean condition, R column, Object val);

    /**
     * LIKE '%值'
     */
    default This likeLeft(R column, Object val) {
        return likeLeft(true, column, val);
    }

    /**
     * LIKE '%值'
     */
    This likeLeft(boolean condition, R column, Object val);

    /**
     * LIKE '值%'
     */
    default This likeRight(R column, Object val) {
        return likeRight(true, column, val);
    }

    /**
     * LIKE '值%'
     */
    This likeRight(boolean condition, R column, Object val);
}
