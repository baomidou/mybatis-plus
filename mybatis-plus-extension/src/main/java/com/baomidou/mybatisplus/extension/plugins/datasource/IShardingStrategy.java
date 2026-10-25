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

import org.apache.ibatis.mapping.SqlCommandType;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

/**
 * 表和库切分策略接口
 *
 * @author hubin
 * @since 3.5.18
 */
public interface IShardingStrategy {

    void determineDatasourceKey(String group, String statementId, Object parameter, SqlCommandType commandType);

    default void changeDatabaseKey(String group, SqlCommandType commandType, Function<List<String>, String> selector) {
        ShardingGroupProperty property = ShardingContext.group(group);
        if (property == null) {
            throw new IllegalStateException("Datasource group not found: " + group);
        }
        List<String> keys = property.getDatasourceKeys(commandType, group);
        if (keys == null || keys.isEmpty()) {
            throw new IllegalStateException("No datasource available for group: " + group);
        }
        ShardingKey.change(group, commandType, ignored -> selector.apply(keys));
    }

    default String chooseRandomKey(List<String> keys) {
        return keys.size() == 1 ? keys.get(0) : keys.get(ThreadLocalRandom.current().nextInt(keys.size()));
    }
}
