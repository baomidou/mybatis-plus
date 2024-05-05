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
package com.baomidou.mybatisplus.core.handlers;

import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import org.apache.ibatis.session.Configuration;

/**
 * 初始化 TableInfo 同时进行一些操作
 *
 * @author miemie
 * @since 2022-09-20
 */
public interface PostInitTableInfoHandler {

    /**
     * 提供对 TableInfo 增强的能力
     *
     * @param configuration MybatisConfiguration
     * @param entityType    实体类型
     * @return {@link TableInfo}
     */
    default TableInfo creteTableInfo(Configuration configuration, Class<?> entityType) {
        return new TableInfo(configuration, entityType);
    }

    /**
     * 参与 TableInfo 初始化
     *
     * @param tableInfo     TableInfo
     * @param configuration Configuration
     */
    default void postTableInfo(TableInfo tableInfo, Configuration configuration) {
        // ignore
    }

    /**
     * 参与 TableFieldInfo 初始化
     *
     * @param fieldInfo     TableFieldInfo
     * @param configuration Configuration
     */
    default void postFieldInfo(TableFieldInfo fieldInfo, Configuration configuration) {
        // ignore
    }
}
