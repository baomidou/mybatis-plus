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

import lombok.Getter;
import org.apache.ibatis.mapping.SqlCommandType;

/**
 * Mapper 方法分库对象
 *
 * @author hubin
 * @since 3.5.18
 */
@Getter
public class MappedSharding {
    private final String group;
    private final IShardingStrategy strategy;

    public MappedSharding(String group, IShardingStrategy strategy) {
        this.group = group;
        this.strategy = strategy;
    }

    public void changeDatasource(String statementId, Object parameter, SqlCommandType commandType) {
        strategy.determineDatasourceKey(group, statementId, parameter, commandType);
    }

}
