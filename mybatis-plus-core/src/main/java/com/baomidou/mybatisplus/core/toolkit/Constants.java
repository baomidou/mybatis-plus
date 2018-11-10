/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baomidou.mybatisplus.core.toolkit;

/**
 * <p>
 * mybatis_plus 自用常量集中管理
 * </p>
 *
 * @author miemie
 * @since 2018-07-22
 */
public interface Constants extends StringPool {

    /**
     * MD5
     */
    String MD5 = "MD5";
    /**
     * 实体类
     */
    String ENTITY = "et";
    /**
     * 实体类 带后缀 ==> .
     */
    String ENTITY_SPOT = ENTITY + DOT;
    /**
     * wrapper 类
     */
    String WRAPPER = "ew";
    /**
     * wrapper 类 带后缀 ==> .
     */
    String WRAPPER_SPOT = WRAPPER + DOT;
    /**
     * wrapper 类的属性 entity
     */
    String WRAPPER_ENTITY = WRAPPER_SPOT + "entity";
    /**
     * wrapper 类的属性 sqlSegment
     */
    String WRAPPER_SQLSEGMENT = WRAPPER_SPOT + "sqlSegment";
    /**
     * wrapper 类的属性 emptyOfNormal
     */
    String WRAPPER_EMPTYOFNORMAL = WRAPPER_SPOT + "emptyOfNormal";
    /**
     * wrapper 类的属性 nonEmptyOfNormal
     */
    String WRAPPER_NONEMPTYOFNORMAL = WRAPPER_SPOT + "nonEmptyOfNormal";
    /**
     * wrapper 类的属性 emptyOfWhere
     */
    String WRAPPER_EMPTYOFWHERE = WRAPPER_SPOT + "emptyOfWhere";
    /**
     * wrapper 类的判断属性 nonEmptyOfWhere
     */
    String WRAPPER_NONEMPTYOFWHERE = WRAPPER_SPOT + "nonEmptyOfWhere";
    /**
     * wrapper 类的属性 entity 带后缀 ==> .
     */
    String WRAPPER_ENTITY_SPOT = WRAPPER_SPOT + "entity" + DOT;
    /**
     * UpdateWrapper 类的属性 sqlSet
     */
    String U_WRAPPER_SQL_SET = WRAPPER_SPOT + "sqlSet";
    /**
     * QueryWrapper 类的属性 sqlSelect
     */
    String Q_WRAPPER_SQL_SELECT = WRAPPER_SPOT + "sqlSelect";
    /**
     * columnMap
     */
    String COLUMN_MAP = "cm";
    /**
     * columnMap.isEmpty
     */
    String COLUMN_MAP_IS_EMPTY = COLUMN_MAP + DOT + "isEmpty";
    /**
     * collection
     */
    String COLLECTION = "coll";
}
