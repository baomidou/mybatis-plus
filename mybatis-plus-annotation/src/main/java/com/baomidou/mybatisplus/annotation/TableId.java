/*
 * Copyright (c) 2011-2019, hubin (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.annotation;

import java.lang.annotation.*;

/**
 * 表主键标识
 *
 * @author hubin
 * @since 2016-01-23
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface TableId {

    /**
     * 字段值（驼峰命名方式，该值可无）
     */
    String value() default "";

    /**
     * 主键ID
     * {@link IdType}
     */
    IdType type() default IdType.NONE;

    /**
     * 是否保持使用全局的 columnPrefix 以及 columnPrefix 的值
     * <p> 只生效于 既设置了全局的 columnPrefix 或者 columnSuffix 也设置了上面 {@link #value()} 的值 </p>
     * <li> 如果是 false , 全局的 columnPrefix 以及 columnSuffix 不生效 </li>
     */
    boolean keepGlobalPrefixSuffix() default false;
}
