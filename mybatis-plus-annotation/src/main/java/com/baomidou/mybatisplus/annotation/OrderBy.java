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
package com.baomidou.mybatisplus.annotation;

import java.lang.annotation.*;

/**
 * 自动排序，用法与SpringDtaJpa的OrderBy类似
 * 在执行MybatisPlus的方法selectList(),Page()等非手写查询时自动带上.
 * @author Dervish
 * @date 2021-04-13
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface OrderBy {

    /**
     * 默认倒序，设置 true 顺序
     */
    boolean asc() default false;

    /**
     * 数字越小越靠前
     */
    short sort() default Short.MAX_VALUE;
}
