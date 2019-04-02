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
 * 数据库表相关
 *
 * @author hubin, hanchunlin
 * @since 2016-01-23
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TableName {

    /**
     * 实体对应的表名
     */
    String value() default "";

    /**
     * 如果您需要为表指定前缀，请在此处指定，默认无前缀
     * <p>
     * 1、Mysql 等数据库中该前缀应指向表所在的数据库
     * 2、PostgreSQL 中应指向表的 Schema
     *
     * @return 表前缀
     */
    String prefix() default "";

    /**
     * 实体映射结果集
     */
    String resultMap() default "";

}
