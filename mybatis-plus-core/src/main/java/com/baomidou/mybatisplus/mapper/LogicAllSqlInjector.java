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
package com.baomidou.mybatisplus.mapper;

import com.baomidou.mybatisplus.entity.TableInfo;

/**
 * <p>
 * 逻辑删除，支持全update、select类型SQL自动添加逻辑删除字段，不仅仅是mp提供的api自动添加<br>
 * 1、支持逻辑删除
 * </p>
 *
 * @author hubin willenfoo
 * @Date 2017-09-09
 */
public class LogicAllSqlInjector extends SuperLogicSqlInjector {

    /**
     * 根据 ID 删除
     */
    @Override
    protected void injectDeleteByIdSql(boolean batch, Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
        super.injectDeleteByIdSql(batch, mapperClass, modelClass, table);
    }

    /**
     * 根据 SQL 删除
     */
    @Override
    protected void injectDeleteSql(Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
        super.injectDeleteSql(mapperClass, modelClass, table);
    }

    /**
     * 根据 MAP 删除
     */
    @Override
    protected void injectDeleteByMapSql(Class<?> mapperClass, TableInfo table) {
        super.injectDeleteByMapSql(mapperClass, table);
    }
}
