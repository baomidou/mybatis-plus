/*
 * Copyright (c) 2011-2025, baomidou (jobob@qq.com).
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
package com.baomidou.mybatisplus.extension.plugins.datasource;

import org.apache.ibatis.mapping.MappedStatement;

/**
 * 动态数据源处理器
 *
 * @author hubin
 * @since 3.5.18
 */
public interface IShardingProcessor {

    /**
     * @param mappedStatement 执行语句
     * @param parameter 执行参数
     * @param datasourceKey 当前上下文数据源 KEY
     * @return 是否已处理数据源切换，返回 false 时走默认分片路由
     */
    boolean changeDatasource(MappedStatement mappedStatement, Object parameter, String datasourceKey);
}
